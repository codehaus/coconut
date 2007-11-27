/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.policy.spi.AbstractPolicy;
import org.coconut.internal.util.IndexedList;

/**
 * <a href="http://larch-www.lcs.mit.edu:8001/~corbato/">Frank Corbató</a> introduced
 * CLOCK in 1968 as a one-bit approximation to LRU in the Multics system.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <T>
 *            the type of data maintained by this policy
 */
@NotThreadSafe
public class ClockPolicy<T> extends AbstractPolicy<T> implements Serializable, Cloneable {

    /** A unique policy name. */
    public static final String NAME = "Clock";

    /** serialVersionUID. */
    private static final long serialVersionUID = -861463593222316896L;

    /** The wrapped inner clock policy. */
    private final InnerClockPolicy<T> policy;

    /**
     * Constructs a new ClockPolicy with an initial size of 100.
     */
    public ClockPolicy() {
        this(100);
    }

    /**
     * Constructs a new ClockPolicy by copying an existing ClockPolicy.
     * 
     * @param other
     *            the ClockPolicy policy to copy from
     */
    public ClockPolicy(ClockPolicy other) {
        policy = new InnerClockPolicy<T>(other.policy);
    }

    /**
     * Constructs a new LFUPolicy with a specified initial size.
     * 
     * @param initialCapacity
     *            the initial size of the internal list, must be 0 or greater
     * @throws IllegalArgumentException
     *             if the specified size is a negative number
     */
    public ClockPolicy(int initialCapacity) {
        policy = new InnerClockPolicy<T>(initialCapacity);
    }

    /**
     * {@inheritDoc}
     */
    public int add(T data, AttributeMap ignore) {
        return policy.add(data);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        while (evictNext() != null) {
            /* ignore */
        }
    }

    /**
     * {@inheritDoc}
     */
    public ClockPolicy<T> clone() {
        return new ClockPolicy<T>(this);
    }

    /**
     * {@inheritDoc}
     */
    public T evictNext() {
        return policy.evictNext();
    }

    /**
     * {@inheritDoc}
     */
    public int getSize() {
        return policy.getSize();
    }

    /**
     * {@inheritDoc}
     */
    public T peek() {
        return policy.peek();
    }

    /**
     * {@inheritDoc}
     */
    public List<T> peekAll() {
        return policy.peekAll();
    }

    /**
     * {@inheritDoc}
     */
    public T remove(int index) {
        return policy.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Clock Policy with " + getSize() + " entries";
    }

    /**
     * {@inheritDoc}
     */
    public void touch(int index) {
        policy.touch(index);
    }

    /**
     * {@inheritDoc}
     */
    public boolean update(int index, T newElement, AttributeMap ignore) {
        policy.replace(index, newElement);
        return false;
    }

    /**
     * An inner class used for maintaining the clock datastructure.
     * 
     * @param <T>
     *            the type of data maintained by this policy
     */
    static class InnerClockPolicy<T> extends IndexedList<T> implements Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -2146530585201851381L;

        /** bits indicating whether or not an entry has been visited. */
        private int[] bits;

        /** the current placement of the clock. */
        private int handPosition;

        /**
         * Constructs a new ClockPolicy by copying an existing ClockPolicy.
         * 
         * @param other
         *            the clock policy to copy from
         */
        InnerClockPolicy(InnerClockPolicy other) {
            super(other);
            handPosition = other.handPosition;
            bits = new int[other.bits.length];
            System.arraycopy(other.bits, 0, bits, 0, other.bits.length);
        }

        /**
         * Constructs a new ClockPolicy with a specified initial size.
         * 
         * @param initialCapacity
         *            the initial capacity for this policy, must be bigger then 0.
         * @throws IllegalArgumentException
         *             if <tt>initialCapacity</tt> is less than 1
         */
        InnerClockPolicy(int initialCapacity) {
            super(initialCapacity);
            bits = new int[initialCapacity + 1];
            bits[0] = 1;
        }

        /** Evicts the next element. */
        public T evictNext() {
            if (currentEntryIndex == 0) {
                return null;
            } else {
                for (;;) {
                    if (bits[handPosition] == 0) {
                        freeEntries[--currentEntryIndex] = handPosition; // recycle
                        // entry
                        T removeMe = data[handPosition];
                        data[handPosition] = null;

                        if (currentEntryIndex == 0) { // clear
                            next[0] = 0;
                            prev[0] = 0;

                        } else {
                            prev[next[handPosition]] = prev[handPosition]; // update
                            // next
                            // head
                            next[prev[handPosition]] = next[handPosition];
                        }
                        bits[handPosition] = 0;
                        handPosition = next[handPosition];
                        return removeMe;
                    } else {
                        if (handPosition != 0) // keep head-index at 1
                            bits[handPosition] = 0;
                        handPosition = next[handPosition];
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public T peek() {
            if (currentEntryIndex == 0) {
                return null;
            } else {
                return peekAll().get(0); // TODO optimize
            }
        }

        /**
         * {@inheritDoc}
         */
        public List<T> peekAll() {
            ArrayList<T> col = new ArrayList<T>(currentEntryIndex);
            if (currentEntryIndex == 0) {
                return col;
            } else {
                int tempClock = handPosition;
                int clockCount = 2; // we need to go around two times, one 0's
                // and
                // one for 1's
                for (;;) {
                    if (tempClock == handPosition && clockCount-- == 0)
                        return col; // okay we have been around twice
                    if (clockCount == 1) {
                        // looking for 0's
                        if (bits[tempClock] == 0) {
                            col.add(data[tempClock]);
                        }
                    } else {
                        // looking for 1's
                        if (bits[tempClock] == 1) {
                            col.add(data[tempClock]);
                        }
                    }
                    tempClock = next[tempClock];
                    if (tempClock == 0) {
                        tempClock = next[tempClock];
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public void touch(int index) {
            bits[index] = 1;
        }

        /**
         * {@inheritDoc}
         */
        protected void innerAdd(int index) {
            if (currentEntryIndex == 1) // first element
                handPosition = index;
        }

        /**
         * {@inheritDoc}
         */
        protected void innerRemove(int index) {
            bits[index] = 0; // lazy recycle
            if (index == handPosition)
                handPosition = next[index];
        }

        /**
         * {@inheritDoc}
         */
        protected void innerResize(int newSize) {
            super.innerResize(newSize);
            int[] oldBits = bits;
            bits = new int[newSize];
            System.arraycopy(oldBits, 0, bits, 0, Math.min(oldBits.length, freeEntries.length));

        }
    }
}

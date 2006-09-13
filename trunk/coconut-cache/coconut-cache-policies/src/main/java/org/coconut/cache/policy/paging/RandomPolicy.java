/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.spi.AbstractPolicy;

/**
 * This cache policy is not safe for concurrent access.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@ThreadSafe(false)
public class RandomPolicy<T> extends AbstractPolicy<T> implements ReplacementPolicy<T>, Serializable, Cloneable {

    /** A unique policy name. */
    public static final String NAME = "Random";

    /** serialVersionUID. */
    private static final long serialVersionUID = -7065776518426915749L;

    private T[] data;

    private int[] freeEntries;

    private int nextEntryIndex;

    private int[] references;

    /** The source of randomness. */
    private final Random rnd = new Random();

    /** The maximum number of elements, is automatically resized */
    private int threshold;

    /**
     * Constructs a new RandomPolicy with an initial size of 100.
     */
    public RandomPolicy() {
        this(100);
    }

    /**
     * Constructs a new RandomPolicy with a specified initial size.
     * 
     * @param size
     *            the initial size of the internal list, must be 0 or greater
     * @throws IllegalArgumentException
     *             if the specified size is a negative number
     */
    @SuppressWarnings("unchecked")
    public RandomPolicy(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(
                    "initialSize must be a positive number or 0");
        }
        threshold = initialCapacity;
        references = new int[initialCapacity];
        data = (T[]) new Object[initialCapacity];
        freeEntries = new int[initialCapacity];
        for (int i = 0; i < freeEntries.length; i++) {
            references[i] = -1;
            freeEntries[i] = i;
        }
    }

    /**
     * Constructs a new RandomPolicy by copying an existing. The copy is not
     * required to use the same source of randomness.
     * 
     * @param h
     *            the policy to copy
     */
    @SuppressWarnings("unchecked")
    public RandomPolicy(RandomPolicy h) {
        threshold = h.threshold;
        nextEntryIndex = h.nextEntryIndex;
        references = new int[h.references.length];
        System.arraycopy(h.references, 0, references, 0, h.references.length);
        freeEntries = new int[h.freeEntries.length];
        System
                .arraycopy(h.freeEntries, 0, freeEntries, 0,
                        h.freeEntries.length);
        data = (T[]) new Object[h.data.length];
        System.arraycopy(h.data, 0, data, 0, h.data.length);
    }

    /**
     * @see org.coconut.cache.CachePolicy#add(null)
     */
    public int add(T newData) {
        if (nextEntryIndex >= threshold - 1)
            resize(threshold * 2);

        this.data[nextEntryIndex] = newData;

        int newIndex = freeEntries[nextEntryIndex];

        references[newIndex] = nextEntryIndex++; // update previous tail to
        // point at new
        return newIndex + 1; // indexs are positive numbers
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#clear()
     */
    public void clear() {
        while (evictNext() != null) {
            /* ignore */
        }
    }
    
    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#evictNext()
     */
    public T evictNext() {
        if (nextEntryIndex == 0) {
            return null; // list is empty
        } else if (nextEntryIndex == 1) {
            return removeIndexed(0); // remove last element from the list
        } else { // removes a random element from the list
            return removeIndexed(getRandomElement());
        }
    }

    private int getRandomElement() {
        return rnd.nextInt(nextEntryIndex - 1);
    }

    
    /**
     * @return the number of entries in currently held by the policy.
     */

    public int getSize() {
        return nextEntryIndex;
    }

    /**
     * Removes a random element.
     * 
     * @see org.coconut.cache.policy.ReplacementPolicy#evictNext()
     */
    public T peek() {
        if (nextEntryIndex == 0) {
            return null; // list is empty
        } else if (nextEntryIndex == 1) {
            return data[0]; // only one element in the list
        } else { // returns a random element from the list
            return data[getRandomElement()];
        }
    }

    /**
     * This method return a random sorted list of all the elements. Multiple
     * invocations of this method will most likely return different results.
     * 
     * @see org.coconut.cache.policy.ReplacementPolicy#peekAll()
     */
    @SuppressWarnings("unchecked")
    public List<T> peekAll() {
        T[] o = (T[]) new Object[nextEntryIndex];
        System.arraycopy(data, 0, o, 0, nextEntryIndex);
        List<T> l = Arrays.asList(o);
        Collections.shuffle(l, rnd);
        return l;
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#remove(int)
     */
    public T remove(int index) {
        index--;
        if (index > data.length - 1 || references[index] == -1)
            return null;
        return removeIndexed(references[index]);
    }

    private T removeIndexed(int remove) {
        references[freeEntries[remove]] = -1;
        T removeMe = data[remove];
        if (--nextEntryIndex == 0) { // clear
            freeEntries[0] = 0;
            data[0] = null;
            nextEntryIndex = 0;
        } else {
            references[freeEntries[nextEntryIndex]] = remove;
            int old = freeEntries[nextEntryIndex];

            freeEntries[nextEntryIndex] = freeEntries[remove]; // recycle entry
            freeEntries[remove] = old;
            data[remove] = data[nextEntryIndex];
            data[nextEntryIndex] = null;
        }

        return removeMe;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newSize) {
        threshold = newSize + 1;
        int[] oldReferences = references;
        references = new int[threshold];
        System.arraycopy(oldReferences, 0, references, 0, Math.min(
                oldReferences.length, references.length));

        int[] oldFreeEntries = freeEntries;
        freeEntries = new int[threshold];
        System.arraycopy(oldFreeEntries, 0, freeEntries, 0, Math.min(
                oldFreeEntries.length, freeEntries.length));
        for (int i = oldFreeEntries.length - 1; i < freeEntries.length; i++) {
            freeEntries[i] = i;
            references[i] = -1;
        }
        Object[] oldData = data;
        data = (T[]) new Object[threshold];
        System.arraycopy(oldData, 0, data, 0, Math.min(oldData.length,
                data.length));
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#touch(int)
     */
    public void touch(int index) {
        // ignore
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#update(int, java.lang.Object)
     */
    public boolean update(int index, T newElement) {
        data[references[index]]=newElement;
        return true; // Random never rejects an entry
    }

    // ///CLOVER:OFF
    // void print() {
    // for (int i = 0; i < threshold; i++) {
    // System.out.println(i + " " + references[i] + " " + freeEntries[i]
    // + " Data: " + data[i]);
    // }
    // }
    // ///CLOVER:ON

}

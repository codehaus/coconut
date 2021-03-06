/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.HitsAttribute;
import org.coconut.cache.policy.AbstractReplacementPolicy;
import org.coconut.internal.util.IndexedHeap;

/**
 * A LFU based replacement policy.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <T>
 *            the type of data maintained by this policy
 */
@NotThreadSafe
public class LFUPolicy<T> extends AbstractReplacementPolicy<T> implements Serializable, Cloneable {

    /** A unique policy name. */
    public static final String NAME = "LFU";

    /** serialVersionUID. */
    private static final long serialVersionUID = -6697601242550775282L;

    /** The internal heap used for bookkeeping. */
    private final IndexedHeap<T> heap;

    /**
     * Constructs a new LFUPolicy with an initial size of 100.
     */
    public LFUPolicy() {
        this(100);
    }

    /**
     * Constructs a new LFUPolicy with a specified initial size.
     *
     * @param initialCapacity
     *            the initial size of the internal list, must be 0 or greater
     * @throws IllegalArgumentException
     *             if the specified size is a negative number
     */
    public LFUPolicy(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("size must be 0 or greater, was " + initialCapacity);
        }
        heap = new IndexedHeap<T>(initialCapacity);
    }

    /**
     * Constructs a new LFUPolicy by copying an existing LFUPolicy.
     *
     * @param policy
     *            the LFU policy to copy from
     */
    public LFUPolicy(LFUPolicy<T> policy) {
        this.heap = new IndexedHeap<T>(policy.heap);
    }

    /** {@inheritDoc} */
    public int add(T data, AttributeMap map) {
        long hits = HitsAttribute.get(map);
        return add(data, hits);
    }

    /**
     * Similar to {@link #add(Object)} but also takes the number of hits for specified
     * data.
     *
     * @param data
     *            the element to add to the replacement policy
     * @param hits
     *            the number of hits for the data
     * @return a positive index that can be used to reference the element in the
     *         replacement policy. A negative number is returned if the element is not
     *         accepted into the replacement policy
     */
    public int add(T data, long hits) {
        if (hits < 0) {
            throw new IllegalArgumentException("hits must a non-negative number, was" + hits);
        }
        return heap.add(data, hits);
    }

    /** {@inheritDoc} */
    public void clear() {
        heap.clear();
    }

    /** {@inheritDoc} */
    @Override
    public LFUPolicy<T> clone() {
        return new LFUPolicy<T>(this);
    }

    /** {@inheritDoc} */
    public T evictNext() {
        return heap.poll();
    }

    /** {@inheritDoc} */
    public int getSize() {
        return heap.size();
    }

    /** {@inheritDoc} */
    public T peek() {
        return heap.peek();
    }

    /** {@inheritDoc} */
    public List<T> peekAll() {
        return heap.peekAll();
    }

    /** {@inheritDoc} */
    public T remove(int index) {
        return heap.remove(index);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "LFU Policy with " + heap.size() + " entries";
    }

    /** {@inheritDoc} */
    public void touch(int index) {
        heap.changePriorityDelta(index, 1);
    }

    /** {@inheritDoc} */
    public boolean update(int index, T newElement, AttributeMap map) {
        heap.replace(index, newElement); // ignore hits for new element for now
        return true;
    }
}

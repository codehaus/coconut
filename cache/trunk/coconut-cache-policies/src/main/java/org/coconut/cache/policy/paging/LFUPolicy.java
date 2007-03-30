/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.policy.PolicyAttributes;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.spi.AbstractPolicy;
import org.coconut.core.AttributeMap;
import org.coconut.internal.util.IndexedHeap;

/**
 * A LFU based replacement policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@NotThreadSafe
public class LFUPolicy<T> extends AbstractPolicy<T> implements ReplacementPolicy<T>,
        Serializable, Cloneable {

    /** serialVersionUID */
    private static final long serialVersionUID = -6697601242550775282L;

    /** A unique policy name. */
    public static final String NAME = "LFU";

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
            throw new IllegalArgumentException("size must be 0 or greater, was "
                    + initialCapacity);
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

    /**
     * @{inheritDoc}
     */
    public int add(T data, AttributeMap map) {
        long hits = map.getLong(PolicyAttributes.HITS, 1);
        return add(data, hits);
    }

    /**
     * @{inheritDoc}
     */
    public int add(T data, long hits) {
        if (hits < 0) {
            throw new IllegalArgumentException("hits must a non-negative number, was"
                    + hits);
        }
        return heap.add(data, hits);
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
     * @{inheritDoc}
     */
    @Override
    public LFUPolicy<T> clone() {
        return new LFUPolicy<T>(this);
    }

    /**
     * @{inheritDoc}
     */
    public T evictNext() {
        return heap.poll();
    }

    /**
     * @{inheritDoc}
     */
    public int getSize() {
        return heap.size();
    }

    /**
     * @{inheritDoc}
     */
    public T peek() {
        return heap.peek();
    }

    /**
     * @{inheritDoc}
     */
    public List<T> peekAll() {
        return heap.peekAll();
    }

    /**
     * @{inheritDoc}
     */
    public T remove(int index) {
        return heap.remove(index);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public String toString() {
        return "LFU Policy with " + heap.size() + " entries";
    }

    /**
     * @{inheritDoc}
     */
    public void touch(int index) {
        // TODO what if instanceof PolicyObject?
        // use getHits()?
        heap.changePriorityDelta(index, 1);
    }

    /**
     * @{inheritDoc}
     */
    public boolean update(int index, T newElement) {
        // TODO what about hits for newElement
        heap.replace(index, newElement);
        return true;
    }
    /**
     * @{inheritDoc}
     */
    public boolean update(int index, T newElement, AttributeMap map) {
        // TODO what about hits for newElement
        heap.replace(index, newElement);
        return true;
    }
}

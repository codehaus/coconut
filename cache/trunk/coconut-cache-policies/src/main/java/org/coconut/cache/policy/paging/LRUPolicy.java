/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.ReplacementPolicy;
import org.coconut.cache.spi.AbstractPolicy;
import org.coconut.core.AttributeMap;
import org.coconut.internal.util.IndexedList;

/**
 * A Least Recently Used replacement policy discards the least recently used
 * items first.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $id$
 */
@NotThreadSafe
public class LRUPolicy<T> extends AbstractPolicy<T> implements ReplacementPolicy<T>, Serializable {

    /** A unique policy name. */
    public static final String NAME = "LRU";

    /** serialVersionUID. */
    private static final long serialVersionUID = 888111204668789362L;

    /** The list used for bookkeeping. */
    private final IndexedList<T> list;

    /**
     * Constructs a new LRUPolicy with an initial size of 100.
     */
    public LRUPolicy() {
        this(100);
    }

    /**
     * Constructs a new LRUPolicy with a specified initial size.
     * 
     * @param initialCapacity
     *            the initial size of the internal list, must be 0 or greater
     * @throws IllegalArgumentException
     *             if the specified size is a negative number
     */
    public LRUPolicy(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("size must be a positive number or 0");
        }
        list = new IndexedList<T>(initialCapacity);
    }

    /**
     * Constructs a new LRUPolicy by copying an existing LRUPolicy.
     * 
     * @param policy
     *            the LRUPolicy policy to copy from
     */
    public LRUPolicy(LRUPolicy<T> policy) {
        this.list = new IndexedList<T>(policy.list);
    }

    /**
     * {@inheritDoc}
     */
    public int add(T data, AttributeMap ignore) {
        return list.add(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LRUPolicy<T> clone() {
        return new LRUPolicy<T>(this);
    }

    /**
     * {@inheritDoc}
     */
    public T evictNext() {
        return list.removeFirst();
    }

    /**
     * Returns the number of elements in the policy.
     */
    public int getSize() {
        return list.getSize();
    }

    /**
     * {@inheritDoc}
     */
    public T peek() {
        return list.peek();
    }

    /**
     * {@inheritDoc}
     */
    public List<T> peekAll() {
        return list.peekAll();
    }

    /**
     * {@inheritDoc}
     */
    public T remove(int index) {
        return list.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LRU Policy with " + list.getSize() + " entries";
    }

    /**
     * {@inheritDoc}
     */
    public void touch(int index) {
        list.touch(index);
    }

    /**
     * {@inheritDoc}
     */
    public boolean update(int index, T newElement, AttributeMap ignore) {
        list.replace(index, newElement);
        return true; // LRU never rejects an entry
    }

    public void clear() {
        list.clear();
    }
}

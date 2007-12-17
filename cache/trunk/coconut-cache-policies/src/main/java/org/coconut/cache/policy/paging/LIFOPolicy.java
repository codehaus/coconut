/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.policy.spi.AbstractReplacementPolicy;

/**
 * A LIFO based replacement policy.
 * <p>
 * This implementation works by wrapping an LRU replacement policy and ignoring any calls
 * to touch.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <T>
 *            the type of data maintained by this policy
 */
@NotThreadSafe
public class LIFOPolicy<T> extends AbstractReplacementPolicy<T> implements Serializable {

    /** A unique policy name. */
    public static final String NAME = "LIFO";

    /** serialVersionUID. */
    private static final long serialVersionUID = 6547391198048004043L;

    /** The wrapped LRU list. */
    private final LRUPolicy<T> policy;

    /**
     * Constructs a new LFUPolicy with an initial size of 100.
     */
    public LIFOPolicy() {
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
    public LIFOPolicy(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("size must be 0 or greater, was "
                    + initialCapacity);
        }
        policy = new LRUPolicy<T>(initialCapacity);
    }

    /**
     * Constructs a new LFUPolicy by copying an existing LFUPolicy.
     * 
     * @param policy
     *            the LFU policy to copy from
     */
    public LIFOPolicy(LIFOPolicy<T> policy) {
        this.policy = new LRUPolicy<T>(policy.policy);
    }

    /** {@inheritDoc} */
    public int add(T data, AttributeMap ignore) {
        return policy.add(data, ignore);
    }

    /** {@inheritDoc} */
    public void clear() {
        while (evictNext() != null) {
            /* ignore */
        }
    }

    /** {@inheritDoc} */
    @Override
    public LIFOPolicy<T> clone() {
        return new LIFOPolicy<T>(this);
    }

    /** {@inheritDoc} */
    public T evictNext() {
        return policy.evictNext();
    }

    /** {@inheritDoc} */
    public int getSize() {
        return policy.getSize();
    }

    /** {@inheritDoc} */
    public T peek() {
        return policy.peek();
    }

    /** {@inheritDoc} */
    public List<T> peekAll() {
        return policy.peekAll();
    }

    /** {@inheritDoc} */
    public T remove(int index) {
        return policy.remove(index);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "LIFO Policy with " + policy.getSize() + " entries";
    }

    /** {@inheritDoc} */
    public void touch(int index) {
    // ignore touch
    }

    /** {@inheritDoc} */
    public boolean update(int index, T newElement, AttributeMap ignore) {
        return policy.update(index, newElement, ignore);
    }
}

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

/**
 * A FIFO based replacement policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@NotThreadSafe
public class FIFOPolicy<T> extends AbstractPolicy<T> implements ReplacementPolicy<T>,
        Serializable, Cloneable {

    /** A unique policy name. */
    public static final String NAME = "FIFO";

    /** serialVersionUID. */
    private static final long serialVersionUID = -8585661504028685651L;

    /** Use a MRU policy for ordering, ignoring touch to elements. */
    private final MRUPolicy<T> policy;

    /**
     * Constructs a new FIFOPolicy with an initial size of 100.
     */
    public FIFOPolicy() {
        this(100);
    }

    /**
     * Constructs a new FIFOPolicy by copying an existing FIFOPolicy.
     * 
     * @param policy
     *            the fifo policy to copy from
     */
    public FIFOPolicy(FIFOPolicy<T> policy) {
        this.policy = new MRUPolicy<T>(policy.policy);
    }

    /**
     * Constructs a new FifoPolicy with a specified initial size.
     * 
     * @param initialCapacity
     *            the initial size of the internal list, must be 0 or greater
     * @throws IllegalArgumentException
     *             if the specified size is a negative number
     */
    public FIFOPolicy(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("size must be 0 or greater, was "
                    + initialCapacity);
        }
        policy = new MRUPolicy<T>(initialCapacity);
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#add(java.lang.Object, org.coconut.core.AttributeMap)
     */
    public int add(T data, AttributeMap attributes) {
        return policy.add(data, attributes);
    }

    /**
     * @inheritDoc
     */
    @Override
    public FIFOPolicy<T> clone() {
        return new FIFOPolicy<T>(this);
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#evictNext()
     */
    public T evictNext() {
        return policy.evictNext();
    }

    /**
     * Returns the number of elements contained in this policy.
     * 
     * @return the number of elements contained in this policy.
     */
    public int getSize() {
        return policy.getSize();
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#peek()
     */
    public T peek() {
        return policy.peek();
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#peekAll()
     */
    public List<T> peekAll() {
        return policy.peekAll();
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#remove(int)
     */
    public T remove(int index) {
        return policy.remove(index);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Fifo Policy with " + getSize() + " entries";
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#update(int, java.lang.Object, org.coconut.core.AttributeMap)
     */
    public boolean update(int index, T newElement, AttributeMap ignore) {
        return policy.update(index, newElement, ignore);
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#touch(int)
     */
    public void touch(int index) {
        // ignore
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#clear()
     */
    public void clear() {
        policy.clear();
    }
}

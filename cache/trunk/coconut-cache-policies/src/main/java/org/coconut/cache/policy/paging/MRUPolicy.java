/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.policy.spi.AbstractReplacementPolicy;
import org.coconut.internal.util.IndexedStack;

/**
 * A MRU based replacement policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <T>
 *            the type of data maintained by this policy
 */
@NotThreadSafe
public class MRUPolicy<T> extends AbstractReplacementPolicy<T> implements Serializable {

    /** A unique policy name. */
    public static final String NAME = "MRU";

    /** serialVersionUID. */
    private static final long serialVersionUID = 7334611172293116644L;

    /** The list used for bookkeeping. */
    private final IndexedStack<T> stack;

    /**
     * Constructs a new MRUPolicy with an initial size of 100.
     */
    public MRUPolicy() {
        this(100);
    }

    /**
     * Constructs a new MRUPolicy with a specified initial size.
     * 
     * @param initialCapacity
     *            the initial size of the internal list, must be 0 or greater
     * @throws IllegalArgumentException
     *             if the specified size is a negative number
     */
    public MRUPolicy(int initialCapacity) throws IllegalArgumentException {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be a positive number or 0");
        }
        stack = new IndexedStack<T>(initialCapacity);
    }

    /**
     * Constructs a new MRUPolicy by copying an existing MRUPolicy.
     * 
     * @param policy
     *            the MRUPolicy to copy from
     */
    public MRUPolicy(MRUPolicy policy) {
        stack = new IndexedStack<T>(policy.stack);
    }

    /** {@inheritDoc} */
    public int add(T data, AttributeMap ignore) {
        return stack.add(data);
    }

    /** {@inheritDoc} */
    public void clear() {
        stack.clear();
    }

    /** {@inheritDoc} */
    public MRUPolicy<T> clone() {
        return new MRUPolicy<T>(this);
    }

    /** {@inheritDoc} */
    public T evictNext() {
        return stack.remove();
    }

    /** {@inheritDoc} */
    public int getSize() {
        return stack.getSize();
    }

    /** {@inheritDoc} */
    public T peek() {
        return stack.peek();
    }

    /** {@inheritDoc} */
    public List<T> peekAll() {
        return stack.peekAll();
    }

    /** {@inheritDoc} */
    public T remove(int index) {
        return stack.remove(index);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "MRU Policy with " + stack.getSize() + " entries";
    }

    /** {@inheritDoc} */
    public void touch(int index) {
        stack.touch(index);
    }

    /** {@inheritDoc} */
    public boolean update(int index, T newElement, AttributeMap ignore) {
        stack.replace(index, newElement);
        return true; // MRU never rejects an entry
    }
}

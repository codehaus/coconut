/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.concurrent;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface BulkSink<E> /* extends Offerable<E> */ {

    /**
     * Inserts the specified element into this queue, waiting if necessary for
     * space to become available.
     * 
     * @param e
     *            the element to add
     * @throws InterruptedException
     *             if interrupted while waiting
     * @throws ClassCastException
     *             if the class of the specified element prevents it from being
     *             added to this queue
     * @throws NullPointerException
     *             if the specified element is null
     * @throws IllegalArgumentException
     *             if some property of the specified element prevents it from
     *             being added to this queue
     */
    void put(E e) throws InterruptedException;

    /**
     * Inserts the specified element into this queue, waiting up to the
     * specified wait time if necessary for space to become available.
     * 
     * @param e
     *            the element to add
     * @param timeout
     *            how long to wait before giving up, in units of <tt>unit</tt>
     * @param unit
     *            a <tt>TimeUnit</tt> determining how to interpret the
     *            <tt>timeout</tt> parameter
     * @return <tt>true</tt> if successful, or <tt>false</tt> if the
     *         specified waiting time elapses before space is available
     * @throws InterruptedException
     *             if interrupted while waiting
     * @throws ClassCastException
     *             if the class of the specified element prevents it from being
     *             added to this queue
     * @throws NullPointerException
     *             if the specified element is null
     * @throws IllegalArgumentException
     *             if some property of the specified element prevents it from
     *             being added to this queue
     */
    boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Inserts the specified element into this queue if it is possible to do so
     * immediately without violating capacity restrictions, returning
     * <tt>true</tt> upon success and throwing an
     * <tt>IllegalStateException</tt> if no space is currently available. When
     * using a capacity-restricted queue, it is generally preferable to use
     * {@link #offer(Object) offer}.
     * 
     * @param e
     *            the element to add
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws IllegalStateException
     *             if the element cannot be added at this time due to capacity
     *             restrictions
     * @throws ClassCastException
     *             if the class of the specified element prevents it from being
     *             added to this queue
     * @throws NullPointerException
     *             if the specified element is null
     * @throws IllegalArgumentException
     *             if some property of the specified element prevents it from
     *             being added to this queue
     */
    boolean add(E e);

    /**
     * Inserts the specified elements into this queue, waiting if necessary for
     * space to become available for all the item. The behavior of this
     * operation is undefined if the specified collection is modified while the
     * operation is in progress. (This implies that the behavior of this call is
     * undefined if the specified collection is this collection, and this
     * collection is nonempty.)
     * 
     * @param c
     *            collection containing elements to be inserted into this queue
     * @throws ClassCastException
     *             if the class of an element of the specified collection
     *             prevents it from being added to this collection
     * @throws NullPointerException
     *             if the specified collection contains a null element, or if
     *             the specified collection is null
     * @throws IllegalArgumentException
     *             if some property of an element of the specified collection
     *             prevents it from being added to this collection
     * @throws IllegalStateException
     *             if not all the elements can be added at this time due to
     *             insertion restrictions
     */
    boolean offerAll(Collection<? extends E> c);

    Collection<? extends E> offerAll(Collection<? extends E> c, long timeout,
            TimeUnit unit) throws InterruptedException;

    /**
     * Inserts the specified elements into this queue, waiting if necessary for
     * space to become available for all the items. Not atomic. The behavior of
     * this operation is undefined if the specified collection is modified while
     * the operation is in progress. (This implies that the behavior of this
     * call is undefined if the specified collection is this collection, and
     * this collection is nonempty.)
     * 
     * @param c
     *            collection containing elements to be inserted into this queue
     * @throws ClassCastException
     *             if the class of an element of the specified collection
     *             prevents it from being added to this collection
     * @throws NullPointerException
     *             if the specified collection contains a null element, or if
     *             the specified collection is null
     * @throws IllegalArgumentException
     *             if some property of an element of the specified collection
     *             prevents it from being added to this collection
     * @throws IllegalStateException
     *             if not all the elements can be added at this time due to
     *             insertion restrictions
     */
    void putAll(Collection<? extends E> c) throws InterruptedException;
}

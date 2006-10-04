/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.concurrent;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface BulkQueue<E> extends BlockingQueue<E> {

    int drainTo(Collection<? super E> c, long timeout, TimeUnit unit)
            throws InterruptedException;

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

    /* Usage??? */
    // Collection<? extends E> tryOfferAll(Collection<? extends E> c);
}

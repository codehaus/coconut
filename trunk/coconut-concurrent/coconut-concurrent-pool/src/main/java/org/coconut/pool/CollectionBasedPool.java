/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CollectionBasedPool<E> implements Pool<E> {
    private final IdentityHashMap<E, Integer> ihm = new IdentityHashMap<E, Integer>();

    private final ArrayList<E> al;

    private final Semaphore sem;

    public CollectionBasedPool(Collection<? extends E> col) {
        // Check identity?´
        int count = col.size();
        int i = 0;
        sem = new Semaphore(count);
        al = new ArrayList<E>(count);
        for (E e : col) {
            al.set(i, e);
        }
    }

    /**
     * @see org.coconut.pool.Pool#borrow()
     */
    public E borrow() throws InterruptedException {
        sem.acquire();
        return null;
    }

    /**
     * @see org.coconut.pool.Pool#borrow(int)
     */
    public Collection<E> borrow(int ressources) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.pool.Pool#getAvailable()
     */
    public int getAvailable() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.pool.Pool#getSize()
     */
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.pool.Pool#returnToPool(java.lang.Object)
     */
    public void returnToPool(E buffer) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.coconut.pool.Pool#returnToPool(java.util.Collection)
     */
    public void returnToPool(Collection<? super E> buffers) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.coconut.pool.Pool#tryBorrow()
     */
    public E tryBorrow() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.pool.Pool#tryBorrow(long, java.util.concurrent.TimeUnit)
     */
    public E tryBorrow(long timeout, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.pool.Pool#tryBorrow(int)
     */
    public Collection<E> tryBorrow(int amount) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.pool.Pool#tryBorrow(int, long,
     *      java.util.concurrent.TimeUnit)
     */
    public Collection<E> tryBorrow(int amount, long timeout, TimeUnit unit)
            throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }
}

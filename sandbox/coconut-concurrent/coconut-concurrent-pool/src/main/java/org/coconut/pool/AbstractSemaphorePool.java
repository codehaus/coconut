/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.pool;

import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractSemaphorePool<E> implements Pool<E> {
    final Semaphore sem;

    private final int size;

    AbstractSemaphorePool(int size) {
        this.size = size;
        sem = new Semaphore(size);
    }

    protected abstract E get();

    /**
     * @see org.coconut.pool.Pool#getSize()
     */
    public int getSize() {
        return size;
    }

    protected abstract Collection<E> get(int count);

    public Collection<E> borrow(int amount) throws InterruptedException {
        sem.acquire(amount);
        return get(amount);
    }

    public Collection<E> tryBorrow(int amount) {
        if (sem.tryAcquire(amount)) {
            return get(amount);
        }
        return null;
    }

    /**
     * @see org.coconut.pool.Pool#getAvailable()
     */
    public int getAvailable() {
        return sem.availablePermits();
    }

    public E borrow() throws InterruptedException {
        sem.acquire();
        return get();
    }

    public E tryBorrow() {
        if (sem.tryAcquire()) {
            return get();
        }
        return null;
    }

    public Collection<E> tryBorrow(int amount, long timeout, TimeUnit unit)
            throws InterruptedException {
        if (sem.tryAcquire(amount, timeout, unit)) {
            return get(amount);
        }
        return null;
    }

    /**
     * @see org.coconut.pool.Pool#tryBorrow(long, java.util.concurrent.TimeUnit)
     */
    public E tryBorrow(long timeout, TimeUnit unit) throws InterruptedException {
        if (sem.tryAcquire(timeout, unit)) {
            return get();
        }
        return null;
    }

}

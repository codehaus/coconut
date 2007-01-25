/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.pool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultPool<E> implements Pool<E> {
    /** Synchronization control for DefaultPool */

    private volatile int size;

    /**
     * Lock held on updates to poolSize, corePoolSize, maximumPoolSize, and
     * workers set.
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     * Wait condition to support awaitTermination
     */
    private final Condition termination = mainLock.newCondition();

    /**
     * Set containing all worker threads in pool.
     */
    private final HashSet<E> pool = new HashSet<E>();

    /**
     * Core pool size, updated only while holding mainLock, but volatile to
     * allow concurrent readability even during updates.
     */
    private volatile int corePoolSize;

    /**
     * Maximum pool size, updated only while holding mainLock but volatile to
     * allow concurrent readability even during updates.
     */
    private volatile int maximumPoolSize;

    /**
     * Current pool size, updated only while holding mainLock but volatile to
     * allow concurrent readability even during updates.
     */
    private volatile int poolSize;

    public DefaultPool(int corePoolSize, int maximumPoolSize) {
        sem = new Semaphore(maximumPoolSize);
    }

    private final Semaphore sem;

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

    E get() {
        return null;
    }

    Collection<E> get(int amount) {
        ArrayList<E> al = new ArrayList<E>();
        return al;
    }

    void returnPool(E[] items) {

    }

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

    public Collection<E> tryBorrow(int amount, long timeout, TimeUnit unit)
            throws InterruptedException {
        if (sem.tryAcquire(amount, timeout, unit)) {
            return get(amount);
        }
        return null;
    }

    public void returnToPool(E buffer) {

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
     * @see org.coconut.pool.Pool#returnToPool(java.util.Collection)
     */
    public void returnToPool(Collection<? super E> buffers) {
        // TODO Auto-generated method stub
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

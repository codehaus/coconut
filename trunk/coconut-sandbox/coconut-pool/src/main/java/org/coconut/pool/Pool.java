package org.coconut.pool;

import java.util.concurrent.TimeUnit;

/**
 * Bla bla bla
 * 
 * @version $Id$
 */
public interface Pool<T> {
    T borrow() throws InterruptedException;
    T tryBorrow();
    T tryBorrow(long timeout, TimeUnit unit) throws InterruptedException;

    void borrow(T[] pool) throws InterruptedException;
    boolean tryBorrow(T[] pool);
    boolean tryBorrow(T[] pool, long timeout, TimeUnit unit)
            throws InterruptedException;
    
    void returnToPool(T buffer);
    void returnToPool(T... buffer);

}
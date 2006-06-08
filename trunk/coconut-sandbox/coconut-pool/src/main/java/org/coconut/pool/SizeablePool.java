package org.coconut.pool;

import java.util.concurrent.TimeUnit;

/**
 * Bla bla bla
 * 
 * @version $Id$
 */
public interface SizeablePool<T> {
    T borrow(int size) throws InterruptedException;
    T tryBorrow(int size);
    T tryBorrow(int size, long timeout, TimeUnit unit)
            throws InterruptedException;

    void borrow(T[] array, int size) throws InterruptedException;
    boolean tryBorrow(T[] array, int size);
    boolean tryBorrow(T[] array, int size, long timeout, TimeUnit unit)
            throws InterruptedException;
    void returnToPool(T buffer);
    void returnToPool(T... buffer); //TODO replace with T...
}

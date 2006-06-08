package org.coconut.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class DefaultPool<T> implements Pool<T> {
    /** Synchronization control for DefaultPool */
    private final Sync sync=null;

    private final Queue<T> q = new ConcurrentLinkedQueue<T>();

    class Sync extends AbstractQueuedSynchronizer {
        
        void returnOne() {
            
        }
        
        void returnMany(int number) {
            
        }
    }

    public T borrow() throws InterruptedException {
       //sync.
        return null;
    }

    public T tryBorrow() {
        // TODO Auto-generated method stub
        return null;
    }

    public T tryBorrow(long timeout, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    public void borrow(T[] pool) throws InterruptedException {
        // TODO Auto-generated method stub
        
    }

    public boolean tryBorrow(T[] pool) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean tryBorrow(T[] pool, long timeout, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    public void returnToPool(T buffer) {
        // TODO Auto-generated method stub
        
    }

    public void returnToPool(T... buffer) {
        // TODO Auto-generated method stub
        
    }
}

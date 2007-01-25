/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.pool;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class Pools {

    static class CollectionPool<E> implements Pool<E> {

        /**
         * @see org.coconut.pool.Pool#borrow()
         */
        public E borrow() throws InterruptedException {
            // TODO Auto-generated method stub
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
         * @see org.coconut.pool.Pool#tryBorrow(long,
         *      java.util.concurrent.TimeUnit)
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

    public static <E> Pool<E> random(Collection<? extends E> col) {
        return null;
    }
    
    public static <E> Pool<E> roundRobin(Collection<? extends E> col) {
        return null;
    }
    
    public static <E> Pool<E> mru(Collection<? extends E> col) {
        return null;
    }
    
    public static <E> Pool<E> lru(Collection<? extends E> col) {
        return null;
    }

}

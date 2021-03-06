/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class AggregateLock implements Lock {
    private Lock[] locks;

    AggregateLock(Lock[] locks) {
        this.locks = locks;
    }

    /**
     * @see java.util.concurrent.locks.Lock#lock()
     */
    public void lock() {
        int count = 0;
        try {
            while (count < locks.length) {
                locks[count].lock();
                count++;
            }
        } finally {
            if (count != locks.length) {
                // something went wrong, attempt to unlock other locks
                for (int i = count; i > 0; i--) {
                    try {
                        locks[count].unlock();
                    } catch (Throwable ignore) {
                        // not much we can do about this, allready on exception
                        // path
                    }
                }
            }
        }
    }

    /**
     * @see java.util.concurrent.locks.Lock#tryLock()
     */
    public boolean tryLock() {
        int count = 0;
        try {
            while (count < locks.length) {
                if (!locks[count].tryLock()) {
                    break;
                }
                count++;
                // ReentrantLock
            }
        } finally {
            if (count != locks.length) {
                // something went wrong, attempt to unlock other locks
                for (int i = count; i > 0; i--) {
                    try {
                        locks[count].unlock();
                    } catch (Throwable ignore) {
                        // we could handle cases here.
                    }
                }
            }
        }
        return true;
    }

    /**
     * @see java.util.concurrent.locks.Lock#lockInterruptibly()
     */
    public void lockInterruptibly() throws InterruptedException {
        // TODO Auto-generated method stub

    }

    /**
     * @see java.util.concurrent.locks.Lock#tryLock(long,
     *      java.util.concurrent.TimeUnit)
     */
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see java.util.concurrent.locks.Lock#unlock()
     */
    public void unlock() {
        // TODO Auto-generated method stub

    }

    /**
     * @see java.util.concurrent.locks.Lock#newCondition()
     */
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}

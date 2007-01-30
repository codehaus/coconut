/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.memory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ConcurrentCache<T> {

    private final Queue<T> q = new ConcurrentLinkedQueue<T>();

    private ReplacementPolicy<T> policy;

    private final Semaphore mutex = new Semaphore(1);

    // when access -> put cacheEntry, on a concurrent Queue
    // try acquire mutex
    // if yes -> keep emptying the queue and updating the policy
    // if no ignore

    public int add(T t) {
        mutex.acquireUninterruptibly();
        try {
            int index = policy.add(t);
            emptyQueue();
            return index;
        } finally {
            mutex.release();
        }
    }

    public void touch(T t) {
        q.add(t);
        if (mutex.tryAcquire()) {
            try {
                emptyQueue();
            } finally {
                mutex.release();
            }
        }
    }

    private void emptyQueue() {
        T tt;
     //   policy.touch(t.index);
        while ((tt = q.poll()) != null) {
     //       policy.touch(t.index);
        }
    }
}

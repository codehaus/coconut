/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.Attributes;
import org.coconut.cache.service.worker.CacheWorkerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.junit.Test;

@RequireService( { CacheWorkerService.class })
public class WorkerExecutorService extends AbstractCacheTCKTest {

    @Test
    public void executeDefaultExecutor() throws Exception {
        final Semaphore s = new Semaphore(0);
        c = newCache(newConf().worker().setWorkerManager(null));
        ExecutorService ses = worker().getExecutorService("ignore");
        ses.execute(new Runnable() {
            public void run() {
                s.release();
            }
        });
        assertTrue(s.tryAcquire(10, TimeUnit.SECONDS));
        shutdownAndAwaitTermination();
    }

    @Test
    public void executeDefaultExecutorAttributeMap() throws Exception {
        final Semaphore s = new Semaphore(0);
        c = newCache(newConf().worker().setWorkerManager(null));
        ExecutorService ses = worker().getExecutorService("ignore", Attributes.EMPTY_ATTRIBUTE_MAP);
        ses.execute(new Runnable() {
            public void run() {
                s.release();
            }
        });
        assertTrue(s.tryAcquire(10, TimeUnit.SECONDS));
        shutdownAndAwaitTermination();
    }

    @Test
    public void executeDefaultScheduledExecutor() throws Exception {
        final Semaphore s = new Semaphore(0);
        c = newCache(newConf().worker().setWorkerManager(null));
        ScheduledExecutorService ses = worker().getScheduledExecutorService("ignore");
        ses.schedule(new Runnable() {
            public void run() {
                s.release();
            }
        }, 1, TimeUnit.MICROSECONDS);
        assertTrue(s.tryAcquire(10, TimeUnit.SECONDS));
        shutdownAndAwaitTermination();
    }

    @Test
    public void executeDefaultScheduledExecutorAttributeMap() throws Exception {
        final Semaphore s = new Semaphore(0);
        c = newCache(newConf().worker().setWorkerManager(null));
        ScheduledExecutorService ses = worker().getScheduledExecutorService("ignore", Attributes.EMPTY_ATTRIBUTE_MAP);
        ses.schedule(new Runnable() {
            public void run() {
                s.release();
            }
        }, 1, TimeUnit.MICROSECONDS);
        assertTrue(s.tryAcquire(10, TimeUnit.SECONDS));
        shutdownAndAwaitTermination();
    }
}

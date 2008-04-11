/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.codehaus.cake.test.tck.service.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.codehaus.cake.attribute.Attributes;
import org.codehaus.cake.service.executor.ExecutorsService;
import org.codehaus.cake.test.tck.RequireService;
import org.junit.Test;

@RequireService( { ExecutorsService.class })
public class WorkerExecutorService extends AbstractWorkerTckTest {

    @Test
    public void executeDefaultExecutor() throws Exception {
        final Semaphore s = new Semaphore(0);
        newConfigurationClean();
        c = newContainer();
        ExecutorService ses = withExecutors().getExecutorService("ignore");
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
        conf = newConfigurationClean();
        c = newContainer();

        ExecutorService ses = withExecutors().getExecutorService("ignore",
                Attributes.EMPTY_ATTRIBUTE_MAP);
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
        conf = newConfigurationClean();
        c = newContainer();

        ScheduledExecutorService ses = withExecutors().getScheduledExecutorService("ignore");
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
        conf = newConfigurationClean();
        c = newContainer();

        ScheduledExecutorService ses = withExecutors().getScheduledExecutorService("ignore",
                Attributes.EMPTY_ATTRIBUTE_MAP);
        ses.schedule(new Runnable() {
            public void run() {
                s.release();
            }
        }, 1, TimeUnit.MICROSECONDS);
        assertTrue(s.tryAcquire(10, TimeUnit.SECONDS));
        shutdownAndAwaitTermination();
    }
}

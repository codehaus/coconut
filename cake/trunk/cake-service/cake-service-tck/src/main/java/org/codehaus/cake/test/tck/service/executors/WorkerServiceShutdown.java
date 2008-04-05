/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.codehaus.cake.test.tck.service.executors;

import java.security.Permission;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.cake.service.executor.ExecutorsService;
import org.codehaus.cake.test.tck.RequireService;
import org.junit.Ignore;
import org.junit.Test;

@RequireService( { ExecutorsService.class })
public class WorkerServiceShutdown extends AbstractWorkerTckTest {

    @Ignore @Test
    public void tt() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        newConfigurationClean();
        newContainer();

        ScheduledExecutorService ses = withExecutors().getScheduledExecutorService("ignore");
        ses.execute(new Runnable() {
            public void run() {
                for (;;) {
                    try {
                        new CountDownLatch(1).await(Long.MAX_VALUE, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        latch.countDown();
                        return;
                    }
                }
            }
        });
        c.shutdown();
        assertEquals(1, latch.getCount());
        c.shutdownNow();
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    /**
     * Tests that newly created threads with the default workerservice and a security manager set.
     * Creates threads in the threadgroup of the security manager
     */
    @Test
    public void securityGroupScheduledExecutor() {
        final ThreadGroup tg = new ThreadGroup("myGroup");
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {}

            @Override
            public ThreadGroup getThreadGroup() {
                return tg;
            }
        });
        try {
            newConfigurationClean();
            newContainer();

            // manager
            withExecutors().getScheduledExecutorService("ignore").execute(new Runnable() {
                public void run() {
                    if (tg != Thread.currentThread().getThreadGroup()) {
                        doFail("wrong threadgroup");
                    }
                }
            });
        } finally {
            System.setSecurityManager(null);
        }
        shutdownAndAwaitTermination();
    }

    /**
     * Tests that newly created threads with the default workerservice and a security manager set.
     * Creates threads in the threadgroup of the security manager
     * 
     * @throws InterruptedException
     */
    @Test
    public void threadDeamonPriority() throws InterruptedException {
        newConfigurationClean();
        newContainer();

        // manager
        Thread t = new Thread(new Runnable() {
            public void run() {
                withExecutors().getScheduledExecutorService("ignore").execute(new Runnable() {
                    public void run() {
                        if (Thread.currentThread().isDaemon()) {
                            doFail("should not be deamon");
                        }
                        if (Thread.currentThread().getPriority() != Thread.NORM_PRIORITY) {
                            doFail("wrong priority");
                        }
                    }
                });
            }
        });
        t.setDaemon(true);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
        t.join();
        shutdownAndAwaitTermination();
    }
}

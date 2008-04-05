/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.codehaus.cake.test.tck.service.executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.codehaus.cake.service.executor.ExecutorsService;
import org.codehaus.cake.test.tck.RequireService;
import org.junit.Test;

@RequireService( { ExecutorsService.class })
public class WorkerScheduling extends AbstractWorkerTckTest {

    @Test
    public void tt() throws Exception {
        final Semaphore s = new Semaphore(0);
        newConfigurationClean();
        newContainer();
        ScheduledExecutorService ses = withExecutors().getScheduledExecutorService("ignore");
        ses.execute(new Runnable() {
            public void run() {
                // System.out.println("release");
                s.release();
            }
        });
        assertTrue(s.tryAcquire(10, TimeUnit.SECONDS));
        // System.out.println("acuired");
        shutdownAndAwaitTermination();
    }
}

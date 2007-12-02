package org.coconut.cache.tck.service.worker;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.worker.CacheWorkerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.junit.Test;

@RequireService( { CacheWorkerService.class })
public class WorkerScheduling extends AbstractCacheTCKTest {

    @Test
    public void tt() throws Exception {
        final Semaphore s = new Semaphore(0);
        c = newCache(newConf().worker().setWorkerManager(null));
        ScheduledExecutorService ses = worker().getScheduledExecutorService("ignore");
        ses.execute(new Runnable() {
            public void run() {
                //System.out.println("release");
                s.release();
            }
        });
        assertTrue(s.tryAcquire(10, TimeUnit.SECONDS));
        //System.out.println("acuired");
        shutdownAndAwaitTermination();
    }
}

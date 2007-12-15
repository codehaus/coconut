package org.coconut.cache.tck.service.servicemanager;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleVerifier;
import org.junit.Test;

@RequireService(value = { ThreadSafe.class })
public class LifecycleAsynchronousShutdown extends AbstractCacheTCKTest {

    @Test
    public void dummy() throws Exception {
        final Asyn asyn = new Asyn();
        AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier() {
            @Override
            public void shutdown(Shutdown shutdown) {
                super.shutdown(shutdown);
                shutdown.shutdownAsynchronously(asyn);
            }
        };
        setCache(newConf().serviceManager().add(alv));

        prestart();
        c.shutdown();
        alv.assertShutdownPhase();
        assertTrue(c.isShutdown());
        assertFalse(c.isTerminated());
        assertFalse(c.awaitTermination(50, TimeUnit.MILLISECONDS));

        assertFalse(asyn.isShutdownNow);
        c.shutdownNow();
        asyn.latch.await();
        assertTrue(asyn.isShutdownNow);
        assertTrue(c.awaitTermination(5, TimeUnit.SECONDS));
        alv.assertTerminatedPhase();
    }

    static class Asyn implements Callable {
        private volatile boolean isShutdownNow;

        private final CountDownLatch latch = new CountDownLatch(1);

        public boolean isShutdownNow() {
            return isShutdownNow;
        }

        public void release() {
            latch.countDown();
        }

        public Object call() throws Exception {
            for (;;) {
                try {
                    latch.await(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    isShutdownNow = true;
                    latch.countDown();
                    return null;
                }
            }
        }
    }
}

package org.coconut.cache.tck.service.exceptionhandling;

import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class ExceptionHandlingLifecycle extends AbstractCacheTCKTest {
    private final AtomicInteger status = new AtomicInteger();

    private CacheConfiguration conf;

    @Test
    public void lifecycle() {
        conf = newConf().exceptionHandling().setExceptionHandler(new MyExceptionHandler()).c();
        c = newCache(conf);
        assertEquals(
                "ExceptionHandler.initialize should be called from the constructor of the cache",
                1, status.get());
        assertFalse(c.isStarted());
        c.put(1, "dd");
        assertEquals(1, status.get());
        shutdownAndAwait();
        assertEquals(
                "ExceptionHandler.initialize should be called after the cache has been shutdown and terminated",
                9, status.get());
    }

    @Test
    public void lifecycleAndService() {
        conf = newConf().exceptionHandling().setExceptionHandler(new MyExceptionHandler()).c()
                .serviceManager().add(new MyService()).c();
        c = newCache(conf);
        c.put(1, "dd");
        assertEquals(3, status.get());
        shutdownAndAwait();
        assertEquals(15, status.get());
    }

    class MyExceptionHandler extends CacheExceptionHandlers.DefaultLoggingExceptionHandler {
        @Override
        public void initialize(CacheConfiguration configuration) {
            assertEquals(conf, configuration);
            super.initialize(configuration);
            status.addAndGet(1);
        }

        @Override
        public void terminated() {
            super.terminated();
            status.addAndGet(8);
        }
    }

    class MyService extends AbstractCacheLifecycle {
        @Override
        public void initialize(CacheConfiguration configuration) {
            super.initialize(configuration);
            if (status.get() == 1) {
                status.addAndGet(2);
            }
        }

        @Override
        public void terminated() {
            super.terminated();
            if (status.get() == 3) {
                status.addAndGet(4);
            }
        }
    }
}

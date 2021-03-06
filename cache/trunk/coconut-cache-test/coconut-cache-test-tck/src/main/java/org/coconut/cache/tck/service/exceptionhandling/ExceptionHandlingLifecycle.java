/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.exceptionhandling;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class ExceptionHandlingLifecycle extends AbstractCacheTCKTest {
    private final AtomicInteger status = new AtomicInteger();

    private CacheConfiguration conf;

    @Test(expected = IllegalMonitorStateException.class)
    public void lifecycleIMSE() throws Throwable {
        conf = newConf().exceptionHandling().setExceptionHandler(new ErroneousExceptionHandler())
                .c();
        try {
            c = newCache(conf);
        } catch (IllegalArgumentException iae) {
            throw iae.getCause();
        }
    }

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
        shutdownAndAwaitTermination();
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
        shutdownAndAwaitTermination();
        assertEquals(15, status.get());
    }

    class ErroneousExceptionHandler extends CacheExceptionHandler{
        @Override
        public void initialize(CacheConfiguration configuration) {
            throw new IllegalMonitorStateException();
        }
    }

    class MyExceptionHandler extends CacheExceptionHandler {
        @Override
        public void initialize(CacheConfiguration configuration) {
            assertEquals(conf, configuration);
            super.initialize(configuration);
            status.addAndGet(1);
        }

        @Override
        public void terminated(Map terminationFailures) {
            super.terminated(terminationFailures);
            status.addAndGet(8);
        }
    }

    class MyService extends AbstractCacheLifecycle {
        @Override
        public void initialize(CacheLifecycle.Initializer cli) {
            super.initialize(cli);
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

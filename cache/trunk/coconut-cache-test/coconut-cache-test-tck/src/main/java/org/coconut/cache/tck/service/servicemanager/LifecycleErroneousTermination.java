package org.coconut.cache.tck.service.servicemanager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleVerifier;
import org.coconut.test.throwables.RuntimeException1;
import org.coconut.test.throwables.RuntimeException2;
import org.coconut.test.throwables.RuntimeException3;
import org.junit.Before;
import org.junit.Test;

public class LifecycleErroneousTermination extends AbstractCacheTCKTest {

    CacheConfiguration conf;

    TerminationExceptionHandler handler;

    @Before
    public void setup() {
        conf = newConf();
        handler = new TerminationExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(handler);
    }

    /**
     * Tests a single service that fails in the {@link CacheLifecycle#terminated()} phase.
     */
    @Test
    public void terminationErroneous() {
        final AtomicInteger verifier = new AtomicInteger();
        AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier() {
            @Override
            public void terminated() {
                assertEquals(0, verifier.getAndIncrement());
                throw new RuntimeException1();
            }
        };
        conf.serviceManager().add(alv);
        setCache(conf);
        prestart();
        shutdownAndAwaitTermination();
        assertEquals(1, handler.terminatationMap.size());
        assertTrue(handler.terminatationMap.get(alv) instanceof RuntimeException1);
        assertEquals(1, verifier.get());
    }

    /**
     * Tests a single service that fails in the {@link CacheLifecycle#terminated()} phase.
     */
    @Test
    public void terminationMultipleErroneous() {
        final AtomicInteger verifier = new AtomicInteger();
        AbstractLifecycleVerifier alv1 = new AbstractLifecycleVerifier() {
            @Override
            public void terminated() {
                assertEquals(3, verifier.getAndIncrement());
                throw new RuntimeException3();
            }
        };
        AbstractLifecycleVerifier alv2 = new AbstractLifecycleVerifier() {
            @Override
            public void terminated() {
                assertEquals(2, verifier.getAndIncrement());
            }
        };
        AbstractLifecycleVerifier alv3 = new AbstractLifecycleVerifier() {
            @Override
            public void terminated() {
                assertEquals(1, verifier.getAndIncrement());
                throw new RuntimeException2();
            }
        };
        AbstractLifecycleVerifier alv4 = new AbstractLifecycleVerifier() {
            @Override
            public void terminated() {
                assertEquals(0, verifier.getAndIncrement());
                throw new RuntimeException1();
            }
        };
        conf.serviceManager().add(alv1).add(alv2).add(alv3).add(alv4);
        setCache(conf);
        prestart();
        shutdownAndAwaitTermination();
        assertEquals(3, handler.terminatationMap.size());
        assertTrue(handler.terminatationMap.get(alv4) instanceof RuntimeException1);
        assertTrue(handler.terminatationMap.get(alv3) instanceof RuntimeException2);
        assertTrue(handler.terminatationMap.get(alv1) instanceof RuntimeException3);
        assertEquals(4, verifier.get());
    }

    class TerminationExceptionHandler extends CacheExceptionHandlers.DefaultLoggingExceptionHandler {

        Map terminatationMap;

        @Override
        public void terminated(Map terminationFailures) {
            assertNotNull(terminationFailures);
            this.terminatationMap = terminationFailures;
        }
    }
}

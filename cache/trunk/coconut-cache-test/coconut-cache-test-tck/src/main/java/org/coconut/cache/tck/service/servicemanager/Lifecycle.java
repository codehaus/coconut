/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleVerifier;
import org.coconut.core.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Lifecycle extends AbstractCacheTCKTest {

    CacheConfiguration conf;

    ExceptionHandler handler;

    @Before
    public void setup() {
        conf = newConf();
        handler = new ExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(handler);
    }

    @After
    public void after() {
        assertSame(conf, handler.initializeConf);
    }

    /**
     * Tests the lifecycle on a simple service.
     * 
     * @throws InterruptedException
     */
    @Test
    public void simpleLifecycle() throws InterruptedException {
        AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier();
        conf.serviceManager().add(alv).c();
        alv.setConfigurationToVerify(conf);
        setCache(conf);
        alv.assertInitializedButNotStarted();
        prestart();
        alv.assertInStartedPhase();
        c.shutdown();
        alv.assertShutdownOrTerminatedPhase();
        assertTrue(c.awaitTermination(10, TimeUnit.SECONDS));
        alv.assertTerminatedPhase();
    }

    /**
     * Tests the terminated is called on a cache that was never started only initialized
     * 
     * @throws InterruptedException
     */
    @Test
    public void simpleLifecycleNoStart() throws InterruptedException {
        AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier();
        conf.serviceManager().add(alv).c();
        alv.setConfigurationToVerify(conf);
        setCache(conf);
        alv.assertInitializedButNotStarted();
        alv.setNextStep(AbstractLifecycleVerifier.Step.TERMINATED);
        c.shutdown();
        alv.assertShutdownOrTerminatedPhase();
        assertTrue(c.awaitTermination(10, TimeUnit.SECONDS));
        alv.assertTerminatedPhase();
    }

    /**
     * Tests that the services are initialized in the order they where registered.
     */
    @Test
    public void serviceOrder() throws Exception {
        final int count = 10;
        final AtomicInteger verifier = new AtomicInteger();
        AbstractLifecycleVerifier[] alvs = new AbstractLifecycleVerifier[count];
        for (int i = 0; i < 10; i++) {
            final int j = i;
            alvs[i] = new AbstractLifecycleVerifier() {
                @Override
                public void initialize(Initializer cli) {
                    assertEquals(j, verifier.getAndIncrement());
                    super.initialize(cli);
                }

                @Override
                public void start(CacheServiceManagerService serviceManager) {
                    assertEquals(j + count, verifier.getAndIncrement());
                    super.start(serviceManager);
                }

                @Override
                public void shutdown(Shutdown shutdown) {
                    assertEquals((count - j - 1) + count * 3, verifier.getAndIncrement());
                    super.shutdown(shutdown);
                }

                @Override
                public void started(Cache<?, ?> cache) {
                    assertEquals(j + count * 2, verifier.getAndIncrement());
                    super.started(cache);
                }

                @Override
                public void terminated() {
                    assertEquals((count - j - 1) + count * 4, verifier.getAndIncrement());
                    super.terminated();
                }
            };
            conf.serviceManager().add(alvs[i]);
            alvs[i].setConfigurationToVerify(conf);
        }
        setCache(conf);
        for (int i = 0; i < 10; i++) {
            alvs[i].assertInitializedButNotStarted();
        }
        prestart();
        for (int i = 0; i < 10; i++) {
            alvs[i].assertInStartedPhase();
        }
        c.shutdown();
        for (int i = 0; i < 10; i++) {
            alvs[i].assertShutdownOrTerminatedPhase();
        }
        assertTrue(c.awaitTermination(10, TimeUnit.SECONDS));
        for (int i = 0; i < 10; i++) {
            alvs[i].assertTerminatedPhase();
        }
        assertEquals(count * 5, verifier.get());
    }

    static class ExceptionHandler extends CacheExceptionHandlers.DefaultLoggingExceptionHandler {
        CacheConfiguration initializeConf;

        @Override
        public void serviceManagerStartFailed(CacheExceptionContext context, CacheConfiguration conf,
                Object service) {
            throw new AssertionError("should not be called");
        }

        @Override
        public void serviceManagerInitializationFailed(Logger logger, CacheConfiguration configuration,
                String name, Class cacheType, CacheLifecycle service, RuntimeException cause) {
            throw new AssertionError("should not be called");
        }

        @Override
        public void initialize(CacheConfiguration configuration) {
            this.initializeConf = configuration;
        }

        @Override
        public void terminated(Map terminationFailures) {
            assertNotNull(terminationFailures);
            assertEquals(0, terminationFailures.size());
        }

        @Override
        public void serviceManagerShutdownFailed(CacheExceptionContext cache, CacheLifecycle lifecycle) {
            throw new AssertionError("should not be called");
        }
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleVerifier;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleVerifier.Step;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

public class LifecycleErroneousStart extends AbstractCacheTCKTest {

    CacheConfiguration conf;

    StartExceptionHandler handler;

    @Before
    public void setup() {
        conf = newConf();
        handler = new StartExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(handler);
    }

    /**
     * Tests throwing an {@link RuntimeException} from within
     * {@link CacheLifecycle#initialize(CacheLifecycleInitializer)}. Makes sure
     * {@link CacheExceptionHandler#cacheInitializationFailed(CacheConfiguration, Class, CacheLifecycle, RuntimeException)}
     * is called.
     */
    @Test
    public void exceptionInStart() {
        final AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                super.start(serviceManager);
                setNextStep(Step.TERMINATED);
                throw new IllegalMonitorStateException();
            }
        };
        conf.serviceManager().add(alv).c();
        alv.setConfigurationToVerify(conf);
        setCache(conf);
        try {
            prestart();
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } catch (CacheException e) {
            assertTrue(e.getCause() instanceof IllegalMonitorStateException);
        }
        alv.assertTerminatedPhase();
        assertEquals(0, handler.terminatationMap.size());
    }

    /**
     * Tests that {@link CacheLifecycle#terminated()} is called on components that have
     * already been initialized. When other components fails to initialize.
     */
    @Test
    public void exceptionInStart3() {
        final AtomicInteger verifier = new AtomicInteger();
        AbstractCacheLifecycle alv1 = new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                assertEquals(0, verifier.getAndIncrement());
                super.start(serviceManager);
            }

            @Override
            public void shutdown() {
                assertEquals(2, verifier.getAndIncrement());
            }

            @Override
            public void terminated() {
                assertEquals(5, verifier.getAndIncrement());
            }

        };
        AbstractLifecycleVerifier alv2 = new AbstractLifecycleVerifier() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                assertEquals(1, verifier.getAndIncrement());
                super.start(serviceManager);
                throw new IllegalMonitorStateException();
            }

            @Override
            public void terminated() {
                assertEquals(4, verifier.getAndIncrement());
            }
        };
        AbstractLifecycleVerifier alv3 = new AbstractLifecycleVerifier() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                throw new AssertionError("Should not have been started");
            }

            @Override
            public void terminated() {
                assertEquals(3, verifier.getAndIncrement());
            }
        };
        conf.serviceManager().add(alv1).add(alv2).add(alv3).c();
        setCache(conf);
        try {
            prestart();
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } catch (CacheException e) {
            assertTrue(e.getCause() instanceof IllegalMonitorStateException);
        }
        assertSame(alv2, handler.service);
        assertTrue(handler.cause instanceof IllegalMonitorStateException);
        assertEquals(0, handler.terminatationMap.size());
        assertEquals(6, verifier.get());
    }

    /**
     * Tests that {@link CacheLifecycle#terminated()} is called on components that have
     * already been initialized. When other components fails to initialize.
     */
    @Test
    public void exceptionInStartAndInitialize() {
        final AtomicInteger verifier = new AtomicInteger();
        AbstractCacheLifecycle alv1 = new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                assertEquals(0, verifier.getAndIncrement());
                super.start(serviceManager);
            }

            @Override
            public void shutdown() {
                assertEquals(2, verifier.getAndIncrement());
                throw new ArithmeticException();
            }

            @Override
            public void terminated() {
                assertEquals(5, verifier.getAndIncrement());
                throw new ArrayIndexOutOfBoundsException();
            }

        };
        AbstractLifecycleVerifier alv2 = new AbstractLifecycleVerifier() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                assertEquals(1, verifier.getAndIncrement());
                super.start(serviceManager);
                throw new IllegalMonitorStateException();
            }

            @Override
            public void terminated() {
                assertEquals(4, verifier.getAndIncrement());
                throw new ArrayIndexOutOfBoundsException();
            }
        };
        AbstractLifecycleVerifier alv3 = new AbstractLifecycleVerifier() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                throw new AssertionError("Should not have been started");
            }

            @Override
            public void terminated() {
                assertEquals(3, verifier.getAndIncrement());
            }
        };
        conf.serviceManager().add(alv1).add(alv2).add(alv3).c();
        setCache(conf);
        try {
            prestart();
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } catch (CacheException e) {
            assertTrue(e.getCause() instanceof IllegalMonitorStateException);
        }
        assertSame(alv2, handler.service);
        assertTrue(handler.cause instanceof IllegalMonitorStateException);
        assertEquals(1, handler.shutdownMap.size());
        assertTrue(handler.shutdownMap.get(alv1) instanceof ArithmeticException);
        assertSame(c, handler.shutdownCache);

        assertEquals(2, handler.terminatationMap.size());
        assertTrue(handler.terminatationMap.get(alv1) instanceof ArrayIndexOutOfBoundsException);
        assertTrue(handler.terminatationMap.get(alv2) instanceof ArrayIndexOutOfBoundsException);
        assertEquals(6, verifier.get());
    }

    @Test
    public void cannotCallshutdownServiceAsynchronously() {
        setCache(newConf().serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                if (getCacheType().getAnnotation(ThreadSafe.class) != null) {
                    try {
                        serviceManager.shutdownServiceAsynchronously(MockTestCase
                                .mockDummy(AsynchronousShutdownObject.class));
                        throw new AssertionError(
                                "serviceManager should throw IllegalStateException");
                    } catch (IllegalStateException ok) {/* ok */}
                } else {
                    try {
                        serviceManager.shutdownServiceAsynchronously(MockTestCase
                                .mockDummy(AsynchronousShutdownObject.class));
                        throw new AssertionError(
                                "serviceManager should throw UnsupportedOperationException");
                    } catch (UnsupportedOperationException ok) {/* ok */}
                }
            }
        }));
        prestart();
    }

    class StartExceptionHandler extends CacheExceptionHandlers.DefaultLoggingExceptionHandler {
        CacheConfiguration conf;

        Map terminatationMap;

        RuntimeException cause;

        CacheLifecycle service;

        Cache shutdownCache;

        Map shutdownMap;

        @Override
        public void cacheStartFailed(CacheConfiguration configuration, Class cacheType,
                CacheLifecycle service, RuntimeException cause) {
            this.cause = cause;
            this.service = service;
            this.conf = configuration;
            assertEquals(getCacheType(), cacheType);
            super.cacheStartFailed(configuration, cacheType, service, cause);
        }

        @Override
        public void cacheShutdownFailed(Cache cache, Map terminationFailures) {
            this.shutdownCache = cache;
            this.shutdownMap = terminationFailures;
            super.cacheShutdownFailed(cache, terminationFailures);
        }

        @Override
        public void terminated(Map terminationFailures) {
            assertNotNull(terminationFailures);
            this.terminatationMap = terminationFailures;
        }
    }

}

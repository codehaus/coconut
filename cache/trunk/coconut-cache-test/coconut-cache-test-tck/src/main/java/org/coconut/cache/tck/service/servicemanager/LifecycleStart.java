/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.jcip.annotations.ThreadSafe;

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
import org.coconut.cache.tck.service.servicemanager.LifecycleInitialize.InitializingExceptionHandler;
import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

public class LifecycleStart extends AbstractCacheTCKTest {

    CacheConfiguration conf;

    InitializingExceptionHandler handler;

    @Before
    public void setup() {
        conf = newConf();
        handler = new InitializingExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(handler);
    }

    /**
     * Tests that {@link CacheLifecycle#start(CacheServiceManagerService)} is called on a
     * simple service.
     */
    @Test
    public void startCalled() {
        AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                super.start(serviceManager);
            }
        };
        conf.serviceManager().add(alv).c();
        alv.setConfigurationToVerify(conf);
        setCache(conf);
        prestart();
        shutdownAndAwait();
        assertEquals(0, handler.terminatationMap.size());
    }

    /**
     * Tests that the services are started in the order they where registered.
     */
    @Test
    public void startOrder() {
        final AtomicInteger verifier = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            final int j = i;
            conf.serviceManager().add(new AbstractCacheLifecycle() {
                @Override
                public void start(CacheServiceManagerService serviceManager) {
                    assertEquals(j, verifier.getAndIncrement());
                    super.start(serviceManager);
                }
            });
        }
        setCache(conf);
        prestart();
        assertEquals(10, verifier.get());
        shutdownAndAwait();
        assertEquals(0, handler.terminatationMap.size());
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
                throw new AssertionError("Should not have been initialized");
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
//        assertEquals(alv2, handler.service);
//        assertTrue(handler.cause instanceof IllegalMonitorStateException);
//        assertEquals(0, handler.terminatationMap.size());
//        assertEquals(3, verifier.get());
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


    class InitializingExceptionHandler extends
            CacheExceptionHandlers.DefaultLoggingExceptionHandler {
        CacheConfiguration conf;

        Map terminatationMap;

        RuntimeException cause;

        CacheLifecycle service;

        @Override
        public void cacheInitializationFailed(CacheConfiguration configuration, Class cacheType,
                CacheLifecycle service, RuntimeException cause) {
            this.cause = cause;
            this.service = service;
            assertEquals(conf, configuration);
            assertEquals(getCacheType(), cacheType);
            super.cacheInitializationFailed(configuration, cacheType, service, cause);
        }

        @Override
        public void initialize(CacheConfiguration configuration) {
            this.conf = configuration;
        }

        @Override
        public void terminated(Map terminationFailures) {
            assertNotNull(terminationFailures);
            this.terminatationMap = terminationFailures;
        }

    }
}

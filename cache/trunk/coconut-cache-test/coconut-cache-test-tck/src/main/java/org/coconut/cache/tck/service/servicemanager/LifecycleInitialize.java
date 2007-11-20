/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests initialization of cache services.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LifecycleInitialize extends AbstractCacheTCKTest {

    CacheConfiguration conf;

    InitializingExceptionHandler handler;

    @Before
    public void setup() {
        conf = newConf();
        handler = new InitializingExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(handler);
    }

    @After
    public void after() {
        assertSame(conf, handler.conf);
    }

    /**
     * Tests that {@link CacheLifecycle#initialize(CacheLifecycleInitializer)} is called
     * on a simple service.
     */
    @Test
    public void initializeCalled() {
        AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                super.initialize(cli);
                assertEquals(getCacheType(), cli.getCacheType());
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
     * Tests that the services are initialized in the order they where registered.
     */
    @Test
    public void initializeOrder() {
        final AtomicInteger verifier = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            final int j = i;
            conf.serviceManager().add(new AbstractCacheLifecycle() {
                @Override
                public void initialize(CacheLifecycleInitializer cli) {
                    assertEquals(j, verifier.getAndIncrement());
                }
            });
        }
        newCache(conf);
        assertEquals(10, verifier.get());
    }

    /**
     * Tests throwing an {@link RuntimeException} from within
     * {@link CacheLifecycle#initialize(CacheLifecycleInitializer)}. Makes sure
     * {@link CacheExceptionHandler#cacheInitializationFailed(CacheConfiguration, Class, CacheLifecycle, RuntimeException)}
     * is called.
     */
    @Test
    public void exceptionInInitialize() {
        AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                super.initialize(cli);
                assertEquals(getCacheType(), cli.getCacheType());
                throw new IllegalMonitorStateException();
            }
        };
        conf.serviceManager().add(alv).c();
        alv.setConfigurationToVerify(conf);
        try {
            setCache(conf);
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } catch (IllegalMonitorStateException e) {/* ok */}
        assertEquals(alv, handler.service);
        assertTrue(handler.cause instanceof IllegalMonitorStateException);
        assertEquals(0, handler.terminatationMap.size());
    }

    /**
     * Tests that we do not try to handle an {@link Error} when thrown in
     * {@link CacheLifecycle#initialize(CacheLifecycleInitializer)}.
     */
    @Test
    public void errorInInitialize() {
        AbstractLifecycleVerifier alv = new AbstractLifecycleVerifier() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                super.initialize(cli);
                assertEquals(getCacheType(), cli.getCacheType());
                throw new IncompatibleClassChangeError();
            }
        };
        conf.serviceManager().add(alv).c();
        alv.setConfigurationToVerify(conf);
        try {
            setCache(conf);
            throw new AssertionError("Should have failed with IncompatibleClassChangeError");
        } catch (IncompatibleClassChangeError e) {/* ok */}
        assertNull(handler.terminatationMap); // terminated should not have been called
    }

    /**
     * Tests that {@link CacheLifecycle#terminated()} is called on components that have
     * already been initialized. When other components fails to initialize.
     */
    @Test
    public void exceptionInInitialize3() {
        final AtomicInteger verifier = new AtomicInteger();
        AbstractCacheLifecycle alv1 = new AbstractCacheLifecycle() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                assertEquals(0, verifier.getAndIncrement());
                super.initialize(cli);
            }

            @Override
            public void terminated() {
                assertEquals(2, verifier.getAndIncrement());
            }
        };
        AbstractLifecycleVerifier alv2 = new AbstractLifecycleVerifier() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                assertEquals(1, verifier.getAndIncrement());
                super.initialize(cli);
                throw new IllegalMonitorStateException();
            }
        };
        AbstractLifecycleVerifier alv3 = new AbstractLifecycleVerifier() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                throw new AssertionError("Should not have been initialized");
            }
        };
        conf.serviceManager().add(alv1).add(alv2).add(alv3).c();
        alv2.setConfigurationToVerify(conf);
        try {
            setCache(conf);
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } catch (IllegalMonitorStateException e) {/* ok */}
        assertEquals(alv2, handler.service);
        assertTrue(handler.cause instanceof IllegalMonitorStateException);
        assertEquals(0, handler.terminatationMap.size());
        assertEquals(3, verifier.get());
    }

    /**
     * Tests components that fail both in
     * {@link CacheLifecycle#initialize(CacheLifecycleInitializer)} and
     * {@link CacheLifecycle#terminated()}.
     */
    @Test
    public void exceptionInInitializeAndTerminate() {
        final AtomicInteger verifier = new AtomicInteger();
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                assertEquals(0, verifier.getAndIncrement());
                super.initialize(cli);
            }

            @Override
            public void terminated() {
                assertEquals(6, verifier.getAndIncrement());
            }
        });
        AbstractCacheLifecycle alv1 = new AbstractCacheLifecycle() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                assertEquals(1, verifier.getAndIncrement());
                super.initialize(cli);
            }

            @Override
            public void terminated() {
                assertEquals(5, verifier.getAndIncrement());
                throw new IndexOutOfBoundsException();
            }
        };

        AbstractCacheLifecycle alv2 = new AbstractCacheLifecycle() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                assertEquals(2, verifier.getAndIncrement());
                super.initialize(cli);
            }

            @Override
            public void terminated() {
                assertEquals(4, verifier.getAndIncrement());
                throw new ArithmeticException();
            }
        };
        conf.serviceManager().add(alv1).add(alv2).add(new AbstractLifecycleVerifier() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                assertEquals(3, verifier.getAndIncrement());
                super.initialize(cli);
                throw new IllegalMonitorStateException();
            }
        });
        conf.serviceManager().add(new AbstractLifecycleVerifier() {
            @Override
            public void initialize(CacheLifecycleInitializer cli) {
                throw new AssertionError("Should not have been initialized");
            }
        });
        try {
            setCache(conf);
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } catch (IllegalMonitorStateException e) {/* ok */}
        assertTrue(handler.cause instanceof IllegalMonitorStateException);
        assertEquals(2, handler.terminatationMap.size());
        assertTrue(handler.terminatationMap.get(alv1) instanceof IndexOutOfBoundsException);
        assertTrue(handler.terminatationMap.get(alv2) instanceof ArithmeticException);
        assertEquals(7, verifier.get());
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

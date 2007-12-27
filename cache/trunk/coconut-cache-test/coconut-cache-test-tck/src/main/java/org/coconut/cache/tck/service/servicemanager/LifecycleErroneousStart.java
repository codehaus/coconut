/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.servicemanager.CacheLifecycle.Initializer;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleVerifier;
import org.coconut.cache.test.util.lifecycle.LifecycleVerifier;
import org.coconut.cache.test.util.lifecycle.LifecycleVerifierContext;
import org.coconut.core.Loggers;
import org.coconut.test.throwables.Error1;
import org.coconut.test.throwables.RuntimeException1;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LifecycleErroneousStart extends AbstractCacheTCKTest {

    CacheConfiguration conf;

    StartExceptionHandler handler;

    LifecycleVerifierContext context;

    @After
    public void after() {
        context.verify();
    }

    @Before
    public void setup() {
        conf = newConf();
        context = new LifecycleVerifierContext(conf);

        handler = new StartExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(handler);
    }

    /**
     * Tests throwing an {@link RuntimeException} from within
     * {@link CacheLifecycle#initialize(Initializer)}. Makes sure
     * {@link CacheExceptionHandler#lifecycleInitializationFailed(CacheConfiguration, Class, CacheLifecycle, RuntimeException)}
     * is called.
     */
    @Test(expected = RuntimeException1.class)
    public void exceptionInStart() {
        LifecycleVerifier lv = context.create(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                throw new RuntimeException1();
            }
        });
        init(conf);
        try {
            prestart();
        } finally {
            assertEquals(0, handler.terminatationMap.size());
            assertTrue(lv.isInTerminatationStatus());// terminated called
        }
    }

    /**
     * Tests throwing an {@link RuntimeException} from within
     * {@link CacheLifecycle#initialize(Initializer)}. Makes sure
     * {@link CacheExceptionHandler#lifecycleInitializationFailed(CacheConfiguration, Class, CacheLifecycle, RuntimeException)}
     * is called.
     */
    @Test(expected = Error1.class)
    public void errorInStart() {
        LifecycleVerifier lv = context.create(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                throw new Error1();
            }
        });
        init(conf);
        try {
            prestart();
        } catch (CacheException e) {
            assertTrue(e.getCause() instanceof RuntimeException1);
            throw e;
        } finally {
            assertNull(handler.context);// start service failed not called
            assertNull(handler.terminatationMap);
            assertFalse(lv.isInTerminatationStatus());// terminated not called
        }
    }

    /**
     * Tests that {@link CacheLifecycle#terminated()} is called on components that have
     * already been initialized. When other components fails to initialize.
     */
    @Test(expected = RuntimeException1.class)
    public void exceptionInStart3() {
        context.create();
        LifecycleVerifier lv = context.create(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                throw new RuntimeException1();
            }
        });
        context.create();
        init(conf);
        try {
            prestart();
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } finally {
            assertEquals(0, handler.terminatationMap.size());
        }
    }

    /**
     * Tests that {@link CacheLifecycle#terminated()} is called on components that have
     * already been initialized. When other components fails to initialize.
     */
    @Test
    public void exceptionInStartAndTerminate() {
        final AtomicInteger verifier = new AtomicInteger();
        AbstractCacheLifecycle alv1 = new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) throws Exception {
                assertEquals(0, verifier.getAndIncrement());
                super.start(serviceManager);
            }

            @Override
            public void shutdown(Shutdown shutdown) {
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
            public void start(CacheServiceManagerService serviceManager) throws Exception{
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
        conf.setDefaultLogger(Loggers.NULL_LOGGER);
        init(conf);
        try {
            prestart();
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } catch (IllegalMonitorStateException ok) {
            
        }
//        assertEquals(1, handler.shutdownMap.size());
//        assertTrue(handler.shutdownMap.get(alv1) instanceof ArithmeticException);
//        assertSame(c, handler.shutdownCache);

        assertEquals(2, handler.terminatationMap.size());
        assertTrue(handler.terminatationMap.get(alv1) instanceof ArrayIndexOutOfBoundsException);
        assertTrue(handler.terminatationMap.get(alv2) instanceof ArrayIndexOutOfBoundsException);
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
            public void start(CacheServiceManagerService serviceManager) throws Exception {
                assertEquals(0, verifier.getAndIncrement());
                super.start(serviceManager);
            }

            @Override
            public void shutdown(Shutdown shutdown) {
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
            public void start(CacheServiceManagerService serviceManager) throws Exception{
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
        conf.setDefaultLogger(Loggers.NULL_LOGGER);
        init(conf);
        try {
            prestart();
            throw new AssertionError("Should have failed with IllegalMonitorStateException");
        } catch (IllegalMonitorStateException ok) {
            
        }
//        assertEquals(1, handler.shutdownMap.size());
//        assertTrue(handler.shutdownMap.get(alv1) instanceof ArithmeticException);
//        assertSame(c, handler.shutdownCache);

        assertEquals(2, handler.terminatationMap.size());
        assertTrue(handler.terminatationMap.get(alv1) instanceof ArrayIndexOutOfBoundsException);
        assertTrue(handler.terminatationMap.get(alv2) instanceof ArrayIndexOutOfBoundsException);
        assertEquals(6, verifier.get());
    }

    class StartExceptionHandler extends CacheExceptionHandler {
        Map terminatationMap;

        CacheExceptionContext context;


        @Override
        public void terminated(Map terminationFailures) {
            assertNotNull(terminationFailures);
            this.terminatationMap = terminationFailures;
        }
    }
}

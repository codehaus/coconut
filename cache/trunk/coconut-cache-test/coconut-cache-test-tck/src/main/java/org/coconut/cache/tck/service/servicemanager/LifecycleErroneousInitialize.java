/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle.Initializer;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.lifecycle.LifecycleVerifier;
import org.coconut.cache.test.util.lifecycle.LifecycleVerifierContext;
import org.coconut.core.Logger;
import org.coconut.test.throwables.Error1;
import org.coconut.test.throwables.RuntimeException1;
import org.coconut.test.throwables.RuntimeException2;
import org.coconut.test.throwables.RuntimeException3;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests initialization of cache services.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LifecycleErroneousInitialize extends AbstractCacheTCKTest {

    InitializingExceptionHandler handler;

    LifecycleVerifierContext context;

    @Before
    public void setup() {
        context = new LifecycleVerifierContext(conf);
        handler = new InitializingExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(handler);
    }

    @After
    public void after() {
        context.verify();
        assertSame(conf, handler.conf);
    }

    /**
     * Tests throwing an {@link RuntimeException} from within
     * {@link CacheLifecycle#initialize(Initializer)}. Makes sure
     * {@link CacheExceptionHandler#lifecycleInitializationFailed(CacheConfiguration, Class, CacheLifecycle, RuntimeException)}
     * is called.
     */
    @Test
    public void exceptionInInitialize() {
        LifecycleVerifier alv = context.create(new AbstractCacheLifecycle() {
            @Override
            public void initialize(Initializer cli) {
                throw new RuntimeException1();
            }
        });
        try {
            setCache(conf);
            fail();
        } catch (IllegalArgumentException ok) {
            alv.initialization().assertFailed();
            assertEquals(0, handler.terminatationMap.size());
        }
    }

    /**
     * Tests that we do not try to handle an {@link Error} when thrown in
     * {@link CacheLifecycle#initialize(Initializer)}.
     */
    @Test(expected = Error1.class)
    public void errorInInitialize() {
        context.create(new AbstractCacheLifecycle() {
            @Override
            public void initialize(Initializer cli) {
                throw new Error1();
            }
        });
        try {
            setCache(conf);
        } finally {
            assertNull(handler.terminatationMap);
        }
    }

    /**
     * Tests that {@link CacheLifecycle#terminated()} is called on components that have
     * already been initialized. When other components fails to initialize.
     */
    @Test
    public void exceptionInInitialize33() {
        context.create();
        LifecycleVerifier alv = context.create(new AbstractCacheLifecycle() {
            @Override
            public void initialize(Initializer cli) {
                throw new RuntimeException1();
            }
        });
        context.createNever();

        try {
            setCache(conf);
            fail("should fail");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof RuntimeException1);
            assertEquals(0, handler.terminatationMap.size());
        }
    }

    /**
     * Tests components that fail both in {@link CacheLifecycle#initialize(Initializer)}
     * and {@link CacheLifecycle#terminated()}.
     */
    @Test
    public void exceptionInInitializeAndTerminate1() {
        context.create();
        LifecycleVerifier alv1 = context.create(new AbstractCacheLifecycle() {
            @Override
            public void terminated() {
                throw new RuntimeException2();
            }
        });
        LifecycleVerifier alv2 = context.create(new AbstractCacheLifecycle() {
            @Override
            public void terminated() {
                throw new RuntimeException3();
            }
        });
        context.create(new AbstractCacheLifecycle() {
            @Override
            public void initialize(Initializer cli) {
                throw new RuntimeException1();
            }
        });
        context.createNever();

        try {
            setCache(conf);
            fail("should fail");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof RuntimeException1);
            assertEquals(2, handler.terminatationMap.size());
            assertTrue(handler.terminatationMap.get(alv1) instanceof RuntimeException2);
            assertTrue(handler.terminatationMap.get(alv2) instanceof RuntimeException3);
        }
    }

    class InitializingExceptionHandler extends
            CacheExceptionHandlers.DefaultLoggingExceptionHandler {
        CacheConfiguration conf;

        Map terminatationMap;

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

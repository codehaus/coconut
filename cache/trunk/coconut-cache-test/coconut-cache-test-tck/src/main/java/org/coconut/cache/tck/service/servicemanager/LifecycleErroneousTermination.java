/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.Map;

import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.lifecycle.LifecycleVerifier;
import org.coconut.cache.test.util.lifecycle.LifecycleVerifierContext;
import org.coconut.test.throwables.RuntimeException1;
import org.coconut.test.throwables.RuntimeException2;
import org.coconut.test.throwables.RuntimeException3;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LifecycleErroneousTermination extends AbstractCacheTCKTest {

    private TerminationExceptionHandler handler;

    private LifecycleVerifierContext context;

    @Before
    public void setup() {
        handler = new TerminationExceptionHandler();
        context = new LifecycleVerifierContext(conf);
        conf.exceptionHandling().setExceptionHandler(handler);
        //conf.management().setEnabled(true);
       //conf.setDefaultLogger(Loggers.systemErrLogger(Level.Debug));
    }

    @After
    public void after() {
        context.verify();
    }

    /**
     * Tests a single service that fails in the {@link CacheLifecycle#terminated()} phase.
     */
    @Test
    public void terminationErroneous() {
        LifecycleVerifier alv = context.create(new AbstractCacheLifecycle() {
            @Override
            public void terminated() {
                throw new RuntimeException1();
            }
        });
        setCache();
        prestart();
        shutdownAndAwaitTermination();
        assertEquals(1, handler.terminatationMap.size());
        assertTrue(handler.terminatationMap.get(alv) instanceof RuntimeException1);
    }

    /**
     * Tests a single service that fails in the {@link CacheLifecycle#terminated()} phase.
     */
    //@Test
    public void terminationMultipleErroneous() {

        // final AtomicInteger verifier = new AtomicInteger();

        LifecycleVerifier alv1 = context.create(new AbstractCacheLifecycle() {
            @Override
            public void terminated() {
                throw new RuntimeException1();
            }
        });
        context.create();
        LifecycleVerifier alv2 = context.create(new AbstractCacheLifecycle() {
            @Override
            public void terminated() {
                throw new RuntimeException2();
            }
        });
        LifecycleVerifier alv3 = context.create(new AbstractCacheLifecycle() {
            @Override
            public void terminated() {
                throw new RuntimeException3();
            }
        });
        setCache();
        prestart();
        shutdownAndAwaitTermination();
        assertEquals(3, handler.terminatationMap.size());
        assertTrue(handler.terminatationMap.get(alv3) instanceof RuntimeException3);
        assertTrue(handler.terminatationMap.get(alv2) instanceof RuntimeException2);
        assertTrue(handler.terminatationMap.get(alv1) instanceof RuntimeException1);
    }

    class TerminationExceptionHandler extends CacheExceptionHandler {

        Map terminatationMap;

        @Override
        public void terminated(Map terminationFailures) {
            assertNotNull(terminationFailures);
            this.terminatationMap = terminationFailures;
        }
    }
}

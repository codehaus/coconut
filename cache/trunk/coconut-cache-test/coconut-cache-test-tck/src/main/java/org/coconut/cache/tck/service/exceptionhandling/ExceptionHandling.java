/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.exceptionhandling;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;

import java.util.logging.LogManager;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.core.Logger;
import org.coconut.test.SystemErrOutHelper;
import org.coconut.test.throwables.Exception1;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ExceptionHandling extends AbstractCacheTCKTest {

    Mockery context = new JUnit4Mockery();

    CacheLoader<Integer, String> loader = new FailingLoader();

    private Throwable failure;
    Logger logger;
    @Before
    public void setup() {
        logger = context.mock(Logger.class);
        context.checking(new Expectations() {
            {
                ignoring(logger);
            }
        });
    }
    @After
    public void resetLogging() throws Exception {
        // reset the logging system
        LogManager.getLogManager().readConfiguration();
    }

    @Test
    public void defaultLogger() {
        c = newCache(newConf().setDefaultLogger(logger).exceptionHandling().setExceptionHandler(
                new LoaderVerifyingExceptionHandler(logger)).setExceptionLogger(logger).c()
                .loading().setLoader(loader));
        assertEquals("foo", c.get(10));
    }

    @Test
    public void exceptionLogger() {
        final Logger logger = context.mock(Logger.class);
        c = newCache(newConf().exceptionHandling().setExceptionHandler(
                new LoaderVerifyingExceptionHandler(logger)).setExceptionLogger(logger).c()
                .loading().setLoader(loader));
        assertEquals("foo", c.get(10));
    }

    /**
     * Tests that the logger set by
     * {@link CacheExceptionHandlingConfiguration#setExceptionLogger(Logger)} takes
     * precedence over the logger set by
     * {@link CacheConfiguration#setDefaultLogger(Logger)}
     */
    @Test
    public void exceptionLoggerPreference() {
        final Logger logger2 = context.mock(Logger.class);
        c = newCache(newConf().setDefaultLogger(logger).exceptionHandling().setExceptionHandler(
                new LoaderVerifyingExceptionHandler(logger2)).setExceptionLogger(logger2).c()
                .loading().setLoader(loader));
        assertEquals("foo", c.get(10));
    }

    static class LoaderVerifyingExceptionHandler extends BaseExceptionHandler {
        private final Logger logger;

        LoaderVerifyingExceptionHandler(Logger logger) {
            this.logger = logger;
        }

        @Override
        public String loadingLoadValueFailed(CacheExceptionContext<Integer, String> context,
                CacheLoader<? super Integer, ?> loader, Integer key, AttributeMap map) {
            assertSame(logger, context.defaultLogger());
            return "foo";
        }
    }

    @Test
    public void loadFailedToSystemOut() throws Exception {
        c = newCache(newConf().loading().setLoader(loader));
        SystemErrOutHelper str = SystemErrOutHelper.getErr();
        try {
            loading().load(10);
            awaitAllLoads();
            assertNotNull(str.getFromLast(1));
        } finally {
            str.terminate();
        }
        LogManager.getLogManager().readConfiguration();
    }

    @Test
    public void loadFailed() {
        final Logger logger = context.mock(Logger.class);
        c = newCache(newConf().exceptionHandling().setExceptionHandler(new BaseExceptionHandler() {
            @Override
            public String loadingLoadValueFailed(CacheExceptionContext<Integer, String> context,
                    CacheLoader<? super Integer, ?> loader, Integer key, AttributeMap map) {
                try {
                    assertNotNull(context);
                    assertSame(logger, context.defaultLogger());
                    assertSame(c, context.getCache());
                    assertEquals(10, key.intValue());
                    assertNotNull(map);
                    assertTrue(context.getCause() instanceof Exception1);
                } catch (Error t) {
                    failure = t;
                    throw t;
                }
                return "foo";
            }
        }).setExceptionLogger(logger).c().loading().setLoader(loader));
        loadAndAwait(M1);
        assertSize(1);
        loading().load(10);
        awaitAllLoads();
        assertSize(2);
        assertEquals("foo", c.get(10));
        loadAndAwait(M2);
        assertSize(3);
    }

    @Test
    public void loadFailedNoValue() {
        final Logger logger = context.mock(Logger.class);
        c = newCache(newConf().exceptionHandling().setExceptionHandler(new BaseExceptionHandler() {
            @Override
            public String loadingLoadValueFailed(CacheExceptionContext<Integer, String> context,
                    CacheLoader<? super Integer, ?> loader, Integer key, AttributeMap map) {
                try {
                    assertNotNull(context);
                    assertSame(logger, context.defaultLogger());
                    assertSame(c, context.getCache());
                    assertEquals(10, key.intValue());
                    assertNotNull(map);
                    assertTrue(context.getCause() instanceof Exception1);
                } catch (Error t) {
                    failure = t;
                    throw t;
                }
                return null;
            }
        }

        ).setExceptionLogger(logger).c().loading().setLoader(loader));
        loadAndAwait(M1);
        assertSize(1);
        assertNull(c.get(10));
        assertSize(1);
        loadAndAwait(M2);
        assertSize(2);
    }

    @After
    public void checkFailure() throws Throwable {
        if (failure != null) {
            throw failure;
        }
    }

    class FailingLoader extends AbstractCacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            if (key.equals(10)) {
                throw new Exception1();
            } else {
                return new IntegerToStringLoader().load(key, attributes);
            }
        }

    }

    static class BaseExceptionHandler extends
            CacheExceptionHandlers.DefaultLoggingExceptionHandler<Integer, String> {}
}

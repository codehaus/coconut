package org.coconut.cache.tck.service.exceptionhandling;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import junit.framework.AssertionFailedError;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.core.AttributeMap;
import org.coconut.core.Logger;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ExceptionHandling extends AbstractCacheTCKTestBundle {
    private static Exception e = new Exception();

    Mockery context = new JUnit4Mockery();

    CacheLoader<Integer, String> loader = new FailingLoader();

    private Throwable failure;

    @Test
    public void defaultLogger() {
        final Logger logger = context.mock(Logger.class);
        c = newCache(newConf().setDefaultLogger(logger).exceptionHandling()
                .setExceptionHandler(new LoaderVerifyingExceptionHandler(logger))
                .setExceptionLogger(logger).c().loading().setLoader(loader));
        assertEquals("foo", c.get(10));
    }

    @Test
    public void exceptionLogger() {
        final Logger logger = context.mock(Logger.class);
        c = newCache(newConf().exceptionHandling().setExceptionHandler(
                new LoaderVerifyingExceptionHandler(logger)).setExceptionLogger(logger)
                .c().loading().setLoader(loader));
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
        final Logger logger = context.mock(Logger.class);
        final Logger logger2 = context.mock(Logger.class);
        c = newCache(newConf().setDefaultLogger(logger2).exceptionHandling()
                .setExceptionHandler(new LoaderVerifyingExceptionHandler(logger))
                .setExceptionLogger(logger).c().loading().setLoader(loader));
        assertEquals("foo", c.get(10));
    }

    static class LoaderVerifyingExceptionHandler extends BaseExceptionHandler {
        private final Logger logger;

        LoaderVerifyingExceptionHandler(Logger logger) {
            this.logger = logger;
        }

        @Override
        public String loadFailed(CacheExceptionContext<Integer, String> context,
                CacheLoader<? super Integer, ?> loader, Integer key, AttributeMap map,
                boolean isGet, Exception cause) {
            assertSame(logger, context.defaultLogger());
            return "foo";
        }
    }

    @Test
    public void loadFailed() {
        final Logger logger = context.mock(Logger.class);
        c = newCache(newConf().exceptionHandling().setExceptionHandler(
                new BaseExceptionHandler() {
                    @Override
                    public String loadFailed(
                            CacheExceptionContext<Integer, String> context,
                            CacheLoader<? super Integer, ?> loader, Integer key,
                            AttributeMap map, boolean isGet, Exception cause) {
                        try {
                            assertNotNull(context);
                            assertSame(logger, context.defaultLogger());
                            assertSame(c, context.getCache());
                            assertEquals(10, key.intValue());
                            assertNotNull(map);
                            assertFalse(isGet);
                            assertSame(e, cause);
                        } catch (Error t) {
                            failure = t;
                            throw t;
                        }
                        return "foo";
                    }
                }
        ).setExceptionLogger(logger).c().loading().setLoader(loader));
        loadAndAwait(M1);
        assertSize(1);
        loadAndAwait(10);
        assertSize(2);
        assertEquals("foo", c.get(10));
        loadAndAwait(M2);
        assertSize(3);
    }

    @Test
    public void loadFailedNoValue() {
        final Logger logger = context.mock(Logger.class);
        c = newCache(newConf().exceptionHandling().setExceptionHandler(
                new BaseExceptionHandler() {
                    @Override
                    public String loadFailed(
                            CacheExceptionContext<Integer, String> context,
                            CacheLoader<? super Integer, ?> loader, Integer key,
                            AttributeMap map, boolean isGet, Exception cause) {
                        try {
                            assertNotNull(context);
                            assertSame(logger, context.defaultLogger());
                            assertSame(c, context.getCache());
                            assertEquals(10, key.intValue());
                            assertNotNull(map);
                            assertTrue(isGet);
                            assertSame(e, cause);
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

    static class FailingLoader implements CacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            if (key.equals(10)) {
                throw e;
            } else {
                return new IntegerToStringLoader().load(key, attributes);
            }
        }

    }

    static class BaseExceptionHandler extends
            CacheExceptionHandlers.DefaultLoggingExceptionHandler<Integer, String> {}
}

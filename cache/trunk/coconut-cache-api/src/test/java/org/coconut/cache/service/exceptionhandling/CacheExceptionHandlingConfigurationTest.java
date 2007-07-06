package org.coconut.cache.service.exceptionhandling;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import org.coconut.core.Logger;
import org.coconut.core.Loggers;
import org.junit.Before;
import org.junit.Test;

public class CacheExceptionHandlingConfigurationTest {

    static CacheExceptionHandlingConfiguration<Integer, String> DEFAULT = new CacheExceptionHandlingConfiguration<Integer, String>();

    private CacheExceptionHandlingConfiguration<Integer, String> conf;

    @Before
    public void setUp() {
        conf = new CacheExceptionHandlingConfiguration<Integer, String>();
    }

    @Test
    public void testExceptionHandler() {
        LoadableAbstractCacheExceptionHandler h = new LoadableAbstractCacheExceptionHandler();
        assertNull(conf.getExceptionHandler());
        assertSame(conf, conf.setExceptionHandler(h));
        assertSame(h, conf.getExceptionHandler());
    }

    @Test
    public void testExceptionHandlerXML() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getExceptionHandler());

        conf.setExceptionHandler(new LoadableAbstractCacheExceptionHandler());
        conf = reloadService(conf);
        assertTrue(conf.getExceptionHandler() instanceof LoadableAbstractCacheExceptionHandler);

        conf.setExceptionHandler(new NonLoadableAbstractCacheExceptionHandler());
        conf = reloadService(conf);
        assertNull(conf.getExceptionHandler());
    }

    @Test
    public void testLoggerHandler() {
        Logger l = Loggers.nullLog();
        assertNull(conf.getExceptionLogger());
        assertSame(conf, conf.setExceptionLogger(l));
        assertSame(l, conf.getExceptionLogger());
    }

    @Test
    public void testLoggerHandlerXML() {

    }

    public static class LoadableAbstractCacheExceptionHandler extends
            CacheExceptionHandler<Integer, String> {

        @Override
        public void handleError(CacheExceptionContext<Integer, String> context,
                Error cause) {}

        @Override
        public void handleException(CacheExceptionContext<Integer, String> context,
                Exception cause) {}

        @Override
        public void handleRuntimeException(
                CacheExceptionContext<Integer, String> context, RuntimeException cause) {}

        @Override
        public void handleWarning(CacheExceptionContext<Integer, String> context, String warning) {}}

    public class NonLoadableAbstractCacheExceptionHandler extends
            CacheExceptionHandler<Integer, String> {

        @Override
        public void handleError(CacheExceptionContext<Integer, String> context,
                Error cause) {}

        @Override
        public void handleException(CacheExceptionContext<Integer, String> context,
                Exception cause) {}

        @Override
        public void handleRuntimeException(
                CacheExceptionContext<Integer, String> context, RuntimeException cause) {}

        @Override
        public void handleWarning(CacheExceptionContext<Integer, String> context, String warning) {}}

}

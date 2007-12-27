package org.coconut.cache.service.exceptionhandling;

import static org.coconut.test.TestUtil.dummy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.Logger;
import org.coconut.core.Logger.Level;
import org.coconut.internal.util.LogHelper.AbstractLogger;
import org.coconut.test.throwables.Error1;
import org.coconut.test.throwables.Exception1;
import org.coconut.test.throwables.RuntimeException1;
import org.coconut.test.throwables.Throwable1;
import org.junit.Test;

public class CacheExceptionHandlerTest {

    CacheExceptionHandler<Integer, String> ceh = new CacheExceptionHandler<Integer, String>();

    @Test(expected = Error1.class)
    public void applyError() {
        MyExceptionContext<Integer, String> cec = new MyExceptionContext(Error1.INSTANCE, "msg3",
                Level.Fatal);
        try {
            ceh.apply(cec);
        } finally {
            assertSame(Error1.INSTANCE, cec.logger.cause);
            assertSame(Level.Fatal, cec.logger.level);
            assertEquals("msg3", cec.logger.msg);
        }
    }

    @Test
    public void applyException() {
        MyExceptionContext<Integer, String> cec = new MyExceptionContext(Exception1.INSTANCE,
                "msg1", Level.Error);
        ceh.apply(cec);
        assertSame(Exception1.INSTANCE, cec.logger.cause);
        assertSame(Level.Error, cec.logger.level);
        assertEquals("msg1", cec.logger.msg);
    }

    @Test
    public void applyRuntimeException() {
        MyExceptionContext<Integer, String> cec = new MyExceptionContext(
                RuntimeException1.INSTANCE, "msg2", Level.Fatal);
        ceh.apply(cec);
        assertSame(RuntimeException1.INSTANCE, cec.logger.cause);
        assertSame(Level.Fatal, cec.logger.level);
        assertEquals("msg2", cec.logger.msg);
    }

    @Test
    public void applyThrowable() {
        MyExceptionContext<Integer, String> cec = new MyExceptionContext(Throwable1.INSTANCE,
                "msg4", Level.Fatal);
        ceh.apply(cec);
        assertSame(Throwable1.INSTANCE, cec.logger.cause);
        assertSame(Level.Fatal, cec.logger.level);
        assertEquals("msg4", cec.logger.msg);
    }

    @Test
    public void handleWarning() {
        MyExceptionContext<Integer, String> cec = new MyExceptionContext(Exception1.INSTANCE,
                "msg", Level.Warn);
        ceh.apply(cec);
        assertSame(Exception1.INSTANCE, cec.logger.cause);
        assertSame(Level.Warn, cec.logger.level);
        assertEquals("msg", cec.logger.msg);
    }

    @Test
    public void initializeHandle() {
        ceh.initialize(null);
        ceh.terminated(null);
    }

    @Test
    public void loadingLoadValueFailed() {
        MyExceptionContext<Integer, String> cec = new MyExceptionContext(
                RuntimeException1.INSTANCE, "msg5", Level.Fatal);
        assertNull(ceh.loadingLoadValueFailed(cec, dummy(CacheLoader.class), 1,
                dummy(AttributeMap.class)));
        assertSame(RuntimeException1.INSTANCE, cec.logger.cause);
        assertSame(Level.Fatal, cec.logger.level);
        assertEquals("msg5", cec.logger.msg);
    }

    static class MyExceptionContext<K, V> extends CacheExceptionContext<K, V> {

        private final Throwable cause;

        private final MyLogger logger;

        private final String msg;

        private final Level level;

        public MyExceptionContext(Throwable cause, String msg, Level level) {
            this.logger = new MyLogger();
            this.cause = cause;
            this.msg = msg;
            this.level = level;
        }

        @Override
        public Logger defaultLogger() {
            return logger;
        }

        @Override
        public Cache<K, V> getCache() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Throwable getCause() {
            return cause;
        }

        @Override
        public String getMessage() {
            return msg;
        }

        @Override
        public Level getLevel() {
            return level;
        }

    }

    static class MyLogger extends AbstractLogger {

        private Throwable cause;

        private Level level;

        private String msg;

        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

        public boolean isEnabled(Level level) {
            throw new UnsupportedOperationException();
        }

        public void log(Level level, String message, Throwable cause) {
            if (this.level != null) {
                throw new IllegalStateException("Can only invoke once");
            }
            this.level = level;
            this.msg = message;
            this.cause = cause;
        }
    }
}

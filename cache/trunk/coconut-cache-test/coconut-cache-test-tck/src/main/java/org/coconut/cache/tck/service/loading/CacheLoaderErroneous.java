package org.coconut.cache.tck.service.loading;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.core.Logger.Level;
import org.coconut.internal.util.LogHelper.AbstractLogger;
import org.coconut.test.throwables.RuntimeException1;
import org.junit.Test;

public class CacheLoaderErroneous extends AbstractCacheTCKTest {
    // IntegerToStringLoader
    @Test
    public void loadAllRuntime() {
        MyLoader loader = new MyLoader();
        MyLogger logger = new MyLogger();
        conf.exceptionHandling().setExceptionLogger(logger);
        init(conf.loading().setLoader(loader));

        loading().loadAll(Arrays.asList(1, 2, 3));

        if (loader.key != null) {
            assertSize(1);
            assertEquals(loader.value, c.get(loader.key));

            assertTrue(logger.cause instanceof RuntimeException1);
            assertEquals(Level.Fatal, logger.level);
            assertNotNull(logger.msg);
        }
    }

    @Test
    public void loadAllRuntime1() {
        MyLoader loader = new MyLoader();
        MyLogger logger = new MyLogger();
        conf.exceptionHandling().setExceptionLogger(logger);
        init(conf.loading().setLoader(loader));

        loading().loadAll(Arrays.asList(1));

        if (loader.key != null) {
            assertSize(1);
            assertEquals(loader.value, c.get(loader.key));

            assertTrue(logger.cause instanceof RuntimeException1);
            assertEquals(Level.Fatal, logger.level);
            assertNotNull(logger.msg);
        }
    }

    @Test
    public void failedToComplete() {
        MyLoader2 loader = new MyLoader2();
        MyLogger logger = new MyLogger();
        conf.exceptionHandling().setExceptionLogger(logger);
        init(conf.loading().setLoader(loader));

        loading().loadAll(Arrays.asList(1, 2));

        if (loader.key != null) {
            assertSize(1);
            assertEquals(loader.value, c.get(loader.key));
            assertNull(logger.cause);
            assertEquals(Level.Fatal, logger.level);
            assertNotNull(logger.msg);
        }
    }

    static class MyLogger extends AbstractLogger {

        private Throwable cause;

        private Level level;

        private String msg;

        @Override
        public String getName() {
            return "unknown";
        }

        public boolean isEnabled(Level level) {
            return true;
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

    static class MyLoader2 implements CacheLoader<Integer, String> {
        private Integer key;

        private String value;

        public String load(Integer key, AttributeMap attributes) {
            if (1 <= key && key <= 5) {
                return "" + (char) (key + 64);
            } else {
                return null;
            }
        }

        public void loadAll(
                Collection<? extends LoaderCallback<? extends Integer, ? super String>> loadCallbacks) {
            LoaderCallback<? extends Integer, ? super String> clc = loadCallbacks.iterator()
                    .next();
            key = clc.getKey();
            value = load(clc.getKey(), clc.getAttributes());
            clc.completed(value);
        }
    }

    static class MyLoader implements CacheLoader<Integer, String> {
        private Integer key;

        private String value;

        public String load(Integer key, AttributeMap attributes) {
            if (1 <= key && key <= 5) {
                return "" + (char) (key + 64);
            } else {
                return null;
            }
        }

        public void loadAll(
                Collection<? extends LoaderCallback<? extends Integer, ? super String>> loadCallbacks) {
            LoaderCallback<? extends Integer, ? super String> clc = loadCallbacks.iterator()
                    .next();
            key = clc.getKey();
            value = load(clc.getKey(), clc.getAttributes());
            clc.completed(value);
            throw new RuntimeException1();
        }
    }
}

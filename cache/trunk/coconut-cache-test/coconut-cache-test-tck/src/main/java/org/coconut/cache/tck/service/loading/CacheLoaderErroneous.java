package org.coconut.cache.tck.service.loading;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.core.Logger.Level;
import org.coconut.test.throwables.RuntimeException1;
import org.junit.Test;

public class CacheLoaderErroneous extends AbstractCacheTCKTest {
    // IntegerToStringLoader
    @Test
    public void loadAllRuntime() {
        MyLoader loader = new MyLoader();
        init(conf.loading().setLoader(loader));

        loading().loadAll(Arrays.asList(1, 2, 3));

        if (loader.key != null) {
            assertSize(1);
            assertEquals(loader.value, c.get(loader.key));
            exceptionHandler.eat(RuntimeException1.INSTANCE, Level.Fatal);
        }
    }

    @Test
    public void loadAllRuntime1() {
        MyLoader loader = new MyLoader();
        init(conf.loading().setLoader(loader));

        loading().loadAll(Arrays.asList(1));

        if (loader.key != null) {
            assertSize(1);
            assertEquals(loader.value, c.get(loader.key));
            exceptionHandler.eat(RuntimeException1.INSTANCE, Level.Fatal);
        }
    }

    @Test
    public void failedToComplete() {
        MyLoader2 loader = new MyLoader2();
        init(conf.loading().setLoader(loader));

        loading().loadAll(Arrays.asList(1, 2));

        if (loader.key != null) {
            assertSize(1);
            assertEquals(loader.value, c.get(loader.key));
            exceptionHandler.eat(null, Level.Fatal);
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
            throw RuntimeException1.INSTANCE;
        }
    }
}

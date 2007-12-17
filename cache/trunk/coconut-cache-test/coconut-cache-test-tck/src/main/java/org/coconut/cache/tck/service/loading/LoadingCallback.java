/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.spi.AbstractAttribute;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoaderCallback;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.service.exceptionhandling.ExceptionHandling.BaseExceptionHandler;
import org.coconut.core.Logger;
import org.coconut.test.TestUtil;
import org.coconut.test.throwables.Exception1;
import org.junit.Before;
import org.junit.Test;

public class LoadingCallback extends AbstractCacheTCKTest {

    static final AbstractAttribute RA = new AbstractAttribute("resultAttr", Object.class, null) {};

    private CacheLoader<Integer, String> loader;

    private boolean wasLoadAll;

    @Test
    public void loadAll1() {
        conf.loading().setLoader(new MyCacheLoader());
        Map<Integer, AttributeMap> map = singletons(1, RA.singleton("A"));
        setCache();
        loading().loadAll(map);
        awaitAllLoads();
        if (wasLoadAll) {
            assertEquals("A", c.get(1));
        }
    }

    @Test
    public void loadAll3() {
        conf.loading().setLoader(new MyCacheLoader());
        Map<Integer, AttributeMap> map = singletons(1, RA.singleton("A"), 2, RA.singleton("B"), 3,
                RA.singleton("C"));
        setCache();
        loading().loadAll(map);
        awaitAllLoads();
        if (wasLoadAll) {
            assertSize(3);
            assertEquals("A", c.get(1));
            assertEquals("B", c.get(2));
            assertEquals("C", c.get(3));
        }
    }

    @Test
    public void loadFailed() {
        final Logger logger = TestUtil.dummy(Logger.class);
        setCache(conf.exceptionHandling().setExceptionHandler(new BaseExceptionHandler() {
            @Override
            public String loadingLoadValueFailed(CacheExceptionContext<Integer, String> context,
                    CacheLoader<? super Integer, ?> loader, Integer key, AttributeMap map) {
                try {
                    assertNotNull(context);
                    assertSame(logger, context.defaultLogger());
                    assertSame(c, context.getCache());
                    assertEquals(1, key.intValue());
                    assertNotNull(map);
                    assertTrue(context.getCause() instanceof Exception1);
                } catch (Error t) {
                    failed(t);
                }
                return "a";
            }
        }).setExceptionLogger(logger).c().loading().setLoader(loader));
        Map<Integer, AttributeMap> map = singletons(1, RA.singleton(new Exception1()));
        loading().loadAll(map);
        awaitAllLoads();
        if (wasLoadAll) {
            assertSize(1);
            assertEquals("a", c.get(1));
        }
    }

    @Before
    public void setup() {
        wasLoadAll = false;
        loader = new MyCacheLoader();
    }

    static <K> Map<K, AttributeMap> singletons(K k1, AttributeMap a1) {
        Map<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        map.put(k1, a1);
        return map;
    }

    static <K> Map<K, AttributeMap> singletons(K k1, AttributeMap a1, K k2, AttributeMap a2) {
        Map<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        map.put(k1, a1);
        map.put(k2, a2);
        return map;
    }

    static <K> Map<K, AttributeMap> singletons(K k1, AttributeMap a1, K k2, AttributeMap a2, K k3,
            AttributeMap a3) {
        Map<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        map.put(k1, a1);
        map.put(k2, a2);
        map.put(k3, a3);
        return map;
    }

    class MyCacheLoader implements CacheLoader<Integer, String> {

        public String load(Integer key, AttributeMap attributes) throws Exception {
            return null;
        }

        public void loadAll(
                Collection<? extends CacheLoaderCallback<? extends Integer, ? super String>> loadCallbacks) {
            wasLoadAll = true;
            for (CacheLoaderCallback<? extends Integer, ? super String> cc : loadCallbacks) {
                AttributeMap am = cc.getAttributes();
                Object result = RA.getValue(am);

                try {
                    cc.failed(null);
                    fail();
                } catch (NullPointerException ok) {/* ok */}
                
                if (result instanceof Throwable) {
                    assertFalse(cc.isDone());
                    cc.failed((Throwable) result);
                    assertTrue(cc.isDone());
                } else {
                    assertFalse(cc.isDone());
                    cc.completed((String) result);
                    assertTrue(cc.isDone());
                }
                
                try {
                    cc.failed(new Exception());
                    fail();
                } catch (IllegalStateException ok) {/* ok */}
                
                try {
                    cc.completed("foo");
                    fail();
                } catch (IllegalStateException ok) {/* ok */}

            }
        }
    }

}

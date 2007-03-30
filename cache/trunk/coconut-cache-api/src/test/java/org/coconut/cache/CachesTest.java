/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import static org.coconut.core.AttributeMaps.EMPTY_MAP;
import static org.coconut.core.AttributeMaps.createMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.Loaders;
import org.coconut.cache.spi.CacheExecutorRunnable;
import org.coconut.core.AttributeMap;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class CachesTest extends MockTestCase {

    private final Exception ex = new Exception();

    @Test
    public void testConstanst() {
        assertEquals(0l, CacheExpirationService.DEFAULT_EXPIRATION);
        assertEquals(Long.MAX_VALUE, CacheExpirationService.NEVER_EXPIRE);
    }

    public void testNullCacheLoader() throws Exception {
        CacheLoader<Object, Object> loader = Loaders.nullLoader();
        assertNull(loader.load(new Object(), EMPTY_MAP));
    }

    public void testSynchronizedCacheLoader1() throws Exception {
        Mock mock = mock(CacheLoader.class);
        CacheLoader<Integer, String> loader = Loaders
                .synchronizedCacheLoader((CacheLoader<Integer, String>) mock.proxy());
        Collection<Integer> col = new LinkedList<Integer>();
        Map<Integer, String> map = new HashMap<Integer, String>();
        mock.expects(once()).method("load").with(eq(0), same(EMPTY_MAP)).will(
                returnValue("foo"));
        mock.expects(once()).method("load").with(eq(1), same(EMPTY_MAP)).will(
                throwException(ex));
        Map m = createMap(col);
        mock.expects(once()).method("loadAll").with(same(m)).will(returnValue(map));

        assertEquals("foo", loader.load(0, EMPTY_MAP));

        try {
            loader.load(1, EMPTY_MAP);
            shouldThrow();
        } catch (Exception e) { /* okay */
            assertSame(ex, e);
        }

        assertSame(map, loader.loadAll(m));
    }

    public void testSynchronizedCacheLoader1Sync() throws Exception {
        final AtomicInteger ai = new AtomicInteger();

        final CacheLoader<Integer, String> loader = Loaders
                .synchronizedCacheLoader(new CacheLoader<Integer, String>() {
                    public String load(Integer key, AttributeMap attributes)
                            throws Exception {
                        int i = ai.incrementAndGet();
                        Thread.sleep(15);
                        assertEquals(i, ai.get());
                        return null;
                    }

                    public Map<Integer, String> loadAll(
                            Map<? extends Integer, AttributeMap> mapsWithAttributes)
                            throws Exception {
                        int i = ai.incrementAndGet();
                        Thread.sleep(15);
                        assertEquals(i, ai.get());
                        return null;
                    }
                });
        Runnable r = new Runnable() {
            public void run() {
                for (int i = 0; i < 5; i++) {
                    try {
                        loader.load(null, EMPTY_MAP);
                        loader.loadAll(null);
                    } catch (Exception e) {
                        threadFailed();
                    }
                }
            }

        };
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    public void testSynchronizedCacheLoader2() throws Exception {

        final Mock mock = mock(CacheLoader.class);
        CacheLoader<Integer, String> loader = Loaders
                .synchronizedCacheLoader(new AbstractCacheLoader<Integer, String>() {
                    public String load(Integer key, AttributeMap ignore) throws Exception {
                        return ((CacheLoader<Integer, String>) mock.proxy()).load(key,
                                EMPTY_MAP);
                    }
                });
        assertTrue(loader instanceof AbstractCacheLoader);
        mock.expects(once()).method("load").with(eq(0), same(EMPTY_MAP)).will(
                returnValue("foo"));
        mock.expects(once()).method("load").with(eq(1), same(EMPTY_MAP)).will(
                throwException(ex));

        assertEquals("foo", loader.load(0, EMPTY_MAP));

        try {
            loader.load(1, EMPTY_MAP);
            shouldThrow();
        } catch (Exception e) { /* okay */
            assertSame(ex, e);
        }
    }

    public void testSynchronizedCacheLoader2Sync() throws Exception {
        final AtomicInteger ai = new AtomicInteger();

        final CacheLoader<Integer, String> loader = Loaders
                .synchronizedCacheLoader(new AbstractCacheLoader<Integer, String>() {
                    public String load(Integer key, AttributeMap ignore) throws Exception {
                        int i = ai.incrementAndGet();
                        Thread.sleep(15);
                        assertEquals(i, ai.get());
                        return null;
                    }
                });
        Runnable r = new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        loader.load(null, EMPTY_MAP);
                    } catch (Exception e) {
                        threadFailed();
                    }
                }
            }

        };
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    public void testSynchronizedCacheLoaderNullPointer() {
        try {
            Loaders.synchronizedCacheLoader(null);
            shouldThrow();
        } catch (NullPointerException e) { /* okay */
        }
    }

    public void testClearAsRunnable() {
        Mock m = mock(Cache.class);
        Cache c = (Cache) m.proxy();
        Runnable r = Caches.clearAsRunnable(c);
        assertTrue(r instanceof CacheExecutorRunnable.CacheClear);
        assertEquals(c, ((CacheExecutorRunnable.CacheClear) r).getCache());

        m.expects(once()).method("clear");
        r.run();

        try {
            Caches.clearAsRunnable(null);
            shouldThrow();
        } catch (NullPointerException e) { /* okay */
        }
    }

    public void testEvictAsRunnable() {
        Mock m = mock(Cache.class);
        Cache c = (Cache) m.proxy();
        Runnable r = Caches.evictAsRunnable(c);
        assertTrue(r instanceof CacheExecutorRunnable.CacheEvict);
        assertEquals(c, ((CacheExecutorRunnable.CacheEvict) r).getCache());

        m.expects(once()).method("evict");
        r.run();

        try {
            Caches.evictAsRunnable(null);
            shouldThrow();
        } catch (NullPointerException e) { /* okay */
        }
    }

    public void testCacheAsCacheLoader() throws Exception {
        Map dummy = new HashMap();
        Collection dummyCol = new ArrayList();
        Map attributeMap = createMap(dummyCol);
        Mock m = mock(Cache.class);
        Cache c = (Cache) m.proxy();
        CacheLoader cl = Loaders.cacheAsCacheLoader(c);

        m.expects(once()).method("get").with(eq(0)).will(returnValue(1));
        assertEquals(1, cl.load(0, EMPTY_MAP));

//        m.expects(once()).method("getAll").with(eq(dummyCol))
//                .will(returnValue(dummy));
//        assertEquals(dummy, cl.loadAll(attributeMap));

        try {
            Loaders.cacheAsCacheLoader(null);
            shouldThrow();
        } catch (NullPointerException e) { /* okay */
        }
    }

}

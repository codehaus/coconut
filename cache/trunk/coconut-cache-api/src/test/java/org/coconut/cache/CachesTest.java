/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

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
        assertNull(loader.load(new Object()));
    }

    public void testSynchronizedCacheLoader1() throws Exception {
        Mock mock = mock(CacheLoader.class);
        CacheLoader<Integer, String> loader = Loaders
                .synchronizedCacheLoader((CacheLoader<Integer, String>) mock.proxy());
        Collection<Integer> col = new LinkedList<Integer>();
        Map<Integer, String> map = new HashMap<Integer, String>();
        mock.expects(once()).method("load").with(eq(0)).will(returnValue("foo"));
        mock.expects(once()).method("load").with(eq(1)).will(throwException(ex));
        mock.expects(once()).method("loadAll").with(eq(col)).will(returnValue(map));

        assertEquals("foo", loader.load(0));

        try {
            loader.load(1);
            shouldThrow();
        } catch (Exception e) { /* okay */
            assertSame(ex, e);
        }

        assertSame(map, loader.loadAll(col));
    }

    public void testSynchronizedCacheLoader1Sync() throws Exception {
        final AtomicInteger ai = new AtomicInteger();

        final CacheLoader<Integer, String> loader = Loaders
                .synchronizedCacheLoader(new CacheLoader<Integer, String>() {
                    public String load(Integer key) throws Exception {
                        int i = ai.incrementAndGet();
                        Thread.sleep(15);
                        assertEquals(i, ai.get());
                        return null;
                    }

                    public Map<Integer, String> loadAll(Collection<? extends Integer> keys)
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
                        loader.load(null);
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
                    public String load(Integer key) throws Exception {
                        return ((CacheLoader<Integer, String>) mock.proxy()).load(key);
                    }
                });
        assertTrue(loader instanceof AbstractCacheLoader);
        mock.expects(once()).method("load").with(eq(0)).will(returnValue("foo"));
        mock.expects(once()).method("load").with(eq(1)).will(throwException(ex));

        assertEquals("foo", loader.load(0));

        try {
            loader.load(1);
            shouldThrow();
        } catch (Exception e) { /* okay */
            assertSame(ex, e);
        }
    }

    public void testSynchronizedCacheLoader2Sync() throws Exception {
        final AtomicInteger ai = new AtomicInteger();

        final CacheLoader<Integer, String> loader = Loaders
                .synchronizedCacheLoader(new AbstractCacheLoader<Integer, String>() {
                    public String load(Integer key) throws Exception {
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
                        loader.load(null);
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
        Mock m = mock(Cache.class);
        Cache c = (Cache) m.proxy();
        CacheLoader cl = Loaders.cacheAsCacheLoader(c);

        m.expects(once()).method("get").with(eq(0)).will(returnValue(1));
        assertEquals(1, cl.load(0));

        m.expects(once()).method("getAll").with(eq(dummyCol)).will(returnValue(dummy));
        assertEquals(dummy, cl.loadAll(dummyCol));

        try {
            Loaders.cacheAsCacheLoader(null);
            shouldThrow();
        } catch (NullPointerException e) { /* okay */
        }
    }


}

/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import static org.coconut.test.TestUtil.assertEqual;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.util.AbstractCacheLoader;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class CachesTest extends MockTestCase {

    private final Exception ex = new Exception();

    public void testNullCacheLoader() throws Exception {
        CacheLoader<Object, Object> loader = Caches.nullLoader();
        assertNull(loader.load(new Object()));
    }

    public void testMapAsCache() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        Cache<Integer, Integer> cc = Caches.mapToCache(map);
        Mock mock = mock(Map.class);
        ConcurrentMap<Integer, Integer> c = Caches.mapToCache((Map) mock
                .proxy());

        mock.expects(once()).method("clear");
        c.clear();

        mock.expects(once()).method("containsKey").with(eq(0)).will(
                returnValue(true));
        assertTrue(c.containsKey(0));

        mock.expects(once()).method("containsValue").with(eq(0)).will(
                returnValue(false));
        assertFalse(c.containsValue(0));

        mock.expects(once()).method("entrySet").will(
                returnValue(map.entrySet()));
        assertSame(map.entrySet(), c.entrySet());

        mock.expects(once()).method("equals").with(eq(cc)).will(
                returnValue(true));
        assertTrue(c.equals(cc));

        mock.expects(once()).method("get").with(eq(0)).will(returnValue(1));
        assertEqual(1, c.get(0));

        mock.expects(once()).method("hashCode").will(returnValue(123));
        assertEquals(123, c.hashCode());

        mock.expects(once()).method("isEmpty").will(returnValue(false));
        assertFalse(c.isEmpty());

        mock.expects(once()).method("keySet").will(returnValue(map.keySet()));
        assertSame(map.keySet(), c.keySet());

        mock.expects(once()).method("put").with(eq(0), eq(1)).will(
                returnValue(Integer.valueOf(2)));
        assertEqual(2, c.put(0, 1));

        mock.expects(once()).method("putAll").with(eq(map));
        c.putAll(map);

        mock.expects(once()).method("remove").with(eq(0)).will(returnValue(1));
        assertEqual(1, c.remove(0));

        mock.expects(once()).method("size").will(returnValue(2));
        assertEquals(2, c.size());

//        mock.expects(once()).method("toString").will(returnValue("foo"));
//        assertEquals("foo", c.toString());

        mock.expects(once()).method("values").will(returnValue(map.values()));
        assertSame(map.values(), c.values());
        // c.putIfAbsent()
        //        
        // c.remove()
        //        
        // c.replace()
        //        
        // c.replace()

    }

    public void testSynchronizedCacheLoader1() throws Exception {
        Mock mock = mock(CacheLoader.class);
        CacheLoader<Integer, String> loader = Caches
                .synchronizedCacheLoader((CacheLoader<Integer, String>) mock
                        .proxy());
        Collection<Integer> col = new LinkedList<Integer>();
        Map<Integer, String> map = new HashMap<Integer, String>();
        mock.expects(once()).method("load").with(eq(0))
                .will(returnValue("foo"));
        mock.expects(once()).method("load").with(eq(1))
                .will(throwException(ex));
        mock.expects(once()).method("loadAll").with(eq(col)).will(
                returnValue(map));

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

        final CacheLoader<Integer, String> loader = Caches
                .synchronizedCacheLoader(new CacheLoader<Integer, String>() {
                    public String load(Integer key) throws Exception {
                        int i = ai.incrementAndGet();
                        Thread.sleep(15);
                        assertEquals(i, ai.get());
                        return null;
                    }

                    public Map<Integer, String> loadAll(
                            Collection<? extends Integer> keys)
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
        CacheLoader<Integer, String> loader = Caches
                .synchronizedCacheLoader(new AbstractCacheLoader<Integer, String>() {
                    public String load(Integer key) throws Exception {
                        return ((CacheLoader<Integer, String>) mock.proxy())
                                .load(key);
                    }
                });
        assertTrue(loader instanceof AbstractCacheLoader);
        mock.expects(once()).method("load").with(eq(0))
                .will(returnValue("foo"));
        mock.expects(once()).method("load").with(eq(1))
                .will(throwException(ex));

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

        final CacheLoader<Integer, String> loader = Caches
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
            Caches.synchronizedCacheLoader(null);
            shouldThrow();
        } catch (NullPointerException e) { /* okay */
        }
    }
}

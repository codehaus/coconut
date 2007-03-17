/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.loading;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.asMap;
import static org.coconut.test.CollectionUtils.newEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CompositeCacheLoader;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CompositeCacheLoaderTest extends MockTestCase {

    CacheLoader<Integer, String> loader1;

    Mock m1;

    CacheLoader<Integer, String> loader2;

    Mock m2;

    @Override
    public void setUp() {

    }

    /**
     * construtor(null) throws NPE.
     */
    @SuppressWarnings("unchecked")
    public void testConstrutorArray_NullPointerException() {
        try {
            new CompositeCacheLoader((CacheLoader[]) null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testNullInArray() {
        loader1 = mockDummy(CacheLoader.class);
        try {
            new CompositeCacheLoader(loader1, null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    /**
     * construtor(null) throws NPE.
     */
    @SuppressWarnings("unchecked")
    public void testConstrutorList_NullPointerException() {
        try {
            new CompositeCacheLoader((List) null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testGetList() {
        loader1 = mockDummy(CacheLoader.class);
        loader2 = mockDummy(CacheLoader.class);
        CompositeCacheLoader ccl = new CompositeCacheLoader(loader1, loader2);
        assertEquals(Arrays.asList(loader1, loader2), ccl.getLoaders());

        ccl = new CompositeCacheLoader(Arrays.asList(loader1, loader2));
        assertEquals(Arrays.asList(loader1, loader2), ccl.getLoaders());
    }

    public void testLoad() throws Exception {
        Mock m1 = mock(CacheLoader.class);
        Mock m2 = mock(CacheLoader.class);
        loader1 = (CacheLoader<Integer, String>) m1.proxy();
        loader2 = (CacheLoader<Integer, String>) m2.proxy();

        m1.expects(once()).method("load").with(eq(1)).will(returnValue("A"));

        m1.expects(once()).method("load").with(eq(2)).will(returnValue(null));
        m2.expects(once()).method("load").with(eq(2)).will(returnValue("B"));

        m1.expects(once()).method("load").with(eq(3)).will(returnValue(null));
        m2.expects(once()).method("load").with(eq(3)).will(returnValue(null));
        CompositeCacheLoader ccl = new CompositeCacheLoader(loader1, loader2);
        assertEquals("A", ccl.load(1));
        assertEquals("B", ccl.load(2));
        assertEquals(null, ccl.load(3));
    }

    @SuppressWarnings("unchecked")
    public void testLoadAll() throws Exception {
        Mock m1 = mock(CacheLoader.class);
        Mock m2 = mock(CacheLoader.class);
        loader1 = (CacheLoader<Integer, String>) m1.proxy();
        loader2 = (CacheLoader<Integer, String>) m2.proxy();
        HashMap hm1 = new HashMap();
        hm1.put(M1.getKey(), M1.getValue());
        hm1.put(M2.getKey(), null);
        hm1.put(M3.getKey(), null);
        m1.expects(once()).method("loadAll")
                .with(eq(new HashSet(Arrays.asList(1, 2, 3)))).will(returnValue(hm1));

        HashMap hm2 = new HashMap();
        hm2.put(M2.getKey(), M2.getValue());
        hm2.put(M3.getKey(), null);
        m2.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(2, 3))))
                .will(returnValue(hm2));

        Map.Entry<Integer, String> M3NULL = newEntry(3, null);

        CompositeCacheLoader ccl = new CompositeCacheLoader(loader1, loader2);
        // CompositeCacheLoader ccl = new CompositeCacheLoader(loader1);
        // ccl.loadAll(new HashSet(Arrays.asList(1, 2, 3)));

        assertEquals(asMap(M1, M2, M3NULL), ccl.loadAll(new HashSet(Arrays
                .asList(1, 2, 3))));
        m1.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(1))))
                .will(returnValue(asMap(M1)));
        assertEquals(asMap(M1), ccl.loadAll(new HashSet(Arrays.asList(1))));

    }

    public void testExceptions() throws Exception {
        Mock m1 = mock(CacheLoader.class);
        Composite.cause = new Exception();
        Mock m2 = mock(CacheLoader.class);
        loader1 = (CacheLoader<Integer, String>) m1.proxy();
        loader2 = (CacheLoader<Integer, String>) m2.proxy();
        CompositeCacheLoader ccl = new CompositeCacheLoader(loader1, loader2);
        m1.expects(once()).method("load").with(eq(4)).will(
                throwException(Composite.cause));
        try {
            ccl.load(4);
            shouldThrow();
        } catch (Exception e) { /* okay */
            assertEquals(Composite.cause, e);
        }

        m1.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(1))))
                .will(throwException(Composite.cause));

        try {
            ccl.loadAll(Arrays.asList(1));
            shouldThrow();
        } catch (Exception e) { /* okay */
            assertEquals(Composite.cause, e);
        }
    }

    @SuppressWarnings("unchecked")
    public void testHandleException() throws Exception {
        Mock m1 = mock(CacheLoader.class, "M1");
        Mock m2 = mock(CacheLoader.class);
        Composite c = new Composite((CacheLoader) m1.proxy(), (CacheLoader) m2.proxy());
        c.cause=new Exception();
        c.keys=new HashSet(Arrays.asList(4, 3, 5));
        c.loader= (CacheLoader) m1.proxy();
        m1.expects(once()).method("load").with(eq(4)).will(
                throwException(Composite.cause));
        assertEquals("foo", c.load(4));

        /* LOAD ALl */
        Map m = new HashMap();
        m.put(5, null);
        m.put(3, null);

        m1.expects(once()).method("loadAll").with(eq(Composite.keys)).will(
                throwException(Composite.cause));
        m2.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(5, 3))))
                .will(returnValue(m));
        Map result = c.loadAll(Composite.keys);
        assertEquals(Composite.m, result);
    }

    static class Composite extends CompositeCacheLoader<Integer, String> {

        /**
         * @param loaders
         */
        public Composite(CacheLoader<Integer, String>... loaders) {
            super(loaders);
        }

         Exception throwMe = new IllegalThreadStateException();

        static CacheLoader<Integer, String> loader;

        static Collection<? extends Integer> keys;

        static Exception cause;

        static Map<Integer, String> m = new HashMap<Integer, String>();
        static {
            m.put(4, "fooo");
            m.put(5, null);
            m.put(3, null);
        }

        /**
         * @see org.coconut.cache.util.CompositeCacheLoader#loadingFailed(org.coconut.cache.CacheLoader,
         *      java.util.Collection, java.lang.Exception)
         */
        @Override
        protected Map<Integer, String> loadAllFailed(CacheLoader<Integer, String> loader,
                Collection<? extends Integer> keys, Exception cause) throws Exception {
            assertEquals(this.loader, loader);
            assertEquals(this.keys, keys);
            assertEquals(this.cause, cause);
            return m;
        }

        /**
         * @see org.coconut.cache.util.CompositeCacheLoader#loadingFailed(org.coconut.cache.CacheLoader,
         *      java.lang.Object, java.lang.Exception)
         */
        @Override
        protected String loadFailed(CacheLoader<Integer, String> loader, Integer key,
                Exception cause) throws Exception {
            assertEquals(this.loader, loader);
            assertEquals(this.keys.iterator().next(), key);
            assertEquals(this.cause, cause);
            return "foo";
        }

    }
}

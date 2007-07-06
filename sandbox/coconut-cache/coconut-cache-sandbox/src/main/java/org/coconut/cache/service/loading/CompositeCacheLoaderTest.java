/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.loading;

import static org.coconut.core.AttributeMaps.EMPTY_MAP;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.core.AttributeMap;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CompositeCacheLoaderTest.java 327 2007-06-06 08:37:16Z kasper $
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
            new CacheLoaders.CompositeCacheLoader((CacheLoader[]) null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testNullInArray() {
        loader1 = mockDummy(CacheLoader.class);
        try {
            new CacheLoaders.CompositeCacheLoader(loader1, null);
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
            new CacheLoaders.CompositeCacheLoader((List) null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testGetList() {
        loader1 = mockDummy(CacheLoader.class);
        loader2 = mockDummy(CacheLoader.class);
        CacheLoaders.CompositeCacheLoader ccl = new CacheLoaders.CompositeCacheLoader(loader1, loader2);
        assertEquals(Arrays.asList(loader1, loader2), ccl.getLoaders());

        ccl = new CacheLoaders.CompositeCacheLoader(Arrays.asList(loader1, loader2));
        assertEquals(Arrays.asList(loader1, loader2), ccl.getLoaders());
    }

    public void testLoad() throws Exception {
        Mock m1 = mock(CacheLoader.class);
        Mock m2 = mock(CacheLoader.class);
        loader1 = (CacheLoader<Integer, String>) m1.proxy();
        loader2 = (CacheLoader<Integer, String>) m2.proxy();

        m1.expects(once()).method("load").with(eq(1),same(EMPTY_MAP)).will(returnValue("A"));

        m1.expects(once()).method("load").with(eq(2),same(EMPTY_MAP)).will(returnValue(null));
        m2.expects(once()).method("load").with(eq(2),same(EMPTY_MAP)).will(returnValue("B"));

        m1.expects(once()).method("load").with(eq(3),same(EMPTY_MAP)).will(returnValue(null));
        m2.expects(once()).method("load").with(eq(3),same(EMPTY_MAP)).will(returnValue(null));
        CacheLoaders.CompositeCacheLoader<Integer, String> ccl = new CacheLoaders.CompositeCacheLoader<Integer, String>(
                loader1, loader2);
        assertEquals("A", ccl.load(1, EMPTY_MAP));
        assertEquals("B", ccl.load(2, EMPTY_MAP));
        assertEquals(null, ccl.load(3, EMPTY_MAP));
    }

//    @SuppressWarnings("unchecked")
//    public void testLoadAll() throws Exception {
//        Mock m1 = mock(CacheLoader.class);
//        Mock m2 = mock(CacheLoader.class);
//        loader1 = (CacheLoader<Integer, String>) m1.proxy();
//        loader2 = (CacheLoader<Integer, String>) m2.proxy();
//        HashMap hm1 = new HashMap();
//        hm1.put(M1.getKey(), M1.getValue());
//        hm1.put(M2.getKey(), null);
//        hm1.put(M3.getKey(), null);
//        m1.expects(once()).method("loadAll")
//                .with(eq(new HashSet(Arrays.asList(1, 2, 3)))).will(returnValue(hm1));
//
//        HashMap hm2 = new HashMap();
//        hm2.put(M2.getKey(), M2.getValue());
//        hm2.put(M3.getKey(), null);
//        m2.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(2, 3))))
//                .will(returnValue(hm2));
//
//        Map.Entry<Integer, String> M3NULL = newEntry(3, null);
//
//        CompositeCacheLoader ccl = new CompositeCacheLoader(loader1, loader2);
//        // CompositeCacheLoader ccl = new CompositeCacheLoader(loader1);
//        // ccl.loadAll(new HashSet(Arrays.asList(1, 2, 3)));
//
//        assertEquals(asMap(M1, M2, M3NULL), ccl.loadAll(createMap(new HashSet(Arrays
//                .asList(1, 2, 3)))));
//        m1.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(1))))
//                .will(returnValue(asMap(M1)));
//        assertEquals(asMap(M1), ccl.loadAll(createMap(new HashSet(Arrays.asList(1)))));
//
//    }
//
//    public void testExceptions() throws Exception {
//        Mock m1 = mock(CacheLoader.class);
//        Composite.cause = new Exception();
//        Mock m2 = mock(CacheLoader.class);
//        loader1 = (CacheLoader<Integer, String>) m1.proxy();
//        loader2 = (CacheLoader<Integer, String>) m2.proxy();
//        CompositeCacheLoader ccl = new CompositeCacheLoader(loader1, loader2);
//        m1.expects(once()).method("load").with(eq(4),same(EMPTY_MAP)).will(
//                throwException(Composite.cause));
//        try {
//            ccl.load(4, EMPTY_MAP);
//            shouldThrow();
//        } catch (Exception e) { /* okay */
//            assertEquals(Composite.cause, e);
//        }
//
//        m1.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(1))))
//                .will(throwException(Composite.cause));
//
//        try {
//            ccl.loadAll(createMap(1));
//            shouldThrow();
//        } catch (Exception e) { /* okay */
//            assertEquals(Composite.cause, e);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    public void testHandleException() throws Exception {
//        Mock m1 = mock(CacheLoader.class, "M1");
//        Mock m2 = mock(CacheLoader.class);
//        Composite c = new Composite((CacheLoader) m1.proxy(), (CacheLoader) m2.proxy());
//        c.cause = new Exception();
//        c.keys = new HashSet(Arrays.asList(4, 3, 5));
//        c.loader = (CacheLoader) m1.proxy();
//        m1.expects(once()).method("load").with(eq(4),same(EMPTY_MAP)).will(
//                throwException(Composite.cause));
//        assertEquals("foo", c.load(4, EMPTY_MAP));
//
//        /* LOAD ALl */
//        Map m = new HashMap();
//        m.put(5, null);
//        m.put(3, null);
//
//        m1.expects(once()).method("loadAll").with(eq(Composite.keys)).will(
//                throwException(Composite.cause));
//        m2.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(5, 3))))
//                .will(returnValue(m));
//        Map result = c.loadAll(createMap(Composite.keys));
//        assertEquals(Composite.m, result);
//    }

    @SuppressWarnings("serial")
    static class Composite extends CacheLoaders.CompositeCacheLoader<Integer, String> {

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
                Map<? extends Integer, AttributeMap> keys, Exception cause)
                throws Exception {
            assertEquals(Composite.loader, loader);
            assertEquals(Composite.keys, keys);
            assertEquals(Composite.cause, cause);
            return m;
        }

        /**
         * @see org.coconut.cache.util.CompositeCacheLoader#loadingFailed(org.coconut.cache.CacheLoader,
         *      java.lang.Object, java.lang.Exception)
         */
        @Override
        protected String loadFailed(CacheLoader<Integer, String> loader, Integer key,
                Exception cause) throws Exception {
            assertEquals(Composite.loader, loader);
            assertEquals(Composite.keys.iterator().next(), key);
            assertEquals(Composite.cause, cause);
            return "foo";
        }

    }
}

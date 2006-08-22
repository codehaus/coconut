/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.coconut.cache.CacheLoader;
import static org.coconut.test.CollectionUtils.*;
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
        m1.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(1, 2, 3)))).will(
                returnValue(hm1));

        HashMap hm2 = new HashMap();
        hm2.put(M2.getKey(), M2.getValue());
        hm2.put(M3.getKey(), null);
        m2.expects(once()).method("loadAll").with(eq(new HashSet(Arrays.asList(2, 3)))).will(
                returnValue(hm2));
        
        Map.Entry<Integer, String> M3NULL = newEntry(3, null);

        CompositeCacheLoader ccl = new CompositeCacheLoader(loader1, loader2);
        //CompositeCacheLoader ccl = new CompositeCacheLoader(loader1);
        //ccl.loadAll(new HashSet(Arrays.asList(1, 2, 3)));
       
        assertEquals(asMap(M1, M2, M3NULL), ccl.loadAll(new HashSet(Arrays.asList(1, 2, 3))));
    }
}

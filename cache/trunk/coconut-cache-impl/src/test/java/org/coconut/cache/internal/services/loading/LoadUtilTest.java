/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.services.loading;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_KEY_NULL;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.asMap;
import static org.coconut.test.CollectionUtils.asSet;

import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.internal.service.loading.LoadUtil;
import org.coconut.cache.util.AbstractCacheLoader;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadUtilTest extends MockTestCase{

    public void testLoaderOrdinary() throws Exception {
        Mock mock = mock(CacheLoader.class);
        CacheLoader<Integer, CacheEntry<Integer, String>> cl = LoadUtil
                .toExtendedCacheLoader((CacheLoader) mock.proxy());
        assertFalse(cl instanceof AbstractCacheLoader);

        mock.expects(once()).method("load").with(eq(M1.getKey())).will(
                returnValue(M1.getValue()));
        CacheEntry<Integer, String> e = cl.load(M1.getKey());
        assertEquals(M1.getKey(), e.getKey());
        assertEquals(M1.getValue(), e.getValue());
        
        /* load null */
        mock.expects(once()).method("load").with(eq(M1.getKey())).will(returnValue(null));
        assertNull(cl.load(M1.getKey()));

        /* load all */
        mock.expects(once()).method("loadAll").with(eq(asSet(1, 2))).will(
                returnValue(asMap(M1, M2)));

        Map<Integer, CacheEntry<Integer, String>> m = cl.loadAll(asSet(1, 2));
        assertEquals(2, m.size());
        assertEquals(M1.getValue(), m.get(M1.getKey()).getValue());
        assertEquals(M2.getValue(), m.get(M2.getKey()).getValue());
        
        /* load all null*/
        
        mock.expects(once()).method("loadAll").with(eq(asSet(1, 2))).will(
                returnValue(asMap(M1_KEY_NULL, M2)));

        m = cl.loadAll(asSet(1, 2));
        assertEquals(2, m.size());
        assertEquals(null, m.get(M1.getKey()));
        assertEquals(M2.getValue(), m.get(M2.getKey()).getValue());
    }

    public void testLoaderAbstract() throws Exception {
        Mock mock = mock(AbstractCacheLoader.class);
        CacheLoader<Integer, CacheEntry<Integer, String>> cl = LoadUtil
                .toExtendedCacheLoader((CacheLoader) mock.proxy());
        assertTrue(cl instanceof AbstractCacheLoader);

        mock.expects(once()).method("load").with(eq(M1.getKey())).will(
                returnValue(M1.getValue()));
        CacheEntry<Integer, String> e = cl.load(M1.getKey());
        assertEquals(M1.getKey(), e.getKey());
        assertEquals(M1.getValue(), e.getValue());
        
        /* load null */
        mock.expects(once()).method("load").with(eq(M1.getKey())).will(returnValue(null));
        assertNull(cl.load(M1.getKey()));
    }

}

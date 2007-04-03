/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheException;
import org.coconut.cache.DummyCache;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.junit.After;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheSingletonTest {
    private final static String pck1 = "org/coconut/cache/util/TestConfig1.xml";

    private final static String pck2 = "org/coconut/cache/util/TestConfig2.xml";

    @After
    public void after() throws Exception {
        Field f = CacheSingleton.class.getDeclaredField("isInitialized");
        f.setAccessible(true);
        f.setBoolean(null, false);

        f = CacheSingleton.class.getDeclaredField("caches");
        f.setAccessible(true);
        ((Map) f.get(null)).clear();

        f = CacheSingleton.class.getDeclaredField("cacheInstance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test(expected = CacheException.class)
    public void testNoConfiguration1() {
        CacheSingleton.getSingleCache();
    }

    @Test(expected = CacheException.class)
    public void testNoConfiguration2() {
        CacheSingleton.getCache("foo");
    }

    @Test
    public void testSetGetDefault() {
        Cache c = MockTestCase.mockDummy(Cache.class);
        CacheSingleton.setSingleCache(c);
        assertEquals(c, CacheSingleton.getSingleCache());
    }

    @Test(expected = NullPointerException.class)
    public void testSetNPE() {
        CacheSingleton.setSingleCache(null);
    }

    @Test
    public void testSetAbstract() {
        MockTestCase mtc = new MockTestCase();
        Mock m = mtc.mock(Cache.class);
        m.stubs().method("getName").will(mtc.returnValue("foo"));
        CacheSingleton.setSingleCache((Cache<?, ?>) m.proxy());
        assertEquals(m.proxy(), CacheSingleton.getSingleCache());
        assertEquals(m.proxy(), CacheSingleton.getCache("foo"));
    }

    @Test(expected = NullPointerException.class)
    public void testAddNPE1() {
        CacheSingleton.addCache(MockTestCase.mockDummy(Cache.class), null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNPE2() {
        CacheSingleton.addCache(null, "foo");
    }

    @Test
    public void testAddCache() {
        MockTestCase mtc = new MockTestCase();
        Mock m = mtc.mock(Cache.class);
        m.stubs().method("getName").will(mtc.returnValue("foo"));
        CacheSingleton.addCache((Cache<?, ?>) m.proxy());
        assertTrue(CacheSingleton.hasCache("foo"));
        assertFalse(CacheSingleton.hasCache("foo1"));
        assertEquals(m.proxy(), CacheSingleton.getCache("foo"));
    }

    @Test
    public void testInitializeFromClasspathGetSingleCache() {
        assertEquals(CacheSingleton.DEFAULT_CACHE_RESSOURCE, CacheSingleton
                .getCacheRessourceLocation());
        CacheSingleton.setCacheRessourceLocation(pck1);
        assertEquals(pck1, CacheSingleton.getCacheRessourceLocation());
        Cache c = CacheSingleton.getSingleCache();
        assertTrue(c instanceof DummyCache);
        assertEquals("foobar", c.getName());
        assertEquals(c, CacheSingleton.getCache("foobar"));
        CacheSingleton.setCacheRessourceLocation(CacheSingleton.DEFAULT_CACHE_RESSOURCE);
    }

    @Test
    public void testInitializeFromClasspathGetCache() {
        assertEquals(CacheSingleton.DEFAULT_CACHE_RESSOURCE, CacheSingleton
                .getCacheRessourceLocation());
        CacheSingleton.setCacheRessourceLocation(pck1);
        assertEquals(pck1, CacheSingleton.getCacheRessourceLocation());
        Cache c =CacheSingleton.getCache("foobar");
        assertTrue(c instanceof DummyCache);
        assertEquals("foobar", c.getName());
        CacheSingleton.setCacheRessourceLocation(CacheSingleton.DEFAULT_CACHE_RESSOURCE);
    }
    @Test(expected = CacheException.class)
    public void testInitializeFromClasspathNoTypeInfo() {
        CacheSingleton.setCacheRessourceLocation(pck2);
        CacheSingleton.getSingleCache();
    }

    @Test(expected = CacheException.class)
    public void testUnknownCache() {
        CacheSingleton.setCacheRessourceLocation(pck1);
        CacheSingleton.getCache("foo1");

    }
}

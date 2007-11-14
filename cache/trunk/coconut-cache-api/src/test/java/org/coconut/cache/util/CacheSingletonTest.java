/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.DummyCache;
import org.coconut.test.MockTestCase;
import org.junit.After;
import org.junit.Test;

/**
 * Tests CacheSingleton.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheSingletonTest {
    private final static String PCK1 = "org/coconut/cache/util/CacheSingleton1.xml";

    private final static String PCK2 = "org/coconut/cache/util/CacheSingleton2.xml";

    @After
    public void after() throws Exception {
        Field f = CacheSingleton.class.getDeclaredField("status");
        f.setAccessible(true);
        f.setInt(null, 0);

        f = CacheSingleton.class.getDeclaredField("cacheInstance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test(expected = CacheException.class)
    public void testNoConfiguration1() {
        CacheSingleton.getCache();
    }

    @Test
    public void testSetGetDefault() {
        Cache<?, ?> c = MockTestCase.mockDummy(Cache.class);
        CacheSingleton.setCache(c);
        assertEquals(c, CacheSingleton.getCache());
    }

    public void testSetNull() {
        CacheSingleton.setCache(null);
        assertNull(CacheSingleton.getCache());
    }

    @Test
    public void testSetAbstract() {
        Cache c = new DummyCache(CacheConfiguration.create("foo"));
        CacheSingleton.setCache(c);
        assertSame(c, CacheSingleton.getCache());
    }

    @Test
    public void testInitializeFromClasspathGetSingleCache() {
        assertEquals(CacheSingleton.DEFAULT_CACHE_RESSOURCE, CacheSingleton
                .getCacheRessourceLocation());
        CacheSingleton.setCacheRessourceLocation(PCK1);
        assertEquals(PCK1, CacheSingleton.getCacheRessourceLocation());
        Cache<?, ?> c = CacheSingleton.getCache();
        assertTrue(c instanceof DummyCache);
        assertEquals("foobar", c.getName());
        CacheSingleton.setCacheRessourceLocation(CacheSingleton.DEFAULT_CACHE_RESSOURCE);
    }

    @Test(expected = CacheException.class)
    public void testInitializeFromClasspathNoTypeInfo() {
        CacheSingleton.setCacheRessourceLocation(PCK2);
        CacheSingleton.getCache();
    }
}

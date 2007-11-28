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
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
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

    private final static String PCK3 = "org/coconut/cache/util/CacheSingletonIllegal.xml";

    @After
    public void after() throws Exception {
        Field f = CacheSingleton.class.getDeclaredField("isTerminated");
        f.setAccessible(true);
        f.setBoolean(null, false);
        f = CacheSingleton.class.getDeclaredField("cacheInstance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test(expected = CacheException.class)
    public void noConfiguration() {
        CacheSingleton.getCache();
    }

    @Test(expected = IllegalStateException.class)
    public void setNull() {
        CacheSingleton.setCache(null);//
        CacheSingleton.getCache();
    }

    @Test
    public void shutdownAndClearCache() {
        CacheSingleton.shutdownAndClearCache();// ignored
        final Mockery context = new JUnit4Mockery();
        final Cache cache = context.mock(Cache.class);
        context.checking(new Expectations() {
            {
                one(cache).shutdown();
            }
        });
        CacheSingleton.setCache(cache);
        CacheSingleton.shutdownAndClearCache();// ignored
        context.assertIsSatisfied();

        try {
            CacheSingleton.getCache();
            throw new AssertionError("should fail");
        } catch (IllegalStateException ok) {}
    }

    @Test(expected = IllegalStateException.class)
    public void setNull1() {
        Cache<?, ?> c = MockTestCase.mockDummy(Cache.class);
        CacheSingleton.setCache(c);
        CacheSingleton.getCache();// ok
        CacheSingleton.setCache(null);
        CacheSingleton.getCache();
    }

    @Test
    public void illegalConfiguration() {
        String current = CacheSingleton.getCacheRessourceLocation();
        CacheException e = null;
        try {
            CacheSingleton.setCacheRessourceLocation(PCK3);
            try {
                CacheSingleton.getCache();
                throw new AssertionError("should fail");
            } catch (CacheException ok) {
                e = ok;
                assertTrue(ok.getCause() instanceof ClassNotFoundException);
            }
            try {
                CacheSingleton.getCache();
                throw new AssertionError("should fail");
            } catch (CacheException ok) {
                assertSame(e, ok);
            }
        } finally {
            CacheSingleton.setCacheRessourceLocation(current);
        }
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

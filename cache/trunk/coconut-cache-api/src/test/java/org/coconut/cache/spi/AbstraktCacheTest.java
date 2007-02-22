/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.core.Clock;
import org.coconut.test.CollectionUtils;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstraktCacheTest {

    TestCache t;

    @Before
    public void setup() {
        t = new TestCache();
        t.m.put(1, "a");
        t.m.put(2, "b");
    }

    @Test
    public void testNoop() {
        t.evict();
        t.resetStatistics();
        t.preStart();
        t.shutdown();
    }

    @Test
    public void testConfiguration() {
        CacheConfiguration cc = CacheConfiguration.create();
        Clock c = new Clock.DeterministicClock();
        CacheErrorHandler ceh = new CacheErrorHandler();
        cc.setClock(c);
        cc.setErrorHandler(ceh);
        t = new TestCache(cc);
        assertEquals(c, t.getClock());
        assertEquals(ceh, t.getErrorHandler());
    }

    @Test(expected = NullPointerException.class)
    public void testConfigurationNPE() {
        new TestCache(null);
    }

    @Test
    public void testContainsKey() {
        assertTrue(t.containsKey(1));
        assertFalse(t.containsKey(3));
    }

    @Test
    public void testContainsValue() {
        assertTrue(t.containsValue("a"));
        assertFalse(t.containsValue("c"));
    }

    @Test(expected = NullPointerException.class)
    public void testContainsValueNPE() {
        t.containsValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetAllNPE() {
        t.getAll(null);
    }

    @Test
    public void testGetAll() {
        Map m = t.getAll(Arrays.asList(1, 2, 3));
        assertTrue(m.containsValue("a"));
        assertTrue(m.containsValue("b"));
        assertFalse(m.containsValue("c"));
    }

    @Test
    public void testGetHitStat() {
        assertEquals(CacheUtil.newImmutableHitStat(0, 0), t.getHitStat());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLoadUOE() {
        t.load("");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLoadAllUOE() {
        t.loadAll(Arrays.asList(""));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutEntryUOE() {
        t.putEntry(MockTestCase.mockDummy(CacheEntry.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutEntriesUOE() {
        t.putEntries(Arrays.asList(MockTestCase.mockDummy(CacheEntry.class)));
    }

    @Test
    public void testPutIAE() {
        t.put(3, "c", -1, TimeUnit.SECONDS);
        assertEquals("c", t.m.get(3));
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllNPE1() {
        t.putAll(null);
    }

    @Test
    public void testPutAllNPE2() {
        t.putAll((Map) CollectionUtils.M1_TO_M5_MAP, 0, TimeUnit.SECONDS);
        assertEquals(5, t.m.size());
    }

    @Test
    public void testPutIfAbsent1() {
        assertEquals("b", t.putIfAbsent(2, "d"));
        assertNull(t.putIfAbsent(3, "c"));
        assertEquals("c", t.m.get(3));
    }

    @Test(expected = NullPointerException.class)
    public void testPutIfAbsentNPE() {
        t.putIfAbsent("t", null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNPE() {
        t.remove("t", null);
    }

    @Test
    public void testRemove() {
        assertTrue(t.remove(1, "a"));
        assertFalse(t.remove(1, "b"));
        assertFalse(t.remove(3, "a"));
    }

    @Test
    public void testReplace1() {
        assertEquals("a", t.replace(1, "b"));
        assertEquals("b", t.m.get(1));
        assertNull(t.replace(3, "c"));
    }

    @Test
    public void testReplace2() {
        assertTrue("a", t.replace(1, "a", "b"));
        assertEquals("b", t.m.get(1));
        assertFalse(t.replace(2, "c", "d"));
        assertFalse(t.replace(6, "c", "d"));
    }

    @Test(expected = NullPointerException.class)
    public void testReplaceNPE1() {
        t.replace("t", null);
    }

    @Test(expected = NullPointerException.class)
    public void testReplaceNPE2() {
        t.replace("t", "d", null);
    }

    @Test(expected = NullPointerException.class)
    public void testReplaceNPE3() {
        t.replace("t", null, "g");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTrimToSize() {
        t.trimToSize(34);
    }

    @Test
    public void testToString() {
        assertTrue(t.toString().contains("!#!"));
    }

    static class TestCache extends AbstractCache {

        Map m = new HashMap();

        /**
         * 
         */
        public TestCache() {
            super();
            // TODO Auto-generated constructor stub
        }

        /**
         * @param configuration
         */
        public TestCache(CacheConfiguration configuration) {
            super(configuration);
            // TODO Auto-generated constructor stub
        }

        /**
         * @see java.util.AbstractMap#entrySet()
         */
        @Override
        public Set entrySet() {
            return m.entrySet();
        }

        /**
         * @see org.coconut.cache.spi.AbstractCache#toString0(java.lang.StringBuilder)
         */
        @Override
        protected void toString0(StringBuilder buf) {
            buf.append("!#!");
            super.toString0(buf);
        }

        /**
         * @see org.coconut.cache.Cache#getEntry(java.lang.Object)
         */
        public CacheEntry getEntry(Object key) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see org.coconut.cache.Cache#peek(java.lang.Object)
         */
        public Object peek(Object key) {
            return m.get(key);
        }

        /**
         * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
         */
        public CacheEntry peekEntry(Object key) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see org.coconut.cache.Cache#put(java.lang.Object, java.lang.Object,
         *      long, java.util.concurrent.TimeUnit)
         */
        public Object put(Object key, Object value, long timeout, TimeUnit unit) {
            return m.put(key, value);
        }
    }

}

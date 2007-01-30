/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.core.Clock;
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
    }

    @Test
    public void testNoop() {
        t.evict();
        t.resetStatistics();
        t.start();
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

    @Test(expected = UnsupportedOperationException.class)
    public void testEventBusUOE() {
        t.getEventBus();
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
        t.putEntries(Arrays.asList());
    }

    @Test(expected = NullPointerException.class)
    public void testPutNPE1() {
        t.put(null, "");
    }

    @Test(expected = NullPointerException.class)
    public void testPutNPE2() {
        t.put("", null);
    }

    @Test(expected = NullPointerException.class)
    public void testPutNPE3() {
        t.put("", "", 0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutIAE() {
        t.put("", "", -1, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllNPE1() {
        t.putAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllNPE2() {
        t.putAll(Collections.EMPTY_MAP, 0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAllIAE() {
        t.putAll(Collections.EMPTY_MAP, -1, TimeUnit.SECONDS);
    }
    
    @Test
    public void testToString() {
        assertTrue(t.toString().contains("!#!"));
    }

    static class TestCache extends AbstractCache {

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
         * @see org.coconut.cache.spi.AbstractCache#put0(java.lang.Object,
         *      java.lang.Object, long)
         */
        @Override
        protected Object put(Object key, Object value, long expirationTime) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see org.coconut.cache.spi.AbstractCache#putAll0(java.util.Map, long)
         */
        @Override
        protected void putAll(Map t, long expirationTime) {
            // TODO Auto-generated method stub

        }

        /**
         * @see org.coconut.cache.spi.AbstractCache#trimToSize(int)
         */
        @Override
        public void trimToSize(int newSize) {
            // TODO Auto-generated method stub

        }

        /**
         * @see java.util.AbstractMap#entrySet()
         */
        @Override
        public Set entrySet() {
            return new HashMap().entrySet();
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
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
         */
        public CacheEntry peekEntry(Object key) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}

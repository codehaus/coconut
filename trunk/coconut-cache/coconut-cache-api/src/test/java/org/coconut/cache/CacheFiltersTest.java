/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import static org.coconut.filter.ComparisonFilters.equal;

import java.util.Arrays;

import org.coconut.filter.Filter;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheFiltersCacheFilterTest.java 203 2007-01-25 19:43:03Z
 *          kasper $
 */
@SuppressWarnings("unchecked")
public class CacheFiltersTest extends MockTestCase {

    public void testCacheFilter() {
        Mock mock = mock(CacheEvent.class);
        Cache c = (Cache) mock(Cache.class).proxy();
        Filter f = equal(c);
        mock.expects(once()).method("getCache").will(returnValue(c));
        Filter<CacheEvent> filter = CacheFilters.cacheFilter(f);
        assertTrue(filter.accept((CacheEvent) mock.proxy()));
    }

    public void testCacheEqualsFilter() {
        Mock mock = mock(CacheEvent.class);
        Mock mock2 = mock(CacheEvent.class);
        Cache c = (Cache) mock(Cache.class).proxy();
        Cache c2 = (Cache) mock(Cache.class).proxy();
        mock.expects(once()).method("getCache").will(returnValue(c));
        mock2.expects(once()).method("getCache").will(returnValue(c2));
        Filter<CacheEvent> f = CacheFilters.cacheEqualsFilter(c);
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertFalse(f.accept((CacheEvent) mock2.proxy()));
    }

    public void testAcceptNull() {
        Cache<Integer, Integer> c = (Cache) mock(Cache.class).proxy();
        try {
            CacheFilters.cacheFilter(equal(c)).accept(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testNotNull2() {
        try {
            CacheFilters.cacheFilter(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testNameFilter() {
        Mock mock = mock(CacheEvent.class);
        Filter<String> f = equal("TT");
        mock.expects(once()).method("getName").will(returnValue("TT"));
        Filter<CacheEvent<Integer, String>> filter = CacheFilters.cacheName(f);
        assertTrue(filter.accept((CacheEvent) mock.proxy()));
    }

    public void testNameEqualsFilter() {
        Mock mock = mock(CacheEvent.class);
        Mock mock2 = mock(CacheEvent.class);
        mock.expects(once()).method("getName").will(returnValue("T1"));
        mock2.expects(once()).method("getName").will(returnValue("T2"));
        Filter<CacheEvent<Integer, String>> f = CacheFilters.cacheName(equal("T1"));
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertFalse(f.accept((CacheEvent) mock2.proxy()));
    }

    public void testNotNull() {
        try {
            CacheFilters.cacheName(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testKeyEqualsFilter() {
        Mock mock = mock(CacheEntryEvent.class);
        Mock mock2 = mock(CacheEntryEvent.class);
        mock.expects(once()).method("getKey").will(returnValue(1));
        mock2.expects(once()).method("getKey").will(returnValue(2));
        Filter<CacheEvent<Integer, String>> f = CacheFilters.keyEqualsFilter(1);
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertFalse(f.accept((CacheEvent) mock2.proxy()));
    }

    public void testAnyKeyEqualsFilter() {
        Mock mock = mock(CacheEntryEvent.class);
        Mock mock2 = mock(CacheEntryEvent.class);
        Mock mock3 = mock(CacheEntryEvent.class);
        mock.stubs().method("getKey").will(returnValue(1));
        mock2.stubs().method("getKey").will(returnValue(2));
        mock3.stubs().method("getKey").will(returnValue(3));
        Filter<CacheEvent<Integer, String>> f = CacheFilters.anyKeyEquals(1, 2);
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertTrue(f.accept((CacheEvent) mock2.proxy()));
        assertFalse(f.accept((CacheEvent) mock3.proxy()));
    }
    
    public void testAnyKeyInCollectionEqualsFilter() {
        Mock mock = mock(CacheEntryEvent.class);
        Mock mock2 = mock(CacheEntryEvent.class);
        Mock mock3 = mock(CacheEntryEvent.class);
        mock.stubs().method("getKey").will(returnValue(1));
        mock2.stubs().method("getKey").will(returnValue(2));
        mock3.stubs().method("getKey").will(returnValue(3));
        Filter<CacheEvent<Integer, String>> f = CacheFilters.anyKeyInCollection(Arrays.asList(1, 2));
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertTrue(f.accept((CacheEvent) mock2.proxy()));
        assertFalse(f.accept((CacheEvent) mock3.proxy()));
    }
    public void testValueEqualsFilter() {
        Mock mock = mock(CacheEntryEvent.class);
        Mock mock2 = mock(CacheEntryEvent.class);
        mock.expects(once()).method("getValue").will(returnValue("A"));
        mock2.expects(once()).method("getValue").will(returnValue("B"));
        Filter<CacheEvent<Integer, String>> f = CacheFilters.valueEqualsFilter("A");
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertFalse(f.accept((CacheEvent) mock2.proxy()));
    }
    
    public void testAnyValueEqualsFilter() {
        Mock mock = mock(CacheEntryEvent.class);
        Mock mock2 = mock(CacheEntryEvent.class);
        Mock mock3 = mock(CacheEntryEvent.class);
        mock.stubs().method("getValue").will(returnValue("A"));
        mock2.stubs().method("getValue").will(returnValue("B"));
        mock3.stubs().method("getValue").will(returnValue("C"));
        Filter<CacheEvent<Integer, String>> f = CacheFilters.anyValueEquals("A", "B");
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertTrue(f.accept((CacheEvent) mock2.proxy()));
        assertFalse(f.accept((CacheEvent) mock3.proxy()));
    }
    
    public void testAnyValueInCollectionFilter() {
        Mock mock = mock(CacheEntryEvent.class);
        Mock mock2 = mock(CacheEntryEvent.class);
        Mock mock3 = mock(CacheEntryEvent.class);
        mock.stubs().method("getValue").will(returnValue("A"));
        mock2.stubs().method("getValue").will(returnValue("B"));
        mock3.stubs().method("getValue").will(returnValue("C"));
        Filter<CacheEvent<Integer, String>> f = CacheFilters.anyValueInCollection(Arrays.asList("A", "B"));
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertTrue(f.accept((CacheEvent) mock2.proxy()));
        assertFalse(f.accept((CacheEvent) mock3.proxy()));
    }
}

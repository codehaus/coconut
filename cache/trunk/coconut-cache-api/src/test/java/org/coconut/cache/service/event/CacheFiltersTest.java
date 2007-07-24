/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.event;

import org.coconut.cache.Cache;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class CacheFiltersTest extends MockTestCase {

    public void testCacheFilter() {
        Mock mock = mock(CacheEvent.class);
        Cache c = (Cache) mock(Cache.class).proxy();
        Filter f = Filters.equal(c);
        mock.expects(once()).method("getCache").will(returnValue(c));
        Filter<CacheEvent> filter = CacheEventFilters.cacheFilter(f);
        assertTrue(filter.accept((CacheEvent) mock.proxy()));
    }

    public void testCacheEqualsFilter() {
        Mock mock = mock(CacheEvent.class);
        Mock mock2 = mock(CacheEvent.class);
        Cache c = (Cache) mock(Cache.class).proxy();
        Cache c2 = (Cache) mock(Cache.class).proxy();
        mock.expects(once()).method("getCache").will(returnValue(c));
        mock2.expects(once()).method("getCache").will(returnValue(c2));
        Filter<CacheEvent> f = CacheEventFilters.cacheEqualsFilter(c);
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertFalse(f.accept((CacheEvent) mock2.proxy()));
    }

    public void testAcceptNull() {
        Cache<Integer, Integer> c = (Cache) mock(Cache.class).proxy();
        try {
            CacheEventFilters.cacheFilter(Filters.equal(c)).accept(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {/* ignore */
        }
    }

    public void testNotNull2() {
        try {
            CacheEventFilters.cacheFilter(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {/* ignore */
        }
    }

    public void testNameFilter() {
        Mock mock = mock(CacheEvent.class);
        Filter<String> f = Filters.equal("TT");
        mock.expects(once()).method("getName").will(returnValue("TT"));
        Filter<CacheEvent<Integer, String>> filter = CacheEventFilters.cacheName(f);
        assertTrue(filter.accept((CacheEvent) mock.proxy()));
    }

    public void testNameEqualsFilter() {
        Mock mock = mock(CacheEvent.class);
        Mock mock2 = mock(CacheEvent.class);
        mock.expects(once()).method("getName").will(returnValue("T1"));
        mock2.expects(once()).method("getName").will(returnValue("T2"));
        Filter<CacheEvent<Integer, String>> f = CacheEventFilters.cacheName(Filters.equal("T1"));
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertFalse(f.accept((CacheEvent) mock2.proxy()));
    }

    public void testNotNull() {
        try {
            CacheEventFilters.cacheName(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {/* ignore */
        }
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.event;

import org.coconut.cache.Cache;
import org.coconut.predicate.Predicate;
import org.coconut.predicate.Predicates;
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
        Predicate f = Predicates.equal(c);
        mock.expects(once()).method("getCache").will(returnValue(c));
        Predicate<CacheEvent> filter = CacheEventFilters.cacheFilter(f);
        assertTrue(filter.evaluate((CacheEvent) mock.proxy()));
    }

    public void testCacheEqualsFilter() {
        Mock mock = mock(CacheEvent.class);
        Mock mock2 = mock(CacheEvent.class);
        Cache c = (Cache) mock(Cache.class).proxy();
        Cache c2 = (Cache) mock(Cache.class).proxy();
        mock.expects(once()).method("getCache").will(returnValue(c));
        mock2.expects(once()).method("getCache").will(returnValue(c2));
        Predicate<CacheEvent> f = CacheEventFilters.cacheSameFilter(c);
        assertTrue(f.evaluate((CacheEvent) mock.proxy()));
        assertFalse(f.evaluate((CacheEvent) mock2.proxy()));
    }

    public void testAcceptNull() {
        Cache<Integer, Integer> c = (Cache) mock(Cache.class).proxy();
        try {
            CacheEventFilters.cacheFilter(Predicates.equal(c)).evaluate(null);
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
        Predicate<String> f = Predicates.equal("TT");
        mock.expects(once()).method("getName").will(returnValue("TT"));
        Predicate<CacheEvent<Integer, String>> filter = CacheEventFilters.cacheName(f);
        assertTrue(filter.evaluate((CacheEvent) mock.proxy()));
    }

    public void testNameEqualsFilter() {
        Mock mock = mock(CacheEvent.class);
        Mock mock2 = mock(CacheEvent.class);
        mock.expects(once()).method("getName").will(returnValue("T1"));
        mock2.expects(once()).method("getName").will(returnValue("T2"));
        Predicate<CacheEvent<Integer, String>> f = CacheEventFilters.cacheName(Predicates.equal("T1"));
        assertTrue(f.evaluate((CacheEvent) mock.proxy()));
        assertFalse(f.evaluate((CacheEvent) mock2.proxy()));
    }

    public void testNotNull() {
        try {
            CacheEventFilters.cacheName(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {/* ignore */
        }
    }
}

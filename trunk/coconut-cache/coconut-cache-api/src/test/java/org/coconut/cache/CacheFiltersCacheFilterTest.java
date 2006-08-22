/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import static org.coconut.filter.ComparisonFilters.equal;

import org.coconut.filter.Filter;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class CacheFiltersCacheFilterTest extends MockTestCase {

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
        } catch (NullPointerException npe) {
            return; // success
        }
        fail("Did not fail with NullPointerException");
    }

    public void testNotNull() {
        try {
            CacheFilters.cacheFilter(null);
        } catch (NullPointerException npe) {
            return; // success
        }
        fail("Did not fail with NullPointerException");
    }

}
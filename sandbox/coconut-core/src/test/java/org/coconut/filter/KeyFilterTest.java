/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package coconut.cache.filter;

import coconut.cache.AbstractCacheTestCase;
import coconut.cache.CacheFilters;
import coconut.cache.CacheItemEvent;
import coconut.filter.ComparisonFilters;
import coconut.filter.Filter;
import coconut.filter.reflect.CollectionExtractor.KeyFilter;

import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class KeyFilterTest extends AbstractCacheTestCase {

    public void testKeyFilter() {
        Mock mock = mock(CacheItemEvent.class);
        Filter f = ComparisonFilters.equal(zero);
        mock.expects(once()).method("getKey").will(returnValue(0));
        KeyFilter filter = CacheFilters.keyFilter(f);
        assertEquals(f, filter.getFilter());
        assertTrue(filter.accept((CacheItemEvent) mock.proxy()));
    }

    public void testKeyEqualsFilter() {
        Mock mock = mock(CacheItemEvent.class);
        Mock mock2 = mock(CacheItemEvent.class);
        mock.expects(once()).method("getKey").will(returnValue(0));
        mock2.expects(once()).method("getKey").will(returnValue(1));
        Filter f = CacheFilters.keyEqualsFilter(zero);
        assertTrue(f.accept( mock.proxy()));
        assertFalse(f.accept( mock2.proxy()));
    }

    public void testAnyKeyEquals() {
        Mock mock = mock(CacheItemEvent.class);
        Mock mock2 = mock(CacheItemEvent.class);
        Mock mock3 = mock(CacheItemEvent.class);
        mock.expects(once()).method("getKey").will(returnValue(0));
        mock2.expects(once()).method("getKey").will(returnValue(1));
        mock3.expects(once()).method("getKey").will(returnValue(2));
        Filter f = CacheFilters.anyKeyEquals(zero, two);
        assertTrue(f.accept( mock.proxy()));
        assertFalse(f.accept( mock2.proxy()));
        assertTrue(f.accept( mock3.proxy()));
    }

    public void testAcceptNull() {
        try {
            CacheFilters.keyFilter(ComparisonFilters.equal(zero)).accept(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testNotNull() {
        try {
            CacheFilters.keyFilter(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }
}
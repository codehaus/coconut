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
import coconut.filter.Filters;
import coconut.filter.reflect.CollectionExtractor;
import coconut.filter.reflect.CollectionExtractor.ValueFilter;

import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class ValueFilterTest extends AbstractCacheTestCase {

    public void testValueFilter() {
        Mock mock = mock(CacheItemEvent.class);
        Filter f = ComparisonFilters.equal(zero);
        mock.expects(once()).method("getValue").will(returnValue(0));
        CollectionExtractor.ValueFilter filter = CacheFilters.valueFilter(f);
        assertEquals(f, filter.getFilter());
        assertTrue(filter.accept((CacheItemEvent) mock.proxy()));
    }

    public void testValueEqualsFilter() {
        Mock mock = mock(CacheItemEvent.class);
        Mock mock2 = mock(CacheItemEvent.class);
        mock.expects(once()).method("getValue").will(returnValue(0));
        mock2.expects(once()).method("getValue").will(returnValue(1));
        Filter f = CacheFilters.valueEqualsFilter(zero);
        assertTrue(f.accept(mock.proxy()));
        assertFalse(f.accept(mock2.proxy()));
    }

    public void testAnyValueEquals() {
        Mock mock = mock(CacheItemEvent.class);
        Mock mock2 = mock(CacheItemEvent.class);
        Mock mock3 = mock(CacheItemEvent.class);
        mock.expects(once()).method("getValue").will(returnValue(0));
        mock2.expects(once()).method("getValue").will(returnValue(1));
        mock3.expects(once()).method("getValue").will(returnValue(2));
        ValueFilter<Integer, Integer> f = CacheFilters
                .anyValueEquals(zero, two);
        assertTrue(f.accept((CacheItemEvent) mock.proxy()));
        assertFalse(f.accept((CacheItemEvent) mock2.proxy()));
        assertTrue(f.accept((CacheItemEvent) mock3.proxy()));
    }

    public void testAcceptNull() {
        try {
            CacheFilters.valueFilter(ComparisonFilters.equal(zero)).accept(null);
        } catch (NullPointerException npe) {
            return; // success
        }
        fail("Did not fail with NullPointerException");
    }

    public void testNotNull() {
        try {
            CacheFilters.valueFilter(null);
        } catch (NullPointerException npe) {
            return; // success
        }
        fail("Did not fail with NullPointerException");
    }

}
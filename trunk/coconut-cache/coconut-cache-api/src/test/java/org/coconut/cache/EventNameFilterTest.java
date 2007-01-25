/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
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
public class EventNameFilterTest extends MockTestCase {

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
        Filter<CacheEvent<Integer, String>> f = CacheFilters
                .cacheName(equal("T1"));
        assertTrue(f.accept((CacheEvent) mock.proxy()));
        assertFalse(f.accept((CacheEvent) mock2.proxy()));
    }

    public void testNotNull() {
        try {
            CacheFilters.cacheName(null);
        } catch (NullPointerException npe) {
            return; // success
        }
        fail("Did not fail with NullPointerException");
    }

}
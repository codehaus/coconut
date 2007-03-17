/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter;

import java.util.Arrays;
import java.util.Map;

import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CollectionFiltersTest extends MockTestCase {

    public void testKeyEqualsFilter() {
        Mock mock = mock(Map.Entry.class);
        Mock mock2 = mock(Map.Entry.class);
        mock.expects(once()).method("getKey").will(returnValue(1));
        mock2.expects(once()).method("getKey").will(returnValue(2));
        Filter<Map.Entry<Integer, String>> f = CollectionFilters.keyEqualsFilter(1);
        assertTrue(f.accept((Map.Entry) mock.proxy()));
        assertFalse(f.accept((Map.Entry) mock2.proxy()));
    }

    public void testAnyKeyEqualsFilter() {
        Mock mock = mock(Map.Entry.class);
        Mock mock2 = mock(Map.Entry.class);
        Mock mock3 = mock(Map.Entry.class);
        mock.stubs().method("getKey").will(returnValue(1));
        mock2.stubs().method("getKey").will(returnValue(2));
        mock3.stubs().method("getKey").will(returnValue(3));
        Filter<Map.Entry<Integer, String>> f = CollectionFilters.anyKeyEquals(1, 2);
        assertTrue(f.accept((Map.Entry) mock.proxy()));
        assertTrue(f.accept((Map.Entry) mock2.proxy()));
        assertFalse(f.accept((Map.Entry) mock3.proxy()));
    }

    public void testAnyKeyInCollectionEqualsFilter() {
        Mock mock = mock(Map.Entry.class);
        Mock mock2 = mock(Map.Entry.class);
        Mock mock3 = mock(Map.Entry.class);
        mock.stubs().method("getKey").will(returnValue(1));
        mock2.stubs().method("getKey").will(returnValue(2));
        mock3.stubs().method("getKey").will(returnValue(3));
        Filter<Map.Entry<Integer, String>> f = CollectionFilters
                .anyKeyInCollection(Arrays.asList(1, 2));
        assertTrue(f.accept((Map.Entry) mock.proxy()));
        assertTrue(f.accept((Map.Entry) mock2.proxy()));
        assertFalse(f.accept((Map.Entry) mock3.proxy()));
    }

    public void testValueEqualsFilter() {
        Mock mock = mock(Map.Entry.class);
        Mock mock2 = mock(Map.Entry.class);
        mock.expects(once()).method("getValue").will(returnValue("A"));
        mock2.expects(once()).method("getValue").will(returnValue("B"));
        Filter<Map.Entry<Integer, String>> f = CollectionFilters.valueEqualsFilter("A");
        assertTrue(f.accept((Map.Entry) mock.proxy()));
        assertFalse(f.accept((Map.Entry) mock2.proxy()));
    }

    public void testNullFilter() {
        Filter f = CollectionFilters.isNull();
        assertTrue(f.accept(null));
        assertFalse(f.accept(1));
        assertFalse(f.accept(f));
    }

    public void testAnyValueEqualsFilter() {
        Mock mock = mock(Map.Entry.class);
        Mock mock2 = mock(Map.Entry.class);
        Mock mock3 = mock(Map.Entry.class);
        mock.stubs().method("getValue").will(returnValue("A"));
        mock2.stubs().method("getValue").will(returnValue("B"));
        mock3.stubs().method("getValue").will(returnValue("C"));
        Filter<Map.Entry<Integer, String>> f = CollectionFilters.anyValueEquals("A", "B");
        assertTrue(f.accept((Map.Entry) mock.proxy()));
        assertTrue(f.accept((Map.Entry) mock2.proxy()));
        assertFalse(f.accept((Map.Entry) mock3.proxy()));
    }

    public void testAnyValueInCollectionFilter() {
        Mock mock = mock(Map.Entry.class);
        Mock mock2 = mock(Map.Entry.class);
        Mock mock3 = mock(Map.Entry.class);
        mock.stubs().method("getValue").will(returnValue("A"));
        mock2.stubs().method("getValue").will(returnValue("B"));
        mock3.stubs().method("getValue").will(returnValue("C"));
        Filter<Map.Entry<Integer, String>> f = CollectionFilters
                .anyValueInCollection(Arrays.asList("A", "B"));
        assertTrue(f.accept((Map.Entry) mock.proxy()));
        assertTrue(f.accept((Map.Entry) mock2.proxy()));
        assertFalse(f.accept((Map.Entry) mock3.proxy()));
    }

}

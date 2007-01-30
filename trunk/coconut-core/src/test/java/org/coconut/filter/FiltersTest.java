/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.Filters.AnyFilter;
import org.junit.Test;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: FiltersTest.java 36 2006-08-22 09:59:45Z kasper $
 */
public class FiltersTest {

    @Test 
    public void testAnyEqualsFilter() {
        AnyFilter<String> filter = Filters.anyEquals("1", "2");
        assertTrue(filter.accept("1"));
        assertTrue(filter.accept("2"));
        assertFalse(filter.accept("3"));
    }
    @Test 
    public void testFilterCollection() {
        Collection<String> c = new ArrayList<String>();
        c.add("1");
        c.add("2");
        c.add("3");
        c.add("4");
        c = CollectionFilters.filter(c, Filters.anyEquals("2", "3"));
        assertEquals(2, c.size());
        assertTrue(c.contains("2"));
        assertTrue(c.contains("3"));
    }
    @Test 
    public void testFilterList() {
        List<String> c = new ArrayList<String>();
        c.add("1");
        c.add("2");
        c.add("3");
        c.add("4");
        c = CollectionFilters.filterList(c, Filters.anyEquals("2", "3"));
        assertEquals(2, c.size());
        assertTrue(c.contains("2"));
        assertTrue(c.contains("3"));
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(FiltersTest.class);
    }
}

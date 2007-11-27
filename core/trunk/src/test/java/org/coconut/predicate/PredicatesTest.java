/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.coconut.predicate.Predicates.AnyPredicate;
import org.junit.Test;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class PredicatesTest {

    @Test 
    public void testAnyEqualsFilter() {
        AnyPredicate<String> filter = Predicates.anyEquals("1", "2");
        assertTrue(filter.evaluate("1"));
        assertTrue(filter.evaluate("2"));
        assertFalse(filter.evaluate("3"));
    }
    @Test 
    public void testFilterCollection() {
        Collection<String> c = new ArrayList<String>();
        c.add("1");
        c.add("2");
        c.add("3");
        c.add("4");
        c = CollectionPredicates.filter(c, Predicates.anyEquals("2", "3"));
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
        c = CollectionPredicates.filterList(c, Predicates.anyEquals("2", "3"));
        assertEquals(2, c.size());
        assertTrue(c.contains("2"));
        assertTrue(c.contains("3"));
    }

}

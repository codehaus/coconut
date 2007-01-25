/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.coconut.filter.ComparisonFilters.equal;
import static org.coconut.filter.LogicFilters.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: ComparisonFiltersEqualsTest.java 36 2006-08-22 09:59:45Z kasper $
 */
public class ComparisonFiltersEqualsTest{

    @Test
    public void testEquals() {
        assertEquals("1", equal("1").getObject());
        assertTrue(equal("1").accept("1"));
        assertFalse(equal("1").accept("2"));
        assertFalse(equal("1").accept(null));
        equal(TRUE).toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        equal(null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ComparisonFiltersEqualsTest.class);
    }
}
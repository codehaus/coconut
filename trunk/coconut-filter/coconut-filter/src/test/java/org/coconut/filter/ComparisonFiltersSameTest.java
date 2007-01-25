/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.coconut.filter.ComparisonFilters.same;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: ComparisonFiltersSameTest.java 36 2006-08-22 09:59:45Z kasper $
 */
public class ComparisonFiltersSameTest {

    @Test
    public void testEquals() {
        String o = "1";
        assertEquals("1", same("1").getObject());
        assertTrue(same(o).accept(o));
        assertFalse(same(new HashMap()).accept(new HashMap()));
        assertFalse(same("1").accept("2"));
        assertFalse(same("1").accept(null));
        same("1").toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        same(null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ComparisonFiltersSameTest.class);
    }
}
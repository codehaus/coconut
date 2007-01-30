/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: LogicFiltersAndTest.java 36 2006-08-22 09:59:45Z kasper $
 */
@SuppressWarnings("unchecked")
public class LogicFiltersAndTest {

    @Test
    public void testAnd() {
        assertTrue(Filters.and(Filters.TRUE, Filters.TRUE).accept(null));
        assertFalse(Filters.and(Filters.TRUE, Filters.FALSE).accept(null));
        assertFalse(Filters.and(Filters.FALSE, Filters.TRUE).accept(null));
        assertFalse(Filters.and(Filters.FALSE, Filters.FALSE).accept(null));
        assertSame(Filters.and(Filters.FALSE, Filters.TRUE).getLeftFilter(), Filters.FALSE);
        assertSame(Filters.and(Filters.FALSE, Filters.TRUE).getRightFilter(), Filters.TRUE);
        assertEquals(Filters.and(Filters.FALSE, Filters.TRUE).getFilters(), Arrays.asList(Filters.FALSE, Filters.TRUE));
        Filters.and(Filters.FALSE, Filters.FALSE).toString(); // check no exception
    }

    @Test
    public void testStrict() {
        assertTrue(Filters.and(Filters.TRUE, Filters.TRUE).isStrict());
        assertFalse(Filters.and(Filters.FALSE, Filters.TRUE, false).isStrict());
    }

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        Filters.and(null, Filters.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        Filters.and(Filters.TRUE, null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersAndTest.class);
    }
}

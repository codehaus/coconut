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
 * @version $Id: LogicFiltersOrTest.java 36 2006-08-22 09:59:45Z kasper $
 */
@SuppressWarnings("unchecked")
public class LogicFiltersOrTest  {

    @Test
    public void testOr() {
        assertTrue(Filters.or(Filters.TRUE, Filters.TRUE).accept(null));
        assertTrue(Filters.or(Filters.TRUE, Filters.FALSE).accept(null));
        assertTrue(Filters.or(Filters.FALSE, Filters.TRUE).accept(null));
        assertFalse(Filters.or(Filters.FALSE, Filters.FALSE).accept(null));
        assertSame(Filters.or(Filters.FALSE, Filters.TRUE).getLeftFilter(), Filters.FALSE);
        assertSame(Filters.or(Filters.FALSE, Filters.TRUE).getRightFilter(), Filters.TRUE);
        assertEquals(Filters.or(Filters.FALSE, Filters.TRUE).getFilters(), Arrays.asList(Filters.FALSE, Filters.TRUE));
        Filters.or(Filters.FALSE, Filters.FALSE).toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        Filters.or(null, Filters.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        Filters.or(Filters.TRUE, null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersOrTest.class);
    }
}

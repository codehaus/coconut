/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: LogicFiltersXorTest.java 36 2006-08-22 09:59:45Z kasper $
 */
@SuppressWarnings("unchecked")
public class LogicFiltersXorTest {

    @Test
    public void testXor() {
        assertFalse(Filters.xor(Filters.TRUE, Filters.TRUE).accept(null));
        assertTrue(Filters.xor(Filters.TRUE, Filters.FALSE).accept(null));
        assertTrue(Filters.xor(Filters.FALSE, Filters.TRUE).accept(null));
        assertFalse(Filters.xor(Filters.FALSE, Filters.FALSE).accept(null));
        assertSame(Filters.xor(Filters.FALSE, Filters.TRUE).getLeftFilter(), Filters.FALSE);
        assertSame(Filters.xor(Filters.FALSE, Filters.TRUE).getRightFilter(), Filters.TRUE);
        assertSame(Filters.xor(Filters.FALSE, Filters.TRUE).getFilters().get(0), Filters.FALSE);
        assertSame(Filters.xor(Filters.FALSE, Filters.TRUE).getFilters().get(1), Filters.TRUE);
        Filters.xor(Filters.FALSE, Filters.FALSE).toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        Filters.xor(null, Filters.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        Filters.xor(Filters.TRUE, null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersXorTest.class);
    }
}

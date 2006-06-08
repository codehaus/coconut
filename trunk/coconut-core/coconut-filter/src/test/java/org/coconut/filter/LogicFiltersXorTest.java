/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter;

import static org.coconut.filter.LogicFilters.FALSE;
import static org.coconut.filter.LogicFilters.TRUE;
import static org.coconut.filter.LogicFilters.xor;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class LogicFiltersXorTest {

    @Test
    public void testXor() {
        assertFalse(xor(TRUE, TRUE).accept(null));
        assertTrue(xor(TRUE, FALSE).accept(null));
        assertTrue(xor(FALSE, TRUE).accept(null));
        assertFalse(xor(FALSE, FALSE).accept(null));
        assertSame(xor(FALSE, TRUE).getLeftFilter(), FALSE);
        assertSame(xor(FALSE, TRUE).getRightFilter(), TRUE);
        assertSame(xor(FALSE, TRUE).getFilters().get(0), FALSE);
        assertSame(xor(FALSE, TRUE).getFilters().get(1), TRUE);
        xor(FALSE, FALSE).toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        xor(null, TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        xor(TRUE, null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersXorTest.class);
    }
}

/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter;

import static org.coconut.filter.LogicFilters.FALSE;
import static org.coconut.filter.LogicFilters.TRUE;
import static org.coconut.filter.LogicFilters.or;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class LogicFiltersOrTest {

    @Test
    public void testOr() {
        assertTrue(or(TRUE, TRUE).accept(null));
        assertTrue(or(TRUE, FALSE).accept(null));
        assertTrue(or(FALSE, TRUE).accept(null));
        assertFalse(or(FALSE, FALSE).accept(null));
        assertSame(or(FALSE, TRUE).getLeftFilter(), FALSE);
        assertSame(or(FALSE, TRUE).getRightFilter(), TRUE);
        assertEquals(or(FALSE, TRUE).getFilters(), Arrays.asList(FALSE, TRUE));
        or(FALSE, FALSE).toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        or(null, TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        or(TRUE, null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersOrTest.class);
    }
}

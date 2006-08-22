/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.coconut.filter.LogicFilters.FALSE;
import static org.coconut.filter.LogicFilters.TRUE;
import static org.coconut.filter.LogicFilters.and;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import junit.framework.JUnit4TestAdapter;

import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class LogicFiltersAndTest extends MavenDummyTest {

    @Test
    public void testAnd() {
        assertTrue(and(TRUE, TRUE).accept(null));
        assertFalse(and(TRUE, FALSE).accept(null));
        assertFalse(and(FALSE, TRUE).accept(null));
        assertFalse(and(FALSE, FALSE).accept(null));
        assertSame(and(FALSE, TRUE).getLeftFilter(), FALSE);
        assertSame(and(FALSE, TRUE).getRightFilter(), TRUE);
        assertEquals(and(FALSE, TRUE).getFilters(), Arrays.asList(FALSE, TRUE));
        and(FALSE, FALSE).toString(); // check no exception
    }

    @Test
    public void testStrict() {
        assertTrue(and(TRUE, TRUE).isStrict());
        assertFalse(and(FALSE, TRUE, false).isStrict());
    }

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        and(null, TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        and(TRUE, null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersAndTest.class);
    }
}

/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter;

import static org.coconut.filter.LogicFilters.FALSE;
import static org.coconut.filter.LogicFilters.TRUE;
import static org.coconut.filter.LogicFilters.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class LogicFiltersNotTest {

    @Test
    public void testNot() {
        assertFalse(not(TRUE).accept(null));
        assertTrue(not(FALSE).accept(null));
        assertEquals(not(FALSE).getFilter(), FALSE);
        assertEquals(not(FALSE).getFilters(), Collections.singletonList(FALSE));
        not(TRUE).toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNotNull() {
        not(null);
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersNotTest.class);
    }
}

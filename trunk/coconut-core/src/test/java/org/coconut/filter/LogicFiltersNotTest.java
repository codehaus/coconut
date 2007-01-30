/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: LogicFiltersNotTest.java 36 2006-08-22 09:59:45Z kasper $
 */
@SuppressWarnings("unchecked")
public class LogicFiltersNotTest {

    @Test
    public void testNot() {
        assertFalse(Filters.not(Filters.TRUE).accept(null));
        assertTrue(Filters.not(Filters.FALSE).accept(null));
        assertEquals(Filters.not(Filters.FALSE).getFilter(), Filters.FALSE);
        assertEquals(Filters.not(Filters.FALSE).getFilters(), Collections.singletonList(Filters.FALSE));
        Filters.not(Filters.TRUE).toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNotNull() {
        Filters.not(null);
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersNotTest.class);
    }
}

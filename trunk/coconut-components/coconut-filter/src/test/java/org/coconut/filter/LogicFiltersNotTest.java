/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
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

import org.coconut.test.MavenDummyTest;
import org.junit.Test;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: LogicFiltersNotTest.java 36 2006-08-22 09:59:45Z kasper $
 */
@SuppressWarnings("unchecked")
public class LogicFiltersNotTest extends MavenDummyTest {

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
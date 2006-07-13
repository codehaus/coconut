/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter;

import static org.coconut.filter.LogicFilters.TRUE;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class LogicFiltersTrueTest {

    @Test 
    public void testFilter() {
        assertTrue(TRUE.accept(null));
        assertTrue(TRUE.accept(this));
        TRUE.toString(); //does not fail
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersTrueTest.class);
    }
}

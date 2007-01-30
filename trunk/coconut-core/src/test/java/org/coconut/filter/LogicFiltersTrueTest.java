/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: LogicFiltersTrueTest.java 36 2006-08-22 09:59:45Z kasper $
 */
public class LogicFiltersTrueTest {

    @Test 
    public void testFilter() {
        assertTrue(Filters.TRUE.accept(null));
        assertTrue(Filters.TRUE.accept(this));
        Filters.TRUE.toString(); //does not fail
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersTrueTest.class);
    }
}

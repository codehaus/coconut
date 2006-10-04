/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.coconut.filter.LogicFilters.FALSE;
import static org.junit.Assert.assertFalse;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: LogicFiltersFalseTest.java 36 2006-08-22 09:59:45Z kasper $
 */
public class LogicFiltersFalseTest {

    @Test
    public void testFilter() {
        assertFalse(FALSE.accept(null));
        assertFalse(FALSE.accept(this));
        FALSE.toString(); // does not fail
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersFalseTest.class);
    }
}
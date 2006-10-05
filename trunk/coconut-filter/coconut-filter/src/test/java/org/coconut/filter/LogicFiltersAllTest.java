/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.coconut.filter.LogicFilters.FALSE;
import static org.coconut.filter.LogicFilters.TRUE;
import static org.coconut.filter.LogicFilters.all;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.LogicFilters.AllFilter;
import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: LogicFiltersAllTest.java 36 2006-08-22 09:59:45Z kasper $
 */
@SuppressWarnings("unchecked")
public class LogicFiltersAllTest extends MavenDummyTest {

    @Test
    public void testConstructor() {
        Filter[] f = new Filter[] { TRUE, FALSE, TRUE };
        AllFilter<?> filter = all(f);
        assertEquals(filter.getFilters().size(), f.length);
        assertEquals(filter.getFilters().get(0), f[0]);
        assertEquals(filter.getFilters().get(1), f[1]);
        assertEquals(filter.getFilters().get(2), f[2]);
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        Filter[] f = new Filter[] { TRUE, null, TRUE };
        all(f);
    }

    @Test
    public void testIterator() {
        Filter[] f = new Filter[] { TRUE, FALSE, TRUE };
        AllFilter<?> filter = all(f);
        int i = 0;
        for (Filter<?> f1 : filter) {
            if (i == 0 || i == 2) {
                assertSame(TRUE, f1);
            } else if (i == 1) {
                assertSame(FALSE, f1);
            } else {
                fail("too many elements");
            }
            i++;
        }
    }

    @Test
    public void testToString() {
        // just check that they don't throw exceptions
        Filter[] f = new Filter[] { TRUE, FALSE, TRUE };
        all(f).toString();
        all(new Filter[] {}).toString();
        all(new Filter[] { TRUE }).toString();
    }


    @Test
    public void testAllLogic() {
        assertTrue((all(TRUE)).accept(null));
        assertFalse(all(FALSE).accept(null));
        assertTrue(all(TRUE, TRUE).accept(null));
        assertFalse(all(TRUE, FALSE).accept(null));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersAllTest.class);
    }
}

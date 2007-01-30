/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.Filters.AllFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: LogicFiltersAllTest.java 36 2006-08-22 09:59:45Z kasper $
 */
@SuppressWarnings("unchecked")
public class LogicFiltersAllTest  {

    @Test
    public void testConstructor() {
        Filter[] f = new Filter[] { Filters.TRUE, Filters.FALSE, Filters.TRUE };
        AllFilter<?> filter = Filters.all(f);
        assertEquals(filter.getFilters().size(), f.length);
        assertEquals(filter.getFilters().get(0), f[0]);
        assertEquals(filter.getFilters().get(1), f[1]);
        assertEquals(filter.getFilters().get(2), f[2]);
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        Filter[] f = new Filter[] { Filters.TRUE, null, Filters.TRUE };
        Filters.all(f);
    }

    @Test
    public void testIterator() {
        Filter[] f = new Filter[] { Filters.TRUE, Filters.FALSE, Filters.TRUE };
        AllFilter<?> filter = Filters.all(f);
        int i = 0;
        for (Filter<?> f1 : filter) {
            if (i == 0 || i == 2) {
                assertSame(Filters.TRUE, f1);
            } else if (i == 1) {
                assertSame(Filters.FALSE, f1);
            } else {
                fail("too many elements");
            }
            i++;
        }
    }

    @Test
    public void testToString() {
        // just check that they don't throw exceptions
        Filter[] f = new Filter[] { Filters.TRUE, Filters.FALSE, Filters.TRUE };
        Filters.all(f).toString();
        Filters.all(new Filter[] {}).toString();
        Filters.all(new Filter[] { Filters.TRUE }).toString();
    }


    @Test
    public void testAllLogic() {
        assertTrue((Filters.all(Filters.TRUE)).accept(null));
        assertFalse(Filters.all(Filters.FALSE).accept(null));
        assertTrue(Filters.all(Filters.TRUE, Filters.TRUE).accept(null));
        assertFalse(Filters.all(Filters.TRUE, Filters.FALSE).accept(null));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersAllTest.class);
    }
}

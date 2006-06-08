/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter;

import static org.coconut.filter.LogicFilters.FALSE;
import static org.coconut.filter.LogicFilters.TRUE;
import static org.coconut.filter.LogicFilters.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.LogicFilters.AnyFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class LogicFiltersAnyTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testConstructor() {
        Filter<?>[] f = new Filter[] { TRUE, FALSE, TRUE };
        AnyFilter filter = any(f);
        assertEquals(filter.getFilters().size(), f.length);
        assertEquals(filter.getFilters().get(0), f[0]);
        assertEquals(filter.getFilters().get(1), f[1]);
        assertEquals(filter.getFilters().get(2), f[2]);
    }

    @Test(expected = NullPointerException.class)
        @SuppressWarnings("unchecked")
    public void testNull() {
        Filter<?>[] f = new Filter[] { TRUE, null, TRUE };
        any((Filter[]) f);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIterator() {
        Filter<?>[] f = new Filter[] { TRUE, FALSE, TRUE };
        AnyFilter<?> filter = any((Filter[]) f);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testToString() {
        // just check that they don't throw exceptions
        Filter<?>[] f = new Filter[] { TRUE, FALSE, TRUE };
        any((Filter[]) f).toString();
        any(new Filter[0]).toString();
        any(new Filter[] { TRUE }).toString();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAny() {
        assertTrue(any(TRUE).accept(null));
        assertFalse(any(FALSE).accept(null));
        assertTrue(any(FALSE, TRUE).accept(null));
        assertFalse(any(FALSE, FALSE).accept(null));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LogicFiltersAnyTest.class);
    }
}
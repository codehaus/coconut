/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.Filters.IsTypeFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class FiltersIsAssignableFromTest {

    @Test
    public void testFilter() {
        IsTypeFilter filter = Filters.isType(Number.class);
        assertEquals(Number.class, filter.getFilteredClass());
        assertTrue(filter.accept(new Integer(0)));
        assertTrue(filter.accept(new Long(0)));
        assertFalse(filter.accept(new Object()));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        Filters.isType(null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(FiltersIsAssignableFromTest.class);
    }
}

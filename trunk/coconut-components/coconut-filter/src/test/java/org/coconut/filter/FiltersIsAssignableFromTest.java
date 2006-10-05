/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.Filters.IsTypeFilter;
import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: FiltersIsAssignableFromTest.java 36 2006-08-22 09:59:45Z kasper $
 */
public class FiltersIsAssignableFromTest extends MavenDummyTest {

    @Test
    public void testFilter() {
        IsTypeFilter filter = Filters.isType(Number.class);
        assertEquals(Number.class, filter.getFilteredClass());
        assertTrue(filter.accept(Integer.valueOf(0)));
        assertTrue(filter.accept(Long.valueOf(0)));
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

/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter;

import static org.coconut.filter.ComparisonFilters.same;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class ComparisonFiltersSameTest extends MavenDummyTest {

    @Test
    public void testEquals() {
        String o = "1";
        assertEquals("1", same("1").getObject());
        assertTrue(same(o).accept(o));
        assertFalse(same(new Integer(1)).accept(new Integer(1)));
        assertFalse(same("1").accept("2"));
        assertFalse(same("1").accept(null));
        same("1").toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        same(null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ComparisonFiltersSameTest.class);
    }
}
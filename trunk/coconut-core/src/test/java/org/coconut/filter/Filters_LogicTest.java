/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;

import org.coconut.filter.Filters.AllFilter;
import org.coconut.filter.Filters.AnyFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Filters_LogicTest {

    @Test
    public void testTrueFilter() {
        assertTrue(Filters.TRUE.accept(null));
        assertTrue(Filters.TRUE.accept(this));
        Filters.TRUE.toString(); // does not fail
    }

    @Test
    public void testFalseFilter() {
        assertFalse(Filters.FALSE.accept(null));
        assertFalse(Filters.FALSE.accept(this));
        Filters.FALSE.toString(); // does not fail
    }

    /* Test all */
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

    /* Test and */
    @Test
    public void testAnd() {
        assertTrue(Filters.and(Filters.TRUE, Filters.TRUE).accept(null));
        assertFalse(Filters.and(Filters.TRUE, Filters.FALSE).accept(null));
        assertFalse(Filters.and(Filters.FALSE, Filters.TRUE).accept(null));
        assertFalse(Filters.and(Filters.FALSE, Filters.FALSE).accept(null));
        assertSame(Filters.and(Filters.FALSE, Filters.TRUE).getLeftFilter(),
                Filters.FALSE);
        assertSame(Filters.and(Filters.FALSE, Filters.TRUE).getRightFilter(),
                Filters.TRUE);
        assertEquals(Filters.and(Filters.FALSE, Filters.TRUE).getFilters(), Arrays
                .asList(Filters.FALSE, Filters.TRUE));
        Filters.and(Filters.FALSE, Filters.FALSE).toString(); // check no
                                                                // exception
    }

    @Test
    public void testStrict() {
        assertTrue(Filters.and(Filters.TRUE, Filters.TRUE).isStrict());
        assertFalse(Filters.and(Filters.FALSE, Filters.TRUE, false).isStrict());
    }

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        Filters.and(null, Filters.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        Filters.and(Filters.TRUE, null);
    }

    /* Test any */
    @Test
    @SuppressWarnings("unchecked")
    public void testAnyConstructor() {
        Filter<?>[] f = new Filter[] { Filters.TRUE, Filters.FALSE, Filters.TRUE };
        AnyFilter filter = Filters.any(f);
        assertEquals(filter.getFilters().size(), f.length);
        assertEquals(filter.getFilters().get(0), f[0]);
        assertEquals(filter.getFilters().get(1), f[1]);
        assertEquals(filter.getFilters().get(2), f[2]);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void testAnyNull() {
        Filter<?>[] f = new Filter[] { Filters.TRUE, null, Filters.TRUE };
        Filters.any((Filter[]) f);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAnyIterator() {
        Filter<?>[] f = new Filter[] { Filters.TRUE, Filters.FALSE, Filters.TRUE };
        AnyFilter<?> filter = Filters.any((Filter[]) f);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testAnyToString() {
        // just check that they don't throw exceptions
        Filter<?>[] f = new Filter[] { Filters.TRUE, Filters.FALSE, Filters.TRUE };
        Filters.any((Filter[]) f).toString();
        Filters.any(new Filter[0]).toString();
        Filters.any(new Filter[] { Filters.TRUE }).toString();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAny() {
        assertTrue(Filters.any(Filters.TRUE).accept(null));
        assertFalse(Filters.any(Filters.FALSE).accept(null));
        assertTrue(Filters.any(Filters.FALSE, Filters.TRUE).accept(null));
        assertFalse(Filters.any(Filters.FALSE, Filters.FALSE).accept(null));
    }

    /* Test not */
    @Test(expected = NullPointerException.class)
    public void testNot() {
        assertFalse(Filters.not(Filters.TRUE).accept(null));
        assertTrue(Filters.not(Filters.FALSE).accept(null));
        assertEquals(Filters.not(Filters.FALSE).getFilter(), Filters.FALSE);
        assertEquals(Filters.not(Filters.FALSE).getFilters(), Collections
                .singletonList(Filters.FALSE));
        Filters.not(Filters.TRUE).toString(); // check no exception

        Filters.not(null);
    }

    /* Test or */
    @Test
    public void testOr() {
        assertTrue(Filters.or(Filters.TRUE, Filters.TRUE).accept(null));
        assertTrue(Filters.or(Filters.TRUE, Filters.FALSE).accept(null));
        assertTrue(Filters.or(Filters.FALSE, Filters.TRUE).accept(null));
        assertFalse(Filters.or(Filters.FALSE, Filters.FALSE).accept(null));
        assertSame(Filters.or(Filters.FALSE, Filters.TRUE).getLeftFilter(), Filters.FALSE);
        assertSame(Filters.or(Filters.FALSE, Filters.TRUE).getRightFilter(), Filters.TRUE);
        assertEquals(Filters.or(Filters.FALSE, Filters.TRUE).getFilters(), Arrays.asList(
                Filters.FALSE, Filters.TRUE));
        Filters.or(Filters.FALSE, Filters.FALSE).toString(); // check no
        // exception
    }

    @Test(expected = NullPointerException.class)
    public void testOrNullLeft() {
        Filters.or(null, Filters.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testOrNullRight() {
        Filters.or(Filters.TRUE, null);
    }

    /* Test xor */
    @Test
    public void testXor() {
        assertFalse(Filters.xor(Filters.TRUE, Filters.TRUE).accept(null));
        assertTrue(Filters.xor(Filters.TRUE, Filters.FALSE).accept(null));
        assertTrue(Filters.xor(Filters.FALSE, Filters.TRUE).accept(null));
        assertFalse(Filters.xor(Filters.FALSE, Filters.FALSE).accept(null));
        assertSame(Filters.xor(Filters.FALSE, Filters.TRUE).getLeftFilter(),
                Filters.FALSE);
        assertSame(Filters.xor(Filters.FALSE, Filters.TRUE).getRightFilter(),
                Filters.TRUE);
        assertSame(Filters.xor(Filters.FALSE, Filters.TRUE).getFilters().get(0),
                Filters.FALSE);
        assertSame(Filters.xor(Filters.FALSE, Filters.TRUE).getFilters().get(1),
                Filters.TRUE);
        Filters.xor(Filters.FALSE, Filters.FALSE).toString(); // check no
        // exception
    }

    @Test(expected = NullPointerException.class)
    public void testXorNullLeft() {
        Filters.xor(null, Filters.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testXorNullRight() {
        Filters.xor(Filters.TRUE, null);
    }
}

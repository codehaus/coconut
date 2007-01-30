/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter;

import static org.coconut.filter.Filters.greatherThen;
import static org.coconut.filter.Filters.greatherThenOrEqual;
import static org.coconut.filter.Filters.lessThen;
import static org.coconut.filter.Filters.lessThenOrEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;

import junit.framework.AssertionFailedError;

import org.coconut.filter.Filters.GreaterThenFilter;
import org.coconut.filter.Filters.GreaterThenOrEqualFilter;
import org.coconut.filter.Filters.LessThenFilter;
import org.coconut.filter.Filters.LessThenOrEqualFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Filters_ComparisonTest {

    private final static Comparator<Dummy> COMP = new Comparator<Dummy>() {
        public int compare(Dummy o1, Dummy o2) {
            return (o1.i < o2.i ? -1 : (o1.i == o2.i ? 0 : 1));
        }
    };

    /* Test equals */
    @Test
    public void testEquals() {
        assertEquals("1", Filters.equal("1").getObject());
        assertTrue(Filters.equal("1").accept("1"));
        assertFalse(Filters.equal("1").accept("2"));
        assertFalse(Filters.equal("1").accept(null));
        Filters.equal(Filters.TRUE).toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testEqualsNPE() {
        Filters.equal(null);
    }

    /* Test greater then */
    @Test
    public void testGreaterThenComparable() {
        GreaterThenFilter<Integer> f = greatherThen(5);
        assertEquals(5, f.getObject());
        assertNull(f.getComparator());

        assertFalse(f.accept(4));
        assertFalse(f.accept(5));
        assertTrue(f.accept(6));

        f.toString(); // no exceptions
    }

    @Test
    public void testGreaterThenComparator() {

        GreaterThenFilter<Dummy> f = greatherThen(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());

        assertFalse(f.accept(Dummy.D1));
        assertFalse(f.accept(Dummy.D2));
        assertTrue(f.accept(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = NullPointerException.class)
    public void testGreaterThenNull1() {
        greatherThen(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGreaterThenNull2() {
        greatherThen(null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void testGreaterThenNull3() {
        greatherThen(2, null);
    }

    @Test
    public void testGreaterThenNotComparable() throws Exception {
        Constructor c = GreaterThenFilter.class
                .getConstructor(new Class[] { Object.class });
        try {
            c.newInstance(new Object[] { new Object() });
            throw new AssertionFailedError("Did not throw exception");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /* Test greaterTheOrEqual */
    @Test
    public void testGreaterThenOrEqualComparable() {
        GreaterThenOrEqualFilter<Integer> f = greatherThenOrEqual(5);
        assertEquals(5, f.getObject());
        assertNull(f.getComparator());

        assertFalse(f.accept(4));
        assertTrue(f.accept(5));
        assertTrue(f.accept(6));

        f.toString(); // no exceptions
    }

    @Test
    public void testGreaterThenOrEqualComparator() {

        GreaterThenOrEqualFilter<Dummy> f = greatherThenOrEqual(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());

        assertFalse(f.accept(Dummy.D1));
        assertTrue(f.accept(Dummy.D2));
        assertTrue(f.accept(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = NullPointerException.class)
    public void testGreaterThenOrEqualNull1() {
        greatherThenOrEqual(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGreaterThenOrEqualNull2() {
        greatherThenOrEqual(null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void testGreaterThenOrEqualNull3() {
        greatherThenOrEqual(2, null);
    }

    @Test
    public void testGreaterThenOrEqualNotComparable() throws Exception {
        Constructor c = GreaterThenOrEqualFilter.class
                .getConstructor(new Class[] { Object.class });
        try {
            c.newInstance(new Object[] { new Object() });
            throw new AssertionFailedError("Did not throw exception");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /* Test lessThen */
    @Test
    public void testLessThenComparable() {
        LessThenFilter<Integer> f = lessThen(5);
        assertEquals(5, f.getObject());
        assertNull(f.getComparator());

        assertTrue(f.accept(4));
        assertFalse(f.accept(5));
        assertFalse(f.accept(6));

        f.toString(); // no exceptions
    }

    @Test
    public void testLessThenComparator() {

        LessThenFilter<Dummy> f = lessThen(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());

        assertTrue(f.accept(Dummy.D1));
        assertFalse(f.accept(Dummy.D2));
        assertFalse(f.accept(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = NullPointerException.class)
    public void testLessThenNull1() {
        lessThen(null);
    }

    @Test(expected = NullPointerException.class)
    public void testLessThenNull2() {
        lessThen(null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void testLessThenNull3() {
        lessThen(2, null);
    }

    @Test
    public void testLessThenNotComparable() throws Exception {
        Constructor c = LessThenFilter.class.getConstructor(new Class[] { Object.class });
        try {
            c.newInstance(new Object[] { new Object() });
            throw new AssertionFailedError("Did not throw exception");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    static class Dummy {
        static final Dummy D1 = new Dummy(1);

        static final Dummy D2 = new Dummy(2);

        static final Dummy D3 = new Dummy(3);

        final int i;

        private Dummy(int i) {
            this.i = i;
        }
    }

    /* Test lessThenOrEqual */
    @Test
    public void testLessThenOrEqualComparable() {
        LessThenOrEqualFilter<Integer> f = lessThenOrEqual(5);
        assertEquals(5, f.getObject());
        assertNull(f.getComparator());

        assertTrue(f.accept(4));
        assertTrue(f.accept(5));
        assertFalse(f.accept(6));

        f.toString(); // no exceptions
    }

    @Test
    public void testLessThenOrEqualComparator() {

        LessThenOrEqualFilter<Dummy> f = lessThenOrEqual(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());

        assertTrue(f.accept(Dummy.D1));
        assertTrue(f.accept(Dummy.D2));
        assertFalse(f.accept(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = NullPointerException.class)
    public void testLessThenOrEqualNull1() {
        lessThenOrEqual(null);
    }

    @Test(expected = NullPointerException.class)
    public void testLessThenOrEqualNull2() {
        lessThenOrEqual(null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void testLessThenOrEqualNull3() {
        lessThenOrEqual(2, null);
    }

    @Test
    public void testLessThenOrEqualNotComparable() throws Exception {
        Constructor c = LessThenOrEqualFilter.class
                .getConstructor(new Class[] { Object.class });
        try {
            c.newInstance(new Object[] { new Object() });
            throw new AssertionFailedError("Did not throw exception");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /* Test same */
    @Test
    public void testSameEquals() {
        String o = "1";
        assertEquals("1", Filters.same("1").getObject());
        assertTrue(Filters.same(o).accept(o));
        assertFalse(Filters.same(new HashMap()).accept(new HashMap()));
        assertFalse(Filters.same("1").accept("2"));
        assertFalse(Filters.same("1").accept(null));
        Filters.same("1").toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testSameNull() {
        Filters.same(null);
    }
}

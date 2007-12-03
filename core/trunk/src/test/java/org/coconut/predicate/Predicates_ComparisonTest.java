/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import static org.coconut.predicate.Predicates.greatherThen;
import static org.coconut.predicate.Predicates.greatherThenOrEqual;
import static org.coconut.predicate.Predicates.lessThen;
import static org.coconut.predicate.Predicates.lessThenOrEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;

import junit.framework.AssertionFailedError;

import org.coconut.predicate.Predicates.GreaterThenOrEqualPredicate;
import org.coconut.predicate.Predicates.GreaterThenPredicate;
import org.coconut.predicate.Predicates.LessThenOrEqualPredicate;
import org.coconut.predicate.Predicates.LessThenPredicate;
import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Predicates_ComparisonTest {

    static final class DummyComparator implements Comparator<Dummy>, Serializable {
        public int compare(Dummy o1, Dummy o2) {
            return (o1.i < o2.i ? -1 : (o1.i == o2.i ? 0 : 1));
        }
    };

    private final static Comparator<Dummy> COMP = new DummyComparator();

    @Test(expected = NullPointerException.class)
    public void betweenNPE() {
        Predicates.between(null, 2);
    }

    @Test(expected = NullPointerException.class)
    public void betweenNPE1() {
        Predicates.between(1, null);
    }

    @Test(expected = NullPointerException.class)
    public void betweenNPE2() {
        Predicates.between(null, 2, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void betweenNPE3() {
        Predicates.between(1, null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void betweenNPE4() {
        Predicates.between(1, 2, null);
    }

    @Test
    public void between() {
        Predicate b = Predicates.between(2, 4);
        assertFalse(b.evaluate(1));
        assertTrue(b.evaluate(2));
        assertTrue(b.evaluate(3));
        assertTrue(b.evaluate(4));
        assertFalse(b.evaluate(5));
        TestUtil.assertIsSerializable(b);
    }

    @Test
    public void betweenComparator() {
        Predicate b = Predicates.between(Dummy.D2, Dummy.D4, COMP);
        assertFalse(b.evaluate(Dummy.D1));
        assertTrue(b.evaluate(Dummy.D2));
        assertTrue(b.evaluate(Dummy.D3));
        assertTrue(b.evaluate(Dummy.D4));
        assertFalse(b.evaluate(Dummy.D5));
        TestUtil.assertIsSerializable(b);
    }

    /* Test equals */
    @Test
    public void equalsTo() {
        assertEquals("1", ((Predicates.EqualsToPredicate) Predicates.equalsTo("1")).getObject());
        assertTrue(Predicates.equalsTo("1").evaluate("1"));
        assertFalse(Predicates.equalsTo("1").evaluate("2"));
        assertFalse(Predicates.equalsTo("1").evaluate(null));
        Predicates.equalsTo(Predicates.TRUE).toString(); // check no exception
        TestUtil.assertIsSerializable(Predicates.equalsTo(Predicates.TRUE));
    }

    @Test(expected = NullPointerException.class)
    public void equalsToNPE() {
        Predicates.equalsTo(null);
    }

    /* Test greater then */
    @Test
    public void testGreaterThenComparable() {
        GreaterThenPredicate<Integer> f = (GreaterThenPredicate) greatherThen(5);
        assertEquals(5, f.getObject().intValue());
        assertNull(f.getComparator());

        assertFalse(f.evaluate(4));
        assertFalse(f.evaluate(5));
        assertTrue(f.evaluate(6));

        f.toString(); // no exceptions
    }

    @Test
    public void testGreaterThenComparator() {

        GreaterThenPredicate<Dummy> f = (GreaterThenPredicate) greatherThen(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());

        assertFalse(f.evaluate(Dummy.D1));
        assertFalse(f.evaluate(Dummy.D2));
        assertTrue(f.evaluate(Dummy.D3));

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
        Constructor c = GreaterThenPredicate.class.getConstructor(new Class[] { Object.class });
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
        GreaterThenOrEqualPredicate<Integer> f = (GreaterThenOrEqualPredicate) greatherThenOrEqual(5);
        assertEquals(5, f.getObject().intValue());
        assertNull(f.getComparator());

        assertFalse(f.evaluate(4));
        assertTrue(f.evaluate(5));
        assertTrue(f.evaluate(6));

        f.toString(); // no exceptions
    }

    @Test
    public void testGreaterThenOrEqualComparator() {

        GreaterThenOrEqualPredicate<Dummy> f = (GreaterThenOrEqualPredicate) greatherThenOrEqual(
                Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());

        assertFalse(f.evaluate(Dummy.D1));
        assertTrue(f.evaluate(Dummy.D2));
        assertTrue(f.evaluate(Dummy.D3));

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
        Constructor c = GreaterThenOrEqualPredicate.class
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
        LessThenPredicate<Integer> f = (LessThenPredicate) lessThen(5);
        assertEquals(5, f.getObject().intValue());
        assertNull(f.getComparator());

        assertTrue(f.evaluate(4));
        assertFalse(f.evaluate(5));
        assertFalse(f.evaluate(6));

        f.toString(); // no exceptions
    }

    @Test
    public void testLessThenComparator() {

        LessThenPredicate<Dummy> f = (LessThenPredicate) lessThen(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());

        assertTrue(f.evaluate(Dummy.D1));
        assertFalse(f.evaluate(Dummy.D2));
        assertFalse(f.evaluate(Dummy.D3));

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
        Constructor c = LessThenPredicate.class.getConstructor(new Class[] { Object.class });
        try {
            c.newInstance(new Object[] { new Object() });
            throw new AssertionFailedError("Did not throw exception");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    static final class Dummy implements Serializable{
        static final Dummy D1 = new Dummy(1);

        static final Dummy D2 = new Dummy(2);

        static final Dummy D3 = new Dummy(3);

        static final Dummy D4 = new Dummy(4);

        static final Dummy D5 = new Dummy(5);

        final int i;

        private Dummy(int i) {
            this.i = i;
        }
    }

    /* Test lessThenOrEqual */
    @Test
    public void testLessThenOrEqualComparable() {
        LessThenOrEqualPredicate<Integer> f = (LessThenOrEqualPredicate) lessThenOrEqual(5);
        assertEquals(5, f.getObject().intValue());
        assertNull(f.getComparator());

        assertTrue(f.evaluate(4));
        assertTrue(f.evaluate(5));
        assertFalse(f.evaluate(6));

        f.toString(); // no exceptions
    }

    @Test
    public void testLessThenOrEqualComparator() {

        LessThenOrEqualPredicate<Dummy> f = (LessThenOrEqualPredicate) lessThenOrEqual(Dummy.D2,
                COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());

        assertTrue(f.evaluate(Dummy.D1));
        assertTrue(f.evaluate(Dummy.D2));
        assertFalse(f.evaluate(Dummy.D3));

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
        Constructor c = LessThenOrEqualPredicate.class.getConstructor(new Class[] { Object.class });
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
        assertEquals("1", ((Predicates.SamePredicate) Predicates.same("1")).getObject());
        assertTrue(Predicates.same(o).evaluate(o));
        assertFalse(Predicates.same(new HashMap()).evaluate(new HashMap()));
        assertFalse(Predicates.same("1").evaluate("2"));
        assertFalse(Predicates.same("1").evaluate(null));
        Predicates.same("1").toString(); // check no exception
    }

    @Test(expected = NullPointerException.class)
    public void testSameNull() {
        Predicates.same(null);
    }
}

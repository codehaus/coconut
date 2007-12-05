/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.coconut.operations.Predicates.greaterThen;
import static org.coconut.operations.Predicates.greaterThenOrEqual;
import static org.coconut.operations.Predicates.lessThen;
import static org.coconut.operations.Predicates.lessThenOrEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;

import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Predicates.GreaterThenOrEqualPredicate;
import org.coconut.operations.Predicates.GreaterThenPredicate;
import org.coconut.operations.Predicates.LessThenOrEqualPredicate;
import org.coconut.operations.Predicates.LessThenPredicate;
import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Predicates_ComparisonTest.java 501 2007-12-04 11:03:23Z kasper $
 */
public class Predicates_ComparisonTest {

    private final static Comparator<Dummy> COMP = new DummyComparator();;

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
    public void greaterThenComparable() {
        GreaterThenPredicate<Integer> f = (GreaterThenPredicate) greaterThen(5);
        assertEquals(5, f.getObject().intValue());
        assertNull(f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertFalse(f.evaluate(4));
        assertFalse(f.evaluate(5));
        assertTrue(f.evaluate(6));

        f.toString(); // no exceptions
    }

    @Test
    public void greaterThenComperator() {
        GreaterThenPredicate<Dummy> f = (GreaterThenPredicate) greaterThen(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertFalse(f.evaluate(Dummy.D1));
        assertFalse(f.evaluate(Dummy.D2));
        assertTrue(f.evaluate(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = IllegalArgumentException.class)
    public void greaterThenNotComparableIAE() throws Exception {
        greaterThen(new Object());
    }

    @Test(expected = NullPointerException.class)
    public void greaterThenNPE() {
        greaterThen(null);
    }

    @Test(expected = NullPointerException.class)
    public void greaterThenNPE1() {
        greaterThen(null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void greaterThenNPE2() {
        greaterThen(2, null);
    }

    /* Test greaterTheOrEqual */
    @Test
    public void greaterThenOrEqualComparable() {
        GreaterThenOrEqualPredicate<Integer> f = (GreaterThenOrEqualPredicate) greaterThenOrEqual(5);
        assertEquals(5, f.getObject().intValue());
        assertNull(f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertFalse(f.evaluate(4));
        assertTrue(f.evaluate(5));
        assertTrue(f.evaluate(6));

        f.toString(); // no exceptions
    }

    @Test
    public void greaterThenOrEqualComparator() {
        GreaterThenOrEqualPredicate<Dummy> f = (GreaterThenOrEqualPredicate) greaterThenOrEqual(
                Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertFalse(f.evaluate(Dummy.D1));
        assertTrue(f.evaluate(Dummy.D2));
        assertTrue(f.evaluate(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = IllegalArgumentException.class)
    public void greaterThenOrEqualNotComparableIAE() throws Exception {
        greaterThenOrEqual(new Object());
    }

    @Test(expected = NullPointerException.class)
    public void greaterThenOrEqualNPE() {
        greaterThenOrEqual(null);
    }

    @Test(expected = NullPointerException.class)
    public void greaterThenOrEqualNPE1() {
        greaterThenOrEqual(null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void greaterThenOrEqualNPE2() {
        greaterThenOrEqual(2, null);
    }

    /* Test lessThen */
    @Test
    public void lessThenComparable() {
        LessThenPredicate<Integer> f = (LessThenPredicate) lessThen(5);
        assertEquals(5, f.getObject().intValue());
        assertNull(f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertTrue(f.evaluate(4));
        assertFalse(f.evaluate(5));
        assertFalse(f.evaluate(6));

        f.toString(); // no exceptions
    }

    @Test
    public void lessThenComparator() {

        LessThenPredicate<Dummy> f = (LessThenPredicate) lessThen(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertTrue(f.evaluate(Dummy.D1));
        assertFalse(f.evaluate(Dummy.D2));
        assertFalse(f.evaluate(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = IllegalArgumentException.class)
    public void lessThenNotComparableIAE() throws Exception {
        lessThen(new Object());
    }

    @Test(expected = NullPointerException.class)
    public void lessThenNPE() {
        lessThen(null);
    }

    @Test(expected = NullPointerException.class)
    public void lessThenNPE1() {
        lessThen(null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void lessThenNPE2() {
        lessThen(2, null);
    }

    /* Test lessThenOrEqual */
    @Test
    public void lessThenOrEqualComparable() {
        LessThenOrEqualPredicate<Integer> f = (LessThenOrEqualPredicate) lessThenOrEqual(5);
        assertEquals(5, f.getObject().intValue());
        assertNull(f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertTrue(f.evaluate(4));
        assertTrue(f.evaluate(5));
        assertFalse(f.evaluate(6));

        f.toString(); // no exceptions
    }

    @Test
    public void lessThenOrEqualComparator() {
        LessThenOrEqualPredicate<Dummy> f = (LessThenOrEqualPredicate) lessThenOrEqual(Dummy.D2,
                COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertTrue(f.evaluate(Dummy.D1));
        assertTrue(f.evaluate(Dummy.D2));
        assertFalse(f.evaluate(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = IllegalArgumentException.class)
    public void lessThenOrEqualNotComparable() throws Exception {
        lessThenOrEqual(new Object());
    }

    @Test(expected = NullPointerException.class)
    public void lessThenOrEqualNPE() {
        lessThenOrEqual(null);
    }

    @Test(expected = NullPointerException.class)
    public void lessThenOrEqualNPE1() {
        lessThenOrEqual(null, COMP);
    }

    @Test(expected = NullPointerException.class)
    public void lessThenOrEqualNPE2() {
        lessThenOrEqual(2, null);
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

    static final class Dummy implements Serializable {
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

    static final class DummyComparator implements Comparator<Dummy>, Serializable {
        public int compare(Dummy o1, Dummy o2) {
            return (o1.i < o2.i ? -1 : (o1.i == o2.i ? 0 : 1));
        }
    }
}


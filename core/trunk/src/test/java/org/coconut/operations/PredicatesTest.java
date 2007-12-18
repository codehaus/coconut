/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.coconut.operations.Predicates.FALSE;
import static org.coconut.operations.Predicates.TRUE;
import static org.coconut.operations.Predicates.greaterThen;
import static org.coconut.operations.Predicates.greaterThenOrEqual;
import static org.coconut.operations.Predicates.lessThen;
import static org.coconut.operations.Predicates.lessThenOrEqual;
import static org.coconut.operations.Predicates.not;
import static org.coconut.test.TestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Predicates.AllPredicate;
import org.coconut.operations.Predicates.AnyPredicate;
import org.coconut.operations.Predicates.GreaterThenOrEqualPredicate;
import org.coconut.operations.Predicates.GreaterThenPredicate;
import org.coconut.operations.Predicates.IsEqualsPredicate;
import org.coconut.operations.Predicates.IsSamePredicate;
import org.coconut.operations.Predicates.IsTypePredicate;
import org.coconut.operations.Predicates.LessThenOrEqualPredicate;
import org.coconut.operations.Predicates.LessThenPredicate;
import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * Tests {@link Predicates}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: PredicatesTest.java 501 2007-12-04 11:03:23Z kasper $
 */
public class PredicatesTest {

    private final static Comparator<Dummy> COMP = new DummyComparator();

    private static Predicate[] PREDICATES_WITH_NULL_ARRAY = { TRUE, null, TRUE };

    private static Iterable PREDICATES_WITH_NULL_ITERABLE = Arrays
            .asList(PREDICATES_WITH_NULL_ARRAY);

    private static Iterable STRING_PREDICATE_ITERABLE = Arrays.asList(StringPredicates
            .startsWith("foo"), StringPredicates.contains("boo"));

    private static Predicate[] TRUE_FALSE_TRUE_ARRAY = { TRUE, FALSE, TRUE };

    private static Iterable TRUE_FALSE_TRUE_ITERABLE = Arrays.asList(TRUE_FALSE_TRUE_ARRAY);

    /**
     * Tests {@link Predicates#all(Predicate...)}.
     */
    @Test
    public void allArray() {
        AllPredicate<?> p = (AllPredicate) Predicates.all(TRUE_FALSE_TRUE_ARRAY);

        // evaluate
        assertTrue((Predicates.all(TRUE)).evaluate(null));
        assertFalse(Predicates.all(FALSE).evaluate(null));
        assertTrue(Predicates.all(TRUE, TRUE).evaluate(null));
        assertFalse(Predicates.all(TRUE, FALSE).evaluate(null));

        // getPredicates
        assertEquals(3, p.getPredicates().size());
        assertEquals(TRUE, p.getPredicates().get(0));
        assertEquals(FALSE, p.getPredicates().get(1));
        assertEquals(TRUE, p.getPredicates().get(2));

        // iterable
        Iterator<?> i = p.iterator();
        assertSame(TRUE, i.next());
        assertSame(FALSE, i.next());
        assertSame(TRUE, i.next());
        assertFalse(i.hasNext());

        // Serializable
        TestUtil.assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.all(TRUE_FALSE_TRUE_ARRAY).toString();
        Predicates.all(new Predicate[] {}).toString();
        Predicates.all(new Predicate[] { TRUE }).toString();

        // shortcircuted evaluation
        Predicates.all(TRUE, FALSE, TestUtil.dummy(Predicate.class)).evaluate(null);
    }

    /**
     * Tests that {@link Predicates#all(Predicate...)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void allArrayNPE() {
        Predicates.all((Predicate[]) null);
    }

    /**
     * Tests that {@link Predicates#all(Predicate...)} throws a
     * {@link NullPointerException} when invoked with an array containing a
     * <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void allArrayNPE1() {
        Predicates.all(PREDICATES_WITH_NULL_ARRAY);
    }

    /**
     * Tests {@link Predicates#all(Iterable)}.
     */
    @Test
    public void allIterable() {
        AllPredicate<String> p = (AllPredicate) Predicates.all(STRING_PREDICATE_ITERABLE);

        // evaluate
        assertFalse(p.evaluate("fobo"));
        assertFalse(p.evaluate("foobo"));
        assertFalse(p.evaluate("foboo"));
        assertTrue(p.evaluate("fooboo"));

        // getPredicates
        assertEquals(2, p.getPredicates().size());
        assertEquals(StringPredicates.startsWith("foo"), p.getPredicates().get(0));
        assertEquals(StringPredicates.contains("boo"), p.getPredicates().get(1));

        // iterable
        Iterator<?> i = p.iterator();
        assertEquals(StringPredicates.startsWith("foo"), i.next());
        assertEquals(StringPredicates.contains("boo"), i.next());
        assertFalse(i.hasNext());

        // Serializable
        TestUtil.assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.all(TRUE_FALSE_TRUE_ITERABLE).toString();
        Predicates.all((Iterable) Arrays.asList()).toString();
        Predicates.all((Iterable) Arrays.asList(TRUE)).toString();

        // shortcircuted evaluation
        Predicates.all((Iterable) Arrays.asList(TRUE, FALSE, TestUtil.dummy(Predicate.class)))
                .evaluate(null);
    }

    /**
     * Tests that {@link Predicates#all(Iterable)} throws a {@link NullPointerException}
     * when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void allIterableNPE() {
        Predicates.all((Iterable) null);
    }

    /**
     * Tests that {@link Predicates#all(Iterable)} throws a {@link NullPointerException}
     * when invoked with an iterable containing a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void allIterableNPE1() {
        Predicates.all(PREDICATES_WITH_NULL_ITERABLE);
    }

    /**
     * Tests {@link Predicates#and(Predicate, Predicate)}.
     */
    @Test
    public void and() {
        assertTrue(Predicates.and(TRUE, TRUE).evaluate(null));
        assertFalse(Predicates.and(TRUE, FALSE).evaluate(null));
        assertFalse(Predicates.and(FALSE, TRUE).evaluate(null));
        assertFalse(Predicates.and(FALSE, FALSE).evaluate(null));

        Predicates.AndPredicate p = new Predicates.AndPredicate(FALSE, TRUE);
        assertSame(p.getLeftPredicate(), FALSE);
        assertSame(p.getRightPredicate(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        Predicates.and(FALSE, TestUtil.dummy(Predicate.class)).evaluate(null);
    }

    /**
     * Tests that {@link Predicates#and(Predicate, Predicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        Predicates.and(null, TRUE);
    }

    /**
     * Tests that {@link Predicates#and(Predicate, Predicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        Predicates.and(TRUE, null);
    }

    /**
     * Tests {@link Predicates#any(Predicate...)}.
     */
    @Test
    public void anyArray() {
        AnyPredicate<?> filter = (AnyPredicate) Predicates.any(TRUE_FALSE_TRUE_ARRAY);

        // evaluate
        assertTrue((Predicates.any(TRUE)).evaluate(null));
        assertFalse(Predicates.any(FALSE).evaluate(null));
        assertTrue(Predicates.any(TRUE, TRUE).evaluate(null));
        assertTrue(Predicates.any(TRUE, FALSE).evaluate(null));
        assertFalse(Predicates.any(FALSE, FALSE).evaluate(null));

        // getPredicates
        assertEquals(3, filter.getPredicates().size());
        assertEquals(TRUE, filter.getPredicates().get(0));
        assertEquals(FALSE, filter.getPredicates().get(1));
        assertEquals(TRUE, filter.getPredicates().get(2));

        // iterable
        Iterator<?> i = filter.iterator();
        assertSame(TRUE, i.next());
        assertSame(FALSE, i.next());
        assertSame(TRUE, i.next());
        assertFalse(i.hasNext());

        // Serializable
        TestUtil.assertIsSerializable(filter);

        // toString, just check that they don't throw exceptions
        Predicates.any(TRUE_FALSE_TRUE_ARRAY).toString();
        Predicates.any(new Predicate[] {}).toString();
        Predicates.any(new Predicate[] { TRUE }).toString();

        // shortcircuted evaluation
        Predicates.any(FALSE, TRUE, TestUtil.dummy(Predicate.class)).evaluate(null);
    }

    /**
     * Tests that {@link Predicates#any(Predicate...)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyArrayNPE() {
        Predicates.any((Predicate[]) null);
    }

    /**
     * Tests that {@link Predicates#any(Predicate...)} throws a
     * {@link NullPointerException} when invoked with an array containing a
     * <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyArrayNPE1() {
        Predicates.any(PREDICATES_WITH_NULL_ARRAY);
    }

    /**
     * Tests {@link Predicates#anyEquals(Object...)}.
     */
    @Test
    public void anyEqualsArray() {
        Predicate<?> p = Predicates.anyEquals(TRUE_FALSE_TRUE_ARRAY);

        // evaluate
        assertTrue((Predicates.anyEquals(TRUE)).evaluate(TRUE));
        assertFalse(Predicates.anyEquals(TRUE).evaluate(FALSE));
        assertTrue(Predicates.anyEquals(TRUE, FALSE).evaluate(TRUE));
        assertTrue(Predicates.anyEquals(TRUE, FALSE).evaluate(FALSE));
        assertFalse(Predicates.anyEquals(FALSE, FALSE).evaluate(TRUE));
        // Serializable
        assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.anyEquals(TRUE_FALSE_TRUE_ARRAY).toString();
        Predicates.anyEquals(new Predicate[] {}).toString();
        Predicates.anyEquals(new Predicate[] { TRUE }).toString();

        // shortcircuted evaluation
        Predicates.anyEquals(FALSE, TRUE, TestUtil.dummy(Predicate.class)).evaluate(TRUE);
    }

    /**
     * Tests that {@link Predicates#anyEquals(Object...)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyEqualsArrayNPE() {
        Predicates.anyEquals((Predicate[]) null);
    }

    /**
     * Tests that {@link Predicates#anyEquals(Object...)} throws a
     * {@link NullPointerException} when invoked with an array containing a
     * <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyEqualsArrayNPE1() {
        Predicates.anyEquals(PREDICATES_WITH_NULL_ARRAY);
    }

    /**
     * Tests {@link Predicates#anyEquals(Iterable)}.
     */
    @Test
    public void anyEqualsIterable() {
        Predicate<?> p = Predicates.anyEquals(TRUE_FALSE_TRUE_ITERABLE);
        assertTrue(Predicates.anyEquals(Arrays.asList(TRUE, FALSE)).evaluate(FALSE));
        assertFalse(Predicates.anyEquals(Arrays.asList(FALSE, FALSE)).evaluate(TRUE));
        // Serializable
        assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.anyEquals(TRUE_FALSE_TRUE_ITERABLE).toString();
        Predicates.anyEquals(Arrays.asList()).toString();
        Predicates.anyEquals(Arrays.asList(TRUE)).toString();

        // shortcircuted evaluation
        Predicates.anyEquals(Arrays.asList(FALSE, TRUE, dummy(Predicate.class))).evaluate(TRUE);

    }

    /**
     * Tests that {@link Predicates#anyEquals(Iterable)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyEqualsIterableNPE() {
        Predicates.anyEquals((Iterable) null);
    }

    /**
     * Tests that {@link Predicates#anyEquals(Iterable)} throws a
     * {@link NullPointerException} when invoked with an iterable containing a
     * <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyEqualsIterableNPE1() {
        Predicates.anyEquals(PREDICATES_WITH_NULL_ITERABLE);
    }

    /**
     * Tests {@link Predicates#any(Iterable)}.
     */
    @Test
    public void anyIterable() {
        AnyPredicate<String> p = (AnyPredicate) Predicates.any(STRING_PREDICATE_ITERABLE);

        // evaluate
        assertFalse(p.evaluate("fobo"));
        assertTrue(p.evaluate("foobo"));
        assertTrue(p.evaluate("foboo"));
        assertTrue(p.evaluate("fooboo"));

        // getPredicates
        assertEquals(2, p.getPredicates().size());
        assertEquals(StringPredicates.startsWith("foo"), p.getPredicates().get(0));
        assertEquals(StringPredicates.contains("boo"), p.getPredicates().get(1));

        // iterable
        Iterator<?> i = p.iterator();
        assertEquals(StringPredicates.startsWith("foo"), i.next());
        assertEquals(StringPredicates.contains("boo"), i.next());
        assertFalse(i.hasNext());

        // Serializable
        TestUtil.assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.any(TRUE_FALSE_TRUE_ITERABLE).toString();
        Predicates.any((Iterable) Arrays.asList()).toString();
        Predicates.any((Iterable) Arrays.asList(TRUE)).toString();

        // shortcircuted evaluation
        Predicates.any((Iterable) Arrays.asList(FALSE, TRUE, TestUtil.dummy(Predicate.class)))
                .evaluate(null);
    }

    /**
     * Tests that {@link Predicates#any(Iterable)} throws a {@link NullPointerException}
     * when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyIterableNPE() {
        Predicates.any((Iterable) null);
    }

    /**
     * Tests that {@link Predicates#any(Iterable)} throws a {@link NullPointerException}
     * when invoked with an iterable containing a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyIterableNPE1() {
        Predicates.any(PREDICATES_WITH_NULL_ITERABLE);
    }

    /**
     * Tests {@link Predicates#anyType(Class...)}.
     */
    @Test
    public void anyTypeArray() {
        Predicate p = Predicates.anyType(String.class, Number.class, LinkedList.class);

        // evaluate
        assertTrue(p.evaluate("ddd"));
        assertTrue(p.evaluate(1));
        assertTrue(p.evaluate(1.0d));
        assertTrue(p.evaluate(new LinkedList()));
        assertFalse(p.evaluate(new ArrayList()));
        assertFalse(p.evaluate(this));
        assertTrue(p.evaluate(Byte.valueOf((byte) 3)));
        assertFalse(p.evaluate(Boolean.FALSE));
        assertFalse(p.evaluate(new Object()));

        // Serializable
        assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        p.toString();
        Predicates.anyType(new Class[] {}).toString();
        Predicates.anyType(new Class[] { Predicates.class }).toString();
    }

    /**
     * Tests that {@link Predicates#anyType(Class...)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyTypeArrayNPE() {
        Predicates.anyType((Class[]) null);
    }

    /**
     * Tests that {@link Predicates#anyType(Class...)} throws a
     * {@link NullPointerException} when invoked with an array containing a
     * <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyTypeArrayNPE1() {
        Predicates.anyType(String.class, Long.class, null, Integer.class);
    }

    /**
     * Tests {@link Predicates#anyType(Iterable)}.
     */
    @Test
    public void anyTypeIterable() {
        Predicate p = Predicates.anyType(Arrays
                .asList(String.class, Number.class, LinkedList.class));

        // evaluate
        assertTrue(p.evaluate("ddd"));
        assertTrue(p.evaluate(1));
        assertTrue(p.evaluate(1.0d));
        assertTrue(p.evaluate(new LinkedList()));
        assertFalse(p.evaluate(new ArrayList()));
        assertFalse(p.evaluate(this));
        assertTrue(p.evaluate(Byte.valueOf((byte) 3)));
        assertFalse(p.evaluate(Boolean.FALSE));
        assertFalse(p.evaluate(new Object()));

        // Serializable
        assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        p.toString();
        Predicates.anyType((Iterable) Arrays.asList()).toString();
        Predicates.anyType(Arrays.asList(Predicates.class)).toString();
    }

    /**
     * Tests that {@link Predicates#anyType(Iterable)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyTypeIterableNPE() {
        Predicates.anyType((Iterable) null);
    }

    /**
     * Tests that {@link Predicates#anyType(Iterable)} throws a
     * {@link NullPointerException} when invoked with an iterable containing a
     * <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void anyTypeIterableNPE1() {
        Predicates.anyType(Arrays.asList(String.class, Long.class, null, Integer.class));
    }

    @Test
    public void between() {
        Predicate b = Predicates.between(2, 4);
        assertFalse(b.evaluate(1));
        assertTrue(b.evaluate(2));
        assertTrue(b.evaluate(3));
        assertTrue(b.evaluate(4));
        assertFalse(b.evaluate(5));
        assertIsSerializable(b);
    }

    @Test(expected = ClassCastException.class)
    public void betweenCCE() {
        Predicate p = Predicates.between(2, 4);
        p.evaluate("foo");
    }

    @Test
    public void betweenComparator() {
        Predicate b = Predicates.between(Dummy.D2, Dummy.D4, COMP);
        assertFalse(b.evaluate(Dummy.D1));
        assertTrue(b.evaluate(Dummy.D2));
        assertTrue(b.evaluate(Dummy.D3));
        assertTrue(b.evaluate(Dummy.D4));
        assertFalse(b.evaluate(Dummy.D5));
        assertIsSerializable(b);
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

    /**
     * Tests {@link Predicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.evaluate(null));
        assertFalse(FALSE.evaluate(this));
        assertSame(FALSE, Predicates.falsePredicate());
        FALSE.toString(); // does not fail
        assertIsSerializable(Predicates.falsePredicate());
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }

    /* Test greater then */
    @Test
    public void greaterThenComparable() {
        GreaterThenPredicate<Integer> f = (GreaterThenPredicate) greaterThen(5);
        assertEquals(5, f.getObject().intValue());
        assertSame(Comparators.NATURAL_COMPARATOR, f.getComparator());
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
    };

    /* Test greaterTheOrEqual */
    @Test
    public void greaterThenOrEqualComparable() {
        GreaterThenOrEqualPredicate<Integer> f = (GreaterThenOrEqualPredicate) greaterThenOrEqual(5);
        assertEquals(5, f.getObject().intValue());
        assertSame(Comparators.NATURAL_COMPARATOR, f.getComparator());
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

    /**
     * Tests {@link Predicates#isEquals(Object)}.
     */
    @Test
    public void isEquals() {
        assertTrue(Predicates.isEquals("1").evaluate("1"));
        assertTrue(Predicates.isEquals(new HashMap()).evaluate(new HashMap()));
        assertFalse(Predicates.isEquals("1").evaluate("2"));
        assertFalse(Predicates.isEquals("1").evaluate(null));

        IsEqualsPredicate p = new IsEqualsPredicate("1");
        assertEquals("1", p.getElement());
        p.toString(); // check no exception
        assertIsSerializable(p);
    }

    /**
     * Tests that {@link Predicates#isEquals(Object)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void isEqualsNPE() {
        Predicates.isEquals(null);
    }

    /**
     * Tests {@link Predicates#isNull()}.
     */
    @Test
    public void isNotNull() {
        assertTrue(Predicates.isNull().evaluate(null));
        assertFalse(Predicates.isNull().evaluate(1));
        assertFalse(Predicates.isNull().evaluate("f"));
        Predicates.IS_NOT_NULL.toString();// no fail
        TestUtil.assertIsSerializable(Predicates.IS_NOT_NULL);
    }

    /**
     * Tests {@link Predicates#isNotNull()}.
     */
    @Test
    public void isNull() {
        assertFalse(Predicates.isNotNull().evaluate(null));
        assertTrue(Predicates.isNotNull().evaluate(1));
        assertTrue(Predicates.isNotNull().evaluate("f"));
        assertSame(Predicates.IS_NOT_NULL, Predicates.isNotNull());
        Predicates.IS_NOT_NULL.toString();// no fail
        TestUtil.assertIsSerializable(Predicates.IS_NOT_NULL);
        assertSame(Predicates.IS_NOT_NULL, TestUtil.serializeAndUnserialize(Predicates.IS_NOT_NULL));
    }

    /**
     * Tests {@link Predicates#isSame(Object)}.
     */
    @Test
    public void isSame() {
        Object o = new Object();
        assertTrue(Predicates.isSame(o).evaluate(o));
        assertFalse(Predicates.isSame(o).evaluate(new Object()));
        assertFalse(Predicates.isSame(o).evaluate(null));
        assertEquals(new HashMap(), new HashMap());
        assertFalse(Predicates.isSame(new HashMap()).evaluate(new HashMap()));

        IsSamePredicate p = new IsSamePredicate("1");
        assertEquals("1", p.getElement());
        p.toString(); // check no exception
        assertIsSerializable(p);
    }

    /**
     * Tests that {@link Predicates#isSame(Object)} throws a {@link NullPointerException}
     * when invoked with a <code>null</code> element.
     */
    @Test(expected = NullPointerException.class)
    public void isSameNPE() {
        Predicates.isSame(null);
    }

    /**
     * Tests {@link Predicates#isType(Class)}.
     */
    @Test
    public void isType() {
        assertTrue(Predicates.isType(Object.class).evaluate(new Object()));
        assertTrue(Predicates.isType(Object.class).evaluate(1));
        assertTrue(Predicates.isType(Object.class).evaluate(new HashMap()));

        assertFalse(Predicates.isType(Number.class).evaluate(new Object()));
        assertTrue(Predicates.isType(Number.class).evaluate(1));
        assertTrue(Predicates.isType(Number.class).evaluate(1L));
        assertFalse(Predicates.isType(Number.class).evaluate(new HashMap()));

        IsTypePredicate p = new IsTypePredicate(Map.class);
        assertEquals(Map.class, p.getType());
        assertTrue(p.evaluate(new HashMap()));
        assertFalse(p.evaluate(new Object()));
        TestUtil.assertIsSerializable(p);
    }

    /**
     * Tests that {@link Predicates#isType(Class)} throws a
     * {@link IllegalArgumentException} when invoked with a pritimive type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void isTypeIAE() {
        Predicates.isType(Long.TYPE);
    }

    /**
     * Tests that {@link Predicates#isType(Class)} throws a {@link NullPointerException}
     * when invoked with a right side <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void isTypeNPE() {
        Predicates.isType(null);
    }

    /* Test lessThen */
    @Test
    public void lessThenComparable() {
        LessThenPredicate<Integer> f = (LessThenPredicate) lessThen(5);
        assertEquals(5, f.getObject().intValue());
        assertSame(Comparators.NATURAL_COMPARATOR, f.getComparator());
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
        assertSame(Comparators.NATURAL_COMPARATOR, f.getComparator());
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

    /**
     * Tests {@link Predicates#not(Predicate)}.
     */
    @Test
    public void not() {
        assertFalse(Predicates.not(TRUE).evaluate(null));
        assertTrue(Predicates.not(FALSE).evaluate(null));

        Predicates.NotPredicate p = new Predicates.NotPredicate(TRUE);
        assertSame(p.getPredicate(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);
    }

    /**
     * Tests that {@link Predicates#not(Predicate)} throws a {@link NullPointerException}
     * when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        Predicates.not(null);
    }

    /**
     * Tests {@link Predicates#notNullAnd(Predicate)}.
     */
    @Test
    public void notNullAnd() {
        Predicate<Integer> p = Predicates.notNullAnd(Predicates.anyEquals(1, 2));
        assertFalse(p.evaluate(null));
        assertTrue(p.evaluate(1));
        assertFalse(p.evaluate(3));
        p.toString();
        assertIsSerializable(p);
    }

    /**
     * Tests that {@link Predicates#notNullAnd(Predicate)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNullAndNPE() {
        Predicates.notNullAnd(null);
    }

    /**
     * Tests {@link Predicates#or(Predicate, Predicate)}.
     */
    @Test
    public void or() {
        assertTrue(Predicates.or(TRUE, TRUE).evaluate(null));
        assertTrue(Predicates.or(TRUE, FALSE).evaluate(null));
        assertTrue(Predicates.or(FALSE, TRUE).evaluate(null));
        assertFalse(Predicates.or(FALSE, FALSE).evaluate(null));

        Predicates.OrPredicate p = new Predicates.OrPredicate(FALSE, TRUE);
        assertSame(p.getLeftPredicate(), FALSE);
        assertSame(p.getRightPredicate(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        Predicates.or(TRUE, TestUtil.dummy(Predicate.class)).evaluate(null);
    }

    /**
     * Tests that {@link Predicates#or(Predicate, Predicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        Predicates.or(null, TRUE);
    }

    /**
     * Tests that {@link Predicates#or(Predicate, Predicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE1() {
        Predicates.or(TRUE, null);
    }

    /**
     * Tests {@link Predicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.evaluate(null));
        assertTrue(TRUE.evaluate(this));
        assertSame(TRUE, Predicates.truePredicate());
        TRUE.toString(); // does not fail
        assertIsSerializable(Predicates.truePredicate());
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }

    /**
     * Tests {@link Predicates#xor(Predicate, Predicate)}.
     */
    @Test
    public void xor() {
        assertFalse(Predicates.xor(TRUE, TRUE).evaluate(null));
        assertTrue(Predicates.xor(TRUE, FALSE).evaluate(null));
        assertTrue(Predicates.xor(FALSE, TRUE).evaluate(null));
        assertFalse(Predicates.xor(FALSE, FALSE).evaluate(null));

        Predicates.XorPredicate p = new Predicates.XorPredicate(FALSE, TRUE);
        assertSame(p.getLeftPredicate(), FALSE);
        assertSame(p.getRightPredicate(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);
    }

    /**
     * Tests that {@link Predicates#xor(Predicate, Predicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void xorNPE() {
        Predicates.xor(null, TRUE);
    }

    /**
     * Tests that {@link Predicates#xor(Predicate, Predicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void xorNPE1() {
        Predicates.xor(TRUE, null);
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

/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166yops;

import static org.codehaus.cake.jsr166yops.Predicates.FALSE;
import static org.codehaus.cake.jsr166yops.Predicates.TRUE;
import static org.codehaus.cake.jsr166yops.Predicates.greaterThen;
import static org.codehaus.cake.jsr166yops.Predicates.greaterThenOrEqual;
import static org.codehaus.cake.jsr166yops.Predicates.lessThen;
import static org.codehaus.cake.jsr166yops.Predicates.lessThenOrEqual;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.codehaus.cake.test.util.TestUtil.dummy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import jsr166y.forkjoin.Ops.Op;
import jsr166y.forkjoin.Ops.Predicate;

import org.codehaus.cake.jsr166yops.Predicates.AllPredicate;
import org.codehaus.cake.jsr166yops.Predicates.AnyPredicate;
import org.codehaus.cake.jsr166yops.Predicates.GreaterThenOrEqualPredicate;
import org.codehaus.cake.jsr166yops.Predicates.GreaterThenPredicate;
import org.codehaus.cake.jsr166yops.Predicates.IsEqualsPredicate;
import org.codehaus.cake.jsr166yops.Predicates.IsSamePredicate;
import org.codehaus.cake.jsr166yops.Predicates.IsTypePredicate;
import org.codehaus.cake.jsr166yops.Predicates.LessThenOrEqualPredicate;
import org.codehaus.cake.jsr166yops.Predicates.LessThenPredicate;
import org.codehaus.cake.test.util.TestUtil;
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

    private static Iterable STRING_PREDICATE_ITERABLE = Arrays.asList(StringOps.startsWith("foo"),
            StringOps.contains("boo"));

    private static Predicate[] TRUE_FALSE_TRUE_ARRAY = { TRUE, FALSE, TRUE };

    private static Iterable TRUE_FALSE_TRUE_ITERABLE = Arrays.asList(TRUE_FALSE_TRUE_ARRAY);

    /**
     * Tests {@link Predicates#all(Predicate...)}.
     */
    @Test
    public void allArray() {
        AllPredicate<?> p = (AllPredicate) Predicates.all(TRUE_FALSE_TRUE_ARRAY);

        // evaluate
        assertTrue((Predicates.all(TRUE)).op(null));
        assertFalse(Predicates.all(FALSE).op(null));
        assertTrue(Predicates.all(TRUE, TRUE).op(null));
        assertFalse(Predicates.all(TRUE, FALSE).op(null));

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
        Predicates.all(TRUE, FALSE, TestUtil.dummy(Predicate.class)).op(null);
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
        assertFalse(p.op("fobo"));
        assertFalse(p.op("foobo"));
        assertFalse(p.op("foboo"));
        assertTrue(p.op("fooboo"));

        // getPredicates
        assertEquals(2, p.getPredicates().size());
        assertEquals(StringOps.startsWith("foo"), p.getPredicates().get(0));
        assertEquals(StringOps.contains("boo"), p.getPredicates().get(1));

        // iterable
        Iterator<?> i = p.iterator();
        assertEquals(StringOps.startsWith("foo"), i.next());
        assertEquals(StringOps.contains("boo"), i.next());
        assertFalse(i.hasNext());

        // Serializable
        TestUtil.assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.all(TRUE_FALSE_TRUE_ITERABLE).toString();
        Predicates.all((Iterable) Arrays.asList()).toString();
        Predicates.all((Iterable) Arrays.asList(TRUE)).toString();

        // shortcircuted evaluation
        Predicates.all((Iterable) Arrays.asList(TRUE, FALSE, TestUtil.dummy(Predicate.class))).op(
                null);
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
        assertTrue(Predicates.and(TRUE, TRUE).op(null));
        assertFalse(Predicates.and(TRUE, FALSE).op(null));
        assertFalse(Predicates.and(FALSE, TRUE).op(null));
        assertFalse(Predicates.and(FALSE, FALSE).op(null));

        Predicates.AndPredicate p = new Predicates.AndPredicate(FALSE, TRUE);
        assertSame(p.getLeftPredicate(), FALSE);
        assertSame(p.getRightPredicate(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        Predicates.and(FALSE, TestUtil.dummy(Predicate.class)).op(null);
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
        assertTrue((Predicates.any(TRUE)).op(null));
        assertFalse(Predicates.any(FALSE).op(null));
        assertTrue(Predicates.any(TRUE, TRUE).op(null));
        assertTrue(Predicates.any(TRUE, FALSE).op(null));
        assertFalse(Predicates.any(FALSE, FALSE).op(null));

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
        Predicates.any(FALSE, TRUE, TestUtil.dummy(Predicate.class)).op(null);
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
        assertTrue((Predicates.anyEquals(TRUE)).op(TRUE));
        assertFalse(Predicates.anyEquals(TRUE).op(FALSE));
        assertTrue(Predicates.anyEquals(TRUE, FALSE).op(TRUE));
        assertTrue(Predicates.anyEquals(TRUE, FALSE).op(FALSE));
        assertFalse(Predicates.anyEquals(FALSE, FALSE).op(TRUE));
        // Serializable
        assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.anyEquals(TRUE_FALSE_TRUE_ARRAY).toString();
        Predicates.anyEquals(new Predicate[] {}).toString();
        Predicates.anyEquals(new Predicate[] { TRUE }).toString();

        // shortcircuted evaluation
        Predicates.anyEquals(FALSE, TRUE, TestUtil.dummy(Predicate.class)).op(TRUE);
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
        assertTrue(Predicates.anyEquals(Arrays.asList(TRUE, FALSE)).op(FALSE));
        assertFalse(Predicates.anyEquals(Arrays.asList(FALSE, FALSE)).op(TRUE));
        // Serializable
        assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.anyEquals(TRUE_FALSE_TRUE_ITERABLE).toString();
        Predicates.anyEquals(Arrays.asList()).toString();
        Predicates.anyEquals(Arrays.asList(TRUE)).toString();

        // shortcircuted evaluation
        Predicates.anyEquals(Arrays.asList(FALSE, TRUE, dummy(Predicate.class))).op(TRUE);

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
        assertFalse(p.op("fobo"));
        assertTrue(p.op("foobo"));
        assertTrue(p.op("foboo"));
        assertTrue(p.op("fooboo"));

        // getPredicates
        assertEquals(2, p.getPredicates().size());
        assertEquals(StringOps.startsWith("foo"), p.getPredicates().get(0));
        assertEquals(StringOps.contains("boo"), p.getPredicates().get(1));

        // iterable
        Iterator<?> i = p.iterator();
        assertEquals(StringOps.startsWith("foo"), i.next());
        assertEquals(StringOps.contains("boo"), i.next());
        assertFalse(i.hasNext());

        // Serializable
        TestUtil.assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        Predicates.any(TRUE_FALSE_TRUE_ITERABLE).toString();
        Predicates.any((Iterable) Arrays.asList()).toString();
        Predicates.any((Iterable) Arrays.asList(TRUE)).toString();

        // shortcircuted evaluation
        Predicates.any((Iterable) Arrays.asList(FALSE, TRUE, TestUtil.dummy(Predicate.class))).op(
                null);
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
        assertTrue(p.op("ddd"));
        assertTrue(p.op(1));
        assertTrue(p.op(1.0d));
        assertTrue(p.op(new LinkedList()));
        assertFalse(p.op(new ArrayList()));
        assertFalse(p.op(this));
        assertTrue(p.op(Byte.valueOf((byte) 3)));
        assertFalse(p.op(Boolean.FALSE));
        assertFalse(p.op(new Object()));

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
        assertTrue(p.op("ddd"));
        assertTrue(p.op(1));
        assertTrue(p.op(1.0d));
        assertTrue(p.op(new LinkedList()));
        assertFalse(p.op(new ArrayList()));
        assertFalse(p.op(this));
        assertTrue(p.op(Byte.valueOf((byte) 3)));
        assertFalse(p.op(Boolean.FALSE));
        assertFalse(p.op(new Object()));

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
        assertFalse(b.op(1));
        assertTrue(b.op(2));
        assertTrue(b.op(3));
        assertTrue(b.op(4));
        assertFalse(b.op(5));
        assertIsSerializable(b);
    }

    @Test(expected = ClassCastException.class)
    public void betweenCCE() {
        Predicate p = Predicates.between(2, 4);
        p.op("foo");
    }

    @Test
    public void betweenComparator() {
        Predicate b = Predicates.between(Dummy.D2, Dummy.D4, COMP);
        assertFalse(b.op(Dummy.D1));
        assertTrue(b.op(Dummy.D2));
        assertTrue(b.op(Dummy.D3));
        assertTrue(b.op(Dummy.D4));
        assertFalse(b.op(Dummy.D5));
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
        assertFalse(FALSE.op(null));
        assertFalse(FALSE.op(this));
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
        assertFalse(f.op(4));
        assertFalse(f.op(5));
        assertTrue(f.op(6));

        f.toString(); // no exceptions
    }

    @Test
    public void greaterThenComperator() {
        GreaterThenPredicate<Dummy> f = (GreaterThenPredicate) greaterThen(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertFalse(f.op(Dummy.D1));
        assertFalse(f.op(Dummy.D2));
        assertTrue(f.op(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = NullPointerException.class)
    public void greaterThenNPE() {
        greaterThen((Integer) null);
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
        assertFalse(f.op(4));
        assertTrue(f.op(5));
        assertTrue(f.op(6));

        f.toString(); // no exceptions
    }

    @Test
    public void greaterThenOrEqualComparator() {
        GreaterThenOrEqualPredicate<Dummy> f = (GreaterThenOrEqualPredicate) greaterThenOrEqual(
                Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertFalse(f.op(Dummy.D1));
        assertTrue(f.op(Dummy.D2));
        assertTrue(f.op(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = NullPointerException.class)
    public void greaterThenOrEqualNPE() {
        greaterThenOrEqual((Integer) null);
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
        assertTrue(Predicates.isEquals("1").op("1"));
        assertTrue(Predicates.isEquals(new HashMap()).op(new HashMap()));
        assertFalse(Predicates.isEquals("1").op("2"));
        assertFalse(Predicates.isEquals("1").op(null));

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
        assertTrue(Predicates.isNull().op(null));
        assertFalse(Predicates.isNull().op(1));
        assertFalse(Predicates.isNull().op("f"));
        Predicates.IS_NOT_NULL.toString();// no fail
        TestUtil.assertIsSerializable(Predicates.IS_NOT_NULL);
    }

    /**
     * Tests {@link Predicates#isNotNull()}.
     */
    @Test
    public void isNull() {
        assertFalse(Predicates.isNotNull().op(null));
        assertTrue(Predicates.isNotNull().op(1));
        assertTrue(Predicates.isNotNull().op("f"));
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
        assertTrue(Predicates.isSame(o).op(o));
        assertFalse(Predicates.isSame(o).op(new Object()));
        assertFalse(Predicates.isSame(o).op(null));
        assertEquals(new HashMap(), new HashMap());
        assertFalse(Predicates.isSame(new HashMap()).op(new HashMap()));

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
        assertTrue(Predicates.isType(Object.class).op(new Object()));
        assertTrue(Predicates.isType(Object.class).op(1));
        assertTrue(Predicates.isType(Object.class).op(new HashMap()));

        assertFalse(Predicates.isType(Number.class).op(new Object()));
        assertTrue(Predicates.isType(Number.class).op(1));
        assertTrue(Predicates.isType(Number.class).op(1L));
        assertFalse(Predicates.isType(Number.class).op(new HashMap()));

        IsTypePredicate p = new IsTypePredicate(Map.class);
        assertEquals(Map.class, p.getType());
        assertTrue(p.op(new HashMap()));
        assertFalse(p.op(new Object()));
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
        assertTrue(f.op(4));
        assertFalse(f.op(5));
        assertFalse(f.op(6));

        f.toString(); // no exceptions
    }

    @Test
    public void lessThenComparator() {

        LessThenPredicate<Dummy> f = (LessThenPredicate) lessThen(Dummy.D2, COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertTrue(f.op(Dummy.D1));
        assertFalse(f.op(Dummy.D2));
        assertFalse(f.op(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = NullPointerException.class)
    public void lessThenNPE() {
        lessThen((Integer) null);
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
        assertTrue(f.op(4));
        assertTrue(f.op(5));
        assertFalse(f.op(6));

        f.toString(); // no exceptions
    }

    @Test
    public void lessThenOrEqualComparator() {
        LessThenOrEqualPredicate<Dummy> f = (LessThenOrEqualPredicate) lessThenOrEqual(Dummy.D2,
                COMP);
        assertEquals(Dummy.D2, f.getObject());
        assertEquals(COMP, f.getComparator());
        TestUtil.assertIsSerializable(f);
        assertTrue(f.op(Dummy.D1));
        assertTrue(f.op(Dummy.D2));
        assertFalse(f.op(Dummy.D3));

        f.toString(); // no exceptions
    }

    @Test(expected = NullPointerException.class)
    public void lessThenOrEqualNPE() {
        lessThenOrEqual((Integer) null);
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
        assertFalse(Predicates.not(TRUE).op(null));
        assertTrue(Predicates.not(FALSE).op(null));

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
        assertFalse(p.op(null));
        assertTrue(p.op(1));
        assertFalse(p.op(3));
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
        assertTrue(Predicates.or(TRUE, TRUE).op(null));
        assertTrue(Predicates.or(TRUE, FALSE).op(null));
        assertTrue(Predicates.or(FALSE, TRUE).op(null));
        assertFalse(Predicates.or(FALSE, FALSE).op(null));

        Predicates.OrPredicate p = new Predicates.OrPredicate(FALSE, TRUE);
        assertSame(p.getLeftPredicate(), FALSE);
        assertSame(p.getRightPredicate(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        Predicates.or(TRUE, TestUtil.dummy(Predicate.class)).op(null);
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
        assertTrue(TRUE.op(null));
        assertTrue(TRUE.op(this));
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
        assertFalse(Predicates.xor(TRUE, TRUE).op(null));
        assertTrue(Predicates.xor(TRUE, FALSE).op(null));
        assertTrue(Predicates.xor(FALSE, TRUE).op(null));
        assertFalse(Predicates.xor(FALSE, FALSE).op(null));

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

    @Test
    public void mapperPredicate() {
        Predicate<Number> p = (Predicate) Predicates.anyEquals(4, 16);
        Op<Integer, Integer> m = new Op<Integer, Integer>() {
            public Integer op(Integer from) {
                return from.intValue() * from.intValue();
            }
        };
        Predicate mapped = Predicates.mapAndEvaluate(m, p);
        assertTrue(mapped.op(2));
        assertFalse(mapped.op(3));
        assertTrue(mapped.op(4));

        assertSame(p, ((Predicates.MapAndEvaluatePredicate) mapped).getPredicate());
        assertSame(m, ((Predicates.MapAndEvaluatePredicate) mapped).getMapper());
        mapped.toString();
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE1() {
        Predicates.mapAndEvaluate(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE2() {
        Predicates.mapAndEvaluate(TestUtil.dummy(Op.class), null);
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

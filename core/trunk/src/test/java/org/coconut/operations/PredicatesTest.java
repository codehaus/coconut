/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Predicates.AllPredicate;
import org.coconut.operations.Predicates.AndPredicate;
import org.coconut.operations.Predicates.AnyPredicate;
import org.coconut.operations.Predicates.IsTypePredicate;
import org.coconut.test.MockTestCase;
import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: PredicatesTest.java 501 2007-12-04 11:03:23Z kasper $
 */
public class PredicatesTest {
    
    static Predicate<? extends Number> P_EXTEND_NUMBER = null;

    static Predicate<Long> P_LONG = Predicates.FALSE;

    static Predicate<Number> P_NUMBER = Predicates.FALSE;
    
    static Predicate<Object> P_OBJECT = Predicates.FALSE;

    static Predicate[] PREDICATES_WITH_NULL_ARRAY = { Predicates.TRUE, null, Predicates.TRUE };
    static Iterable PREDICATES_WITH_NULL_ITERABLE = Arrays.asList(PREDICATES_WITH_NULL_ARRAY);

    static Predicate[] STRING_PREDICATE_ARRAY = { StringPredicates.startsWith("foo"),
            StringPredicates.contains("boo") };

    static Iterable STRING_PREDICATE_ITERABLE = Arrays.asList(STRING_PREDICATE_ARRAY);

    static Predicate[] TRUE_FALSE_TRUE_ARRAY = { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };

    static Iterable TRUE_FALSE_TRUE_ITERABLE = Arrays.asList(TRUE_FALSE_TRUE_ARRAY);

    @Test
    public void all() {
        AllPredicate<?> filter = (AllPredicate) Predicates.all(TRUE_FALSE_TRUE_ARRAY);

        // evaluate
        assertTrue((Predicates.all(Predicates.TRUE)).evaluate(null));
        assertFalse(Predicates.all(Predicates.FALSE).evaluate(null));
        assertTrue(Predicates.all(Predicates.TRUE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.all(Predicates.TRUE, Predicates.FALSE).evaluate(null));
        assertFalse(Predicates.all(STRING_PREDICATE_ITERABLE).evaluate("fobo"));
        assertFalse(Predicates.all(STRING_PREDICATE_ITERABLE).evaluate("foobo"));
        assertFalse(Predicates.all(STRING_PREDICATE_ITERABLE).evaluate("foboo"));
        assertTrue(Predicates.all(STRING_PREDICATE_ITERABLE).evaluate("fooboo"));

        // getPredicates
        assertEquals(3, filter.getPredicates().size());
        assertEquals(Predicates.TRUE, filter.getPredicates().get(0));
        assertEquals(Predicates.FALSE, filter.getPredicates().get(1));
        assertEquals(Predicates.TRUE, filter.getPredicates().get(2));

        // iterable
        Iterator<?> i = filter.iterator();
        assertSame(Predicates.TRUE, i.next());
        assertSame(Predicates.FALSE, i.next());
        assertSame(Predicates.TRUE, i.next());
        assertFalse(i.hasNext());

        // Serializable
        TestUtil.assertIsSerializable(filter);

        // toString, just check that they don't throw exceptions
        Predicates.all(TRUE_FALSE_TRUE_ARRAY).toString();
        Predicates.all(new Predicate[] {}).toString();
        Predicates.all(new Predicate[] { Predicates.TRUE }).toString();

        // shortcircuted evaluation
        Predicates.all(Predicates.TRUE, Predicates.FALSE, MockTestCase.mockDummy(Predicate.class))
                .evaluate(null);
    }

    @Test(expected = NullPointerException.class)
    public void allNPE1() {
        Predicates.all((Predicate[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void allNPE2() {
        Predicates.all((Iterable) null);
    }

    @Test(expected = NullPointerException.class)
    public void allNPE3() {
        Predicates.all(PREDICATES_WITH_NULL_ARRAY);
    }

    @Test(expected = NullPointerException.class)
    public void allNPE4() {
        Predicates.all(PREDICATES_WITH_NULL_ITERABLE);
    }

    /* Test and */
    @Test
    public void and() {
        assertTrue(Predicates.and(Predicates.TRUE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.and(Predicates.TRUE, Predicates.FALSE).evaluate(null));
        assertFalse(Predicates.and(Predicates.FALSE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.and(Predicates.FALSE, Predicates.FALSE).evaluate(null));

        assertSame(((AndPredicate) Predicates.and(Predicates.FALSE, Predicates.TRUE))
                .getLeftPredicate(), Predicates.FALSE);
        assertSame(((AndPredicate) Predicates.and(Predicates.FALSE, Predicates.TRUE))
                .getRightPredicate(), Predicates.TRUE);
        assertEquals(((AndPredicate) Predicates.and(Predicates.FALSE, Predicates.TRUE))
                .getPredicates(), Arrays.asList(Predicates.FALSE, Predicates.TRUE));
        Predicates.and(Predicates.FALSE, Predicates.FALSE).toString(); // no exception

        //serializable
        TestUtil.assertIsSerializable(Predicates.and(Predicates.TRUE, Predicates.TRUE));
        
        // shortcircuted evaluation
        Predicates.and(Predicates.FALSE, MockTestCase.mockDummy(Predicate.class)).evaluate(null);

        // generic test
        Predicate<Number> pn = Predicates.TRUE;
        Predicate<Integer> ok = Predicates.and(pn, pn);
    }

    @Test(expected = NullPointerException.class)
    public void andNPE() {
        Predicates.and(null, Predicates.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        Predicates.and(Predicates.TRUE, null);
    }

    @Test
    public void any() {
        AnyPredicate<?> filter = (AnyPredicate) Predicates.any(TRUE_FALSE_TRUE_ARRAY);

        // evaluate
        assertTrue((Predicates.any(Predicates.TRUE)).evaluate(null));
        assertFalse(Predicates.any(Predicates.FALSE).evaluate(null));
        assertTrue(Predicates.any(Predicates.TRUE, Predicates.TRUE).evaluate(null));
        assertTrue(Predicates.any(Predicates.TRUE, Predicates.FALSE).evaluate(null));
        assertFalse(Predicates.any(Predicates.FALSE, Predicates.FALSE).evaluate(null));
        assertFalse(Predicates.any(STRING_PREDICATE_ITERABLE).evaluate("fobo"));
        assertTrue(Predicates.any(STRING_PREDICATE_ITERABLE).evaluate("foobo"));
        assertTrue(Predicates.any(STRING_PREDICATE_ITERABLE).evaluate("foboo"));
        assertTrue(Predicates.any(STRING_PREDICATE_ITERABLE).evaluate("fooboo"));

        // getPredicates
        assertEquals(3, filter.getPredicates().size());
        assertEquals(Predicates.TRUE, filter.getPredicates().get(0));
        assertEquals(Predicates.FALSE, filter.getPredicates().get(1));
        assertEquals(Predicates.TRUE, filter.getPredicates().get(2));

        // iterable
        Iterator<?> i = filter.iterator();
        assertSame(Predicates.TRUE, i.next());
        assertSame(Predicates.FALSE, i.next());
        assertSame(Predicates.TRUE, i.next());
        assertFalse(i.hasNext());

        // Serializable
        TestUtil.assertIsSerializable(filter);

        // toString, just check that they don't throw exceptions
        Predicates.any(TRUE_FALSE_TRUE_ARRAY).toString();
        Predicates.any(new Predicate[] {}).toString();
        Predicates.any(new Predicate[] { Predicates.TRUE }).toString();

        // shortcircuted evaluation
        Predicates.any(Predicates.FALSE, Predicates.TRUE, MockTestCase.mockDummy(Predicate.class))
                .evaluate(null);
    }

    @Test
    public void anyEquals() {
        Predicate<?> filter = Predicates.anyEquals(TRUE_FALSE_TRUE_ARRAY);

        // evaluate
        assertTrue((Predicates.anyEquals(Predicates.TRUE)).evaluate(Predicates.TRUE));
        assertFalse(Predicates.anyEquals(Predicates.TRUE).evaluate(Predicates.FALSE));
        assertTrue(Predicates.anyEquals(Predicates.TRUE, Predicates.FALSE)
                .evaluate(Predicates.TRUE));
        assertTrue(Predicates.anyEquals(Predicates.TRUE, Predicates.FALSE).evaluate(
                Predicates.FALSE));
        assertFalse(Predicates.anyEquals(Predicates.FALSE, Predicates.FALSE).evaluate(
                Predicates.TRUE));
        assertTrue(Predicates.anyEquals(Arrays.asList(Predicates.TRUE, Predicates.FALSE)).evaluate(
                Predicates.FALSE));
        assertFalse(Predicates.anyEquals(Arrays.asList(Predicates.FALSE, Predicates.FALSE))
                .evaluate(Predicates.TRUE));
        // Serializable
        TestUtil.assertIsSerializable(filter);

        // toString, just check that they don't throw exceptions
        Predicates.anyEquals(TRUE_FALSE_TRUE_ARRAY).toString();
        Predicates.anyEquals(new Predicate[] {}).toString();
        Predicates.anyEquals(new Predicate[] { Predicates.TRUE }).toString();

        // shortcircuted evaluation
        Predicates.anyEquals(Predicates.FALSE, Predicates.TRUE,
                MockTestCase.mockDummy(Predicate.class)).evaluate(Predicates.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void anyEqualsNPE1() {
        Predicates.anyEquals((Predicate[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void anyEqualsNPE2() {
        Predicates.anyEquals((Iterable) null);
    }

    @Test(expected = NullPointerException.class)
    public void anyEqualsNPE3() {
        Predicates.anyEquals(PREDICATES_WITH_NULL_ARRAY);
    }

    @Test(expected = NullPointerException.class)
    public void anyEqualsNPE4() {
        Predicates.anyEquals(PREDICATES_WITH_NULL_ITERABLE);
    }

    @Test(expected = NullPointerException.class)
    public void anyNPE1() {
        Predicates.any((Predicate[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void anyNPE2() {
        Predicates.any((Iterable) null);
    }

    @Test(expected = NullPointerException.class)
    public void anyNPE3() {
        Predicates.any(PREDICATES_WITH_NULL_ARRAY);
    }

    @Test(expected = NullPointerException.class)
    public void anyNPE4() {
        Predicates.any(PREDICATES_WITH_NULL_ITERABLE);
    }

    @Test
    public void anyType() {
        Predicate p = Predicates.anyType(String.class, Number.class, LinkedList.class);

        // evaluate
        assertTrue(p.evaluate("ddd"));
        assertTrue(p.evaluate(1));
        assertTrue(p.evaluate(1.0d));
        assertTrue(p.evaluate(new LinkedList()));
        assertFalse(p.evaluate(new ArrayList()));
        assertFalse(p.evaluate(this));

        p = Predicates.anyType(Arrays.asList(String.class, Number.class, LinkedList.class));
        assertTrue(p.evaluate(Byte.valueOf((byte) 3)));
        assertFalse(p.evaluate(Boolean.FALSE));
        assertFalse(p.evaluate(new Object()));
        
        // Serializable
        TestUtil.assertIsSerializable(p);

        // toString, just check that they don't throw exceptions
        p.toString();
        Predicates.anyType(new Class[] {}).toString();
        Predicates.anyType(new Class[] { Predicates.class }).toString();

    }

    @Test(expected = NullPointerException.class)
    public void anyTypeNPE1() {
        Predicates.anyType((Class[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void anyTypeNPE2() {
        Predicates.anyType((Iterable) null);
    }

    @Test(expected = NullPointerException.class)
    public void anyTypeNPE3() {
        Predicates.anyType(String.class, Long.class, null, Integer.class);
    }

    @Test(expected = NullPointerException.class)
    public void anyTypeNPE4() {
        Predicates.anyType(Arrays.asList(String.class, Long.class, null, Integer.class));
    }

    @Test
    public void falseFilter() {
        assertFalse(Predicates.FALSE.evaluate(null));
        assertFalse(Predicates.FALSE.evaluate(this));
        assertSame(Predicates.FALSE, Predicates.falsePredicate());
        Predicates.FALSE.toString(); // does not fail
        TestUtil.assertIsSerializable(Predicates.falsePredicate());
    }

    @Test
    public void isNull() {
        assertFalse(Predicates.isNotNull().evaluate(null));
        assertTrue(Predicates.isNotNull().evaluate(1));
        assertTrue(Predicates.isNotNull().evaluate("f"));
        assertSame(Predicates.IS_NOT_NULL, Predicates.isNotNull());
        Predicates.IS_NOT_NULL.toString();// no fail
        TestUtil.assertIsSerializable(Predicates.IS_NOT_NULL);
    }

    @Test
    public void isType() {
        IsTypePredicate filter = (IsTypePredicate) Predicates.isType(Number.class);
        assertEquals(Number.class, filter.getFilteredClass());
        assertTrue(filter.evaluate(Integer.valueOf(0)));
        assertTrue(filter.evaluate(Long.valueOf(0)));
        assertFalse(filter.evaluate(new Object()));
        TestUtil.assertIsSerializable(filter);
    }

    @Test(expected = NullPointerException.class)
    public void isTypeNPE() {
        Predicates.isType(null);
    }

    @Test
    public void notNullAnd() {
        Predicate<Integer> p = Predicates.notNullAnd(Predicates.anyEquals(1, 2));
        assertFalse(p.evaluate(null));
        assertTrue(p.evaluate(1));
        assertFalse(p.evaluate(3));
        p.toString();
    }

    @Test(expected = NullPointerException.class)
    public void notNullAndNPE() {
       Predicates.notNullAnd(null);
    }

    /* Test not */
    @Test(expected = NullPointerException.class)
    public void testNot() {
        assertFalse(Predicates.not(Predicates.TRUE).evaluate(null));
        assertTrue(Predicates.not(Predicates.FALSE).evaluate(null));
        assertEquals(((Predicates.NotPredicate) Predicates.not(Predicates.FALSE)).getPredicate(),
                Predicates.FALSE);
        assertEquals(((Predicates.NotPredicate) Predicates.not(Predicates.FALSE)).getPredicates(),
                Collections.singletonList(Predicates.FALSE));
        Predicates.not(Predicates.TRUE).toString(); // check no exception

        Predicates.not(null);
    }

    /* Test or */
    @Test
    public void testOr() {
        assertTrue(Predicates.or(Predicates.TRUE, Predicates.TRUE).evaluate(null));
        assertTrue(Predicates.or(Predicates.TRUE, Predicates.FALSE).evaluate(null));
        assertTrue(Predicates.or(Predicates.FALSE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.or(Predicates.FALSE, Predicates.FALSE).evaluate(null));
        assertSame(((Predicates.OrPredicate) Predicates.or(Predicates.FALSE, Predicates.TRUE))
                .getLeftPredicate(), Predicates.FALSE);
        assertSame(((Predicates.OrPredicate) Predicates.or(Predicates.FALSE, Predicates.TRUE))
                .getRightPredicate(), Predicates.TRUE);
        assertEquals(((Predicates.OrPredicate) Predicates.or(Predicates.FALSE, Predicates.TRUE))
                .getPredicates(), Arrays.asList(Predicates.FALSE, Predicates.TRUE));
        Predicates.or(Predicates.FALSE, Predicates.FALSE).toString(); // check no
        // exception
    }

    @Test(expected = NullPointerException.class)
    public void testOrNullLeft() {
        Predicates.or(null, Predicates.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testOrNullRight() {
        Predicates.or(Predicates.TRUE, null);
    }

    @Test
    public void testTrueFilter() {
        assertTrue(Predicates.TRUE.evaluate(null));
        assertTrue(Predicates.TRUE.evaluate(this));
        assertSame(Predicates.TRUE, Predicates.truePredicate());
        Predicates.TRUE.toString(); // does not fail
    }

    /* Test xor */
    @Test
    public void testXor() {
        assertFalse(Predicates.xor(Predicates.TRUE, Predicates.TRUE).evaluate(null));
        assertTrue(Predicates.xor(Predicates.TRUE, Predicates.FALSE).evaluate(null));
        assertTrue(Predicates.xor(Predicates.FALSE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.xor(Predicates.FALSE, Predicates.FALSE).evaluate(null));
        assertSame(((Predicates.XorPredicate) Predicates.xor(Predicates.FALSE, Predicates.TRUE))
                .getLeftPredicate(), Predicates.FALSE);
        assertSame(((Predicates.XorPredicate) Predicates.xor(Predicates.FALSE, Predicates.TRUE))
                .getRightPredicate(), Predicates.TRUE);
        assertSame(((Predicates.XorPredicate) Predicates.xor(Predicates.FALSE, Predicates.TRUE))
                .getPredicates().get(0), Predicates.FALSE);
        assertSame(((Predicates.XorPredicate) Predicates.xor(Predicates.FALSE, Predicates.TRUE))
                .getPredicates().get(1), Predicates.TRUE);
        Predicates.xor(Predicates.FALSE, Predicates.FALSE).toString(); // check no
        // exception
    }

    @Test(expected = NullPointerException.class)
    public void testXorNullLeft() {
        Predicates.xor(null, Predicates.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testXorNullRight() {
        Predicates.xor(Predicates.TRUE, null);
    }
}

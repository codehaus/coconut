/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.coconut.predicate.Predicates.AllPredicate;
import org.coconut.predicate.Predicates.AndPredicate;
import org.coconut.predicate.Predicates.AnyPredicate;
import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Predicates_LogicTest {
    static Predicate<Object> P_OBJECT = null;

    static Predicate<Number> P_NUMBER = null;

    static Predicate<? extends Number> P_EXTEND_NUMBER = null;

    static Predicate<Long> P_LONG = null;

    static Predicate[] PREDICATES_WITH_NULL_ARRAY = { Predicates.TRUE, null, Predicates.TRUE };

    static Iterable PREDICATES_WITH_NULL_ITERABLE = Arrays.asList(PREDICATES_WITH_NULL_ARRAY);

    static Predicate[] TRUE_FALSE_TRUE_ARRAY = { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };

    static Iterable TRUE_FALSE_TRUE_ITERABLE = Arrays.asList(TRUE_FALSE_TRUE_ARRAY);

    static Predicate[] STRING_PREDICATE_ARRAY = { StringPredicates.startsWith("foo"),
            StringPredicates.contains("boo") };

    static Iterable STRING_PREDICATE_ITERABLE = Arrays.asList(STRING_PREDICATE_ARRAY);

    /* Test all */
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
    public void testAnd() {
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
        Predicates.and(Predicates.FALSE, Predicates.FALSE).toString(); // check no
        // exception
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAny() {
        assertTrue(Predicates.any(Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.any(Predicates.FALSE).evaluate(null));
        assertTrue(Predicates.any(Predicates.FALSE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.any(Predicates.FALSE, Predicates.FALSE).evaluate(null));
    }

    /* Test any */
    @Test
    @SuppressWarnings("unchecked")
    public void testAnyConstructor() {
        Predicate[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        AnyPredicate filter = (AnyPredicate) Predicates.any(f);
        assertEquals(filter.getPredicates().size(), f.length);
        assertEquals(filter.getPredicates().get(0), f[0]);
        assertEquals(filter.getPredicates().get(1), f[1]);
        assertEquals(filter.getPredicates().get(2), f[2]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAnyIterator() {
        Predicate<?>[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        AnyPredicate<?> filter = (AnyPredicate) Predicates.any((Predicate[]) f);
        int i = 0;
        for (Predicate<?> f1 : filter) {
            if (i == 0 || i == 2) {
                assertSame(Predicates.TRUE, f1);
            } else if (i == 1) {
                assertSame(Predicates.FALSE, f1);
            } else {
                fail("too many elements");
            }
            i++;
        }
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void testAnyNull() {
        Predicate<?>[] f = new Predicate[] { Predicates.TRUE, null, Predicates.TRUE };
        Predicates.any((Predicate[]) f);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAnyToString() {
        // just check that they don't throw exceptions
        Predicate<?>[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        Predicates.any((Predicate[]) f).toString();
        Predicates.any(new Predicate[0]).toString();
        Predicates.any(new Predicate[] { Predicates.TRUE }).toString();
    }

    @Test
    public void testFalseFilter() {
        assertFalse(Predicates.FALSE.evaluate(null));
        assertFalse(Predicates.FALSE.evaluate(this));
        assertSame(Predicates.FALSE, Predicates.falsePredicate());
        Predicates.FALSE.toString(); // does not fail
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

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        Predicates.and(null, Predicates.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        Predicates.and(Predicates.TRUE, null);
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
    public void testStrict() {
        assertTrue(((AndPredicate) Predicates.and(Predicates.TRUE, Predicates.TRUE)).isStrict());
        assertFalse((new AndPredicate(Predicates.FALSE, Predicates.TRUE, false)).isStrict());
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

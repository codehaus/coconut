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

import org.coconut.predicate.Predicate;
import org.coconut.predicate.Predicates;
import org.coconut.predicate.Predicates.AllPredicate;
import org.coconut.predicate.Predicates.AnyPredicate;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Predicates_LogicTest {

    @Test
    public void testTrueFilter() {
        assertTrue(Predicates.TRUE.evaluate(null));
        assertTrue(Predicates.TRUE.evaluate(this));
        Predicates.TRUE.toString(); // does not fail
    }

    @Test
    public void testFalseFilter() {
        assertFalse(Predicates.FALSE.evaluate(null));
        assertFalse(Predicates.FALSE.evaluate(this));
        Predicates.FALSE.toString(); // does not fail
    }

    /* Test all */
    @Test
    public void testConstructor() {
        Predicate[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        AllPredicate<?> filter = Predicates.all(f);
        assertEquals(filter.getPredicates().size(), f.length);
        assertEquals(filter.getPredicates().get(0), f[0]);
        assertEquals(filter.getPredicates().get(1), f[1]);
        assertEquals(filter.getPredicates().get(2), f[2]);
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        Predicate[] f = new Predicate[] { Predicates.TRUE, null, Predicates.TRUE };
        Predicates.all(f);
    }

    @Test
    public void testIterator() {
        Predicate[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        AllPredicate<?> filter = Predicates.all(f);
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

    @Test
    public void testToString() {
        // just check that they don't throw exceptions
        Predicate[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        Predicates.all(f).toString();
        Predicates.all(new Predicate[] {}).toString();
        Predicates.all(new Predicate[] { Predicates.TRUE }).toString();
    }

    @Test
    public void testAllLogic() {
        assertTrue((Predicates.all(Predicates.TRUE)).evaluate(null));
        assertFalse(Predicates.all(Predicates.FALSE).evaluate(null));
        assertTrue(Predicates.all(Predicates.TRUE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.all(Predicates.TRUE, Predicates.FALSE).evaluate(null));
    }

    /* Test and */
    @Test
    public void testAnd() {
        assertTrue(Predicates.and(Predicates.TRUE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.and(Predicates.TRUE, Predicates.FALSE).evaluate(null));
        assertFalse(Predicates.and(Predicates.FALSE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.and(Predicates.FALSE, Predicates.FALSE).evaluate(null));
        assertSame(Predicates.and(Predicates.FALSE, Predicates.TRUE).getLeftPredicate(),
                Predicates.FALSE);
        assertSame(Predicates.and(Predicates.FALSE, Predicates.TRUE).getRightPredicate(),
                Predicates.TRUE);
        assertEquals(Predicates.and(Predicates.FALSE, Predicates.TRUE).getPredicates(), Arrays
                .asList(Predicates.FALSE, Predicates.TRUE));
        Predicates.and(Predicates.FALSE, Predicates.FALSE).toString(); // check no
                                                                // exception
    }

    @Test
    public void testStrict() {
        assertTrue(Predicates.and(Predicates.TRUE, Predicates.TRUE).isStrict());
        assertFalse(Predicates.and(Predicates.FALSE, Predicates.TRUE, false).isStrict());
    }

    @Test(expected = NullPointerException.class)
    public void testNullLeft() {
        Predicates.and(null, Predicates.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullRight() {
        Predicates.and(Predicates.TRUE, null);
    }

    /* Test any */
    @Test
    @SuppressWarnings("unchecked")
    public void testAnyConstructor() {
        Predicate<?>[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        AnyPredicate filter = Predicates.any(f);
        assertEquals(filter.getPredicates().size(), f.length);
        assertEquals(filter.getPredicates().get(0), f[0]);
        assertEquals(filter.getPredicates().get(1), f[1]);
        assertEquals(filter.getPredicates().get(2), f[2]);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void testAnyNull() {
        Predicate<?>[] f = new Predicate[] { Predicates.TRUE, null, Predicates.TRUE };
        Predicates.any((Predicate[]) f);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAnyIterator() {
        Predicate<?>[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        AnyPredicate<?> filter = Predicates.any((Predicate[]) f);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testAnyToString() {
        // just check that they don't throw exceptions
        Predicate<?>[] f = new Predicate[] { Predicates.TRUE, Predicates.FALSE, Predicates.TRUE };
        Predicates.any((Predicate[]) f).toString();
        Predicates.any(new Predicate[0]).toString();
        Predicates.any(new Predicate[] { Predicates.TRUE }).toString();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAny() {
        assertTrue(Predicates.any(Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.any(Predicates.FALSE).evaluate(null));
        assertTrue(Predicates.any(Predicates.FALSE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.any(Predicates.FALSE, Predicates.FALSE).evaluate(null));
    }

    /* Test not */
    @Test(expected = NullPointerException.class)
    public void testNot() {
        assertFalse(Predicates.not(Predicates.TRUE).evaluate(null));
        assertTrue(Predicates.not(Predicates.FALSE).evaluate(null));
        assertEquals(Predicates.not(Predicates.FALSE).getPredicate(), Predicates.FALSE);
        assertEquals(Predicates.not(Predicates.FALSE).getPredicates(), Collections
                .singletonList(Predicates.FALSE));
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
        assertSame(Predicates.or(Predicates.FALSE, Predicates.TRUE).getLeftPredicate(), Predicates.FALSE);
        assertSame(Predicates.or(Predicates.FALSE, Predicates.TRUE).getRightPredicate(), Predicates.TRUE);
        assertEquals(Predicates.or(Predicates.FALSE, Predicates.TRUE).getPredicates(), Arrays.asList(
                Predicates.FALSE, Predicates.TRUE));
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

    /* Test xor */
    @Test
    public void testXor() {
        assertFalse(Predicates.xor(Predicates.TRUE, Predicates.TRUE).evaluate(null));
        assertTrue(Predicates.xor(Predicates.TRUE, Predicates.FALSE).evaluate(null));
        assertTrue(Predicates.xor(Predicates.FALSE, Predicates.TRUE).evaluate(null));
        assertFalse(Predicates.xor(Predicates.FALSE, Predicates.FALSE).evaluate(null));
        assertSame(Predicates.xor(Predicates.FALSE, Predicates.TRUE).getLeftPredicate(),
                Predicates.FALSE);
        assertSame(Predicates.xor(Predicates.FALSE, Predicates.TRUE).getRightPredicate(),
                Predicates.TRUE);
        assertSame(Predicates.xor(Predicates.FALSE, Predicates.TRUE).getPredicates().get(0),
                Predicates.FALSE);
        assertSame(Predicates.xor(Predicates.FALSE, Predicates.TRUE).getPredicates().get(1),
                Predicates.TRUE);
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

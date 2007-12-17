/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.coconut.operations.LongPredicates.TRUE;
import static org.coconut.operations.LongPredicates.FALSE;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.operations.LongPredicates.EqualsToLongPredicate;
import org.coconut.operations.LongPredicates.GreaterThenOrEqualsLongPredicate;
import org.coconut.operations.LongPredicates.GreaterThenLongPredicate;
import org.coconut.operations.LongPredicates.LessThenOrEqualsLongPredicate;
import org.coconut.operations.LongPredicates.LessThenLongPredicate;
import org.coconut.operations.Ops.LongPredicate;
import org.coconut.operations.Ops.MapperToLong;
import org.coconut.operations.Ops.Predicate;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class LongPredicatesTest {

    /**
     * Tests {@link LongPredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.evaluate(2));
        assertFalse(FALSE.evaluate(Long.MIN_VALUE));
        FALSE.toString(); // does not fail
        assertIsSerializable(FALSE);
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }

    /**
     * Tests {@link LongPredicates#between(long, long)}.
     */
    @Test
    public void between() {
        LongPredicate b = LongPredicates.between(2, 4);
        assertFalse(b.evaluate(1));
        assertTrue(b.evaluate(2));
        assertTrue(b.evaluate(3));
        assertTrue(b.evaluate(4));
        assertFalse(b.evaluate(5));
        assertIsSerializable(b);
    }
    
    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(LongPredicates.and(TRUE, TRUE).evaluate(1));
        assertFalse(LongPredicates.and(TRUE, FALSE).evaluate(1));
        assertFalse(LongPredicates.and(FALSE, TRUE).evaluate(1));
        assertFalse(LongPredicates.and(FALSE, FALSE).evaluate(1));

        LongPredicates.AndLongPredicate p = new LongPredicates.AndLongPredicate(FALSE, TRUE);
        assertSame(p.getLeftPredicate(), FALSE);
        assertSame(p.getRightPredicate(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        LongPredicates.and(FALSE, TestUtil.dummy(LongPredicate.class)).evaluate(1);
    }

    /**
     * Tests that {@link LongPredicates#and(LongPredicate, LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        LongPredicates.and(null, TRUE);
    }

    /**
     * Tests that {@link LongPredicates#and(LongPredicate, LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        LongPredicates.and(TRUE, null);
    }

    /**
     * Tests {@link LongPredicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.evaluate(2));
        assertTrue(TRUE.evaluate(Long.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }

    /**
     * Tests {@link LongPredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(LongPredicates.not(TRUE).evaluate(2));
        assertTrue(LongPredicates.not(FALSE).evaluate(2));
        LongPredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(LongPredicates.not(TRUE));
        assertSame(TRUE,LongPredicates.not(TRUE).getPredicate());
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        LessThenOrEqualsLongPredicate f = LongPredicates.lessThenOrEquals(5);
        assertEquals(5L, f.getLessThenOrEquals());
        assertTrue(f.evaluate(4));
        assertTrue(f.evaluate(5));
        assertFalse(f.evaluate(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
    /**
     * Tests that {@link LongPredicates#not(LongPredicate)} throws a {@link NullPointerException}
     * when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        LongPredicates.not(null);
    }
    @Test
    public void greaterThenOrEquals() {
        GreaterThenOrEqualsLongPredicate f = LongPredicates.greaterThenOrEquals(5);
        assertEquals(5L, f.getGreaterThenOrEquals());
        assertFalse(f.evaluate(4));
        assertTrue(f.evaluate(5));
        assertTrue(f.evaluate(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void greaterThen() {
        GreaterThenLongPredicate f = LongPredicates.greaterThen(5);
        assertEquals(5L, f.getGreaterThen());
        assertFalse(f.evaluate(4));
        assertFalse(f.evaluate(5));
        assertTrue(f.evaluate(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        LessThenLongPredicate f = LongPredicates.lessThen(5);
        assertEquals(5L, f.getLessThen());
        assertTrue(f.evaluate(4));
        assertFalse(f.evaluate(5));
        assertFalse(f.evaluate(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void equalsTo() {
        EqualsToLongPredicate f = LongPredicates.equalsTo(5);
        assertEquals(5L, f.getEqualsTo());
        assertFalse(f.evaluate(4));
        assertTrue(f.evaluate(5));
        assertFalse(f.evaluate(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void mapperPredicate() {
        LongPredicate p = LongPredicates.equalsTo(9);

        MapperToLong<Integer> m = new MapperToLong<Integer>() {
            public long map(Integer from) {
                return from.intValue() * from.intValue();
            }
        };
        Predicate mapped = LongPredicates.mapAndEvaluate(m, p);
        assertFalse(mapped.evaluate(2));
        assertTrue(mapped.evaluate(3));
        assertFalse(mapped.evaluate(4));

        assertSame(p, ((LongPredicates.MapToLongAndEvaluatePredicate) mapped).getPredicate());
        assertSame(m, ((LongPredicates.MapToLongAndEvaluatePredicate) mapped).getMapper());
        mapped.toString();
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE1() {
        LongPredicates.mapAndEvaluate(null, TestUtil.dummy(LongPredicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE2() {
        LongPredicates.mapAndEvaluate(TestUtil.dummy(MapperToLong.class), null);
    }
}

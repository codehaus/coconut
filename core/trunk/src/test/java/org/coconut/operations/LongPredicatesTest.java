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
        Predicate mapped = LongPredicates.mapperPredicate(m, p);
        assertFalse(mapped.evaluate(2));
        assertTrue(mapped.evaluate(3));
        assertFalse(mapped.evaluate(4));

        assertSame(p, ((LongPredicates.MapperToLongPredicate) mapped).getPredicate());
        assertSame(m, ((LongPredicates.MapperToLongPredicate) mapped).getMapper());
        mapped.toString();
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE1() {
        LongPredicates.mapperPredicate(null, TestUtil.dummy(LongPredicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE2() {
        LongPredicates.mapperPredicate(TestUtil.dummy(MapperToLong.class), null);
    }
}

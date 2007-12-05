package org.coconut.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.operations.LongPredicates.EqualsToPredicate;
import org.coconut.operations.LongPredicates.GreaterThenOrEqualsPredicate;
import org.coconut.operations.LongPredicates.GreaterThenPredicate;
import org.coconut.operations.LongPredicates.LessThenOrEqualsPredicate;
import org.coconut.operations.LongPredicates.LessThenPredicate;
import org.coconut.operations.Ops.LongPredicate;
import org.coconut.operations.Ops.MapperToLong;
import org.coconut.operations.Ops.Predicate;
import org.coconut.test.MockTestCase;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class LongPredicatesTest {
    /* Test greater then */

    @Test
    public void lessThenOrEquals() {
        LessThenOrEqualsPredicate f = LongPredicates.lessThenOrEquals(5);
        assertEquals(5L, f.getLessThenOrEquals());
        assertTrue(f.evaluate(4));
        assertTrue(f.evaluate(5));
        assertFalse(f.evaluate(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void greaterThenOrEquals() {
        GreaterThenOrEqualsPredicate f = LongPredicates.greaterThenOrEquals(5);
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
        GreaterThenPredicate f = LongPredicates.greaterThen(5);
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
        LessThenPredicate f = LongPredicates.lessThen(5);
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
        EqualsToPredicate f = LongPredicates.equalsTo(5);
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

        assertSame(p, ((LongPredicates.MapperPredicate) mapped).getPredicate());
        assertSame(m, ((LongPredicates.MapperPredicate) mapped).getMapper());
        mapped.toString();
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE1() {
        LongPredicates.mapperPredicate(null, MockTestCase.mockDummy(LongPredicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE2() {
        LongPredicates.mapperPredicate(MockTestCase.mockDummy(MapperToLong.class), null);
    }
}

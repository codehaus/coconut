/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import static org.codehaus.cake.ops.LongPredicates.FALSE;
import static org.codehaus.cake.ops.LongPredicates.TRUE;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.codehaus.cake.ops.LongPredicates.EqualsToLongPredicate;
import org.codehaus.cake.ops.LongPredicates.GreaterThenLongPredicate;
import org.codehaus.cake.ops.LongPredicates.GreaterThenOrEqualsLongPredicate;
import org.codehaus.cake.ops.LongPredicates.LessThenLongPredicate;
import org.codehaus.cake.ops.LongPredicates.LessThenOrEqualsLongPredicate;
import org.codehaus.cake.ops.LongPredicates.NotLongPredicate;
import org.codehaus.cake.ops.Ops.LongPredicate;
import org.codehaus.cake.ops.Ops.ObjectToLong;
import org.codehaus.cake.ops.Ops.Predicate;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

public class LongPredicatesTest {

    /**
     * Tests {@link LongPredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.op(2));
        assertFalse(FALSE.op(Long.MIN_VALUE));
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
        assertFalse(b.op(1));
        assertTrue(b.op(2));
        assertTrue(b.op(3));
        assertTrue(b.op(4));
        assertFalse(b.op(5));
        assertIsSerializable(b);
    }

    /**
     * Tests {@link LongPredicates#or(LongPredicate, LongPredicate)}.
     */
    @Test
    public void or() {
        assertTrue(LongPredicates.or(TRUE, TRUE).op(1));
        assertTrue(LongPredicates.or(TRUE, FALSE).op(1));
        assertTrue(LongPredicates.or(FALSE, TRUE).op(1));
        assertFalse(LongPredicates.or(FALSE, FALSE).op(1));

        LongPredicates.OrLongPredicate p = new LongPredicates.OrLongPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        LongPredicates.or(TRUE, TestUtil.dummy(LongPredicate.class)).op(1);
    }

    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(LongPredicates.and(TRUE, TRUE).op(1));
        assertFalse(LongPredicates.and(TRUE, FALSE).op(1));
        assertFalse(LongPredicates.and(FALSE, TRUE).op(1));
        assertFalse(LongPredicates.and(FALSE, FALSE).op(1));

        LongPredicates.AndLongPredicate p = new LongPredicates.AndLongPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        LongPredicates.and(FALSE, TestUtil.dummy(LongPredicate.class)).op(1);
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
     * Tests that {@link LongPredicates#or(LongPredicate, LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        LongPredicates.or(null, TRUE);
    }

    /**
     * Tests that {@link LongPredicates#or(LongPredicate, LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE1() {
        LongPredicates.or(TRUE, null);
    }

    /**
     * Tests {@link LongPredicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.op(2));
        assertTrue(TRUE.op(Long.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }

    /**
     * Tests {@link LongPredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(LongPredicates.not(TRUE).op(2));
        assertTrue(LongPredicates.not(FALSE).op(2));
        LongPredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(LongPredicates.not(TRUE));
        assertSame(TRUE, ((NotLongPredicate) LongPredicates.not(TRUE)).getPredicate());
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        LongPredicate f = LongPredicates.lessThenOrEquals(5);
        assertEquals(5L, new LessThenOrEqualsLongPredicate(5).getLessThenOrEquals());
        assertTrue(f.op(4));
        assertTrue(f.op(5));
        assertFalse(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /**
     * Tests that {@link LongPredicates#not(LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        LongPredicates.not(null);
    }

    @Test
    public void greaterThenOrEquals() {
        LongPredicate f = LongPredicates.greaterThenOrEquals(5);
        assertEquals(5L, new GreaterThenOrEqualsLongPredicate(5).getGreaterThenOrEquals());
        assertFalse(f.op(4));
        assertTrue(f.op(5));
        assertTrue(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void greaterThen() {
        LongPredicate f = LongPredicates.greaterThen(5);
        assertEquals(5L, new GreaterThenLongPredicate(5).getGreaterThen());
        assertFalse(f.op(4));
        assertFalse(f.op(5));
        assertTrue(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        LongPredicate f = LongPredicates.lessThen(5);
        assertEquals(5L, new LessThenLongPredicate(5).getLessThen());
        assertTrue(f.op(4));
        assertFalse(f.op(5));
        assertFalse(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void equalsTo() {
        LongPredicate f = LongPredicates.equalsToPredicate(5);
        assertEquals(5L, new EqualsToLongPredicate(5).getEqualsTo());
        assertFalse(f.op(4));
        assertTrue(f.op(5));
        assertFalse(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void mapperPredicate() {
        LongPredicate p = LongPredicates.equalsToPredicate(9);

        ObjectToLong<Integer> m = new ObjectToLong<Integer>() {
            public long op(Integer from) {
                return from.intValue() * from.intValue();
            }
        };
        Predicate mapped = LongPredicates.mapAndEvaluate(m, p);
        assertFalse(mapped.op(2));
        assertTrue(mapped.op(3));
        assertFalse(mapped.op(4));

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
        LongPredicates.mapAndEvaluate(TestUtil.dummy(ObjectToLong.class), null);
    }
}

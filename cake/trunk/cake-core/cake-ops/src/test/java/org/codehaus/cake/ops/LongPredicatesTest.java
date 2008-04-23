/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
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
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

/**
 * Various tests for {@link LongPredicates}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: LongPredicatesTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class LongPredicatesTest {

    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(LongPredicates.and(TRUE, TRUE).op(1L));
        assertFalse(LongPredicates.and(TRUE, FALSE).op(1L));
        assertFalse(LongPredicates.and(FALSE, TRUE).op(1L));
        assertFalse(LongPredicates.and(FALSE, FALSE).op(1L));

        LongPredicates.AndLongPredicate p = new LongPredicates.AndLongPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        LongPredicates.and(FALSE, TestUtil.dummy(LongPredicate.class)).op(1L);
    }

    /**
     * Tests that {@link LongPredicates#and(LongPredicate, LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        LongPredicates.and(null, TRUE);
    }

    /**
     * Tests that {@link LongPredicates#and(LongPredicate, LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        LongPredicates.and(TRUE, null);
    }

    /* Test greater then */
    @Test
    public void equalsTo() {
        LongPredicate f = LongPredicates.equalsTo(5L);
        assertEquals(5L, new EqualsToLongPredicate(5L).getEqualsTo());
        assertFalse(f.op(4L));
        assertTrue(f.op(5L));
        assertFalse(f.op(6L));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /**
     * Tests {@link LongPredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.op(2L));
        assertFalse(FALSE.op(Long.MIN_VALUE));
        FALSE.toString(); // does not fail
        assertIsSerializable(FALSE);
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }

    /* Test greater then */
    @Test
    public void greaterThen() {
        LongPredicate f = LongPredicates.greaterThen(5L);
        assertEquals(5L, new GreaterThenLongPredicate(5L).getGreaterThen());
        assertFalse(f.op(4L));
        assertFalse(f.op(5L));
        assertTrue(f.op(6L));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void greaterThenOrEquals() {
        LongPredicate f = LongPredicates.greaterThenOrEquals(5L);
        assertEquals(5L, new GreaterThenOrEqualsLongPredicate(5L).getGreaterThenOrEquals());
        assertFalse(f.op(4L));
        assertTrue(f.op(5L));
        assertTrue(f.op(6L));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        LongPredicate f = LongPredicates.lessThen(5L);
        assertEquals(5L, new LessThenLongPredicate(5L).getLessThen());
        assertTrue(f.op(4L));
        assertFalse(f.op(5L));
        assertFalse(f.op(6L));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        LongPredicate f = LongPredicates.lessThenOrEquals(5L);
        assertEquals(5L, new LessThenOrEqualsLongPredicate(5L).getLessThenOrEquals());
        assertTrue(f.op(4L));
        assertTrue(f.op(5L));
        assertFalse(f.op(6L));

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

    /**
     * Tests {@link LongPredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(LongPredicates.not(TRUE).op(2L));
        assertTrue(LongPredicates.not(FALSE).op(2L));
        LongPredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(LongPredicates.not(TRUE));
        assertSame(TRUE, ((NotLongPredicate) LongPredicates.not(TRUE)).getPredicate());
    }

    /**
     * Tests {@link LongPredicates#or(LongPredicate, LongPredicate)}.
     */
    @Test
    public void or() {
        assertTrue(LongPredicates.or(TRUE, TRUE).op(1L));
        assertTrue(LongPredicates.or(TRUE, FALSE).op(1L));
        assertTrue(LongPredicates.or(FALSE, TRUE).op(1L));
        assertFalse(LongPredicates.or(FALSE, FALSE).op(1L));

        LongPredicates.OrLongPredicate p = new LongPredicates.OrLongPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        LongPredicates.or(TRUE, TestUtil.dummy(LongPredicate.class)).op(1L);
    }

    /**
     * Tests that {@link LongPredicates#or(LongPredicate, LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        LongPredicates.or(null, TRUE);
    }

    /**
     * Tests that {@link LongPredicates#or(LongPredicate, LongPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code> argument.
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
        assertTrue(TRUE.op(2L));
        assertTrue(TRUE.op(Long.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }
}

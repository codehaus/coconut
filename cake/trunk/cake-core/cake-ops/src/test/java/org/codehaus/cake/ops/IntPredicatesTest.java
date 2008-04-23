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

import static org.codehaus.cake.ops.IntPredicates.FALSE;
import static org.codehaus.cake.ops.IntPredicates.TRUE;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.codehaus.cake.ops.IntPredicates.EqualsToIntPredicate;
import org.codehaus.cake.ops.IntPredicates.GreaterThenIntPredicate;
import org.codehaus.cake.ops.IntPredicates.GreaterThenOrEqualsIntPredicate;
import org.codehaus.cake.ops.IntPredicates.LessThenIntPredicate;
import org.codehaus.cake.ops.IntPredicates.LessThenOrEqualsIntPredicate;
import org.codehaus.cake.ops.IntPredicates.NotIntPredicate;
import org.codehaus.cake.ops.Ops.IntPredicate;
import org.codehaus.cake.ops.Ops.LongPredicate;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

/**
 * Various tests for {@link IntPredicates}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: IntPredicatesTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class IntPredicatesTest {

    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(IntPredicates.and(TRUE, TRUE).op(1));
        assertFalse(IntPredicates.and(TRUE, FALSE).op(1));
        assertFalse(IntPredicates.and(FALSE, TRUE).op(1));
        assertFalse(IntPredicates.and(FALSE, FALSE).op(1));

        IntPredicates.AndIntPredicate p = new IntPredicates.AndIntPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        IntPredicates.and(FALSE, TestUtil.dummy(IntPredicate.class)).op(1);
    }

    /**
     * Tests that {@link IntPredicates#and(IntPredicate, IntPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        IntPredicates.and(null, TRUE);
    }

    /**
     * Tests that {@link IntPredicates#and(IntPredicate, IntPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        IntPredicates.and(TRUE, null);
    }

    /* Test greater then */
    @Test
    public void equalsTo() {
        IntPredicate f = IntPredicates.equalsTo(5);
        assertEquals(5, new EqualsToIntPredicate(5).getEqualsTo());
        assertFalse(f.op(4));
        assertTrue(f.op(5));
        assertFalse(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /**
     * Tests {@link IntPredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.op(2));
        assertFalse(FALSE.op(Integer.MIN_VALUE));
        FALSE.toString(); // does not fail
        assertIsSerializable(FALSE);
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }

    /* Test greater then */
    @Test
    public void greaterThen() {
        IntPredicate f = IntPredicates.greaterThen(5);
        assertEquals(5, new GreaterThenIntPredicate(5).getGreaterThen());
        assertFalse(f.op(4));
        assertFalse(f.op(5));
        assertTrue(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void greaterThenOrEquals() {
        IntPredicate f = IntPredicates.greaterThenOrEquals(5);
        assertEquals(5, new GreaterThenOrEqualsIntPredicate(5).getGreaterThenOrEquals());
        assertFalse(f.op(4));
        assertTrue(f.op(5));
        assertTrue(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        IntPredicate f = IntPredicates.lessThen(5);
        assertEquals(5, new LessThenIntPredicate(5).getLessThen());
        assertTrue(f.op(4));
        assertFalse(f.op(5));
        assertFalse(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        IntPredicate f = IntPredicates.lessThenOrEquals(5);
        assertEquals(5, new LessThenOrEqualsIntPredicate(5).getLessThenOrEquals());
        assertTrue(f.op(4));
        assertTrue(f.op(5));
        assertFalse(f.op(6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /**
     * Tests that {@link IntPredicates#not(IntPredicate)} throws a {@link NullPointerException} when
     * invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        IntPredicates.not(null);
    }

    /**
     * Tests {@link IntPredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(IntPredicates.not(TRUE).op(2));
        assertTrue(IntPredicates.not(FALSE).op(2));
        IntPredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(IntPredicates.not(TRUE));
        assertSame(TRUE, ((NotIntPredicate) IntPredicates.not(TRUE)).getPredicate());
    }

    /**
     * Tests {@link IntPredicates#or(IntPredicate, IntPredicate)}.
     */
    @Test
    public void or() {
        assertTrue(IntPredicates.or(TRUE, TRUE).op(1));
        assertTrue(IntPredicates.or(TRUE, FALSE).op(1));
        assertTrue(IntPredicates.or(FALSE, TRUE).op(1));
        assertFalse(IntPredicates.or(FALSE, FALSE).op(1));

        IntPredicates.OrIntPredicate p = new IntPredicates.OrIntPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        IntPredicates.or(TRUE, TestUtil.dummy(IntPredicate.class)).op(1);
    }

    /**
     * Tests that {@link IntPredicates#or(IntPredicate, IntPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        IntPredicates.or(null, TRUE);
    }

    /**
     * Tests that {@link IntPredicates#or(IntPredicate, IntPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE1() {
        IntPredicates.or(TRUE, null);
    }

    /**
     * Tests {@link IntPredicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.op(2));
        assertTrue(TRUE.op(Integer.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }
}

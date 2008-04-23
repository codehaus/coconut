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

import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.codehaus.cake.test.util.TestUtil.serializeAndUnserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.codehaus.cake.ops.Ops.LongReducer;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

/**
 * Various tests for {@link LongOps}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: LongOpsTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class LongOpsTest {

    /**
     * Tests {@link LongOps#ABS_OP}.
     */
    @Test
    public void abs() {
        assertEquals(1L, LongOps.ABS_OP.op(-1L));
        assertEquals(1L, LongOps.ABS_OP.op(1L));
        assertSame(LongOps.ABS_OP, LongOps.abs());
        LongOps.ABS_OP.toString(); // does not fail
        TestUtil.assertSingletonSerializable(LongOps.ABS_OP);
    }

    /**
     * Tests {@link LongOps#ADD_REDUCER} and {@link LongOps#add()}.
     */
    @Test
    public void add() {
        assertEquals(3L, LongOps.ADD_REDUCER.op(1L, 2L));
        assertEquals(3L, LongOps.ADD_REDUCER.op(2L, 1L));
        assertSame(LongOps.ADD_REDUCER, LongOps.add());
        LongOps.ADD_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(LongOps.ADD_REDUCER);
    }

    /**
     * Tests {@link LongOps#add(long)}.
     */
    @Test
    public void addArg() {
        assertEquals(9L, LongOps.add(5L).op(4L));
        assertEquals(9L, LongOps.add(4L).op(5L));
        LongOps.add(9L).toString(); // does not fail
        assertIsSerializable(LongOps.add(5L));
        assertEquals(-9L, serializeAndUnserialize(LongOps.add(12L)).op(-21L));
    }

    /**
     * Tests {@link {Type}Ops#COMPARATOR}.
     */
    @Test
    public void comparator() {
        assertEquals(0, LongOps.COMPARATOR.compare(1L, 1L));
        assertTrue(LongOps.COMPARATOR.compare(2L, 1L) > 0);
        assertTrue(LongOps.COMPARATOR.compare(1L, 2L) < 0);
        LongOps.COMPARATOR.toString(); // does not fail
        TestUtil.assertSingletonSerializable(LongOps.COMPARATOR);
    }

    /**
     * Tests {@link {Type}Ops#REVERSE_COMPARATOR}.
     */
    @Test
    public void comparatorReverse() {
        assertEquals(0, LongOps.REVERSE_COMPARATOR.compare(1L, 1L));
        assertTrue(LongOps.REVERSE_COMPARATOR.compare(2L, 1L) < 0);
        assertTrue(LongOps.REVERSE_COMPARATOR.compare(1L, 2L) > 0);
        LongOps.REVERSE_COMPARATOR.toString(); // does not fail
        TestUtil.assertSingletonSerializable(LongOps.REVERSE_COMPARATOR);
    }

    /**
     * Tests {@link LongOps#DIVIDE_REDUCER} and {@link LongOps#divide()}.
     */
    @Test
    public void divide() {
        assertEquals(4L, LongOps.DIVIDE_REDUCER.op(16L, 4L));
        assertEquals(-4L, LongOps.DIVIDE_REDUCER.op(-8L, 2L));
        assertSame(LongOps.DIVIDE_REDUCER, LongOps.divide());
        LongOps.DIVIDE_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(LongOps.DIVIDE_REDUCER);
    }

    /**
     * Tests {@link LongOps#divide(long)}.
     */
    @Test
    public void divideArg() {
        assertEquals(-2L, LongOps.divide(4L).op(-8L));
        assertEquals(5L, LongOps.divide(5L).op(25L));
        LongOps.divide(9L).toString(); // does not fail
        assertIsSerializable(LongOps.divide(5L));
        assertEquals(-4L, serializeAndUnserialize(LongOps.divide(4L)).op(-16L));
    }

    /**
     * Tests {@link Reducers#MAX_REDUCER}.
     */
    @Test
    public void doubleMaxReducer() {
        assertEquals(2L, LongOps.MAX_REDUCER.op(2L, 1L));
        assertEquals(2L, LongOps.MAX_REDUCER.op(1L, 2L));
        assertEquals(2L, LongOps.MAX_REDUCER.op(2L, 2L));
        LongOps.MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(LongOps.MAX_REDUCER);
        TestUtil.assertSingletonSerializable(LongOps.MAX_REDUCER);
    }

    /**
     * Tests {@link Reducers#doubleMaxReducer(org.codehaus.cake.ops.Ops.DoubleComparator)}
     */
    @Test
    public void doubleMaxReducerComparator() {
        LongReducer r = LongOps.max(LongOps.COMPARATOR);
        assertEquals(2L, r.op(1L, 2L));
        assertEquals(2L, r.op(2L, 1L));
        assertEquals(2L, r.op(2L, 2L));
        assertEquals(2L, serializeAndUnserialize(r).op(1L, 2L));
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void maxNPE() {
        LongOps.max(null);
    }

    /**
     * Tests {@link LongOps#MIN_REDUCER}.
     */
    @Test
    public void min() {
        assertEquals(1L, LongOps.MIN_REDUCER.op(1L, 2L));
        assertEquals(1L, LongOps.MIN_REDUCER.op(2L, 1L));
        assertEquals(1L, LongOps.MIN_REDUCER.op(1L, 1L));
        LongOps.MIN_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(LongOps.MIN_REDUCER);
    }

    /**
     * Tests {@link LongOps#min}.
     */
    @Test
    public void minArg() {
        LongReducer r = LongOps.min(LongOps.COMPARATOR);
        assertEquals(1L, r.op(1L, 2L));
        assertEquals(1L, r.op(2L, 1L));
        assertEquals(1L, r.op(1L, 1L));
        r.toString(); // does not fail
        assertEquals(1L, serializeAndUnserialize(r).op(1L, 2L));
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void minNPE() {
        LongOps.min(null);
    }

    /**
     * Tests {@link LongOps#MULTIPLY_REDUCER} and {@link LongOps#multiply()}.
     */
    @Test
    public void multiply() {
        assertEquals(16L, LongOps.MULTIPLY_REDUCER.op(4L, 4L));
        assertEquals(-8L, LongOps.MULTIPLY_REDUCER.op(-4L, 2L));
        assertSame(LongOps.MULTIPLY_REDUCER, LongOps.multiply());
        LongOps.MULTIPLY_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(LongOps.MULTIPLY_REDUCER);
    }

    /**
     * Tests {@link LongOps#multiply(long)}.
     */
    @Test
    public void multiplyArg() {
        assertEquals(-8L, LongOps.multiply(4L).op(-2L));
        assertEquals(25L, LongOps.multiply(5L).op(5L));
        LongOps.multiply(9L).toString(); // does not fail
        assertIsSerializable(LongOps.multiply(5L));
        assertEquals(-16L, serializeAndUnserialize(LongOps.multiply(4L)).op(-4L));
    }

    /**
     * Tests {@link LongOps#reverseOrder}.
     */
    @Test
    public void reverseOrder() {
        assertEquals(0, LongOps.reverseOrder(LongOps.COMPARATOR).compare(1L, 1L));
        assertTrue(LongOps.reverseOrder(LongOps.COMPARATOR).compare(2L, 1L) < 0);
        assertTrue(LongOps.reverseOrder(LongOps.COMPARATOR).compare(1L, 2L) > 0);
        LongOps.reverseOrder(LongOps.COMPARATOR).toString(); // does not fail
        assertIsSerializable(LongOps.reverseOrder(LongOps.COMPARATOR));
        assertTrue(serializeAndUnserialize(LongOps.reverseOrder(LongOps.COMPARATOR))
                .compare(2L, 1L) < 0);
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrder_NPE() {
        LongOps.reverseOrder(null);
    }

    /**
     * Tests {@link LongOps#SUBTRACT_REDUCER} and {@link LongOps#subtract()}.
     */
    @Test
    public void subtract() {
        assertEquals(-1L, LongOps.SUBTRACT_REDUCER.op(1L, 2L));
        assertEquals(1L, LongOps.SUBTRACT_REDUCER.op(2L, 1L));
        assertSame(LongOps.SUBTRACT_REDUCER, LongOps.subtract());
        LongOps.SUBTRACT_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(LongOps.SUBTRACT_REDUCER);
    }

    /**
     * Tests {@link LongOps#subtract(long)}.
     */
    @Test
    public void subtractArg() {
        assertEquals(-1L, LongOps.subtract(5L).op(4L));
        assertEquals(1L, LongOps.subtract(4L).op(5L));
        LongOps.subtract(9L).toString(); // does not fail
        assertIsSerializable(LongOps.subtract(5L));
        assertEquals(-33L, serializeAndUnserialize(LongOps.subtract(12L)).op(-21L));
    }
}

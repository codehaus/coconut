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

import org.codehaus.cake.ops.Ops.IntReducer;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

/**
 * Various tests for {@link IntOps}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: IntOpsTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class IntOpsTest {

    /**
     * Tests {@link IntOps#ABS_OP}.
     */
    @Test
    public void abs() {
        assertEquals(1, IntOps.ABS_OP.op(-1));
        assertEquals(1, IntOps.ABS_OP.op(1));
        assertSame(IntOps.ABS_OP, IntOps.abs());
        IntOps.ABS_OP.toString(); // does not fail
        TestUtil.assertSingletonSerializable(IntOps.ABS_OP);
    }

    /**
     * Tests {@link IntOps#ADD_REDUCER} and {@link IntOps#add()}.
     */
    @Test
    public void add() {
        assertEquals(3, IntOps.ADD_REDUCER.op(1, 2));
        assertEquals(3, IntOps.ADD_REDUCER.op(2, 1));
        assertSame(IntOps.ADD_REDUCER, IntOps.add());
        IntOps.ADD_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(IntOps.ADD_REDUCER);
    }

    /**
     * Tests {@link IntOps#add(int)}.
     */
    @Test
    public void addArg() {
        assertEquals(9, IntOps.add(5).op(4));
        assertEquals(9, IntOps.add(4).op(5));
        IntOps.add(9).toString(); // does not fail
        assertIsSerializable(IntOps.add(5));
        assertEquals(-9, serializeAndUnserialize(IntOps.add(12)).op(-21));
    }

    /**
     * Tests {@link {Type}Ops#COMPARATOR}.
     */
    @Test
    public void comparator() {
        assertEquals(0, IntOps.COMPARATOR.compare(1, 1));
        assertTrue(IntOps.COMPARATOR.compare(2, 1) > 0);
        assertTrue(IntOps.COMPARATOR.compare(1, 2) < 0);
        IntOps.COMPARATOR.toString(); // does not fail
        TestUtil.assertSingletonSerializable(IntOps.COMPARATOR);
    }

    /**
     * Tests {@link {Type}Ops#REVERSE_COMPARATOR}.
     */
    @Test
    public void comparatorReverse() {
        assertEquals(0, IntOps.REVERSE_COMPARATOR.compare(1, 1));
        assertTrue(IntOps.REVERSE_COMPARATOR.compare(2, 1) < 0);
        assertTrue(IntOps.REVERSE_COMPARATOR.compare(1, 2) > 0);
        IntOps.REVERSE_COMPARATOR.toString(); // does not fail
        TestUtil.assertSingletonSerializable(IntOps.REVERSE_COMPARATOR);
    }

    /**
     * Tests {@link IntOps#DIVIDE_REDUCER} and {@link IntOps#divide()}.
     */
    @Test
    public void divide() {
        assertEquals(4, IntOps.DIVIDE_REDUCER.op(16, 4));
        assertEquals(-4, IntOps.DIVIDE_REDUCER.op(-8, 2));
        assertSame(IntOps.DIVIDE_REDUCER, IntOps.divide());
        IntOps.DIVIDE_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(IntOps.DIVIDE_REDUCER);
    }

    /**
     * Tests {@link IntOps#divide(int)}.
     */
    @Test
    public void divideArg() {
        assertEquals(-2, IntOps.divide(4).op(-8));
        assertEquals(5, IntOps.divide(5).op(25));
        IntOps.divide(9).toString(); // does not fail
        assertIsSerializable(IntOps.divide(5));
        assertEquals(-4, serializeAndUnserialize(IntOps.divide(4)).op(-16));
    }

    /**
     * Tests {@link Reducers#MAX_REDUCER}.
     */
    @Test
    public void doubleMaxReducer() {
        assertEquals(2, IntOps.MAX_REDUCER.op(2, 1));
        assertEquals(2, IntOps.MAX_REDUCER.op(1, 2));
        assertEquals(2, IntOps.MAX_REDUCER.op(2, 2));
        IntOps.MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(IntOps.MAX_REDUCER);
        TestUtil.assertSingletonSerializable(IntOps.MAX_REDUCER);
    }

    /**
     * Tests {@link Reducers#doubleMaxReducer(org.codehaus.cake.ops.Ops.DoubleComparator)}
     */
    @Test
    public void doubleMaxReducerComparator() {
        IntReducer r = IntOps.max(IntOps.COMPARATOR);
        assertEquals(2, r.op(1, 2));
        assertEquals(2, r.op(2, 1));
        assertEquals(2, r.op(2, 2));
        assertEquals(2, serializeAndUnserialize(r).op(1, 2));
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void maxNPE() {
        IntOps.max(null);
    }

    /**
     * Tests {@link IntOps#MIN_REDUCER}.
     */
    @Test
    public void min() {
        assertEquals(1, IntOps.MIN_REDUCER.op(1, 2));
        assertEquals(1, IntOps.MIN_REDUCER.op(2, 1));
        assertEquals(1, IntOps.MIN_REDUCER.op(1, 1));
        IntOps.MIN_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(IntOps.MIN_REDUCER);
    }

    /**
     * Tests {@link IntOps#min}.
     */
    @Test
    public void minArg() {
        IntReducer r = IntOps.min(IntOps.COMPARATOR);
        assertEquals(1, r.op(1, 2));
        assertEquals(1, r.op(2, 1));
        assertEquals(1, r.op(1, 1));
        r.toString(); // does not fail
        assertEquals(1, serializeAndUnserialize(r).op(1, 2));
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void minNPE() {
        IntOps.min(null);
    }

    /**
     * Tests {@link IntOps#MULTIPLY_REDUCER} and {@link IntOps#multiply()}.
     */
    @Test
    public void multiply() {
        assertEquals(16, IntOps.MULTIPLY_REDUCER.op(4, 4));
        assertEquals(-8, IntOps.MULTIPLY_REDUCER.op(-4, 2));
        assertSame(IntOps.MULTIPLY_REDUCER, IntOps.multiply());
        IntOps.MULTIPLY_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(IntOps.MULTIPLY_REDUCER);
    }

    /**
     * Tests {@link IntOps#multiply(int)}.
     */
    @Test
    public void multiplyArg() {
        assertEquals(-8, IntOps.multiply(4).op(-2));
        assertEquals(25, IntOps.multiply(5).op(5));
        IntOps.multiply(9).toString(); // does not fail
        assertIsSerializable(IntOps.multiply(5));
        assertEquals(-16, serializeAndUnserialize(IntOps.multiply(4)).op(-4));
    }

    /**
     * Tests {@link IntOps#reverseOrder}.
     */
    @Test
    public void reverseOrder() {
        assertEquals(0, IntOps.reverseOrder(IntOps.COMPARATOR).compare(1, 1));
        assertTrue(IntOps.reverseOrder(IntOps.COMPARATOR).compare(2, 1) < 0);
        assertTrue(IntOps.reverseOrder(IntOps.COMPARATOR).compare(1, 2) > 0);
        IntOps.reverseOrder(IntOps.COMPARATOR).toString(); // does not fail
        assertIsSerializable(IntOps.reverseOrder(IntOps.COMPARATOR));
        assertTrue(serializeAndUnserialize(IntOps.reverseOrder(IntOps.COMPARATOR)).compare(2, 1) < 0);
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrder_NPE() {
        IntOps.reverseOrder(null);
    }

    /**
     * Tests {@link IntOps#SUBTRACT_REDUCER} and {@link IntOps#subtract()}.
     */
    @Test
    public void subtract() {
        assertEquals(-1, IntOps.SUBTRACT_REDUCER.op(1, 2));
        assertEquals(1, IntOps.SUBTRACT_REDUCER.op(2, 1));
        assertSame(IntOps.SUBTRACT_REDUCER, IntOps.subtract());
        IntOps.SUBTRACT_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(IntOps.SUBTRACT_REDUCER);
    }

    /**
     * Tests {@link IntOps#subtract(int)}.
     */
    @Test
    public void subtractArg() {
        assertEquals(-1, IntOps.subtract(5).op(4));
        assertEquals(1, IntOps.subtract(4).op(5));
        IntOps.subtract(9).toString(); // does not fail
        assertIsSerializable(IntOps.subtract(5));
        assertEquals(-33, serializeAndUnserialize(IntOps.subtract(12)).op(-21));
    }
}

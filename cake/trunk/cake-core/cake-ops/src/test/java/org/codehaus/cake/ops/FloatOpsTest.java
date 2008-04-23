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

import org.codehaus.cake.ops.Ops.FloatReducer;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

/**
 * Various tests for {@link FloatOps}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: FloatOpsTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class FloatOpsTest {

    /**
     * Tests {@link FloatOps#ABS_OP}.
     */
    @Test
    public void abs() {
        assertEquals(1F, FloatOps.ABS_OP.op(-1F), 0);
        assertEquals(1F, FloatOps.ABS_OP.op(1F), 0);
        assertSame(FloatOps.ABS_OP, FloatOps.abs());
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.ABS_OP.op(Float.POSITIVE_INFINITY), 0);
        assertEquals(Float.NaN, FloatOps.ABS_OP.op(Float.NaN), 0);
        FloatOps.ABS_OP.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.ABS_OP);
    }

    /**
     * Tests {@link FloatOps#ADD_REDUCER} and {@link FloatOps#add()}.
     */
    @Test
    public void add() {
        assertEquals(3F, FloatOps.ADD_REDUCER.op(1F, 2F), 0);
        assertEquals(3F, FloatOps.ADD_REDUCER.op(2F, 1F), 0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.ADD_REDUCER.op(1, Float.POSITIVE_INFINITY),
                0);
        assertEquals(Float.NaN, FloatOps.ADD_REDUCER.op(1, Float.NaN), 0);
        assertSame(FloatOps.ADD_REDUCER, FloatOps.add());
        FloatOps.ADD_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.ADD_REDUCER);
    }

    /**
     * Tests {@link FloatOps#add(float)}.
     */
    @Test
    public void addArg() {
        assertEquals(9F, FloatOps.add(5F).op(4F), 0);
        assertEquals(9F, FloatOps.add(4F).op(5F), 0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.add(5).op(Float.POSITIVE_INFINITY), 0);
        assertEquals(Float.NaN, FloatOps.add(5).op(Float.NaN), 0);
        FloatOps.add(9F).toString(); // does not fail
        assertIsSerializable(FloatOps.add(5F));
        assertEquals(-9F, serializeAndUnserialize(FloatOps.add(12F)).op(-21F), 0);
    }

    /**
     * Tests {@link {Type}Ops#COMPARATOR}.
     */
    @Test
    public void comparator() {
        assertEquals(0, FloatOps.COMPARATOR.compare(1F, 1F));
        assertEquals(0, FloatOps.COMPARATOR.compare(Float.NaN, Float.NaN));
        assertTrue(FloatOps.COMPARATOR.compare(2F, 1F) > 0);
        assertTrue(FloatOps.COMPARATOR.compare(1F, 2F) < 0);
        FloatOps.COMPARATOR.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.COMPARATOR);
    }

    /**
     * Tests {@link {Type}Ops#REVERSE_COMPARATOR}.
     */
    @Test
    public void comparatorReverse() {
        assertEquals(0, FloatOps.REVERSE_COMPARATOR.compare(1F, 1F));
        assertEquals(0, FloatOps.REVERSE_COMPARATOR.compare(Float.NaN, Float.NaN));
        assertTrue(FloatOps.REVERSE_COMPARATOR.compare(2F, 1F) < 0);
        assertTrue(FloatOps.REVERSE_COMPARATOR.compare(1F, 2F) > 0);
        FloatOps.REVERSE_COMPARATOR.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.REVERSE_COMPARATOR);
    }

    /**
     * Tests {@link FloatOps#DIVIDE_REDUCER} and {@link FloatOps#divide()}.
     */
    @Test
    public void divide() {
        assertEquals(4F, FloatOps.DIVIDE_REDUCER.op(16F, 4F), 0);
        assertEquals(-4F, FloatOps.DIVIDE_REDUCER.op(-8F, 2F), 0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.DIVIDE_REDUCER
                .op(Float.POSITIVE_INFINITY, 1), 0);
        assertEquals(Float.NaN, FloatOps.DIVIDE_REDUCER.op(1, Float.NaN), 0);
        assertSame(FloatOps.DIVIDE_REDUCER, FloatOps.divide());
        FloatOps.DIVIDE_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.DIVIDE_REDUCER);
    }

    /**
     * Tests {@link FloatOps#divide(float)}.
     */
    @Test
    public void divideArg() {
        assertEquals(-2F, FloatOps.divide(4F).op(-8F), 0);
        assertEquals(5F, FloatOps.divide(5F).op(25F), 0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.divide(5).op(Float.POSITIVE_INFINITY), 0);
        assertEquals(Float.NaN, FloatOps.divide(5).op(Float.NaN), 0);
        FloatOps.divide(9F).toString(); // does not fail
        assertIsSerializable(FloatOps.divide(5F));
        assertEquals(-4F, serializeAndUnserialize(FloatOps.divide(4F)).op(-16F), 0);
    }

    /**
     * Tests {@link Reducers#MAX_REDUCER}.
     */
    @Test
    public void doubleMaxReducer() {
        assertEquals(2F, FloatOps.MAX_REDUCER.op(2F, 1F), 0);
        assertEquals(2F, FloatOps.MAX_REDUCER.op(1F, 2F), 0);
        assertEquals(2F, FloatOps.MAX_REDUCER.op(2F, 2F), 0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.MAX_REDUCER.op(1F, Float.POSITIVE_INFINITY),
                0);
        assertEquals(Float.NaN, FloatOps.MAX_REDUCER.op(1F, Float.NaN), 0);
        FloatOps.MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(FloatOps.MAX_REDUCER);
        TestUtil.assertSingletonSerializable(FloatOps.MAX_REDUCER);
    }

    /**
     * Tests {@link Reducers#doubleMaxReducer(org.codehaus.cake.ops.Ops.DoubleComparator)}
     */
    @Test
    public void doubleMaxReducerComparator() {
        FloatReducer r = FloatOps.max(FloatOps.COMPARATOR);
        assertEquals(2F, r.op(1F, 2F), 0);
        assertEquals(2F, r.op(2F, 1F), 0);
        assertEquals(2F, r.op(2F, 2F), 0);
        assertEquals(Float.POSITIVE_INFINITY, r.op(1F, Float.POSITIVE_INFINITY), 0);
        assertEquals(Float.NaN, r.op(1F, Float.NaN), 0);
        assertEquals(2F, serializeAndUnserialize(r).op(1F, 2F), 0);
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void maxNPE() {
        FloatOps.max(null);
    }

    /**
     * Tests {@link FloatOps#MIN_REDUCER}.
     */
    @Test
    public void min() {
        assertEquals(1F, FloatOps.MIN_REDUCER.op(1F, 2F), 0);
        assertEquals(1F, FloatOps.MIN_REDUCER.op(2F, 1F), 0);
        assertEquals(1F, FloatOps.MIN_REDUCER.op(1F, 1F), 0);
        assertEquals(1F, FloatOps.MIN_REDUCER.op(1F, Float.POSITIVE_INFINITY), 0);
        assertEquals(Float.NaN, FloatOps.MIN_REDUCER.op(1F, Float.NaN), 0);
        FloatOps.MIN_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.MIN_REDUCER);
    }

    /**
     * Tests {@link FloatOps#min}.
     */
    @Test
    public void minArg() {
        FloatReducer r = FloatOps.min(FloatOps.COMPARATOR);
        assertEquals(1F, r.op(1F, 2F), 0);
        assertEquals(1F, r.op(2F, 1F), 0);
        assertEquals(1F, r.op(1F, 1F), 0);
        assertEquals(1F, r.op(1F, Float.POSITIVE_INFINITY), 0);
        // System.out.println(Double.compare(1, Double.NaN));
        // System.out.println(Double.compare(Double.NaN,1));
        assertEquals(1F, r.op(Float.NaN, 1F), 0);
        r.toString(); // does not fail
        assertEquals(1F, serializeAndUnserialize(r).op(1F, 2F), 0);
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void minNPE() {
        FloatOps.min(null);
    }

    /**
     * Tests {@link FloatOps#MULTIPLY_REDUCER} and {@link FloatOps#multiply()}.
     */
    @Test
    public void multiply() {
        assertEquals(16F, FloatOps.MULTIPLY_REDUCER.op(4F, 4F), 0);
        assertEquals(-8F, FloatOps.MULTIPLY_REDUCER.op(-4F, 2F), 0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.MULTIPLY_REDUCER.op(Float.POSITIVE_INFINITY,
                1), 0);
        assertEquals(Float.NaN, FloatOps.MULTIPLY_REDUCER.op(1, Float.NaN), 0);
        assertSame(FloatOps.MULTIPLY_REDUCER, FloatOps.multiply());
        FloatOps.MULTIPLY_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.MULTIPLY_REDUCER);
    }

    /**
     * Tests {@link FloatOps#multiply(float)}.
     */
    @Test
    public void multiplyArg() {
        assertEquals(-8F, FloatOps.multiply(4F).op(-2F), 0);
        assertEquals(25F, FloatOps.multiply(5F).op(5F), 0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.multiply(5).op(Float.POSITIVE_INFINITY), 0);
        assertEquals(Float.NaN, FloatOps.multiply(5).op(Float.NaN), 0);
        FloatOps.multiply(9F).toString(); // does not fail
        assertIsSerializable(FloatOps.multiply(5F));
        assertEquals(-16F, serializeAndUnserialize(FloatOps.multiply(4F)).op(-4F), 0);
    }

    /**
     * Tests {@link FloatOps#reverseOrder}.
     */
    @Test
    public void reverseOrder() {
        assertEquals(0, FloatOps.reverseOrder(FloatOps.COMPARATOR).compare(1F, 1F));
        assertEquals(0, FloatOps.reverseOrder(FloatOps.COMPARATOR).compare(Float.NaN, Float.NaN));
        assertTrue(FloatOps.reverseOrder(FloatOps.COMPARATOR).compare(2F, 1F) < 0);
        assertTrue(FloatOps.reverseOrder(FloatOps.COMPARATOR).compare(1F, 2F) > 0);
        FloatOps.reverseOrder(FloatOps.COMPARATOR).toString(); // does not fail
        assertIsSerializable(FloatOps.reverseOrder(FloatOps.COMPARATOR));
        assertTrue(serializeAndUnserialize(FloatOps.reverseOrder(FloatOps.COMPARATOR)).compare(2F,
                1F) < 0);
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrder_NPE() {
        FloatOps.reverseOrder(null);
    }

    /**
     * Tests {@link FloatOps#SUBTRACT_REDUCER} and {@link FloatOps#subtract()}.
     */
    @Test
    public void subtract() {
        assertEquals(-1F, FloatOps.SUBTRACT_REDUCER.op(1F, 2F), 0);
        assertEquals(1F, FloatOps.SUBTRACT_REDUCER.op(2F, 1F), 0);
        assertEquals(Float.NEGATIVE_INFINITY, FloatOps.SUBTRACT_REDUCER.op(1,
                Float.POSITIVE_INFINITY), 0);
        assertEquals(Float.NaN, FloatOps.SUBTRACT_REDUCER.op(1, Float.NaN), 0);
        assertSame(FloatOps.SUBTRACT_REDUCER, FloatOps.subtract());
        FloatOps.SUBTRACT_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.SUBTRACT_REDUCER);
    }

    /**
     * Tests {@link FloatOps#subtract(float)}.
     */
    @Test
    public void subtractArg() {
        assertEquals(-1F, FloatOps.subtract(5F).op(4F), 0);
        assertEquals(1F, FloatOps.subtract(4F).op(5F), 0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.subtract(5).op(Float.POSITIVE_INFINITY), 0);
        assertEquals(Float.NaN, FloatOps.subtract(5).op(Float.NaN), 0);
        FloatOps.subtract(9F).toString(); // does not fail
        assertIsSerializable(FloatOps.subtract(5F));
        assertEquals(-33F, serializeAndUnserialize(FloatOps.subtract(12F)).op(-21F), 0);
    }
}

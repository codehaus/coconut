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

import static org.junit.Assert.*;

import static org.codehaus.cake.test.util.TestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.codehaus.cake.ops.Ops.DoubleReducer;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
import org.codehaus.cake.ops.Ops.*;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
import java.math.*;
/**
 * Various tests for {@link DoubleOps}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DoubleOpsTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class DoubleOpsTest {

    /**
     * Tests {@link DoubleOps#ABS_OP}.
     */
    @Test
    public void abs() {
        assertEquals(1D, DoubleOps.ABS_OP.op(-1D),0);
        assertEquals(1D, DoubleOps.ABS_OP.op(1D),0);
        assertSame(DoubleOps.ABS_OP, DoubleOps.abs());
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.ABS_OP.op(Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.ABS_OP.op(Double.NaN),0);
        DoubleOps.ABS_OP.toString(); // does not fail
        TestUtil.assertSingletonSerializable(DoubleOps.ABS_OP);
    }

    /**
     * Tests {@link DoubleOps#ADD_REDUCER} and {@link DoubleOps#add()}.
     */
    @Test
    public void add() {
        assertEquals(3D, DoubleOps.ADD_REDUCER.op(1D, 2D),0);
        assertEquals(3D, DoubleOps.ADD_REDUCER.op(2D, 1D),0);
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.ADD_REDUCER.op(1, Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.ADD_REDUCER.op(1, Double.NaN),0);
        assertSame(DoubleOps.ADD_REDUCER, DoubleOps.add());
        DoubleOps.ADD_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(DoubleOps.ADD_REDUCER);
    }

    /**
     * Tests {@link DoubleOps#add(double)}.
     */
    @Test
    public void addArg() {
        assertEquals(9D, DoubleOps.add(5D).op(4D),0);
        assertEquals(9D, DoubleOps.add(4D).op(5D),0);
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.add(5).op(Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.add(5).op(Double.NaN),0);
        DoubleOps.add(9D).toString(); // does not fail
        assertIsSerializable(DoubleOps.add(5D));
        assertEquals(-9D, serializeAndUnserialize(DoubleOps.add(12D)).op(-21D),0);
    }
    
   /**
     * Tests {@link DoubleOps#DIVIDE_REDUCER} and {@link DoubleOps#divide()}.
     */
    @Test
    public void divide() {
        assertEquals(4D, DoubleOps.DIVIDE_REDUCER.op(16D, 4D),0);
        assertEquals(-4D, DoubleOps.DIVIDE_REDUCER.op(-8D, 2D),0);
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.DIVIDE_REDUCER.op(Double.POSITIVE_INFINITY,1),0);
        assertEquals(Double.NaN, DoubleOps.DIVIDE_REDUCER.op(1, Double.NaN),0);
        assertSame(DoubleOps.DIVIDE_REDUCER, DoubleOps.divide());
        DoubleOps.DIVIDE_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(DoubleOps.DIVIDE_REDUCER);
    }

    /**
     * Tests {@link DoubleOps#divide(double)}.
     */
    @Test
    public void divideArg() {
        assertEquals(-2D, DoubleOps.divide(4D).op(-8D),0);
        assertEquals(5D, DoubleOps.divide(5D).op(25D),0);
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.divide(5).op(Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.divide(5).op(Double.NaN),0);
        DoubleOps.divide(9D).toString(); // does not fail
        assertIsSerializable(DoubleOps.divide(5D));
        assertEquals(-4D, serializeAndUnserialize(DoubleOps.divide(4D)).op(-16D),0);
    }
    
        /**
     * Tests {@link DoubleOps#MIN_REDUCER}.
     */
    @Test
    public void min() {
        assertEquals(1D, DoubleOps.MIN_REDUCER.op(1D, 2D),0);
        assertEquals(1D, DoubleOps.MIN_REDUCER.op(2D, 1D),0);
        assertEquals(1D, DoubleOps.MIN_REDUCER.op(1D, 1D),0);
        assertEquals(1D, DoubleOps.MIN_REDUCER.op(1D, Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.MIN_REDUCER.op(1D, Double.NaN),0);
        DoubleOps.MIN_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(DoubleOps.MIN_REDUCER);
    }

    /**
     * Tests {@link DoubleOps#min}.
     */
    @Test
    public void minArg() {
        DoubleReducer r = DoubleOps.min(DoubleOps.COMPARATOR);
        assertEquals(1D, r.op(1D, 2D),0);
        assertEquals(1D, r.op(2D, 1D),0);
        assertEquals(1D, r.op(1D, 1D),0);
        assertEquals(1D, r.op(1D, Double.POSITIVE_INFINITY),0);
        // System.out.println(Double.compare(1, Double.NaN));
        // System.out.println(Double.compare(Double.NaN,1));
        assertEquals(1D, r.op(Double.NaN, 1D),0);
        r.toString(); // does not fail
        assertEquals(1D, serializeAndUnserialize(r).op(1D, 2D),0);
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void minNPE() {
        DoubleOps.min(null);
    }
    
        /**
     * Tests {@link Reducers#MAX_REDUCER}.
     */
    @Test
    public void doubleMaxReducer() {
        assertEquals(2D, DoubleOps.MAX_REDUCER.op(2D, 1D),0);
        assertEquals(2D, DoubleOps.MAX_REDUCER.op(1D, 2D),0);
        assertEquals(2D, DoubleOps.MAX_REDUCER.op(2D, 2D),0);
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.MAX_REDUCER.op(1D, Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.MAX_REDUCER.op(1D, Double.NaN),0);
        DoubleOps.MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(DoubleOps.MAX_REDUCER);
        TestUtil.assertSingletonSerializable(DoubleOps.MAX_REDUCER);
    }

    /**
     * Tests
     * {@link Reducers#doubleMaxReducer(org.codehaus.cake.ops.Ops.DoubleComparator)}
     */
    @Test
    public void doubleMaxReducerComparator() {
        DoubleReducer r = DoubleOps.max(DoubleOps.COMPARATOR);
        assertEquals(2D, r.op(1D, 2D),0);
        assertEquals(2D, r.op(2D, 1D),0);
        assertEquals(2D, r.op(2D, 2D),0);
        assertEquals(Double.POSITIVE_INFINITY, r.op(1D, Double.POSITIVE_INFINITY), 0);
        assertEquals(Double.NaN, r.op(1D, Double.NaN), 0);
assertEquals(2D, serializeAndUnserialize(r).op(1D, 2D),0);
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void maxNPE() {
        DoubleOps.max(null);
    }
    
       /**
     * Tests {@link DoubleOps#MULTIPLY_REDUCER} and {@link DoubleOps#multiply()}.
     */
    @Test
    public void multiply() {
        assertEquals(16D, DoubleOps.MULTIPLY_REDUCER.op(4D, 4D),0);
        assertEquals(-8D, DoubleOps.MULTIPLY_REDUCER.op(-4D, 2D),0);
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.MULTIPLY_REDUCER.op(Double.POSITIVE_INFINITY,1),0);
        assertEquals(Double.NaN, DoubleOps.MULTIPLY_REDUCER.op(1, Double.NaN),0);
        assertSame(DoubleOps.MULTIPLY_REDUCER, DoubleOps.multiply());
        DoubleOps.MULTIPLY_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(DoubleOps.MULTIPLY_REDUCER);
    }

    /**
     * Tests {@link DoubleOps#multiply(double)}.
     */
    @Test
    public void multiplyArg() {
        assertEquals(-8D, DoubleOps.multiply(4D).op(-2D),0);
        assertEquals(25D, DoubleOps.multiply(5D).op(5D),0);
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.multiply(5).op(Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.multiply(5).op(Double.NaN),0);
        DoubleOps.multiply(9D).toString(); // does not fail
        assertIsSerializable(DoubleOps.multiply(5D));
        assertEquals(-16D, serializeAndUnserialize(DoubleOps.multiply(4D)).op(-4D),0);
    }
    
        /**
     * Tests {@link DoubleOps#SUBTRACT_REDUCER} and {@link DoubleOps#subtract()}.
     */
    @Test
    public void subtract() {
        assertEquals(-1D, DoubleOps.SUBTRACT_REDUCER.op(1D, 2D),0);
        assertEquals(1D, DoubleOps.SUBTRACT_REDUCER.op(2D, 1D),0);
        assertEquals(Double.NEGATIVE_INFINITY, DoubleOps.SUBTRACT_REDUCER.op(1, Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.SUBTRACT_REDUCER.op(1, Double.NaN),0);
        assertSame(DoubleOps.SUBTRACT_REDUCER, DoubleOps.subtract());
        DoubleOps.SUBTRACT_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(DoubleOps.SUBTRACT_REDUCER);
    }

    /**
     * Tests {@link DoubleOps#subtract(double)}.
     */
    @Test
    public void subtractArg() {
        assertEquals(-1D, DoubleOps.subtract(5D).op(4D),0);
        assertEquals(1D, DoubleOps.subtract(4D).op(5D),0);
        assertEquals(Double.POSITIVE_INFINITY, DoubleOps.subtract(5).op(Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DoubleOps.subtract(5).op(Double.NaN),0);
        DoubleOps.subtract(9D).toString(); // does not fail
        assertIsSerializable(DoubleOps.subtract(5D));
        assertEquals(-33D, serializeAndUnserialize(DoubleOps.subtract(12D)).op(-21D),0);
    }
    
}
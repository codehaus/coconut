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
     * Tests {@link DoubleOps#REDUCER_ADD} and {@link DoubleOps#add()}.
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
        assertIsSerializable(DoubleOps.add(5));
        assertEquals(-9D, serializeAndUnserialize(DoubleOps.add(12D)).op(-21D),0);
    }
}
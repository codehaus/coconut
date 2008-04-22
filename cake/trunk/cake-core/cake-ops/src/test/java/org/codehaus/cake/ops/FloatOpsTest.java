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
        assertEquals(1F, FloatOps.ABS_OP.op(-1F),0);
        assertEquals(1F, FloatOps.ABS_OP.op(1F),0);
        assertSame(FloatOps.ABS_OP, FloatOps.abs());
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.ABS_OP.op(Float.POSITIVE_INFINITY),0);
        assertEquals(Float.NaN, FloatOps.ABS_OP.op(Float.NaN),0);
        FloatOps.ABS_OP.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.ABS_OP);
    }

    /**
     * Tests {@link FloatOps#REDUCER_ADD} and {@link FloatOps#add()}.
     */
    @Test
    public void add() {
        assertEquals(3F, FloatOps.ADD_REDUCER.op(1F, 2F),0);
        assertEquals(3F, FloatOps.ADD_REDUCER.op(2F, 1F),0);
        assertEquals(Float.POSITIVE_INFINITY, FloatOps.ADD_REDUCER.op(1, Float.POSITIVE_INFINITY),0);
        assertEquals(Float.NaN, FloatOps.ADD_REDUCER.op(1, Float.NaN),0);
        assertSame(FloatOps.ADD_REDUCER, FloatOps.add());
        FloatOps.ADD_REDUCER.toString(); // does not fail
        TestUtil.assertSingletonSerializable(FloatOps.ADD_REDUCER);
    }

    /**
     * Tests {@link FloatOps#add(float)}.
     */
    @Test
    public void addArg() {
        assertEquals(9F, FloatOps.add(5F).op(4F),0);
        assertEquals(9F, FloatOps.add(4F).op(5F),0);
        assertEquals(Float.POSITIVE_INFINITY, DoubleOps.add(5).op(Float.POSITIVE_INFINITY),0);
        assertEquals(Float.NaN, DoubleOps.add(5).op(Float.NaN),0);
        DoubleOps.add(9F).toString(); // does not fail
        assertIsSerializable(FloatOps.add(5));
        assertEquals(-9F, serializeAndUnserialize(FloatOps.add(12F)).op(-21F),0);
    }
}
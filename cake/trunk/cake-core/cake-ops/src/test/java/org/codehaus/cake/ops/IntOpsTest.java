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
     * Tests {@link IntOps#REDUCER_ADD} and {@link IntOps#add()}.
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
        DoubleOps.add(9).toString(); // does not fail
        assertIsSerializable(IntOps.add(5));
        assertEquals(-9, serializeAndUnserialize(IntOps.add(12)).op(-21));
    }
}
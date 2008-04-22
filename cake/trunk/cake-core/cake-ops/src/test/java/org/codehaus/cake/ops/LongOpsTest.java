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
     * Tests {@link LongOps#REDUCER_ADD} and {@link LongOps#add()}.
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
        DoubleOps.add(9L).toString(); // does not fail
        assertIsSerializable(LongOps.add(5));
        assertEquals(-9L, serializeAndUnserialize(LongOps.add(12L)).op(-21L));
    }
}
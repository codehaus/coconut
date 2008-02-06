/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.jsr166y.ops;

import static org.codehaus.cake.jsr166y.ops.Reducers.DOUBLE_ADDER_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.DOUBLE_MAX_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.DOUBLE_MIN_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.INT_ADDER_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.INT_MAX_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.INT_MIN_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.LONG_ADDER_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.LONG_MAX_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.LONG_MIN_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.MAX_REDUCER;
import static org.codehaus.cake.jsr166y.ops.Reducers.MIN_REDUCER;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import jsr166y.forkjoin.Ops.DoubleReducer;
import jsr166y.forkjoin.Ops.IntReducer;
import jsr166y.forkjoin.Ops.LongReducer;
import jsr166y.forkjoin.Ops.Reducer;

import org.codehaus.cake.jsr166y.ops.Comparators;
import org.codehaus.cake.jsr166y.ops.Reducers;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

public class ReducersTest {

    /**
     * Tests {@link Reducers#DOUBLE_ADDER_REDUCER}.
     */
    @Test
    public void doubleAdderReducer() {
        assertEquals(3.0, DOUBLE_ADDER_REDUCER.op(1, 2), 0);
        assertEquals(Double.POSITIVE_INFINITY,
                DOUBLE_ADDER_REDUCER.op(1, Double.POSITIVE_INFINITY), 0);
        assertEquals(Double.NaN, DOUBLE_ADDER_REDUCER.op(1, Double.NaN), 0);
        DOUBLE_ADDER_REDUCER.toString(); // does not fail
        assertIsSerializable(DOUBLE_ADDER_REDUCER);
        assertSame(DOUBLE_ADDER_REDUCER, TestUtil.serializeAndUnserialize(DOUBLE_ADDER_REDUCER));
    }

    /**
     * Tests {@link Reducers#DOUBLE_MAX_REDUCER}.
     */
    @Test
    public void doubleMaxReducer() {
        assertEquals(2.0, DOUBLE_MAX_REDUCER.op(1, 2), 0);
        assertEquals(2.0, DOUBLE_MAX_REDUCER.op(2, 1), 0);
        assertEquals(Double.POSITIVE_INFINITY, DOUBLE_MAX_REDUCER.op(1, Double.POSITIVE_INFINITY),
                0);
        assertEquals(Double.NaN, DOUBLE_MAX_REDUCER.op(1, Double.NaN), 0);
        DOUBLE_MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(DOUBLE_MAX_REDUCER);
        assertSame(DOUBLE_MAX_REDUCER, TestUtil.serializeAndUnserialize(DOUBLE_MAX_REDUCER));
    }

    /**
     * Tests
     * {@link Reducers#doubleMaxReducer(jsr166y.forkjoin.Ops.DoubleComparator)}
     */
    @Test
    public void doubleMaxReducerComparator() {
        DoubleReducer r = Reducers.doubleMaxReducer(Comparators.DOUBLE_COMPARATOR);
        assertEquals(2.0, r.op(1, 2), 0);
        assertEquals(2.0, r.op(2, 1), 0);
        assertEquals(Double.POSITIVE_INFINITY, r.op(1, Double.POSITIVE_INFINITY), 0);
        assertEquals(Double.NaN, r.op(1, Double.NaN), 0);
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void doubleMaxReducerComparatorNPE() {
        Reducers.doubleMaxReducer(null);
    }

    /**
     * Tests {@link Reducers#DOUBLE_MIN_REDUCER}.
     */
    @Test
    public void doubleMinReducer() {
        assertEquals(1.0, DOUBLE_MIN_REDUCER.op(1, 2),0);
        assertEquals(1.0, DOUBLE_MIN_REDUCER.op(2, 1),0);
        assertEquals(1.0, DOUBLE_MIN_REDUCER.op(1, Double.POSITIVE_INFINITY),0);
        assertEquals(Double.NaN, DOUBLE_MIN_REDUCER.op(1, Double.NaN),0);
        DOUBLE_MIN_REDUCER.toString(); // does not fail
        assertIsSerializable(DOUBLE_MIN_REDUCER);
        assertSame(DOUBLE_MIN_REDUCER, TestUtil.serializeAndUnserialize(DOUBLE_MIN_REDUCER));
    }

    /**
     * Tests {@link Reducers#DOUBLE_MIN_REDUCER}.
     */
    @Test
    public void doubleMinReducerComparator() {
        DoubleReducer r = Reducers.doubleMinReducer(Comparators.DOUBLE_COMPARATOR);
        assertEquals(1.0, r.op(1, 2),0);
        assertEquals(1.0, r.op(2, 1),0);
        assertEquals(1.0, r.op(1, Double.POSITIVE_INFINITY),0);
        // System.out.println(Double.compare(1, Double.NaN));
        // System.out.println(Double.compare(Double.NaN,1));
        assertEquals(1.0, r.op(Double.NaN, 1),0);
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void doubleMinReducerComparatorNPE() {
        Reducers.doubleMinReducer(null);
    }

    /**
     * Tests {@link Reducers#INT_ADDER_REDUCER}.
     */
    @Test
    public void intAdderReducer() {
        assertEquals(3, INT_ADDER_REDUCER.op(1, 2));
        assertEquals(Integer.MIN_VALUE, INT_ADDER_REDUCER.op(Integer.MAX_VALUE, 1));
        INT_ADDER_REDUCER.toString(); // does not fail
        assertIsSerializable(INT_ADDER_REDUCER);
        assertSame(INT_ADDER_REDUCER, TestUtil.serializeAndUnserialize(INT_ADDER_REDUCER));
    }

    /**
     * Tests {@link Reducers#INT_MAX_REDUCER}.
     */
    @Test
    public void intMaxReducer() {
        assertEquals(2, INT_MAX_REDUCER.op(1, 2));
        assertEquals(2, INT_MAX_REDUCER.op(2, 1));
        assertEquals(1, INT_MAX_REDUCER.op(1, 1));
        INT_MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(INT_MAX_REDUCER);
        assertSame(INT_MAX_REDUCER, TestUtil.serializeAndUnserialize(INT_MAX_REDUCER));
    }

    @Test
    public void intMaxReducerComparator() {
        IntReducer r = Reducers.intMaxReducer(Comparators.INT_COMPARATOR);
        assertEquals(2, r.op(1, 2));
        assertEquals(2, r.op(2, 1));
        assertEquals(1, r.op(1, 1));
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test
    public void intMinReducerComparator() {
        IntReducer r = Reducers.intMinReducer(Comparators.INT_COMPARATOR);
        assertEquals(1, r.op(1, 2));
        assertEquals(1, r.op(2, 1));
        assertEquals(1, r.op(1, 1));
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test
    public void longMaxReducerComparator() {
        LongReducer r = Reducers.longMaxReducer(Comparators.LONG_COMPARATOR);
        assertEquals(2L, r.op(1, 2));
        assertEquals(2L, r.op(2, 1));
        assertEquals(1L, r.op(1, 1));
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test
    public void longMinReducerComparator() {
        LongReducer r = Reducers.longMinReducer(Comparators.LONG_COMPARATOR);
        assertEquals(1L, r.op(1, 2));
        assertEquals(1L, r.op(2, 1));
        assertEquals(1L, r.op(1, 1));
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void intMaxReducerComparatorNPE() {
        Reducers.intMaxReducer(null);
    }

    /**
     * Tests {@link Reducers#INT_MIN_REDUCER}.
     */
    @Test
    public void intMinReducer() {
        assertEquals(1, INT_MIN_REDUCER.op(1, 2));
        assertEquals(1, INT_MIN_REDUCER.op(2, 1));
        assertEquals(2, INT_MIN_REDUCER.op(2, 2));
        INT_MIN_REDUCER.toString(); // does not fail
        assertIsSerializable(INT_MIN_REDUCER);
        assertSame(INT_MIN_REDUCER, TestUtil.serializeAndUnserialize(INT_MIN_REDUCER));
    }

    @Test(expected = NullPointerException.class)
    public void intMinReducerComparatorNPE() {
        Reducers.intMinReducer(null);
    }

    /**
     * Tests {@link Reducers#LONG_ADDER_REDUCER}.
     */
    @Test
    public void longAdderReducer() {
        assertEquals(3L, LONG_ADDER_REDUCER.op(1, 2));
        assertEquals(Long.MIN_VALUE, LONG_ADDER_REDUCER.op(Long.MAX_VALUE, 1));
        LONG_ADDER_REDUCER.toString(); // does not fail
        assertIsSerializable(LONG_ADDER_REDUCER);
        assertSame(LONG_ADDER_REDUCER, TestUtil.serializeAndUnserialize(LONG_ADDER_REDUCER));
    }

    /**
     * Tests {@link Reducers#LONG_MAX_REDUCER}.
     */
    @Test
    public void longMaxReducer() {
        assertEquals(2L, LONG_MAX_REDUCER.op(1, 2));
        assertEquals(2L, LONG_MAX_REDUCER.op(2, 1));
        assertEquals(1L, LONG_MAX_REDUCER.op(1, 1));
        LONG_MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(LONG_MAX_REDUCER);
        assertSame(LONG_MAX_REDUCER, TestUtil.serializeAndUnserialize(LONG_MAX_REDUCER));
    }

    @Test(expected = NullPointerException.class)
    public void longMaxReducerComparatorNPE() {
        Reducers.longMaxReducer(null);
    }

    /**
     * Tests {@link Reducers#LONG_MIN_REDUCER}.
     */
    @Test
    public void longMinReducer() {
        assertEquals(1L, LONG_MIN_REDUCER.op(1, 2));
        assertEquals(1L, LONG_MIN_REDUCER.op(2, 1));
        assertEquals(2L, LONG_MIN_REDUCER.op(2, 2));
        LONG_MIN_REDUCER.toString(); // does not fail
        assertIsSerializable(LONG_MIN_REDUCER);
        assertSame(LONG_MIN_REDUCER, TestUtil.serializeAndUnserialize(LONG_MIN_REDUCER));
    }

    @Test(expected = NullPointerException.class)
    public void longMinReducerComparatorNPE() {
        Reducers.longMinReducer(null);
    }

    /**
     * Tests {@link Reducers#MAX_REDUCER}.
     */
    @Test
    public void maxReducer() {
        assertEquals(2, MAX_REDUCER.op(1, 2));
        assertEquals(2, MAX_REDUCER.op(2, 1));
        assertEquals(1, MAX_REDUCER.op(1, 1));
        assertEquals(Integer.MIN_VALUE, MAX_REDUCER.op(Integer.MIN_VALUE, null));
        assertEquals(2, MAX_REDUCER.op(null, 2));
        assertNull(MAX_REDUCER.op(null, null));
        assertSame(MAX_REDUCER, Reducers.<Integer> maxReducer());
        MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(MAX_REDUCER);
        assertSame(MAX_REDUCER, TestUtil.serializeAndUnserialize(MAX_REDUCER));
    }

    @Test
    public void maxReducerComparator() {
        Reducer<Integer> r = Reducers.maxReducer(Comparators.NATURAL_COMPARATOR);
        assertEquals(2, r.op(1, 2).intValue());
        assertEquals(2, r.op(2, 1).intValue());
        assertEquals(1, r.op(1, 1).intValue());
        assertEquals(Integer.MIN_VALUE, r.op(Integer.MIN_VALUE, null).intValue());
        assertEquals(2, r.op(null, 2).intValue());
        assertNull(r.op(null, null));
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void maxReducerComparatorNPE() {
        Reducers.maxReducer(null);
    }

    /**
     * Tests {@link Reducers#MIN_REDUCER}.
     */
    @Test
    public void minReducer() {
        assertEquals(1, MIN_REDUCER.op(1, 2));
        assertEquals(1, MIN_REDUCER.op(2, 1));
        assertEquals(1, MIN_REDUCER.op(1, 1));
        assertEquals(Integer.MIN_VALUE, MIN_REDUCER.op(Integer.MIN_VALUE, null));
        assertEquals(2, MIN_REDUCER.op(null, 2));
        assertNull(MIN_REDUCER.op(null, null));
        assertSame(MIN_REDUCER, Reducers.<Integer> minReducer());
        MIN_REDUCER.toString(); // does not fail
        assertIsSerializable(MIN_REDUCER);
        assertSame(MIN_REDUCER, TestUtil.serializeAndUnserialize(MIN_REDUCER));
    }

    /**
     * Tests {@link Reducers#MIN_REDUCER}.
     */
    @Test
    public void minReducerComparator() {
        Reducer<Integer> r = Reducers.minReducer(Comparators.NATURAL_COMPARATOR);
        assertEquals(1, r.op(1, 2).intValue());
        assertEquals(1, r.op(2, 1).intValue());
        assertEquals(1, r.op(1, 1).intValue());
        assertEquals(Integer.MIN_VALUE, r.op(Integer.MIN_VALUE, null).intValue());
        assertEquals(2, r.op(null, 2).intValue());
        assertNull(r.op(null, null));
        r.toString(); // does not fail
        assertIsSerializable(r);
    }

    @Test(expected = NullPointerException.class)
    public void minReducerComparatorNPE() {
        Reducers.minReducer(null);
    }
}

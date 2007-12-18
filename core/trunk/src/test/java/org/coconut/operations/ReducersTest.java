package org.coconut.operations;

import static org.coconut.operations.Reducers.*;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.coconut.test.TestUtil;
import org.junit.Test;

public class ReducersTest {

    /**
     * Tests {@link Reducers#DOUBLE_ADDER_REDUCER}.
     */
    @Test
    public void doubleAdderReducer() {
        assertEquals(3.0, DOUBLE_ADDER_REDUCER.combine(1, 2));
        assertEquals(Double.POSITIVE_INFINITY, DOUBLE_ADDER_REDUCER.combine(1,
                Double.POSITIVE_INFINITY));
        assertEquals(Double.NaN, DOUBLE_ADDER_REDUCER.combine(1, Double.NaN));
        DOUBLE_ADDER_REDUCER.toString(); // does not fail
        assertIsSerializable(DOUBLE_ADDER_REDUCER);
        assertSame(DOUBLE_ADDER_REDUCER, TestUtil.serializeAndUnserialize(DOUBLE_ADDER_REDUCER));
    }

    /**
     * Tests {@link Reducers#DOUBLE_MAX_REDUCER}.
     */
    @Test
    public void doubleMaxReducer() {
        assertEquals(2.0, DOUBLE_MAX_REDUCER.combine(1, 2));
        assertEquals(2.0, DOUBLE_MAX_REDUCER.combine(2, 1));
        assertEquals(Double.POSITIVE_INFINITY, DOUBLE_MAX_REDUCER.combine(1,
                Double.POSITIVE_INFINITY));
        assertEquals(Double.NaN, DOUBLE_MAX_REDUCER.combine(1, Double.NaN));
        DOUBLE_MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(DOUBLE_MAX_REDUCER);
        assertSame(DOUBLE_MAX_REDUCER, TestUtil.serializeAndUnserialize(DOUBLE_MAX_REDUCER));
    }

    /**
     * Tests {@link Reducers#DOUBLE_MIN_REDUCER}.
     */
    @Test
    public void doubleMinReducer() {
        assertEquals(1.0, DOUBLE_MIN_REDUCER.combine(1, 2));
        assertEquals(1.0, DOUBLE_MIN_REDUCER.combine(2, 1));
        assertEquals(1.0, DOUBLE_MIN_REDUCER.combine(1, Double.POSITIVE_INFINITY));
        assertEquals(Double.NaN, DOUBLE_MIN_REDUCER.combine(1, Double.NaN));
        DOUBLE_MIN_REDUCER.toString(); // does not fail
        assertIsSerializable(DOUBLE_MIN_REDUCER);
        assertSame(DOUBLE_MIN_REDUCER, TestUtil.serializeAndUnserialize(DOUBLE_MIN_REDUCER));
    }

    /**
     * Tests {@link Reducers#INT_ADDER_REDUCER}.
     */
    @Test
    public void intAdderReducer() {
        assertEquals(3, INT_ADDER_REDUCER.combine(1, 2));
        assertEquals(Integer.MIN_VALUE, INT_ADDER_REDUCER.combine(Integer.MAX_VALUE, 1));
        INT_ADDER_REDUCER.toString(); // does not fail
        assertIsSerializable(INT_ADDER_REDUCER);
        assertSame(INT_ADDER_REDUCER, TestUtil.serializeAndUnserialize(INT_ADDER_REDUCER));
    }

    /**
     * Tests {@link Reducers#INT_MAX_REDUCER}.
     */
    @Test
    public void intMaxReducer() {
        assertEquals(2, INT_MAX_REDUCER.combine(1, 2));
        assertEquals(2, INT_MAX_REDUCER.combine(2, 1));
        assertEquals(1, INT_MAX_REDUCER.combine(1, 1));
        INT_MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(INT_MAX_REDUCER);
        assertSame(INT_MAX_REDUCER, TestUtil.serializeAndUnserialize(INT_MAX_REDUCER));
    }

    /**
     * Tests {@link Reducers#INT_MIN_REDUCER}.
     */
    @Test
    public void intMinReducer() {
        assertEquals(1, INT_MIN_REDUCER.combine(1, 2));
        assertEquals(1, INT_MIN_REDUCER.combine(2, 1));
        assertEquals(2, INT_MIN_REDUCER.combine(2, 2));
        INT_MIN_REDUCER.toString(); // does not fail
        assertIsSerializable(INT_MIN_REDUCER);
        assertSame(INT_MIN_REDUCER, TestUtil.serializeAndUnserialize(INT_MIN_REDUCER));
    }
    
    /**
     * Tests {@link Reducers#LONG_ADDER_REDUCER}.
     */
    @Test
    public void longAdderReducer() {
        assertEquals(3L, LONG_ADDER_REDUCER.combine(1, 2));
        assertEquals(Long.MIN_VALUE, LONG_ADDER_REDUCER.combine(Long.MAX_VALUE, 1));
        LONG_ADDER_REDUCER.toString(); // does not fail
        assertIsSerializable(LONG_ADDER_REDUCER);
        assertSame(LONG_ADDER_REDUCER, TestUtil.serializeAndUnserialize(LONG_ADDER_REDUCER));
    }

    /**
     * Tests {@link Reducers#LONG_MAX_REDUCER}.
     */
    @Test
    public void longMaxReducer() {
        assertEquals(2L, LONG_MAX_REDUCER.combine(1, 2));
        assertEquals(2L, LONG_MAX_REDUCER.combine(2, 1));
        assertEquals(1L, LONG_MAX_REDUCER.combine(1, 1));
        LONG_MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(LONG_MAX_REDUCER);
        assertSame(LONG_MAX_REDUCER, TestUtil.serializeAndUnserialize(LONG_MAX_REDUCER));
    }

    /**
     * Tests {@link Reducers#LONG_MIN_REDUCER}.
     */
    @Test
    public void longMinReducer() {
        assertEquals(1L, LONG_MIN_REDUCER.combine(1, 2));
        assertEquals(1L, LONG_MIN_REDUCER.combine(2, 1));
        assertEquals(2L, LONG_MIN_REDUCER.combine(2, 2));
        LONG_MIN_REDUCER.toString(); // does not fail
        assertIsSerializable(LONG_MIN_REDUCER);
        assertSame(LONG_MIN_REDUCER, TestUtil.serializeAndUnserialize(LONG_MIN_REDUCER));
    }
}

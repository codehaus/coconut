package org.codehaus.cake.ops;

import static org.codehaus.cake.ops.ObjectOps.MAX_REDUCER;
import static org.codehaus.cake.ops.ObjectOps.MIN_REDUCER;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.codehaus.cake.ops.Ops.Reducer;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
public class ObjectOpsTest {
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
        assertSame(MAX_REDUCER, ObjectOps.<Integer> maxReducer());
        MAX_REDUCER.toString(); // does not fail
        assertIsSerializable(MAX_REDUCER);
        assertSame(MAX_REDUCER, TestUtil.serializeAndUnserialize(MAX_REDUCER));
    }

    @Test
    public void maxReducerComparator() {
        Reducer<Integer> r = ObjectOps.maxReducer(Comparators.NATURAL_COMPARATOR);
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
        ObjectOps.maxReducer(null);
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
        assertSame(MIN_REDUCER, ObjectOps.<Integer> minReducer());
        MIN_REDUCER.toString(); // does not fail
        assertIsSerializable(MIN_REDUCER);
        assertSame(MIN_REDUCER, TestUtil.serializeAndUnserialize(MIN_REDUCER));
    }

    /**
     * Tests {@link Reducers#MIN_REDUCER}.
     */
    @Test
    public void minReducerComparator() {
        Reducer<Integer> r = ObjectOps.minReducer(Comparators.NATURAL_COMPARATOR);
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
        ObjectOps.minReducer(null);
    }
}

package org.codehaus.cake.ops;

import static org.codehaus.cake.ops.ObjectOps.CONSTANT_OP;
import static org.codehaus.cake.ops.ObjectOps.MAX_REDUCER;
import static org.codehaus.cake.ops.ObjectOps.MIN_REDUCER;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.codehaus.cake.ops.Ops.Op;
import org.codehaus.cake.ops.Ops.Reducer;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
public class ObjectOpsTest {
    /**
     * Tests {@link ObjectOps#MAX_REDUCER}.
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
     * Tests {@link ObjectOps#MIN_REDUCER}.
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
     * Tests {@link ObjectOps#MIN_REDUCER}.
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
    /**
     * Tests {@link ObjectOps#compoundMapper(Op, Op)}.
     */
    @Test
    public void compoundMapper() {
        Op<Integer, String> m = ObjectOps.compoundMapper(new Mapper1(), new Mapper2());

        assertEquals("44", m.op(2));
        assertEquals("8181", m.op(9));
        m.toString(); // no exception
        assertIsSerializable(m);
    }

    /**
     * Tests that {@link ObjectOps#compoundMapper(Op, Op)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void compoundMapperNPE() {
        ObjectOps.compoundMapper(null, TestUtil.dummy(Op.class));
    }

    /**
     * Tests that {@link ObjectOps#compoundMapper(Op, Op)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void compoundMapperNPE1() {
        ObjectOps.compoundMapper(TestUtil.dummy(Op.class), null);
    }

    /**
     * Tests {@link ObjectOps#CONSTANT_OP}.
     */
    @Test
    public void noop() {
        assertEquals(0, CONSTANT_OP.op(0));
        assertEquals("1", CONSTANT_OP.op("1"));
        assertSame(CONSTANT_OP, ObjectOps.constant());
        CONSTANT_OP.toString(); // does not fail
        assertIsSerializable(ObjectOps.constant());
        assertSame(CONSTANT_OP, TestUtil.serializeAndUnserialize(CONSTANT_OP));
    }

    static class Mapper1 implements Op<Integer, Integer>, Serializable {
        public Integer op(Integer t) {
            return t * t;
        }
    }

    static class Mapper2 implements Op<Integer, String>, Serializable {
        public String op(Integer t) {
            return t + "" + t;
        }
    }
}

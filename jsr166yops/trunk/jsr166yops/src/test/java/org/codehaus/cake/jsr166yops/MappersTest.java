/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166yops;

import static org.codehaus.cake.jsr166yops.Mappers.CONSTANT_MAPPER;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;

import jsr166y.forkjoin.Ops.Op;

import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

public class MappersTest {

    /**
     * Tests {@link Mappers#compoundMapper(Op, Op)}.
     */
    @Test
    public void compoundMapper() {
        Op<Integer, String> m = Mappers.compoundMapper(new Mapper1(), new Mapper2());

        assertEquals("44", m.op(2));
        assertEquals("8181", m.op(9));
        m.toString(); // no exception
        assertIsSerializable(m);
    }

    /**
     * Tests that {@link Mappers#compoundMapper(Op, Op)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void compoundMapperNPE() {
        Mappers.compoundMapper(null, TestUtil.dummy(Op.class));
    }

    /**
     * Tests that {@link Mappers#compoundMapper(Op, Op)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void compoundMapperNPE1() {
        Mappers.compoundMapper(TestUtil.dummy(Op.class), null);
    }

    /**
     * Tests {@link Mappers#CONSTANT_MAPPER}.
     */
    @Test
    public void noop() {
        assertEquals(0, CONSTANT_MAPPER.op(0));
        assertEquals("1", CONSTANT_MAPPER.op("1"));
        assertSame(CONSTANT_MAPPER, Mappers.constant());
        CONSTANT_MAPPER.toString(); // does not fail
        assertIsSerializable(Mappers.constant());
        assertSame(CONSTANT_MAPPER, TestUtil.serializeAndUnserialize(CONSTANT_MAPPER));
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

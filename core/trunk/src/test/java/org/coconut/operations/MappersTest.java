/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.coconut.operations.Mappers.NOOP_MAPPER;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.coconut.operations.Ops.Mapper;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class MappersTest {

    /**
     * Tests {@link Mappers#compoundMapper(Mapper, Mapper)}.
     */
    @Test
    public void compoundMapper() {
        Mapper<Integer, String> m = Mappers.compoundMapper(new Mapper1(), new Mapper2());

        assertEquals("44", m.map(2));
        assertEquals("8181", m.map(9));
        m.toString(); // no exception
        assertIsSerializable(m);
    }

    /**
     * Tests that {@link Mappers#compoundMapper(Mapper, Mapper)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void compoundMapperNPE() {
        Mappers.compoundMapper(null, TestUtil.dummy(Mapper.class));
    }

    /**
     * Tests that {@link Mappers#compoundMapper(Mapper, Mapper)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void compoundMapperNPE1() {
        Mappers.compoundMapper(TestUtil.dummy(Mapper.class), null);
    }

    /**
     * Tests {@link Mappers#NOOP_MAPPER}.
     */
    @Test
    public void naturalComparator() {
        assertEquals(0, NOOP_MAPPER.map(0));
        assertEquals("1", NOOP_MAPPER.map("1"));
        assertSame(NOOP_MAPPER, Mappers.noOpMapper());
        NOOP_MAPPER.toString(); // does not fail
        assertIsSerializable(Mappers.noOpMapper());
        assertSame(NOOP_MAPPER, TestUtil.serializeAndUnserialize(NOOP_MAPPER));
    }

    static class Mapper1 implements Mapper<Integer, Integer>, Serializable {
        public Integer map(Integer t) {
            return t * t;
        }
    }

    static class Mapper2 implements Mapper<Integer, String>, Serializable {
        public String map(Integer t) {
            return t + "" + t;
        }
    }

}

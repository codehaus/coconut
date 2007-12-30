/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.coconut.operations.Mappers.KEY_MAPPER;
import static org.coconut.operations.Mappers.NOOP_MAPPER;
import static org.coconut.operations.Mappers.VALUE_MAPPER;
import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_KEY_NULL;
import static org.coconut.test.CollectionTestUtil.M1_NULL_VALUE;
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
    public void noop() {
        assertEquals(0, NOOP_MAPPER.map(0));
        assertEquals("1", NOOP_MAPPER.map("1"));
        assertSame(NOOP_MAPPER, Mappers.noOpMapper());
        NOOP_MAPPER.toString(); // does not fail
        assertIsSerializable(Mappers.noOpMapper());
        assertSame(NOOP_MAPPER, TestUtil.serializeAndUnserialize(NOOP_MAPPER));
    }

    /**
     * Tests {@link Mappers#NOOP_MAPPER}.
     */
    @Test
    public void keyFromMap() {
        assertEquals(M1.getKey(), KEY_MAPPER.map(M1));
        assertEquals(null, KEY_MAPPER.map(M1_NULL_VALUE));
        assertSame(KEY_MAPPER, Mappers.keyFromMapEntry());
        KEY_MAPPER.toString(); // does not fail
        assertIsSerializable(Mappers.keyFromMapEntry());
        assertSame(KEY_MAPPER, TestUtil.serializeAndUnserialize(KEY_MAPPER));
    }

    /**
     * Tests {@link Mappers#NOOP_MAPPER}.
     */
    @Test
    public void valueFromMap() {
        assertEquals(M1.getValue(), VALUE_MAPPER.map(M1));
        assertEquals(null, VALUE_MAPPER.map(M1_KEY_NULL));
        assertSame(VALUE_MAPPER, Mappers.valueFromMapEntry());
        VALUE_MAPPER.toString(); // does not fail
        assertIsSerializable(Mappers.valueFromMapEntry());
        assertSame(VALUE_MAPPER, TestUtil.serializeAndUnserialize(VALUE_MAPPER));
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

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractAttributeMapTest {

    Attribute a1 = MockTestCase.mockDummy(Attribute.class);

    Attribute a2 = MockTestCase.mockDummy(Attribute.class);

    Attribute a3 = MockTestCase.mockDummy(Attribute.class);

    AttributeMap m;

    AttributeMap m2;

    @Before
    public void setup() {
        m = create();
        m2 = create();
        m2.put(a1, 12.23);
        m2.put(a2, 12);
    }

    protected abstract AttributeMap create();

    @Test
    public void clear() {
        assertEquals(2, m2.size());
        m2.clear();
        assertEquals(0, m2.size());
        m2.clear();
        assertEquals(0, m2.size());
    }

    @Test
    public void contains() {
        assertTrue(m2.containsKey(a1));
        assertTrue(m2.containsKey(a2));
        assertFalse(m2.containsKey(a3));
        assertTrue(m2.containsValue(12.23));
        assertTrue(m2.containsValue(12));
        assertFalse(m2.containsValue("dd"));
    }

    @Test
    public void putGet() {
        m.put(a1, "foo");
        m.put(a2, true);
        assertEquals("foo", m.get(a1));
        assertEquals(Boolean.TRUE, m.get(a2));
        assertEquals("foo", m.get(a1,"boo"));
        assertEquals(Boolean.TRUE, m.get(a2,Boolean.FALSE));
        assertEquals(true, m.get(a3, true));
        assertEquals("true", m.get(a3, "true"));
    }

    @Test
    public void putGetBoolean() {
        m.putBoolean(a1, false);
        m.put(a2, Boolean.TRUE);
        assertEquals(Boolean.FALSE, m.get(a1));
        assertEquals(Boolean.TRUE, m.get(a2));
        assertEquals(false, m.getBoolean(a1));
        assertEquals(true, m.getBoolean(a2));
        assertEquals(true, m.getBoolean(a3, true));
    }

    @Test
    public void putGetByte() {
        m.putByte(a1, (byte) 1);
        m.put(a2, Byte.valueOf((byte) 2));
        assertEquals(Byte.valueOf((byte) 1), m.get(a1));
        assertEquals(Byte.valueOf((byte) 2), m.get(a2));
        assertEquals((byte) 1, m.getByte(a1));
        assertEquals((byte) 2, m.getByte(a2));
        assertEquals((byte) 3, m.getByte(a3, (byte) 3));
    }

    @Test
    public void putGetChar() {
        m.putChar(a1, (char) 1);
        m.put(a2, Character.valueOf((char) 2));
        assertEquals(Character.valueOf((char) 1), m.get(a1));
        assertEquals(Character.valueOf((char) 2), m.get(a2));
        assertEquals((char) 1, m.getChar(a1));
        assertEquals((char) 2, m.getChar(a2));
        assertEquals((char) 3, m.getChar(a3, (char) 3));
    }

    @Test
    public void putGetDouble() {
        m.putDouble(a1, 1);
        m.put(a2, 2.1d);
        assertEquals(1d, m.get(a1));
        assertEquals(2.1d, m.get(a2));
        assertEquals(1d, m.getDouble(a1));
        assertEquals(2.1d, m.getDouble(a2));
        assertEquals(3.4d, m.getDouble(a3, 3.4d));
    }

    @Test
    public void putGetFloat() {
        m.putFloat(a1, 1);
        m.put(a2, 2.1f);
        assertEquals(1f, m.get(a1));
        assertEquals(2.1f, m.get(a2));
        assertEquals(1f, m.getFloat(a1));
        assertEquals(2.1f, m.getFloat(a2));
        assertEquals(3.4f, m.getFloat(a3, 3.4f));
    }

    @Test
    public void putGetInt() {
        m.putInt(a1, 1);
        m.put(a2, 2);
        assertEquals(1, m.get(a1));
        assertEquals(2, m.get(a2));
        assertEquals(1, m.getInt(a1));
        assertEquals(2, m.getInt(a2));
        assertEquals(3, m.getInt(a3, 3));
    }

    @Test
    public void putGetLong() {
        m.putLong(a1, 1);
        m.put(a2, 2l);
        assertEquals(1l, m.get(a1));
        assertEquals(2l, m.get(a2));
        assertEquals(1l, m.getLong(a1));
        assertEquals(2l, m.getLong(a2));
        assertEquals(3l, m.getLong(a3, 3));
    }

    @Test
    public void putGetShort() {
        m.putShort(a1, (short) 1);
        m.put(a2, Short.valueOf((short) 2));
        assertEquals(Short.valueOf((short) 1), m.get(a1));
        assertEquals(Short.valueOf((short) 2), m.get(a2));
        assertEquals((short) 1, m.getShort(a1));
        assertEquals((short) 2, m.getShort(a2));
        assertEquals((short) 3, m.getShort(a3, (short) 3));
    }
}

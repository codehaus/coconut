/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

import static org.coconut.attribute.Attributes.EMPTY_MAP;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.coconut.test.TestUtil.serializeAndUnserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.coconut.attribute.common.SizeAttribute;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class AttributeMaps_SingletonMapTest {

    private final static Attribute KEY = TestUtil.dummy(Attribute.class);

    private final static Object VALUE = "10";

    private final static Attribute OTHER_KEY = TestUtil.dummy(Attribute.class);

    private final static Object OTHER_VALUE = 10;

    private final AttributeMap singleton = Attributes.singleton(KEY, VALUE);

    private final AttributeMap singleton_boolean = Attributes.singleton(KEY, true);

    private final AttributeMap singleton_byte = Attributes.singleton(KEY, (byte) 10);

    private final AttributeMap singleton_char = Attributes.singleton(KEY, (char) 11);

    private final AttributeMap singleton_double = Attributes.singleton(KEY, (double) 12);

    private final AttributeMap singleton_float = Attributes.singleton(KEY, (float) 13);

    private final AttributeMap singleton_integer = Attributes.singleton(KEY, 14);

    private final AttributeMap singleton_long = Attributes.singleton(KEY, 15L);

    private final AttributeMap singleton_short = Attributes.singleton(KEY, (short) 16);

    private final AttributeMap singleton_null = Attributes.singleton(KEY, null);

    @Test(expected = NullPointerException.class)
    public void singletonNPE() {
        Attributes.singleton(null, VALUE);
    }

    @Test
    public void contains() {
        assertTrue(singleton.containsKey(KEY));
        assertTrue(singleton.containsValue(VALUE));

        assertFalse(singleton.containsKey(OTHER_KEY));
        assertFalse(singleton.containsValue(OTHER_VALUE));

        assertEquals(1, singleton.entrySet().size());
        assertEquals(1, singleton.keySet().size());
        assertEquals(1, singleton.values().size());
        assertEquals(1, singleton.size());
        assertFalse(singleton.isEmpty());
    }

    @Test
    public void hashcode() {
        assertEquals(singleton.hashCode(), singleton.hashCode());
        assertEquals(singleton_null.hashCode(), singleton_null.hashCode());
    }

    @Test
    public void get() {

    }

    @Test
    public void getNull() {
        assertEquals(VALUE, singleton.get(KEY));
        assertEquals(VALUE, singleton.get(KEY, OTHER_VALUE));
        assertNull(singleton.get(OTHER_KEY));
        assertEquals(OTHER_VALUE, singleton.get(OTHER_KEY, OTHER_VALUE));

        assertNull(singleton_null.get(KEY));
        assertNull(singleton_null.get(OTHER_KEY));
        assertNull(singleton_null.get(KEY, VALUE));
        assertEquals(VALUE, singleton_null.get(OTHER_KEY, VALUE));

        assertFalse(singleton_null.getBoolean(KEY));
        assertFalse(singleton_null.getBoolean(KEY, true));
        assertTrue(singleton_boolean.getBoolean(KEY));
        assertTrue(singleton_boolean.getBoolean(KEY, false));
        assertTrue(singleton_boolean.getBoolean(OTHER_KEY, true));
        assertFalse(singleton_boolean.getBoolean(OTHER_KEY));

        assertEquals((byte) 0, singleton_null.getByte(KEY));
        assertEquals((byte) 0, singleton_null.getByte(KEY, (byte) 1));
        assertEquals((byte) 10, singleton_byte.getByte(KEY));
        assertEquals((byte) 10, singleton_byte.getByte(KEY, (byte) 2));
        assertEquals((byte) 3, singleton_byte.getByte(OTHER_KEY, (byte) 3));
        assertEquals((byte) 0, singleton_byte.getByte(OTHER_KEY));

        assertEquals((char) 0, singleton_null.getChar(KEY));
        assertEquals((char) 0, singleton_null.getChar(KEY, (char) 1));
        assertEquals((char) 11, singleton_char.getChar(KEY));
        assertEquals((char) 11, singleton_char.getChar(KEY, (char) 2));
        assertEquals((char) 3, singleton_char.getChar(OTHER_KEY, (char) 3));
        assertEquals((char) 0, singleton_char.getChar(OTHER_KEY));

        assertEquals((double) 0, singleton_null.getDouble(KEY));
        assertEquals((double) 0, singleton_null.getDouble(KEY, 1));
        assertEquals((double) 12, singleton_double.getDouble(KEY));
        assertEquals((double) 12, singleton_double.getDouble(KEY, 2));
        assertEquals((double) 3, singleton_double.getDouble(OTHER_KEY, 3));
        assertEquals((double) 0, singleton_double.getDouble(OTHER_KEY));

        assertEquals((float) 0, singleton_null.getFloat(KEY));
        assertEquals((float) 0, singleton_null.getFloat(KEY, 1));
        assertEquals((float) 13, singleton_float.getFloat(KEY));
        assertEquals((float) 13, singleton_float.getFloat(KEY, 2));
        assertEquals((float) 3, singleton_float.getFloat(OTHER_KEY, 3));
        assertEquals((float) 0, singleton_float.getFloat(OTHER_KEY));

        assertEquals(0, singleton_null.getInt(KEY));
        assertEquals(0, singleton_null.getInt(KEY, 1));
        assertEquals(14, singleton_integer.getInt(KEY));
        assertEquals(14, singleton_integer.getInt(KEY, 2));
        assertEquals(3, singleton_integer.getInt(OTHER_KEY, 3));
        assertEquals(0, singleton_integer.getInt(OTHER_KEY));

        assertEquals(0L, singleton_null.getLong(KEY));
        assertEquals(0L, singleton_null.getLong(KEY, 1));
        assertEquals(15L, singleton_long.getLong(KEY));
        assertEquals(15L, singleton_long.getLong(KEY, 2));
        assertEquals(3L, singleton_long.getLong(OTHER_KEY, 3));
        assertEquals(0L, singleton_long.getLong(OTHER_KEY));

        assertEquals((short) 0, singleton_null.getShort(KEY));
        assertEquals((short) 0, singleton_null.getShort(KEY, (short) 1));
        assertEquals((short) 16, singleton_short.getShort(KEY));
        assertEquals((short) 16, singleton_short.getShort(KEY, (short) 2));
        assertEquals((short) 3, singleton_short.getShort(OTHER_KEY, (short) 3));
        assertEquals((short) 0, singleton_short.getShort(OTHER_KEY));
    }

    @Test
    public void equals() {
        assertFalse(singleton.equals(null));
        assertFalse(singleton.equals(new Object()));
        assertFalse(singleton.equals(Attributes.singleton(OTHER_KEY, VALUE)));
        assertFalse(singleton.equals(Attributes.singleton(KEY, OTHER_VALUE)));
        assertFalse(singleton.equals(Attributes.singleton(OTHER_KEY, null)));
        assertFalse(singleton.equals(Attributes.singleton(KEY, null)));
        assertFalse(singleton.equals(Attributes.singleton(OTHER_KEY, OTHER_VALUE)));
        assertFalse(singleton_null.equals(Attributes.singleton(KEY, VALUE)));
        assertFalse(singleton_null.equals(Attributes.singleton(OTHER_KEY, VALUE)));

        // classcast exception
        Map m = new HashMap() {
            @Override
            public Object get(Object key) {
                String k = (String) key;
                return super.get(key);
            }
        };
        m.put(KEY, VALUE);
        assertFalse(singleton.equals(m));

        // differen sizes
        AttributeMap am = new DefaultAttributeMap();
        am.put(KEY, VALUE);
        am.put(OTHER_KEY, VALUE);
        assertFalse(singleton.equals(am));

        assertTrue(singleton.equals(singleton));
        assertTrue(singleton.equals(Attributes.singleton(KEY, VALUE)));
        assertTrue(singleton_null.equals(Attributes.singleton(KEY, null)));
    }

    @Test
    public void toString_() {
        assertEquals("{attribute=10}", singleton.toString());
    }

    @Test
    public void unsupportedOperations() {
        try {
            singleton.clear();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            singleton.remove(KEY);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        AbstractAttributeMapTest.noPut(singleton, KEY);
    }

    /**
     * Tests that singleton map is serializable
     *
     * @throws Exception
     *             something went wrong
     */
    @Test
    public void serialization() throws Exception {
        assertIsSerializable(Attributes.singleton(SizeAttribute.INSTANCE, 123L));
    }
}

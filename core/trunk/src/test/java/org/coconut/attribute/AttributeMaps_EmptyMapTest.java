/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

import static org.coconut.attribute.Attributes.EMPTY_ATTRIBUTE_MAP;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.coconut.test.TestUtil.serializeAndUnserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;

import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * Tests {@link Attributes#EMPTY_ATTRIBUTE_MAP}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AttributeMaps_EmptyMapTest {
    private final static Attribute KEY = TestUtil.dummy(Attribute.class);

    @Test
    public void various() {
        assertFalse(EMPTY_ATTRIBUTE_MAP.containsKey(KEY));
        assertFalse(EMPTY_ATTRIBUTE_MAP.containsValue(KEY));
        assertEquals(0, EMPTY_ATTRIBUTE_MAP.entrySet().size());
        assertEquals(new HashMap(), EMPTY_ATTRIBUTE_MAP);
        assertEquals(new HashMap().hashCode(), EMPTY_ATTRIBUTE_MAP.hashCode());
        assertEquals(0, EMPTY_ATTRIBUTE_MAP.size());
        assertTrue(EMPTY_ATTRIBUTE_MAP.isEmpty());
        assertEquals(0, EMPTY_ATTRIBUTE_MAP.keySet().size());
        assertEquals(0, EMPTY_ATTRIBUTE_MAP.values().size());
        assertFalse(EMPTY_ATTRIBUTE_MAP.equals(new HashSet()));
    }

    @Test
    public void putters() {
        try {
            EMPTY_ATTRIBUTE_MAP.put(KEY, 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_ATTRIBUTE_MAP.putBoolean(KEY, true);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_ATTRIBUTE_MAP.putByte(KEY, (byte) 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_ATTRIBUTE_MAP.putChar(KEY, 'd');
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_ATTRIBUTE_MAP.putDouble(KEY, 3.4d);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_ATTRIBUTE_MAP.putFloat(KEY, 123.3f);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_ATTRIBUTE_MAP.putInt(KEY, 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_ATTRIBUTE_MAP.putLong(KEY, 34l);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_ATTRIBUTE_MAP.putShort(KEY, (short) 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
    }

    @Test
    public void getters() {
        assertNull(EMPTY_ATTRIBUTE_MAP.get(KEY));
        assertEquals("goo", EMPTY_ATTRIBUTE_MAP.get(KEY, "goo"));
        assertFalse(EMPTY_ATTRIBUTE_MAP.getBoolean(KEY));
        assertTrue(EMPTY_ATTRIBUTE_MAP.getBoolean(KEY, true));
        assertEquals((byte) 0, EMPTY_ATTRIBUTE_MAP.getByte(KEY));
        assertEquals((byte) 100, EMPTY_ATTRIBUTE_MAP.getByte(KEY, (byte) 100));
        assertEquals('\u0000', EMPTY_ATTRIBUTE_MAP.getChar(KEY));
        assertEquals('f', EMPTY_ATTRIBUTE_MAP.getChar(KEY, 'f'));
        assertEquals(0d, EMPTY_ATTRIBUTE_MAP.getDouble(KEY));
        assertEquals(100.5d, EMPTY_ATTRIBUTE_MAP.getDouble(KEY, 100.5d));
        assertEquals(0f, EMPTY_ATTRIBUTE_MAP.getFloat(KEY));
        assertEquals(100.5f, EMPTY_ATTRIBUTE_MAP.getFloat(KEY, 100.5f));
        assertEquals(0, EMPTY_ATTRIBUTE_MAP.getInt(KEY));
        assertEquals(100, EMPTY_ATTRIBUTE_MAP.getInt(KEY, 100));
        assertEquals(0l, EMPTY_ATTRIBUTE_MAP.getLong(KEY));
        assertEquals(100l, EMPTY_ATTRIBUTE_MAP.getLong(KEY, 100l));
        assertEquals((short) 0, EMPTY_ATTRIBUTE_MAP.getShort(KEY));
        assertEquals((short) 100, EMPTY_ATTRIBUTE_MAP.getShort(KEY, (byte) 100));

    }

    /**
     * Tests that EMPTY_MAP is serializable and maintains the singleton property.
     * 
     * @throws Exception
     *             something went wrong
     */
    @Test
    public void serialization() throws Exception {
        assertIsSerializable(EMPTY_ATTRIBUTE_MAP);
        assertSame(EMPTY_ATTRIBUTE_MAP, serializeAndUnserialize(EMPTY_ATTRIBUTE_MAP));
    }
}

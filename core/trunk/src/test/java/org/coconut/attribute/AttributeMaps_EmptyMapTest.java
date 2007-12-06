/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

import static org.coconut.attribute.AttributeMaps.EMPTY_MAP;
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
 * Tests {@link AttributeMaps#EMPTY_MAP}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AttributeMaps_EmptyMapTest {
    private final static Attribute KEY = TestUtil.dummy(Attribute.class);

    @Test
    public void various() {
        assertFalse(EMPTY_MAP.containsKey(KEY));
        assertFalse(EMPTY_MAP.containsValue(KEY));
        assertEquals(0, EMPTY_MAP.entrySet().size());
        assertEquals(new HashMap(), EMPTY_MAP);
        assertEquals(new HashMap().hashCode(), EMPTY_MAP.hashCode());
        assertEquals(0, EMPTY_MAP.size());
        assertTrue(EMPTY_MAP.isEmpty());
        assertEquals(0, EMPTY_MAP.keySet().size());
        assertEquals(0, EMPTY_MAP.values().size());
        assertFalse(EMPTY_MAP.equals(new HashSet()));
    }

    @Test
    public void putters() {
        try {
            EMPTY_MAP.put(KEY, 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_MAP.putBoolean(KEY, true);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_MAP.putByte(KEY, (byte) 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_MAP.putChar(KEY, 'd');
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_MAP.putDouble(KEY, 3.4d);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_MAP.putFloat(KEY, 123.3f);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_MAP.putInt(KEY, 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_MAP.putLong(KEY, 34l);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            EMPTY_MAP.putShort(KEY, (short) 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
    }

    @Test
    public void getters() {
        assertNull(EMPTY_MAP.get(KEY));
        assertEquals("goo", EMPTY_MAP.get(KEY, "goo"));
        assertFalse(EMPTY_MAP.getBoolean(KEY));
        assertTrue(EMPTY_MAP.getBoolean(KEY, true));
        assertEquals((byte) 0, EMPTY_MAP.getByte(KEY));
        assertEquals((byte) 100, EMPTY_MAP.getByte(KEY, (byte) 100));
        assertEquals('\u0000', EMPTY_MAP.getChar(KEY));
        assertEquals('f', EMPTY_MAP.getChar(KEY, 'f'));
        assertEquals(0d, EMPTY_MAP.getDouble(KEY));
        assertEquals(100.5d, EMPTY_MAP.getDouble(KEY, 100.5d));
        assertEquals(0f, EMPTY_MAP.getFloat(KEY));
        assertEquals(100.5f, EMPTY_MAP.getFloat(KEY, 100.5f));
        assertEquals(0, EMPTY_MAP.getInt(KEY));
        assertEquals(100, EMPTY_MAP.getInt(KEY, 100));
        assertEquals(0l, EMPTY_MAP.getLong(KEY));
        assertEquals(100l, EMPTY_MAP.getLong(KEY, 100l));
        assertEquals((short) 0, EMPTY_MAP.getShort(KEY));
        assertEquals((short) 100, EMPTY_MAP.getShort(KEY, (byte) 100));

    }

    /**
     * Tests that EMPTY_MAP is serializable and maintains the singleton property.
     * 
     * @throws Exception
     *             something went wrong
     */
    @Test
    public void serialization() throws Exception {
        assertIsSerializable(EMPTY_MAP);
        assertSame(EMPTY_MAP, serializeAndUnserialize(EMPTY_MAP));
    }
}

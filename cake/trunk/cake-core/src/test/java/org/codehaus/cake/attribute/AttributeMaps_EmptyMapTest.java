/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import static org.codehaus.cake.attribute.Attributes.EMPTY_ATTRIBUTE_MAP;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.codehaus.cake.test.util.TestUtil.serializeAndUnserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

/**
 * Tests {@link Attributes#EMPTY_ATTRIBUTE_MAP}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AttributeMaps_EmptyMapTest extends AtrStubs {
    private final static ObjectAttribute KEY = new ObjectAttribute("key", Integer.class, 5) {};

    @Test
    public void various() {
        assertFalse(EMPTY_ATTRIBUTE_MAP.contains(KEY));
       // assertFalse(EMPTY_ATTRIBUTE_MAP.containsValue(KEY));
       // assertEquals(0, EMPTY_ATTRIBUTE_MAP.entrySet().size());
        assertEquals(new HashMap(), EMPTY_ATTRIBUTE_MAP);
        assertEquals(new HashMap().hashCode(), EMPTY_ATTRIBUTE_MAP.hashCode());
        assertEquals(0, EMPTY_ATTRIBUTE_MAP.size());
        assertTrue(EMPTY_ATTRIBUTE_MAP.isEmpty());
        assertEquals(0, EMPTY_ATTRIBUTE_MAP.attributeSet().size());
       // assertEquals(0, EMPTY_ATTRIBUTE_MAP.values().size());
        assertFalse(EMPTY_ATTRIBUTE_MAP.equals(new HashSet()));
    }

    @Test
    public void putters() {
        try {
            EMPTY_ATTRIBUTE_MAP.put(KEY, 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            EMPTY_ATTRIBUTE_MAP.put(KEY, true);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            EMPTY_ATTRIBUTE_MAP.put(KEY, (byte) 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            EMPTY_ATTRIBUTE_MAP.put(KEY, 'd');
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            EMPTY_ATTRIBUTE_MAP.put(D_1, 3.4d);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            EMPTY_ATTRIBUTE_MAP.put(KEY, 123.3f);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            EMPTY_ATTRIBUTE_MAP.put(I_1, 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            EMPTY_ATTRIBUTE_MAP.put(L_1, 34l);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            EMPTY_ATTRIBUTE_MAP.put(KEY, (short) 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
    }

    @Test
    public void getters() {
        assertEquals(5, EMPTY_ATTRIBUTE_MAP.get(KEY));
        assertEquals("goo", EMPTY_ATTRIBUTE_MAP.get(KEY, "goo"));
        assertFalse(EMPTY_ATTRIBUTE_MAP.get(B_FALSE));
        assertTrue(EMPTY_ATTRIBUTE_MAP.get(B_FALSE, true));
        assertEquals((byte) 1, EMPTY_ATTRIBUTE_MAP.get(B_1));
        assertEquals((byte) 2, EMPTY_ATTRIBUTE_MAP.get(B_2));
        assertEquals((byte) 100, EMPTY_ATTRIBUTE_MAP.get(B_1, (byte) 100));
        assertEquals('\u0001', EMPTY_ATTRIBUTE_MAP.get(C_1));
        assertEquals('\u0002', EMPTY_ATTRIBUTE_MAP.get(C_2));
        assertEquals('f', EMPTY_ATTRIBUTE_MAP.get(C_1, 'f'));
        assertEquals(1.5d, EMPTY_ATTRIBUTE_MAP.get(D_1), 0);
        assertEquals(2.5d, EMPTY_ATTRIBUTE_MAP.get(D_2), 0);
        assertEquals(100.5d, EMPTY_ATTRIBUTE_MAP.get(D_1, 100.5d), 0);
        assertEquals(1.5f, EMPTY_ATTRIBUTE_MAP.get(F_1), 0);
        assertEquals(2.5f, EMPTY_ATTRIBUTE_MAP.get(F_2), 0);
        assertEquals(100.5f, EMPTY_ATTRIBUTE_MAP.get(F_2, 100.5f), 0);
        assertEquals(1, EMPTY_ATTRIBUTE_MAP.get(I_1));
        assertEquals(2, EMPTY_ATTRIBUTE_MAP.get(I_2));
        assertEquals(100, EMPTY_ATTRIBUTE_MAP.get(I_1, 100));
        assertEquals(1l, EMPTY_ATTRIBUTE_MAP.get(L_1));
        assertEquals(100l, EMPTY_ATTRIBUTE_MAP.get(L_2, 100l));
        assertEquals((short) 1, EMPTY_ATTRIBUTE_MAP.get(S_1));
        assertEquals((short) 2, EMPTY_ATTRIBUTE_MAP.get(S_2));
        assertEquals((short) 100, EMPTY_ATTRIBUTE_MAP.get(S_1, (short) 100));

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

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M5;

import org.coconut.cache.tck.CommonCacheTestBundle;
import org.junit.Test;

public class BasicMap extends CommonCacheTestBundle {

    /**
     * size returns the correct values.
     */
    @Test
    public void testSize() {
        assertEquals(0, c0.size());
        assertEquals(5, c5.size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty.
     */
    @Test
    public void testIsEmpty() {
        assertTrue(c0.isEmpty());
        assertFalse(c5.isEmpty());
    }

    /**
     * containsKey returns true for contained key.
     */
    @Test
    public void testContainsKey() {
        assertTrue(c5.containsKey(1));
        assertFalse(c5.containsKey(6));
    }

    /**
     * containsKey(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testContainsKey_NullPointerException() {
        c5.containsKey(null);
    }

    /**
     * containsValue returns true for held values.
     */
    @Test
    public void testContainsValue() {
        assertTrue(c5.containsValue("A"));
        assertFalse(c5.containsValue("Z"));
    }

    /**
     * containsValue(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testContainsValue_NullPointerException() {
        c5.containsValue(null);
    }

    /**
     * Just test that the toString() method works.
     */
    @Test
    public void testToString() {
        String s = c5.toString();
        try {
            for (int i = 1; i < 6; i++) {
                assertTrue(s.indexOf(String.valueOf(i)) >= 0);
                assertTrue(s.indexOf("" + (char) (i + 64)) >= 0);
            }
        } catch (AssertionError ar) {
            System.out.println(s);
            throw ar;
        }
    }

    /**
     * get(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testGetNull() {
        c5.get(null);
    }

    /**
     * Test simple get.
     */
    @Test
    public void testGet() {
        assertEquals(M1.getValue(), c5.get(M1.getKey()));
        assertEquals(M5.getValue(), c5.get(M5.getKey()));
    }
    
    public void testEquals() {
        assertTrue(c4.equals(c4));
        assertFalse(c3.equals(c4));
        assertFalse(c4.equals(c3));
    }
}

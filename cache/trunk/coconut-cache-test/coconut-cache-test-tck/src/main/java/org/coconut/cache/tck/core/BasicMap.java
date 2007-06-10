/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M5;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

public class BasicMap extends AbstractCacheTCKTestBundle {

    /**
     * size returns the correct values.
     */
    @Test
    public void testSize() {
        assertEquals(0, newCache().size());
        assertEquals(5, newCache(5).size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty.
     */
    @Test
    public void testIsEmpty() {
        assertTrue(newCache(0).isEmpty());
        assertFalse(newCache(5).isEmpty());
    }

    /**
     * containsKey returns true for contained key.
     */
    @Test
    public void testContainsKey() {
        c=newCache(5);
        assertTrue(c.containsKey(1));
        assertFalse(c.containsKey(6));
    }

    /**
     * containsKey(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testContainsKey_NullPointerException() {
        c=newCache(5);
        c.containsKey(null);
    }

    /**
     * containsValue returns true for held values.
     */
    @Test
    public void testContainsValue() {
        c=newCache(5);
        assertTrue(c.containsValue("A"));
        assertFalse(c.containsValue("Z"));
    }

    /**
     * containsValue(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testContainsValue_NullPointerException() {
        c=newCache(5);
        c.containsValue(null);
    }

    /**
     * Just test that the toString() method works.
     */
    @Test
    public void testToString() {
        c=newCache(5);
        String s = c.toString();
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
        c=newCache(5);
        c.get(null);
    }

    /**
     * Test simple get.
     */
    @Test
    public void testGet() {
        c=newCache(5);
        assertEquals(M1.getValue(), c.get(M1.getKey()));
        assertEquals(M5.getValue(), c.get(M5.getKey()));
    }
    
    public void testEquals() {
        Cache<Integer,String> c3=newCache(4);
        Cache<Integer,String> c4=newCache(4);
        assertTrue(c4.equals(c4));
        assertFalse(c3.equals(c4));
        assertFalse(c4.equals(c3));
    }
}

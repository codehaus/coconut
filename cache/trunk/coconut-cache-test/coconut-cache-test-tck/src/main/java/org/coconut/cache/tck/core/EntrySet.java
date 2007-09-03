/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_SET;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.MNAN2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests non modifying actions for a caches entryset.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EntrySet extends AbstractCacheTCKTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        newCache().entrySet().add(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAll() {
        newCache().entrySet().addAll(M1_TO_M5_SET);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNullPointerException() {
        newCache().entrySet().addAll(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAllUnsupportedOperationException() {
        newCache().entrySet().addAll(Collections.singleton(M1));
    }

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void testContains() {
        c = newCache(5);
        assertTrue(c.entrySet().contains(M1));
        assertFalse(c.entrySet().contains(MNAN1));
        assertFalse(c.entrySet().contains(MNAN2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testContainsAll() {
        c = newCache(5);
        assertTrue(c.entrySet().containsAll(Arrays.asList(M1, M5)));
        assertFalse(c.entrySet().containsAll(Arrays.asList(M1, MNAN1)));
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsNullPointerException() {
        // this is a "bug"/feature in ConcurrentHashMap
        newCache(5).entrySet().contains(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllNullPointerException() {
        newCache(5).entrySet().containsAll(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    public void testContainsAllNullNullPointerException() {
    // this is a "bug"/feature in ConcurrentHashMap
    // try {
    // c5.entrySet().containsAll(Arrays.asList(new MapEntry(0, "A"),null,new
    // MapEntry(1, "B")));
    // shouldThrow();
    // } catch (NullPointerException e) {
    // }
    }

    // equals is not defined for caches
    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        c = newCache();

        assertTrue(new HashSet().equals(c.entrySet()));
        assertTrue(c.entrySet().equals(new HashSet()));

        assertFalse(c.entrySet().equals(null));
        assertFalse(c.entrySet().equals(newCache(1).entrySet()));
        c = newCache(5);
        assertTrue(M1_TO_M5_SET.equals(c.entrySet()));
        assertTrue(c.entrySet().equals(M1_TO_M5_SET));

        assertFalse(c.entrySet().equals(null));
        assertFalse(c.entrySet().equals(newCache(4).entrySet()));
        assertFalse(c.entrySet().equals(newCache(6).entrySet()));
    }

    @Test
    public void testHashCode() {
        assertEquals(M1_TO_M5_SET.hashCode(), newCache(5).entrySet().hashCode());
        assertEquals(new HashSet().hashCode(), newCache().entrySet().hashCode());
    }

    @Test
    @SuppressWarnings("unused")
    public void testIterator() {
        int count = 0;
        c = newCache();
        for (Map.Entry<Integer, String> entry : c.entrySet()) {
            assertFalse(true);
        }
        c = newCache(5);
        Iterator<Map.Entry<Integer, String>> iter = c.entrySet().iterator();
        while (iter.hasNext()) {
            assertTrue(M1_TO_M5_SET.contains(iter.next()));
            count++;
        }
        assertEquals(5, count);
    }

    /**
     * size returns the correct values
     */
    @Test
    public void testSize() {
        assertEquals(0, newCache().entrySet().size());
        assertEquals(5, newCache(5).entrySet().size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test
    public void testIsEmpty() {
        assertTrue(newCache().entrySet().isEmpty());
        assertFalse(newCache(5).entrySet().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToArray() {
        c = newCache();
        assertEquals(new HashSet(), new HashSet(Arrays.asList(c.entrySet().toArray())));

        assertEquals(new HashSet(), new HashSet(Arrays.asList(c.entrySet().toArray(
                new Map.Entry[0]))));
        c = newCache(5);
        Arrays.toString(c.entrySet().toArray());
        // System.out.println(Arrays.asList(c5.entrySet().toArray()));
        // System.out.println(new
        // HashSet(Arrays.asList(c5.entrySet().toArray())));
        // System.out.println(c5.entrySet());
        // System.out.println(M1_TO_M5_SET);

        assertEquals(M1_TO_M5_SET, new HashSet(Arrays.asList(c.entrySet().toArray())));

        assertEquals(M1_TO_M5_SET, new HashSet(Arrays.asList(c.entrySet().toArray(
                new Map.Entry[0]))));
        assertEquals(M1_TO_M5_SET, new HashSet(Arrays.asList(c.entrySet().toArray(
                new Map.Entry[5]))));
        // assertEquals(createSet(5), new
        // HashSet(Arrays.asList(c5.entrySet().toArray(new Map.Entry[10]))));

    }
}

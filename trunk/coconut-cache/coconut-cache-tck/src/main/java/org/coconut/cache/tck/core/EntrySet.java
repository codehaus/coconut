/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_SET;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.MNAN2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * Tests non modifying actions for a caches entryset.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EntrySet extends CacheTestBundle {

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        c0.entrySet().add(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAll() {
        c0.entrySet().addAll(M1_TO_M5_SET);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNullPointerException() {
        c0.entrySet().addAll(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAllUnsupportedOperationException() {
        c0.entrySet().addAll(Collections.singleton(M1));
    }

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void testContains() {
        assertTrue(c5.entrySet().contains(M1));
        assertFalse(c5.entrySet().contains(MNAN1));
        assertFalse(c5.entrySet().contains(MNAN2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testContainsAll() {
        assertTrue(c5.entrySet().containsAll(Arrays.asList(M1, M5)));
        assertFalse(c5.entrySet().containsAll(Arrays.asList(M1, MNAN1)));
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsNullPointerException() {
        // this is a "bug"/feature in ConcurrentHashMap
        c5.entrySet().contains(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllNullPointerException() {
        c5.entrySet().containsAll(null);
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
        assertTrue(M1_TO_M5_SET.equals(c5.entrySet()));
        assertTrue(c5.entrySet().equals(M1_TO_M5_SET));

        assertTrue(new HashSet().equals(c0.entrySet()));
        assertTrue(c0.entrySet().equals(new HashSet()));

        assertFalse(c0.entrySet().equals(null));
        assertFalse(c0.entrySet().equals(c1.entrySet()));

        assertFalse(c5.entrySet().equals(null));
        assertFalse(c5.entrySet().equals(c4.entrySet()));
        assertFalse(c5.entrySet().equals(c6.entrySet()));
    }

    @Test
    public void testHashCode() {
        assertEquals(M1_TO_M5_SET.hashCode(), c5.entrySet().hashCode());
        assertEquals(new HashSet().hashCode(), c0.entrySet().hashCode());
    }

    @Test
    @SuppressWarnings("unused")
    public void testIterator() {
        int count = 0;

        for (Map.Entry<Integer, String> entry : c0.entrySet()) {
            assertFalse(true);
        }
        Iterator<Map.Entry<Integer, String>> iter = c5.entrySet().iterator();
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
        assertEquals(0, c0.entrySet().size());
        assertEquals(5, c5.entrySet().size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test
    public void testIsEmpty() {
        assertTrue(c0.entrySet().isEmpty());
        assertFalse(c5.entrySet().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToArray() {
        assertEquals(new HashSet(), new HashSet(Arrays.asList(c0.entrySet()
                .toArray())));

        Arrays.toString(c5.entrySet().toArray());
        // System.out.println(Arrays.asList(c5.entrySet().toArray()));
        // System.out.println(new
        // HashSet(Arrays.asList(c5.entrySet().toArray())));
        // System.out.println(c5.entrySet());
        // System.out.println(M1_TO_M5_SET);

        assertEquals(M1_TO_M5_SET, new HashSet(Arrays.asList(c5.entrySet()
                .toArray())));

        assertEquals(new HashSet(), new HashSet(Arrays.asList(c0.entrySet()
                .toArray(new Map.Entry[0]))));

        assertEquals(M1_TO_M5_SET, new HashSet(Arrays.asList(c5.entrySet()
                .toArray(new Map.Entry[0]))));
        assertEquals(M1_TO_M5_SET, new HashSet(Arrays.asList(c5.entrySet()
                .toArray(new Map.Entry[5]))));
        // assertEquals(createSet(5), new
        // HashSet(Arrays.asList(c5.entrySet().toArray(new Map.Entry[10]))));

    }
}

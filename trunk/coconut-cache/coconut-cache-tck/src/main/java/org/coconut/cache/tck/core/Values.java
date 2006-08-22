/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_VALUES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * Tests non modifying actions for a caches value set
 * {@link org.coconut.cache.Cache#values()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Values extends CacheTestBundle {

    @Test(expected = NullPointerException.class)
    public void testAdd() {
        try {
            c0.values().add(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = NullPointerException.class)
    public void testAddAll() {
        try {
            c0.values().addAll(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAllUnsupportedOperationException() {
        c0.values().addAll(Collections.singleton("A"));
    }

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void testContains() {
        assertTrue(c5.values().contains("A"));
        assertFalse(c5.values().contains("Z"));
        assertFalse(c5.values().contains("F"));
    }

    @Test
    public void testContainsAll() {
        assertTrue(c5.values().containsAll(Arrays.asList("A", "E")));
        assertFalse(c5.values().containsAll(Arrays.asList("A", "F")));
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsNullPointerException() {
        c5.values().contains(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllNullPointerException() {
        c5.values().containsAll(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllInNullPointerException() {
        c5.values().containsAll(Arrays.asList(M1.getValue(), null));
    }

    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        // assertTrue(c5.values().equals(c5.values()));

        assertFalse(c0.values().equals(null));
        assertFalse(c0.values().equals(c1.values()));

        assertFalse(c5.values().equals(null));
        assertFalse(c5.values().equals(c4.values()));
        assertFalse(c5.values().equals(c6.values()));
    }

    @Test
    public void testHashCode() {
        // assertEquals(c5.values().hashCode(), c5.values().hashCode());
    }

    @Test
    @SuppressWarnings("unused")
    public void testIterator() {
        int count = 0;

        for (String entry : c0.values()) {
            count++;
        }
        assertEquals(0, count);
        Iterator<String> iter = c5.values().iterator();
        while (iter.hasNext()) {
            assertTrue(M1_TO_M5_VALUES.contains(iter.next()));
            count++;
        }
        assertEquals(5, count);
    }

    /**
     * size returns the correct values
     */
    @Test
    public void testSize() {
        assertEquals(0, c0.values().size());
        assertEquals(5, c5.values().size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test
    public void testIsEmpty() {
        assertTrue(c0.values().isEmpty());
        assertFalse(c5.values().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToArray() {
        assertEquals(new HashSet(), new HashSet(Arrays.asList(c0.values()
                .toArray())));

        assertEquals(new HashSet(M1_TO_M5_VALUES), new HashSet(Arrays.asList(c5
                .values().toArray())));

        assertEquals(new HashSet(), new HashSet(Arrays.asList(c0.values()
                .toArray(new String[0]))));

        assertEquals(new HashSet(M1_TO_M5_VALUES), new HashSet(Arrays.asList(c5
                .values().toArray(new String[0]))));
        assertEquals(new HashSet(M1_TO_M5_VALUES), new HashSet(Arrays.asList(c5
                .values().toArray(new String[5]))));
    }

}

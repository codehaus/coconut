/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_KEY_SET;
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
 * {@link org.coconut.cache.Cache#keySet()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class KeySet extends CacheTestBundle {

    @Test(expected = NullPointerException.class)
    public void testAdd() {
        try {
            c0.keySet().add(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = NullPointerException.class)
    public void testAddAll() {
        try {
            c0.keySet().addAll(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAllUnsupportedOperationException() {
        c0.keySet().addAll(Collections.singleton(1));
    }

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void testContains() {
        assertTrue(c5.keySet().contains(1));
        assertFalse(c5.keySet().contains(1111));
        assertFalse(c5.keySet().contains(6));
    }

    @Test
    public void testContainsAll() {
        assertTrue(c5.keySet().containsAll(Arrays.asList(1, 5)));
        assertFalse(c5.keySet().containsAll(Arrays.asList(1, 6)));
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsNullPointerException() {
        c5.keySet().contains(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllNullPointerException() {
        c5.keySet().containsAll(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllInNullPointerException() {
        c5.keySet().containsAll(Arrays.asList(M1.getKey(), null));
    }

    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        // assertTrue(c5.values().equals(c5.values()));

        assertFalse(c0.keySet().equals(null));
        assertFalse(c0.keySet().equals(c1.keySet()));

        assertFalse(c5.keySet().equals(null));
        assertFalse(c5.keySet().equals(c4.keySet()));
        assertFalse(c5.keySet().equals(c6.keySet()));
    }

    @Test
    public void testHashCode() {
        // assertEquals(c5.values().hashCode(), c5.values().hashCode());
    }

    @Test
    @SuppressWarnings("unused") 
    public void testIterator() {
        int count = 0;

        for (Integer entry : c0.keySet()) {
            count++;
        }
        assertEquals(0, count);
        Iterator<Integer> iter = c5.keySet().iterator();
        while (iter.hasNext()) {
            assertTrue(M1_TO_M5_KEY_SET.contains(iter.next()));
            count++;
        }
        assertEquals(5, count);
    }

    /**
     * size returns the correct values
     */
    @Test
    public void testSize() {
        assertEquals(0, c0.keySet().size());
        assertEquals(5, c5.keySet().size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test
    public void testIsEmpty() {
        assertTrue(c0.keySet().isEmpty());
        assertFalse(c5.keySet().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToArray() {
        assertEquals(new HashSet(), new HashSet(Arrays.asList(c0.keySet()
                .toArray())));

        assertEquals(new HashSet(M1_TO_M5_KEY_SET), new HashSet(Arrays
                .asList(c5.keySet().toArray())));

        assertEquals(new HashSet(), new HashSet(Arrays.asList(c0.keySet()
                .toArray(new Integer[0]))));

        assertEquals(new HashSet(M1_TO_M5_KEY_SET), new HashSet(Arrays
                .asList(c5.keySet().toArray(new Integer[0]))));
        assertEquals(new HashSet(M1_TO_M5_KEY_SET), new HashSet(Arrays
                .asList(c5.keySet().toArray(new Integer[5]))));
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_VALUES;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

/**
 * Tests non modifying actions for a caches value set
 * {@link org.coconut.cache.Cache#values()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Values extends AbstractCacheTCKTestBundle {

    @Test(expected = NullPointerException.class)
    public void testAdd() {
        try {
            newCache().values().add(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = NullPointerException.class)
    public void testAddAll() {
        try {
            newCache().values().addAll(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAllUnsupportedOperationException() {
        newCache().values().addAll(Collections.singleton("A"));
    }

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void testContains() {
        c = newCache(5);
        assertTrue(c.values().contains("A"));
        assertFalse(c.values().contains("Z"));
        assertFalse(c.values().contains("F"));
    }

    @Test
    public void testContainsAll() {
        c = newCache(5);
        assertTrue(c.values().containsAll(Arrays.asList("A", "E")));
        assertFalse(c.values().containsAll(Arrays.asList("A", "F")));
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsNullPointerException() {
        newCache(5).values().contains(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllNullPointerException() {
        newCache(5).values().containsAll(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllInNullPointerException() {
        newCache(5).values().containsAll(Arrays.asList(M1.getValue(), null));
    }

    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        // assertTrue(c5.values().equals(c5.values()));
        c = newCache();
        assertFalse(c.values().equals(null));
        assertFalse(c.values().equals(newCache(1).values()));
        c = newCache(5);
        assertFalse(c.values().equals(null));
        assertFalse(c.values().equals(newCache(4).values()));
        assertFalse(c.values().equals(newCache(6).values()));
    }

    @Test
    public void testHashCode() {
    // assertEquals(c5.values().hashCode(), c5.values().hashCode());
    }

    @Test
    @SuppressWarnings("unused")
    public void testIterator() {
        int count = 0;
        c = newCache();
        for (String entry : c.values()) {
            count++;
        }
        c = newCache(5);
        assertEquals(0, count);
        Iterator<String> iter = c.values().iterator();
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
        assertEquals(0, newCache().values().size());
        assertEquals(5, newCache(5).values().size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test
    public void testIsEmpty() {
        assertTrue(newCache().values().isEmpty());
        assertFalse(newCache(5).values().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToArray() {
        c = newCache();
        assertEquals(new HashSet(), new HashSet(Arrays.asList(c.values().toArray())));
        assertEquals(new HashSet(), new HashSet(Arrays.asList(c.values().toArray(
                new String[0]))));
        c = newCache(5);

        assertEquals(new HashSet(M1_TO_M5_VALUES), new HashSet(Arrays.asList(c.values()
                .toArray())));

        assertEquals(new HashSet(M1_TO_M5_VALUES), new HashSet(Arrays.asList(c.values()
                .toArray(new String[0]))));
        assertEquals(new HashSet(M1_TO_M5_VALUES), new HashSet(Arrays.asList(c.values()
                .toArray(new String[5]))));
    }

}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_KEY_SET;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests non modifying actions for a caches value set
 * {@link org.coconut.cache.Cache#keySet()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class KeySet extends AbstractCacheTCKTest {

    @Test(expected = NullPointerException.class)
    public void testAdd() {
        try {
            newCache().keySet().add(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = NullPointerException.class)
    public void testAddAll() {
        try {
            newCache().keySet().addAll(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAllUnsupportedOperationException() {
        newCache().keySet().addAll(Collections.singleton(1));
    }

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void testContains() {
        c=newCache(5);
        assertTrue(c.keySet().contains(1));
        assertFalse(c.keySet().contains(1111));
        assertFalse(c.keySet().contains(6));
    }

    @Test
    public void testContainsAll() {
        c=newCache(5);
        assertTrue(c.keySet().containsAll(Arrays.asList(1, 5)));
        assertFalse(c.keySet().containsAll(Arrays.asList(1, 6)));
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsNullPointerException() {
        newCache(5).keySet().contains(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllNullPointerException() {
        newCache(5).keySet().containsAll(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void testContainsAllInNullPointerException() {
        newCache(5).keySet().containsAll(Arrays.asList(M1.getKey(), null));
    }

    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        // assertTrue(c5.values().equals(c5.values()));
        c=newCache();
        assertFalse(c.keySet().equals(null));
        assertFalse(c.keySet().equals(newCache(1).keySet()));
        c=newCache(5);
        assertFalse(c.keySet().equals(null));
        assertFalse(c.keySet().equals(newCache(4).keySet()));
        assertFalse(c.keySet().equals(newCache(6).keySet()));
    }

    @Test
    public void testHashCode() {
    // assertEquals(c5.values().hashCode(), c5.values().hashCode());
    }

    @Test
    @SuppressWarnings("unused")
    public void testIterator() {
        int count = 0;
        c=newCache();
        for (Integer entry : c.keySet()) {
            count++;
        }
        assertEquals(0, count);
        c=newCache(5);
        Iterator<Integer> iter = c.keySet().iterator();
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
        assertEquals(0, newCache().keySet().size());
        assertEquals(5, newCache(5).keySet().size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test
    public void testIsEmpty() {
        
        assertTrue(newCache().keySet().isEmpty());
        assertFalse(newCache(5).keySet().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToArray() {
        c = newCache();
        assertEquals(new HashSet(), new HashSet(Arrays.asList(c.keySet().toArray())));

        assertEquals(new HashSet(), new HashSet(Arrays.asList(c.keySet().toArray(
                new Integer[0]))));
        c = newCache(5);
        assertEquals(new HashSet(M1_TO_M5_KEY_SET), new HashSet(Arrays.asList(c.keySet()
                .toArray())));

        assertEquals(new HashSet(M1_TO_M5_KEY_SET), new HashSet(Arrays.asList(c.keySet()
                .toArray(new Integer[0]))));
        assertEquals(new HashSet(M1_TO_M5_KEY_SET), new HashSet(Arrays.asList(c.keySet()
                .toArray(new Integer[5]))));
    }
}

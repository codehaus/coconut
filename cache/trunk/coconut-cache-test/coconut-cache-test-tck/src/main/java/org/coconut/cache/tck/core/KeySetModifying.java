/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.MNAN2;

import java.util.Arrays;
import java.util.Collections;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests the modifying functions of a keySet().
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class KeySetModifying extends AbstractCacheTCKTest {

    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() {
        c = newCache(5);
        assertEquals(c.keySet().size(), 5);
        assertFalse(c.keySet().isEmpty());
        assertEquals(c.size(), 5);
        assertFalse(c.isEmpty());

        c.keySet().clear();

        assertEquals(c.keySet().size(), 0);
        assertTrue(c.keySet().isEmpty());
        assertEquals(c.size(), 0);
        assertTrue(c.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNullPointerException() {
        newCache(0).keySet().remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove2NullPointerException() {
        newCache(5).keySet().remove(null);
    }

    @Test
    public void testRemove() {
        c = newCache(0);
        assertFalse(c.keySet().remove(MNAN2.getKey()));
        assertFalse(c.keySet().remove(MNAN1.getKey()));

        c = newCache(5);
        assertTrue(c.keySet().remove(M1.getKey()));
        assertEquals(4, c.size());
        assertFalse(c.keySet().contains(M1.getKey()));

        c = newCache(1);
        assertTrue(c.keySet().remove(M1.getKey()));
        assertTrue(c.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveAll() {
        c = newCache(5);
        assertFalse(c.keySet().removeAll(Arrays.asList("F", "H")));
        assertTrue(c.keySet().removeAll(Arrays.asList("F", M2.getKey(), "H")));
        assertEquals(4, c.size());
        assertFalse(c.keySet().contains(M2.getKey()));
        assertTrue(c.keySet().removeAll(Arrays.asList(M1.getKey(), M4.getKey())));
        assertFalse(c.keySet().contains(M4.getKey()));
        assertFalse(c.keySet().contains(M1.getKey()));
        assertEquals(2, c.size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullPointerException() {
        newCache(0).keySet().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll2NullPointerException() {
        newCache(5).keySet().removeAll(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRetainAll() {
        c = newCache(1);
        c.keySet().retainAll(Collections.singleton(M1.getKey()));
        assertEquals(1, c.size());

        c.keySet().retainAll(Collections.singleton(M2.getKey()));
        assertEquals(0, c.size());
        c = newCache(5);
        c.keySet().retainAll(
                Arrays.asList(M1.getKey(), "F", M3.getKey(), "G", M5.getKey()));
        assertEquals(3, c.size());
        assertTrue(c.keySet().contains(M1.getKey()) && c.keySet().contains(M3.getKey())
                && c.keySet().contains(M5.getKey()));

    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNullPointerException() {
        newCache(5).keySet().retainAll(null);
    }
}

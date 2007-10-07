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
import static org.coconut.test.CollectionUtils.MNAN4;

import java.util.Arrays;
import java.util.Collections;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class EntrySetModifying extends AbstractCacheTCKTest {

    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() {
        c = newCache(5);
        assertEquals(c.entrySet().size(), 5);
        assertFalse(c.entrySet().isEmpty());
        assertEquals(c.size(), 5);
        assertFalse(c.isEmpty());

        c.entrySet().clear();

        assertEquals(c.entrySet().size(), 0);
        assertTrue(c.entrySet().isEmpty());
        assertEquals(c.size(), 0);
        assertTrue(c.isEmpty());
    }

    @Test
    public void testRemove() {
        c = newCache();
        assertFalse(c.entrySet().remove(1));
        assertFalse(c.entrySet().remove(MNAN1));
        c = newCache(5);
        assertTrue(c.entrySet().remove(M1));
        assertEquals(4, c.size());
        assertFalse(c.entrySet().contains(M1));

        c = newCache(1);
        assertTrue(c.entrySet().remove(M1));
        assertTrue(c.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNullPointerException() {
        newCache().entrySet().remove(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveAll() {
        c = newCache(5);
        assertFalse(c.entrySet().removeAll(Arrays.asList(MNAN1, MNAN2)));
        assertTrue(c.entrySet().removeAll(Arrays.asList(MNAN1, M2, MNAN2)));
        assertEquals(4, c.size());
        assertFalse(c.entrySet().contains(M2));
        assertTrue(c.entrySet().removeAll(Arrays.asList(M1, M4)));
        assertFalse(c.entrySet().contains(M4));
        assertFalse(c.entrySet().contains(M1));
        assertEquals(2, c.size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullPointerException() {
        newCache().entrySet().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll2NullPointerException() {
        newCache(5).entrySet().removeAll(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRetainAll() {
        c = newCache(1);
        c.entrySet().retainAll(Collections.singleton(M1));
        assertEquals(1, c.size());

        c.entrySet().retainAll(Collections.singleton(M2));
        assertEquals(0, c.size());
        c = newCache(5);
        c.entrySet().retainAll(Arrays.asList(M1, MNAN2, M3, MNAN4, M5));
        assertEquals(3, c.size());
        assertTrue(c.entrySet().contains(M1) && c.entrySet().contains(M3)
                && c.entrySet().contains(M5));

    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNullPointerException() {
        newCache(5).entrySet().retainAll(null);
    }

}

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.MNAN2;
import static org.coconut.test.CollectionUtils.MNAN4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

public class EntrySetModifying extends CacheTestBundle {


    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() {
        assertEquals(c5.entrySet().size(), 5);
        assertFalse(c5.entrySet().isEmpty());
        assertEquals(c5.size(), 5);
        assertFalse(c5.isEmpty());

        c5.entrySet().clear();

        assertEquals(c5.entrySet().size(), 0);
        assertTrue(c5.entrySet().isEmpty());
        assertEquals(c5.size(), 0);
        assertTrue(c5.isEmpty());
    }

    @Test
    public void testRemove() {
        assertFalse(c0.entrySet().remove(1));
        assertFalse(c0.entrySet().remove(MNAN1));

        assertTrue(c5.entrySet().remove(M1));
        assertEquals(4, c5.size());
        assertFalse(c5.entrySet().contains(M1));

        assertTrue(c1.entrySet().remove(M1));
        assertTrue(c1.isEmpty());
    }
    
    @Test(expected = NullPointerException.class)
    public void testRemoveNullPointerException() {
        c0.entrySet().remove(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveAll() {
        assertFalse(c5.entrySet().removeAll(Arrays.asList(MNAN1, MNAN2)));
        assertTrue(c5.entrySet().removeAll(Arrays.asList(MNAN1, M2, MNAN2)));
        assertEquals(4, c5.size());
        assertFalse(c5.entrySet().contains(M2));
        assertTrue(c5.entrySet().removeAll(Arrays.asList(M1, M4)));
        assertFalse(c5.entrySet().contains(M4));
        assertFalse(c5.entrySet().contains(M1));
        assertEquals(2, c5.size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullPointerException() {
        c0.entrySet().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll2NullPointerException() {
        c5.entrySet().removeAll(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRetainAll() {
        c1.entrySet().retainAll(Collections.singleton(M1));
        assertEquals(1, c1.size());

        c1.entrySet().retainAll(Collections.singleton(M2));
        assertEquals(0, c1.size());

        c5.entrySet().retainAll(Arrays.asList(M1, MNAN2, M3, MNAN4, M5));
        assertEquals(3, c5.size());
        assertTrue(c5.entrySet().contains(M1) && c5.entrySet().contains(M3)
                && c5.entrySet().contains(M5));

    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNullPointerException() {
        c5.entrySet().retainAll(null);
    }

}

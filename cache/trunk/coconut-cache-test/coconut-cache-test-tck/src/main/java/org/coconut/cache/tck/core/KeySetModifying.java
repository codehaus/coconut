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

import org.coconut.cache.tck.CommonCacheTestBundle;
import org.junit.Test;

/**
 * Tests the modifying functions of a keySet().
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class KeySetModifying extends CommonCacheTestBundle {

    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() {
        assertEquals(c5.keySet().size(), 5);
        assertFalse(c5.keySet().isEmpty());
        assertEquals(c5.size(), 5);
        assertFalse(c5.isEmpty());

        c5.keySet().clear();

        assertEquals(c5.keySet().size(), 0);
        assertTrue(c5.keySet().isEmpty());
        assertEquals(c5.size(), 0);
        assertTrue(c5.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNullPointerException() {
        c0.keySet().remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove2NullPointerException() {
        c5.keySet().remove(null);
    }
    
    @Test
    public void testRemove() {
        assertFalse(c0.keySet().remove(MNAN2.getKey()));
        assertFalse(c0.keySet().remove(MNAN1.getKey()));

        assertTrue(c5.keySet().remove(M1.getKey()));
        assertEquals(4, c5.size());
        assertFalse(c5.keySet().contains(M1.getKey()));

        assertTrue(c1.keySet().remove(M1.getKey()));
        assertTrue(c1.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveAll() {
        assertFalse(c5.keySet().removeAll(Arrays.asList("F", "H")));
        assertTrue(c5.keySet()
                .removeAll(Arrays.asList("F", M2.getKey(), "H")));
        assertEquals(4, c5.size());
        assertFalse(c5.keySet().contains(M2.getKey()));
        assertTrue(c5.keySet().removeAll(
                Arrays.asList(M1.getKey(), M4.getKey())));
        assertFalse(c5.keySet().contains(M4.getKey()));
        assertFalse(c5.keySet().contains(M1.getKey()));
        assertEquals(2, c5.size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullPointerException() {
        c0.keySet().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll2NullPointerException() {
        c5.keySet().removeAll(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRetainAll() {
        c1.keySet().retainAll(Collections.singleton(M1.getKey()));
        assertEquals(1, c1.size());

        c1.keySet().retainAll(Collections.singleton(M2.getKey()));
        assertEquals(0, c1.size());

        c5.keySet().retainAll(
                Arrays.asList(M1.getKey(), "F", M3.getKey(), "G", M5
                        .getKey()));
        assertEquals(3, c5.size());
        assertTrue(c5.keySet().contains(M1.getKey())
                && c5.keySet().contains(M3.getKey())
                && c5.keySet().contains(M5.getKey()));

    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNullPointerException() {
        c5.keySet().retainAll(null);
    }
}
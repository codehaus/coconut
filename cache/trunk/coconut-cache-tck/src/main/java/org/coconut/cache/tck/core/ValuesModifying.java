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

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * Tests the modifying functions of a cache.values().
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ValuesModifying extends CacheTestBundle {

    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() {
        assertEquals(c5.values().size(), 5);
        assertFalse(c5.values().isEmpty());
        assertEquals(c5.size(), 5);
        assertFalse(c5.isEmpty());

        c5.values().clear();

        assertEquals(c5.values().size(), 0);
        assertTrue(c5.values().isEmpty());
        assertEquals(c5.size(), 0);
        assertTrue(c5.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNullPointerException() {
        c0.values().remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove2NullPointerException() {
        c5.values().remove(null);
    }

    @Test
    public void testRemove() {
        assertFalse(c0.values().remove(MNAN2.getValue()));
        assertFalse(c0.values().remove(MNAN1.getValue()));

        assertTrue(c5.values().remove(M1.getValue()));
        assertEquals(4, c5.size());
        assertFalse(c5.values().contains(M1.getValue()));

        assertTrue(c1.values().remove(M1.getValue()));
        assertTrue(c1.isEmpty());
    }

    @Test
    public void testRemoveAll() {
        assertFalse(c5.values().removeAll(Arrays.asList("F", "H")));
        assertTrue(c5.values()
                .removeAll(Arrays.asList("F", M2.getValue(), "H")));
        assertEquals(4, c5.size());
        assertFalse(c5.values().contains(M2.getValue()));
        assertTrue(c5.values().removeAll(
                Arrays.asList(M1.getValue(), M4.getValue())));
        assertFalse(c5.values().contains(M4.getValue()));
        assertFalse(c5.values().contains(M1.getValue()));
        assertEquals(2, c5.size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullPointerException() {
        c0.values().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll2NullPointerException() {
        c5.values().removeAll(null);
    }

    @Test
    public void testRetainAll() {
        c1.values().retainAll(Collections.singleton(M1.getValue()));
        assertEquals(1, c1.size());

        c1.values().retainAll(Collections.singleton(M2.getValue()));
        assertEquals(0, c1.size());

        c5.values().retainAll(
                Arrays.asList(M1.getValue(), "F", M3.getValue(), "G", M5
                        .getValue()));
        assertEquals(3, c5.size());
        assertTrue(c5.values().contains(M1.getValue())
                && c5.values().contains(M3.getValue())
                && c5.values().contains(M5.getValue()));

    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNullPointerException() {
        c5.values().retainAll(null);
    }
}

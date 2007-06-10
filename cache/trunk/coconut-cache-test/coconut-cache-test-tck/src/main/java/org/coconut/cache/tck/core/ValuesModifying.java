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

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

/**
 * Tests the modifying functions of a cache.values().
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ValuesModifying extends AbstractCacheTCKTestBundle {

    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() {
        c = newCache(5);
        assertEquals(c.values().size(), 5);
        assertFalse(c.values().isEmpty());
        assertEquals(c.size(), 5);
        assertFalse(c.isEmpty());

        c.values().clear();

        assertEquals(c.values().size(), 0);
        assertTrue(c.values().isEmpty());
        assertEquals(c.size(), 0);
        assertTrue(c.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNullPointerException() {
        newCache().values().remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove2NullPointerException() {
        newCache(5).values().remove(null);
    }

    @Test
    public void testRemove() {
        c = newCache();
        assertFalse(c.values().remove(MNAN2.getValue()));
        assertFalse(c.values().remove(MNAN1.getValue()));
        c = newCache(5);
        assertTrue(c.values().remove(M1.getValue()));
        assertEquals(4, c.size());
        assertFalse(c.values().contains(M1.getValue()));
        c = newCache(1);
        assertTrue(c.values().remove(M1.getValue()));
        assertTrue(c.isEmpty());
    }

    @Test
    public void testRemoveAll() {
        c = newCache(5);
        assertFalse(c.values().removeAll(Arrays.asList("F", "H")));
        assertTrue(c.values().removeAll(Arrays.asList("F", M2.getValue(), "H")));
        assertEquals(4, c.size());
        assertFalse(c.values().contains(M2.getValue()));
        assertTrue(c.values().removeAll(Arrays.asList(M1.getValue(), M4.getValue())));
        assertFalse(c.values().contains(M4.getValue()));
        assertFalse(c.values().contains(M1.getValue()));
        assertEquals(2, c.size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullPointerException() {
        newCache().values().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll2NullPointerException() {
        newCache(5).values().removeAll(null);
    }

    @Test
    public void testRetainAll() {
        c = newCache(1);
        c.values().retainAll(Collections.singleton(M1.getValue()));
        assertEquals(1, c.size());

        c.values().retainAll(Collections.singleton(M2.getValue()));
        assertEquals(0, c.size());
        c = newCache(5);
        c.values().retainAll(
                Arrays.asList(M1.getValue(), "F", M3.getValue(), "G", M5.getValue()));
        assertEquals(3, c.size());
        assertTrue(c.values().contains(M1.getValue())
                && c.values().contains(M3.getValue())
                && c.values().contains(M5.getValue()));

    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNullPointerException() {
        newCache(5).values().retainAll(null);
    }
}

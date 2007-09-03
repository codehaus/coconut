/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.MNAN2;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class ClearRemove extends AbstractCacheTCKTest {

    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() {
        c = newCache(5);
        assertEquals(c.size(), 5);
        assertFalse(c.isEmpty());
        c.clear();
        assertEquals(c.size(), 0);
        assertTrue(c.isEmpty());
    }

    // TODO: remove, removeAll

    @Test(expected = NullPointerException.class)
    public void testRemoveNullPointerException() {
        newCache(0).remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove2NullPointerException() {
        newCache(5).remove(null);
    }

    @Test
    public void testRemove() {
        c = newCache(0);
        assertNull(c.remove(MNAN2.getKey()));
        assertNull(c.remove(MNAN1.getKey()));

        c = newCache(5);
        assertEquals(M1.getValue(), c.remove(M1.getKey()));
        assertEquals(4, c.size());
        assertFalse(c.containsKey(M1.getKey()));

        c = newCache(1);
        assertEquals(M1.getValue(), c.remove(M1.getKey()));
        assertTrue(c.isEmpty());
    }

}

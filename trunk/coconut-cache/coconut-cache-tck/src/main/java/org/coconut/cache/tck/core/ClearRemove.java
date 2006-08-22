/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.MNAN2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

public class ClearRemove extends CacheTestBundle {

    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() {
        assertEquals(c5.size(), 5);
        assertFalse(c5.isEmpty());
        c5.clear();
        assertEquals(c5.size(), 0);
        assertTrue(c5.isEmpty());
    }

    // TODO: remove, removeAll

    @Test(expected = NullPointerException.class)
    public void testRemoveNullPointerException() {
        c0.remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemove2NullPointerException() {
        c5.remove(null);
    }

    @Test
    public void testRemove() {
        assertNull(c0.remove(MNAN2.getKey()));
        assertNull(c0.remove(MNAN1.getKey()));

        assertEquals(M1.getValue(), c5.remove(M1.getKey()));
        assertEquals(4, c5.size());
        assertFalse(c5.containsKey(M1.getKey()));

        assertEquals(M1.getValue(), c1.remove(M1.getKey()));
        assertTrue(c1.isEmpty());
    }

}

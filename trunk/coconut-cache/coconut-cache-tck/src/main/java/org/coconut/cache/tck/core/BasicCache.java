/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * Test basic functionality of a Cache. This test should be applicable for any
 * cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class BasicCache extends CacheTestBundle {

    @Test(expected = NullPointerException.class)
    public void getAllNullFails() {
        c5.getAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void getAllNull2Fails() {
        c5.getAll(Arrays.asList(1, null));
    }

    @Test
    public void testPeek() {
        assertNull(c5.peek(6));
        assertEquals(M1.getValue(), c5.peek(M1.getKey()));
        assertEquals(M5.getValue(), c5.peek(M5.getKey()));
    }

    @Test(expected = NullPointerException.class)
    public void testPeekNull() {
        c5.get(null);
    }

    @Test
    public void testGetAllElement() {
        Map<Integer, String> map = c4
                .getAll(asList(M1.getKey(), M5.getKey(), M4.getKey()));
        assertEquals(3, map.size());
        assertEquals(M1.getValue(), map.get(M1.getKey()));
        assertTrue(map.entrySet().contains(M1));

        assertEquals(M4.getValue(), map.get(M4.getKey()));
        assertTrue(map.entrySet().contains(M4));

        assertNull(map.get(M5.getKey()));
        assertFalse(map.entrySet().contains(M5));
    }

}

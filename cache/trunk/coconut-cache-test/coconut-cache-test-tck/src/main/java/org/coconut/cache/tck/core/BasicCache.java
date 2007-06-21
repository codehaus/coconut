/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.asList;

import java.util.Arrays;
import java.util.Map;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

/**
 * Test basic functionality of a Cache. This test should be applicable for any cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class BasicCache extends AbstractCacheTCKTestBundle {

    @Test(expected = NullPointerException.class)
    public void getAllNullFails() {
        c = newCache(5);
        getAll((Map.Entry) null);
    }

    @Test(expected = NullPointerException.class)
    public void getAllNull2Fails() {
        c = newCache(5);
        c.getAll(Arrays.asList(1, null));
    }

    @Test
    public void testPeek() {
        c = newCache(5);
        assertNull(c.peek(6));
        assertEquals(M1.getValue(), c.peek(M1.getKey()));
        assertEquals(M5.getValue(), c.peek(M5.getKey()));
    }

    @Test(expected = NullPointerException.class)
    public void testPeekNull() {
        c = newCache(5);
        c.get(null);
    }

    @Test
    public void testGetAllElement() {
        c = newCache(4);
        Map<Integer, String> map = c
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

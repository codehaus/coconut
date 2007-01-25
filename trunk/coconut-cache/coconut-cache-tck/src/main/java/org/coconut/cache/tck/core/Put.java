/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_KEY_NULL;
import static org.coconut.test.CollectionUtils.M1_NULL_VALUE;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * Tests put operations for a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Put extends CacheTestBundle {

    @Test
    public void testPut() {
        c0.put(1, "B");
        assertEquals(1, c0.size());
        c0.put(1, "C");
        assertEquals(1, c0.size());
        assertEquals("C", c0.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void testPutKeyNull() {
        c0.put(null, "A");
    }

    @Test(expected = NullPointerException.class)
    public void testPutValueNull() {
        c0.put(1, null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPutAll() {
        c0.putAll(asMap(M1, M5));
        assertEquals(2, c0.size());
        assertTrue(c0.entrySet().contains(M1));
        assertTrue(c0.entrySet().contains(M5));

        c0.putAll(asMap(M1, M5));
        assertEquals(2, c0.size());

        c0.putAll(asMap(MNAN1, M4));
        assertEquals(3, c0.size());
        assertFalse(c0.entrySet().contains(M1));

    }

    @Test(expected = NullPointerException.class)
    public void testPutAllNull() {
        c0.putAll(null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testPutAllNullKeyMapping() {
        c0.putAll(asMap(M1, M1_NULL_VALUE));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testPutAllNullValueMapping() {
        c0.putAll(asMap(M1, M1_KEY_NULL));

    }

}

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

import java.util.Map;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests put operations for a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Put extends AbstractCacheTCKTest {

    @Before
    public void setup() {
        c = newCache();
    }

    @Test
    public void testPut() {
        c.put(1, "B");
        assertEquals(1, c.size());
        c.put(1, "C");
        assertEquals(1, c.size());
        assertEquals("C", c.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void testPutKeyNull() {
        c.put(null, "A");
    }

    @Test(expected = NullPointerException.class)
    public void testPutValueNull() {
        c.put(1, null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPutAll() {
        c.putAll(asMap(M1, M5));
        assertEquals(2, c.size());
        assertTrue(c.entrySet().contains(M1));
        assertTrue(c.entrySet().contains(M5));

        c.putAll(asMap(M1, M5));
        assertEquals(2, c.size());

        c.putAll(asMap(MNAN1, M4));
        assertEquals(3, c.size());
        assertFalse(c.entrySet().contains(M1));

    }

    @Test(expected = NullPointerException.class)
    public void testPutAllNull() {
        putAll((Map.Entry) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testPutAllNullKeyMapping() {
        c.putAll(asMap(M1, M1_NULL_VALUE));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testPutAllNullValueMapping() {
        c.putAll(asMap(M1, M1_KEY_NULL));
    }
}

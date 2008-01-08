/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import java.util.ArrayList;
import java.util.Random;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests put operations for a cache.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Put extends AbstractCacheTCKTest {

    @Test
    public void put() {
        init();
        c.put(1, "B");
        assertEquals(1, c.size());
        c.put(1, "C");
        assertEquals(1, c.size());
        assertEquals("C", c.peek(1));
    }

    @Test
    public void putMany() {
        final int count = 500;
        Random r = new Random(1123123);
        for (int i = 0; i < count; i++) {
            int key = r.nextInt(250);
            c.put(key, "" + key);
        }
        assertEquals(c.size(), new ArrayList(c.entrySet()).size());
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void putLazyStart() {
        init();
        assertFalse(c.isStarted());
        c.put(1, "B");
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should fail when cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void putShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.put(1, "B");
    }

    @Test(expected = NullPointerException.class)
    public void putKeyNPE() {
        init();
        c.put(null, "A");
    }

    @Test(expected = NullPointerException.class)
    public void putValueNPE() {
        init();
        c.put(1, null);
    }
}

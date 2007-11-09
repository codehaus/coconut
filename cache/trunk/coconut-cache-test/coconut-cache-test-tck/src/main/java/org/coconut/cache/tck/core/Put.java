/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

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
        c = newCache();
        c.put(1, "B");
        assertEquals(1, c.size());
        c.put(1, "C");
        assertEquals(1, c.size());
        assertEquals("C", c.get(1));
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void putLazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.put(1, "B");
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should  fail when cache is shutdown.
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
        c = newCache();
        c.put(null, "A");
    }

    @Test(expected = NullPointerException.class)
    public void putValueNPE() {
        c = newCache();
        c.put(1, null);
    }
}

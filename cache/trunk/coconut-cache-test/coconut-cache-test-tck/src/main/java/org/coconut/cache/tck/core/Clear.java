/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * This class tests the {@link Cache#clear()} operation.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Clear extends AbstractCacheTCKTest {

    /**
     * {@link Cache#clear()} removes all mappings.
     */
    @Test
    public void clear() {
        c = newCache(5);
        assertEquals(c.size(), 5);
        assertFalse(c.isEmpty());
        c.clear();
        assertEquals(c.size(), 0);
        assertTrue(c.isEmpty());
    }

    /**
     * {@link Cache#clear()} lazy starts the cache.
     */
    @Test
    public void clearLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.clear();
        checkLazystart();
    }

    /**
     * {@link Cache#clear()} fails when the cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void clearShutdown() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();
        c.clear();
    }
}

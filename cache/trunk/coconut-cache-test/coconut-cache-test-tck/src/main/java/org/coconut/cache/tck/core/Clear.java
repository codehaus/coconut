package org.coconut.cache.tck.core;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * This class tests the {@link Cache#clear()} operation.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
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

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

import java.util.Set;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EntrySetClear extends AbstractCacheTCKTest {

    /**
     * {@link Set#clear()} removes all mappings.
     */
    @Test
    public void clear() {
        c = newCache(5);
        assertEquals(c.entrySet().size(), 5);
        assertFalse(c.entrySet().isEmpty());
        assertEquals(c.size(), 5);
        assertFalse(c.isEmpty());

        c.entrySet().clear();

        assertEquals(c.entrySet().size(), 0);
        assertTrue(c.entrySet().isEmpty());
        assertEquals(c.size(), 0);
        assertTrue(c.isEmpty());
    }

    /**
     * {@link Set#clear()} lazy starts the cache.
     */
    @Test
    public void clearLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.entrySet().clear();
        checkLazystart();
    }

    /**
     * {@link Set#clear()} fails when the cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void clearShutdown() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();
        c.entrySet().clear();
    }
}

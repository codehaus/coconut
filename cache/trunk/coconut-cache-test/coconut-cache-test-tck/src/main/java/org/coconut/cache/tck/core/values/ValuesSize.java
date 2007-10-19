/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.values;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ValuesSize extends AbstractCacheTCKTest {

    /**
     * size returns the correct values.
     */
    @Test
    public void size() {
        assertEquals(0, newCache().keySet().size());
        assertEquals(5, newCache(5).values().size());
    }

    /**
     * {@link Cache#size()} lazy starts the cache.
     */
    @Test
    public void sizeLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.values().size();
        checkLazystart();
    }

    /**
     * {@link Cache#isEmpty()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void sizeShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.values().size();

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        int size = c.values().size();
        assertEquals(0, size);// cache should be empty
    }

}

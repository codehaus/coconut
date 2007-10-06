package org.coconut.cache.tck.core;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class Size extends AbstractCacheTCKTest {

    /**
     * size returns the correct values.
     */
    @Test
    public void size() {
        assertEquals(0, newCache().size());
        assertEquals(5, newCache(5).size());
    }

    /**
     * {@link Cache#size()} lazy starts the cache.
     */
    @Test
    public void sizeLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.size();
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
        c.size();

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        int size = c.size();
        assertEquals(0, size);// cache should be empty
    }

}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class PeekEntry extends AbstractCacheTCKTest {

    @Test
    public void peekEntry() {
        c = newCache(5);
        assertNull(c.peekEntry(6));
        assertEquals(M1.getValue(), c.peekEntry(M1.getKey()).getValue());
        assertEquals(M1.getKey(), c.peekEntry(M1.getKey()).getKey());
        assertEquals(M5.getValue(), c.peekEntry(M5.getKey()).getValue());
        assertEquals(M5.getKey(), c.peekEntry(M5.getKey()).getKey());
    }

    /**
     * {@link Cache#peek} lazy starts the cache.
     */
    @Test
    public void peekEntryLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.peekEntry(1);
        checkLazystart();
    }

    @Test(expected = NullPointerException.class)
    public void peekEntryNPE() {
        c = newCache(5);
        c.peekEntry(null);
    }

    /**
     * {@link Cache#containsValue()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void peekEntryShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.peekEntry(1);

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        Object peekEntry = c.peekEntry(1);
        assertNull(peekEntry);// cache should be empty
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class Peek extends AbstractCacheTCKTest {

    @Test
    public void peek() {
        c = newCache(5);
        assertNull(c.peek(6));
        assertEquals(M1.getValue(), c.peek(M1.getKey()));
        assertEquals(M5.getValue(), c.peek(M5.getKey()));
    }

    /**
     * {@link Cache#peek} lazy starts the cache.
     */
    @Test
    public void peekLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.peek(1);
        checkLazystart();
    }

    @Test(expected = NullPointerException.class)
    public void peekNPE() {
        c = newCache(5);
        c.peek(null);
    }

    /**
     * {@link Cache#containsValue()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void peekShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.peek(1);

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        Object peek = c.peek(1);
        assertNull(peek);// cache should be empty
    }
}

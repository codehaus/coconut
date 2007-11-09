/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

import static org.coconut.test.CollectionUtils.M1_TO_M5_SET;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EntrySetHashCodeEquals extends AbstractCacheTCKTest {

    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        c = newCache();

        assertTrue(new HashSet().equals(c.entrySet()));
        assertTrue(c.entrySet().equals(new HashSet()));

        assertFalse(c.entrySet().equals(null));
        assertFalse(c.entrySet().equals(newCache(1).entrySet()));
        c = newCache(5);
        assertTrue(M1_TO_M5_SET.equals(c.entrySet()));
        assertTrue(c.entrySet().equals(M1_TO_M5_SET));

        assertFalse(c.entrySet().equals(null));
        assertFalse(c.entrySet().equals(newCache(4).entrySet()));
        assertFalse(c.entrySet().equals(newCache(6).entrySet()));
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void equalsShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.entrySet().equals(new HashSet());

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        boolean equals = c.entrySet().equals(new HashSet());
        assertTrue(equals);// cache should be empty
    }

    @Test
    public void testHashCode() {
        assertEquals(M1_TO_M5_SET.hashCode(), newCache(5).entrySet().hashCode());
        assertEquals(new HashSet().hashCode(), newCache().entrySet().hashCode());
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    // @Test TODO fix
    public void hashCodeShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.entrySet().hashCode();

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        assertEquals(c.entrySet().hashCode(), new HashSet().hashCode());// cache should be
    }

}

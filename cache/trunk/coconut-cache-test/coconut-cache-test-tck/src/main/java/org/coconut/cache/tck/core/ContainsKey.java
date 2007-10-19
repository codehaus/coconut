/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests {@link Cache#containsKey}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ContainsKey extends AbstractCacheTCKTest {

    // TODO contains=lazyStart???
    /**
     * {@link Cache#containsKey} returns <code>true</code> for contained keys.
     */
    @Test
    public void containsKey() {
        c = newCache(5);
        assertTrue(c.containsKey(1));
        assertFalse(c.containsKey(6));
    }

    /**
     * {@link Cache#containsKey} lazy starts the cache.
     */
    @Test
    public void containsKeyLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.containsKey(1);
        checkLazystart();
    }

    /**
     * <code>null</code> parameter to {@link Cache#containsKey} throws
     * {@link NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void containsKeyNPE() {
        c = newCache(5);
        c.containsKey(null);
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void containsKeyShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.containsKey(1);

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        boolean containsKey = c.containsKey(1);
        assertFalse(containsKey);// cache should be empty
    }

}

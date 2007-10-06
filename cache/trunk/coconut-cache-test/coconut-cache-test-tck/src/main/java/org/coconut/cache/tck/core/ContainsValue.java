package org.coconut.cache.tck.core;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests {@link Cache#containsValue}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ContainsValue extends AbstractCacheTCKTest {

    /**
     * {@link Cache#containsValue} returns <code>true</code> for contained values.
     */
    @Test
    public void containsValue() {
        c = newCache(5);
        assertTrue(c.containsValue("A"));
        assertFalse(c.containsValue("Z"));
    }

    /**
     * <code>null</code> parameter to {@link Cache#containsValue} throws
     * {@link NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void containsValueNPE() {
        c = newCache(5);
        c.containsValue(null);
    }

    /**
     * {@link Cache#containsValue} lazy starts the cache.
     */
    @Test
    public void containsValueLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.containsValue("A");
        checkLazystart();
    }

    /**
     * {@link Cache#containsValue()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void containsValueShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.containsValue("A");

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        boolean containsValue = c.containsValue("A");
        assertFalse(containsValue);// cache should be empty
    }
}

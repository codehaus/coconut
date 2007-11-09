/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.keyset;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class KeySet extends AbstractCacheTCKTest {
    /**
     * Calls to {@link Cache#keySet} should not start the cache.
     */
    @Test
    public void noLazyStart() {
        c = newCache(0);
        c.keySet();
        assertFalse(c.isStarted());
    }

    /**
     * Calls to {@link Cache#keySet} should not fail when the cache is shutdown.
     */
    @Test
    public void noFailOnShutdown() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();
        c.keySet(); // should not fail
    }
}

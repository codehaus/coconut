/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

import java.util.Set;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EntrySet extends AbstractCacheTCKTest {
    /**
     * Calls to {@link Cache#entrySet} should not start the cache.
     */
    @Test
    public void noLazyStart() {
        c = newCache(0);
        c.entrySet();
        assertFalse(c.isStarted());
    }

    /**
     * Calls to {@link Cache#entrySet} should not fail when the cache is shutdown.
     */
    @Test
    public void noFailOnShutdown() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();
        c.entrySet(); // should not fail
    }
}

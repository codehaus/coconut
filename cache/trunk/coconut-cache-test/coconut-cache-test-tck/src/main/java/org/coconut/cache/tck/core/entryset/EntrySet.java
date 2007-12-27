/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests {@link Cache#entrySet()} lazy start and shutdown.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EntrySet extends AbstractCacheTCKTest {

    /**
     * Calls to {@link Cache#entrySet} should not start the cache.
     */
    @Test
    public void noLazyStart() {
        init();
        c.entrySet();
        assertNotStarted();
    }

    /**
     * Calls to {@link Cache#entrySet} should not fail when the cache is shutdown.
     */
    @Test
    public void noFailOnShutdown() {
        init(5);
        assertStarted();
        shutdownAndAwaitTermination();
        c.entrySet(); // should not fail
    }
}

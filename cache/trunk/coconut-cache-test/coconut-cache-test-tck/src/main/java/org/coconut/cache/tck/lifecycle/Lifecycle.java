/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.lifecycle;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Test basic functionality of a Cache. This test should be applicable for any cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: BasicCache.java 363 2007-06-21 04:05:34Z kasper $
 */
public class Lifecycle extends AbstractCacheTCKTest {

    @Test
    public void initialStatus() {
        c = newCache();
        assertFalse(c.isStarted());
        assertFalse(c.isTerminated());
        assertFalse(c.isShutdown());
    }

    @Test
    public void lazyStart() {
        c = newCache();
        c.put(1, "foo");
        assertTrue(c.isStarted());
        assertFalse(c.isTerminated());
        assertFalse(c.isShutdown());
    }

    @Test
    public void shutdown() {
        c = newCache();
        c.put(1, "foo");
        assertTrue(c.isStarted());
        c.shutdown();
        assertTrue(c.isStarted());
        assertTrue(c.isShutdown());
    }
    
    @Test
    public void shutdownTerminated() throws InterruptedException {
        c = newCache();
        c.put(1, "foo");
        assertTrue(c.isStarted());
        c.shutdown();
        assertTrue(c.awaitTermination(10, TimeUnit.SECONDS));
        assertTrue(c.isStarted());
        assertTrue(c.isShutdown());
        assertTrue(c.isTerminated());
     }
}

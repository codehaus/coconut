/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheConfiguration_ThreadingTest {
    CacheConfiguration<Number, Collection> conf;

    CacheConfiguration.Threading t;

    private final static Executor e = Executors.newCachedThreadPool();

    private final static ScheduledExecutorService ses = Executors
            .newSingleThreadScheduledExecutor();

    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
        t = conf.threading();
    }

    @Test
    public void testExpiration() {
        assertEquals(conf, conf.threading().c());
    }

    @Test
    public void testExecutor() {
        assertNull(t.getExecutor());
        assertEquals(t, t.setExecutor(e));
        assertEquals(e, t.getExecutor());
    }

    @Test
    public void testShutdownExecutorService() {
        assertFalse(t.getShutdownExecutorService());
        assertEquals(t, t.setShutdownExecutorService(true));
        assertTrue(t.getShutdownExecutorService());
    }
    
    @Test
    public void testScheduledEvictionAtFixedRate() {
        t.setExecutor(ses);
        assertEquals(t, t.setScheduledEvictionAtFixedRate(4, TimeUnit.MICROSECONDS));
        assertEquals(4000, t.getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetScheduledEvictionAtFixedRateIAE() {
        t.setExecutor(ses);
        t.setScheduledEvictionAtFixedRate(-1, TimeUnit.MICROSECONDS);
    }

//    @Test(expected = IllegalStateException.class)
//    public void testSetScheduledEvictionAtFixedRateISE1() {
//        t.setScheduledEvictionAtFixedRate(4, TimeUnit.MICROSECONDS);
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void testSetScheduledEvictionAtFixedRateISE2() {
//        t.setExecutor(e);
//        t.setScheduledEvictionAtFixedRate(4, TimeUnit.MICROSECONDS);
//    }

}

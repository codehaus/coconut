/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.threading;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheThreadingConfigurationTest {

    CacheExecutorConfiguration<?,?> t;

    CacheExecutorConfiguration DEFAULT = new CacheExecutorConfiguration();

    private final static Executor e = Executors.newCachedThreadPool();

    private final static ScheduledExecutorService ses = Executors
            .newSingleThreadScheduledExecutor();

    @Before
    public void setUp() {
        t = new CacheExecutorConfiguration();
    }

    @Test
    public void testNothing() {
        
    }
}

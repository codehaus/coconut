/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

public class AbstractLifecycleVerifier implements CacheLifecycle {

    private final AtomicInteger step = new AtomicInteger();

    private CacheConfiguration<?, ?> conf;

    private Cache<?, ?> c;

    public void setConfigurationToVerify(CacheConfiguration<?, ?> conf) {
        this.conf = conf;
    }

    public String getName() {
        return "noname";
    }
    public void assertInitializedButNotStarted() {
        assertEquals(2, step.get());
    }
    public void assertNotStarted() {
        assertEquals(0, step.get());
    }

    public void assertState(int state) {
        assertEquals(state, step.get());
    }

    public int getState() {
        return step.get();
    }

    public void assertInStartedPhase() {
        assertEquals(4, step.get());
    }

    public void assertShutdownOrTerminatedPhase() {
        int state = step.get();
        assertTrue("state was " + state, state == 5 || state == 6);
    }

    public void assertTerminatedPhase() {
        int state = step.get();
        assertTrue(state == 6);
    }

    public void shutdownAndAssert(Cache<?, ?> c) {
        c.shutdown();
        assertShutdownOrTerminatedPhase();
        try {
            c.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        assertTerminatedPhase();
    }

    public void initialize(CacheLifecycleInitializer cli) {
        assertNotNull("The CacheLifecycleInitialize that was passed to the initialize method was null",
                cli);
        assertNotNull("The configuration that was passed to the initialize method was null",
                cli.getCacheConfiguration());
        assertNotNull("The cache type that was passed to the initialize method was null",
                cli.getCacheType());
        assertEquals(0, step.getAndIncrement());
        if (conf != null) {
            assertEquals(conf, cli.getCacheConfiguration());
        }
        assertEquals(1, step.getAndIncrement());
        // System.out.println("1-initialized");
    }

    public void shutdown() {
        assertEquals(4, step.getAndIncrement());
        // System.out.println("5-shutdown");
    }

    public void start(CacheServiceManagerService serviceManager) {
        assertEquals(2, step.getAndIncrement());
        // System.out.println("3-start");
    }

    public void started(Cache<?, ?> cache) {
        assertEquals(3, step.getAndIncrement());
        if (c != null) {
            assertEquals(c, cache);
        }
        c = cache;
        // System.out.println("4-started");
    }

    public void terminated() {
        assertEquals(5, step.getAndIncrement());
        // System.out.println("6-terminated");
    }
}

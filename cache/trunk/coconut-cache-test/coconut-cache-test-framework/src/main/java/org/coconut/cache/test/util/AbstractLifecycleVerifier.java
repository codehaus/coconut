/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.servicemanager.CacheLifecycle;

public class AbstractLifecycleVerifier implements CacheLifecycle {

    private final AtomicInteger step = new AtomicInteger();

    private CacheConfiguration<?, ?> conf;

    private Cache<?, ?> c;

    public AbstractLifecycleVerifier() {

    }

    public AbstractLifecycleVerifier(Cache<?, ?> c, CacheConfiguration<?, ?> conf) {
        this.c = c;
        this.conf = conf;
    }

    public String getName() {
        return "noname";
    }

    public void assertNotStarted() {
        Assert.assertEquals(0, step.get());
    }
    
    public void assertState(int state) {
        Assert.assertEquals(state, step.get());
    }
    public int getState() {
        return step.get();
    }
    
    public void assertInStartedPhase() {
        Assert.assertEquals(4, step.get());
    }

    public void assertShutdownOrTerminatedPhase() {
        int state = step.get();
        Assert.assertTrue("state was " + state , state == 5 || state == 6);
    }
    public void assertTerminatedPhase() {
        int state = step.get();
        Assert.assertTrue( state == 6);
    }
    public void shutdownAndAssert(Cache<?,?> c) {
        c.shutdown();
        assertShutdownOrTerminatedPhase();
        try {
            c.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        assertTerminatedPhase();
    }

    public void initialize(CacheConfiguration<?, ?> configuration) {
        Assert.assertEquals(0, step.getAndIncrement());
        if (conf != null) {
            Assert.assertEquals(conf, configuration);
        }
        conf = configuration;
        // System.out.println("1-initialized");
    }

    public void registerServices(Map<Class<?>, Object> serviceMap) {
        Assert.assertEquals(1, step.getAndIncrement());
        Assert.assertEquals(0, serviceMap.size());
        // System.out.println("2-register");
    }

    public void shutdown() {
        Assert.assertEquals(4, step.getAndIncrement());
        // System.out.println("5-shutdown");
    }

    public void start(Map<Class<?>, Object> allServiceMap) {
        Assert.assertEquals(2, step.getAndIncrement());
        // System.out.println("3-start");
    }

    public void started(Cache<?, ?> cache) {
        Assert.assertEquals(3, step.getAndIncrement());
        if (c != null) {
            Assert.assertEquals(c, cache);
        }
        c = cache;
        // System.out.println("4-started");
    }

    public void terminated() {
        Assert.assertEquals(5, step.getAndIncrement());
        // System.out.println("6-terminated");
    }
}

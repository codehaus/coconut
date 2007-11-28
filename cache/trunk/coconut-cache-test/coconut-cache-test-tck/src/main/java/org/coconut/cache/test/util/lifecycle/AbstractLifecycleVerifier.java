/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util.lifecycle;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;

public class AbstractLifecycleVerifier implements CacheLifecycle {

    public enum Step {
        INITIALIZE, START, MANAGED, STARTED, SHUTDOWN, TERMINATED
    }

    private final AtomicReference<Step> currentStep = new AtomicReference<Step>();

    private final AtomicReference<Step> nextStep = new AtomicReference<Step>(Step.INITIALIZE);

    private CacheConfiguration<?, ?> conf;

    private Cache<?, ?> c;

    public void setConfigurationToVerify(CacheConfiguration<?, ?> conf) {
        this.conf = conf;
    }

    public String getName() {
        return "noname";
    }

    public void assertInitializedButNotStarted() {
        assertTrue(currentStep.get() == Step.INITIALIZE);
    }

    public Step getCurrentState() {
        return currentStep.get();
    }

    public void assertInStartedPhase() {
        assertTrue(currentStep.get() == Step.STARTED);
    }

    public void assertShutdownPhase() {
        Step state = currentStep.get();
        assertTrue("state was " + state, state == Step.SHUTDOWN);
    }

    public void assertShutdownOrTerminatedPhase() {
        Step state = currentStep.get();
        assertTrue("state was " + state, state == Step.SHUTDOWN || state == Step.TERMINATED);
    }

    public void assertTerminatedPhase() {
        Step state = currentStep.get();
        assertTrue("state was " + state, state == Step.TERMINATED);
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

    private boolean isManaged() {
        return ManagedLifecycle.class.isAssignableFrom(getClass());
    }

    public void initialize(CacheLifecycleInitializer cli) {
        assertTrue(nextStep.compareAndSet(Step.INITIALIZE, Step.START));
        assertNotNull(
                "The CacheLifecycleInitialize that was passed to the initialize method was null",
                cli);
        assertNotNull("The configuration that was passed to the initialize method was null", cli
                .getCacheConfiguration());
        assertNotNull("The cache type that was passed to the initialize method was null", cli
                .getCacheType());
        assertEquals(AbstractCacheTCKTest.getCacheType(), cli.getCacheType());
        if (conf != null) {
            assertEquals(conf, cli.getCacheConfiguration());
        }
        currentStep.set(Step.INITIALIZE);
    }

    public void shutdown() {
        assertTrue(nextStep.compareAndSet(Step.SHUTDOWN, Step.TERMINATED));
        currentStep.set(Step.SHUTDOWN);

    }

    public void start(CacheServiceManagerService serviceManager) {
        if (isManaged()) {
            assertTrue(nextStep.compareAndSet(Step.START, Step.MANAGED));
        } else {
            assertTrue(nextStep.compareAndSet(Step.START, Step.STARTED));
        }
        assertNotNull(serviceManager);
        assertTrue(serviceManager.hasService(CacheServiceManagerService.class));
        assertNotNull(serviceManager.getService(CacheServiceManagerService.class));
        assertTrue(serviceManager.getAllServices().containsKey(CacheServiceManagerService.class));
        assertNotNull(serviceManager.getAllServices().get(CacheServiceManagerService.class));
        currentStep.set(Step.START);
    }

    public void manage(ManagedGroup parent) {
        assertTrue(nextStep.compareAndSet(Step.MANAGED, Step.STARTED));
        assertNotNull(parent);
        currentStep.set(Step.MANAGED);
    }

    public void started(Cache<?, ?> cache) {
        assertTrue(nextStep.compareAndSet(Step.STARTED, Step.SHUTDOWN));
        if (c != null) {
            assertEquals(c, cache);
        }
        c = cache;
        currentStep.set(Step.STARTED);
    }

    public void terminated() {
        assertTrue("NextStep was " + nextStep.get(), nextStep.compareAndSet(Step.TERMINATED, null));
        currentStep.set(Step.TERMINATED);
    }

    public void setNextStep(Step step) {
        nextStep.set(step);
    }
}

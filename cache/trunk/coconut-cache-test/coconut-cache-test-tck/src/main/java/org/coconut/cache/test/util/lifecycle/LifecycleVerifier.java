package org.coconut.cache.test.util.lifecycle;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.core.EventProcessor;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.coconut.test.throwables.Error1;
import org.coconut.test.throwables.RuntimeException1;
import org.coconut.test.throwables.RuntimeException2;

public class LifecycleVerifier extends AbstractCacheLifecycle {

    private static final int STEP_0_UNINITIALIZE = 1 << 0;

    private static final int STEP_1_INITIALIZE = 1 << 1;

    private static final int STEP_2_START = 1 << 2;

    private static final int STEP_3_MANAGED = 1 << 3;

    private static final int STEP_4_STARTED = 1 << 4;

    private static final int STEP_5_SHUTDOWN = 1 << 5;

    private static final int STEP_6_SHUTDOWNNOW = 1 << 6;

    private static final int STEP_7_TERMINATE = 1 << 7;

    private static final int STEP_8_NONE = 1 << 8;

    private final LifecycleVerifierContext context;

    private volatile int currentStep = STEP_0_UNINITIALIZE;

    private final CacheLifecycle decorator;

    private final int id;

    private final Initialization initialization = new Initialization();

    private volatile int nextStep = STEP_1_INITIALIZE | STEP_8_NONE;

    private Throwable startException;

    private final AtomicInteger step = new AtomicInteger();

    private final Terminatation termination = new Terminatation();

    private Throwable throwableShutdown;

    LifecycleVerifier next;

    LifecycleVerifier previous;

    LifecycleVerifier(LifecycleVerifierContext context, CacheLifecycle decorator, int id) {
        this.context = context;
        this.decorator = decorator;
        this.id = id;
    }

    public Initialization initialization() {
        return initialization;
    }

    @Override
    public void initialize(Initializer cli) {
        String msg = id + ": initialize called (but never finished, AssertionError???)";
        try {
            assertEquals(STEP_0_UNINITIALIZE, currentStep);
            validateStep(STEP_1_INITIALIZE);
            currentStep = STEP_1_INITIALIZE;
            if (previous != null) {
                assertTrue(previous.initialization.isInInitializedStatus());
            }
            assertNotNull(
                    "The CacheLifecycleInitialize that was passed to the initialize method was null",
                    cli);
            assertNotNull("The configuration that was passed to the initialize method was null",
                    cli.getCacheConfiguration());
            assertSame(context.conf, cli.getCacheConfiguration());
            assertNotNull("The cache type that was passed to the initialize method was null", cli
                    .getCacheType());
            try {
                decorator.initialize(cli);
                msg = id + ": initialize completed succesfully";
                nextStep = STEP_2_START | STEP_7_TERMINATE;
            } catch (RuntimeException e) {
                initialization.throwable = e;
                nextStep = STEP_8_NONE;
                msg = id + ": initialize failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            } catch (Error e) {
                initialization.throwable = e;
                nextStep = STEP_8_NONE;
                msg = id + ": initialize failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            }
        } finally {
            context.logging.add(msg);
        }
    }

    public boolean isInStartStatus() {
        return currentStep == STEP_2_START;
    }

    public boolean isInTerminatationStatus() {
        return currentStep == STEP_7_TERMINATE;
    }

    public void manage(ManagedGroup parent) {
        String msg = id + ": manage called (but never finished, AssertionError???)";
        try {
            validateStep(STEP_3_MANAGED);
            assertEquals(STEP_2_START, currentStep);
            currentStep = STEP_3_MANAGED;
            assertNotNull("The ManagedGroup that was passed to the managed method was null", parent);
            try {
                ((ManagedLifecycle) decorator).manage(parent);
                msg = id + ": manage completed succesfully";
                nextStep = STEP_4_STARTED | STEP_5_SHUTDOWN;
            } catch (RuntimeException e) {
                startException = e;
                nextStep = STEP_5_SHUTDOWN;
                msg = id + ": manage failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            } catch (Error e) {
                startException = e;
                nextStep = STEP_8_NONE;
                msg = id + ": manage failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            }
        } finally {
            context.logging.add(msg);
        }
    }

    @Override
    public void shutdown(Shutdown shutdown) throws Exception{
        String msg = id + ": shutdown called (but never finished, AssertionError???)";
        try {
            try {
                decorator.shutdown(shutdown);
                msg = id + ": shutdown completed succesfully";
                nextStep = STEP_6_SHUTDOWNNOW | STEP_7_TERMINATE;
            } catch (RuntimeException e) {
                throwableShutdown = e;
                nextStep = STEP_7_TERMINATE;
                msg = id + ": shutdown failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            } catch (Error e) {
                throwableShutdown = e;
                nextStep = STEP_8_NONE;
                msg = id + ": shutdown failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            }
        } finally {
            context.logging.add(msg);
        }
    }

//    @Override
//    public void shutdownNow() {
//        decorator.shutdownNow();
//    }

    @Override
    public void start(CacheServiceManagerService serviceManager) throws Exception {
        String msg = id + ": start called (but never finished, AssertionError???)";
        try {
            validateStep(STEP_2_START);
            assertEquals(STEP_1_INITIALIZE, currentStep);
            currentStep = STEP_2_START;
            if (previous != null) {
                assertTrue(previous.isInStartStatus());
            }
            assertNotNull(
                    "The CacheServiceManagerService that was passed to the start method was null",
                    serviceManager);
            try {
                decorator.start(serviceManager);
                msg = id + ": start completed succesfully";
                if (this instanceof ManagedGroup) {
                    nextStep = STEP_3_MANAGED | STEP_7_TERMINATE;
                } else {
                    nextStep = STEP_4_STARTED | STEP_7_TERMINATE;
                }

            } catch (Exception e) {
                startException = e;
                nextStep = STEP_7_TERMINATE;
                msg = id + ": start failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            } catch (Error e) {
                startException = e;
                nextStep = STEP_8_NONE;
                msg = id + ": start failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            }
        } finally {
            context.logging.add(msg);
        }
    }

    @Override
    public void started(Cache<?, ?> cache) {
        String msg = id + ": started called (but never finished, AssertionError???)";
        try {
            validateStep(STEP_4_STARTED);
            if (this instanceof ManagedLifecycle) {
                assertEquals(STEP_3_MANAGED, currentStep);    
            } else {
                assertEquals(STEP_2_START, currentStep);
            }
            
            currentStep = STEP_4_STARTED;
//            if (previous != null) {
//                assertTrue(previous.isInStartStatus());
//            }
            assertNotNull("The Cache that was passed to the started method was null", cache);
            try {
                decorator.started(cache);
                msg = id + ": started completed succesfully";
                nextStep = STEP_5_SHUTDOWN;
            } catch (RuntimeException e) {
                startException = e;
                nextStep = STEP_7_TERMINATE;
                msg = id + ": started failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            } catch (Error e) {
                startException = e;
                nextStep = STEP_8_NONE;
                msg = id + ": started failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            }
        } finally {
            context.logging.add(msg);
        }

    }

    @Override
    public void terminated() {
        String msg = id + ": terminated called (but never finished, AssertionError???)";
        try {
            validateStep(STEP_7_TERMINATE);
            currentStep = STEP_7_TERMINATE;
            nextStep = STEP_8_NONE;
            if (next != null && !next.initialization().failed()) {
                assertTrue(next.isInTerminatationStatus());
            }
            try {
                decorator.terminated();
                msg = id + ": terminated sucessfully";
            } catch (RuntimeException e) {
                termination.throwable = e;
                msg = id + ": terminated failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            } catch (Error e) {
                termination.throwable = e;
                msg = id + ": terminated failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            }
        } finally {
            context.logging.add(msg);
        }
    }

    public void verify() {
        validateStep(STEP_8_NONE);
    }

    private void validateStep(int step) {
        assertTrue(id + ": next= " + nextStep + ", step= " + step, (nextStep & step) == step);
    }

    void finished() {
        this.nextStep = STEP_8_NONE;
        this.currentStep = STEP_8_NONE;
    }

    void validate() {
        validateStep(STEP_8_NONE);
    }

    public class Initialization {

        private Throwable throwable;

        public void assertFailed() {
            assertTrue(failed());
        }

        public boolean failed() {
            return throwable != null;
        }

        public boolean isInInitializedStatus() {
            return currentStep == STEP_1_INITIALIZE;
        }

        public void wasRun() {
            assert (step.get() > 1);
        }
    }

    public class Terminatation {
        private Throwable throwable;

    }
}

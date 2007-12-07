package org.coconut.cache.test.util.lifecycle;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.core.EventProcessor;
import org.coconut.test.throwables.Error1;
import org.coconut.test.throwables.RuntimeException1;
import org.coconut.test.throwables.RuntimeException2;

public class LifecycleVerifier extends AbstractCacheLifecycle {

    public static EventProcessor THROW_RUNTIMEEXCEPTION1 = new EventProcessor() {
        public void process(Object event) {
            throw new RuntimeException1();
        }
    };

    public static EventProcessor THROW_ERROR1 = new EventProcessor() {
        public void process(Object event) {
            throw new Error1();
        }
    };

    public static EventProcessor THROW_RUNTIMEEXCEPTION2 = new EventProcessor() {
        public void process(Object event) {
            throw new RuntimeException2();
        }
    };

// public static LifecycleVerifier noCall(CacheConfiguration conf) {
// LifecycleVerifier result = new LifecycleVerifier(conf);
// result.nextStep = STEP_NONE;
// return result;
// }

    private static final int STEP_INITIALIZE = 1;

    private static final int STEP_NONE = 1 << 10;

    private static final int STEP_START_START = 1 << 1;

    private static final int STEP_TERMINATE = 1 << 3;

    private volatile CacheConfiguration conf;

    private final Initialization initialization = new Initialization();
    
    private final Terminatation termination = new Terminatation();

    private volatile int nextStep = STEP_INITIALIZE | STEP_NONE;

    private volatile int currentStep = 0;

    private final AtomicInteger step = new AtomicInteger();

    private final LifecycleVerifierContext context;

    private final CacheLifecycle decorator;

    LifecycleVerifier previous;

    LifecycleVerifier next;

    private final int id;

    LifecycleVerifier(LifecycleVerifierContext context, CacheLifecycle decorator, int id) {
        this.context = context;
        this.decorator = decorator;
        this.id = id;
    }

    public Initialization initialization() {
        return initialization;
    }

    void finished() {
        this.nextStep = STEP_NONE;
        this.currentStep = STEP_NONE;
    }

    @Override
    public void initialize(Initializer cli) {
        String msg = id + ": initialize called (but never finished, AssertionError???)";
        try {
            validateStep(STEP_INITIALIZE);
            assertEquals(0, currentStep);
            currentStep = STEP_INITIALIZE;
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
                nextStep = STEP_TERMINATE;
            } catch (RuntimeException e) {
                initialization.re = e;
                nextStep = STEP_NONE;
                msg = id + ": initialize failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            } catch (Error e) {
                initialization.er = e;
                nextStep = STEP_NONE;
                msg = id + ": initialize failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            }
        } finally {
            context.logging.add(msg);
        }
    }

    public void setConfiguration(CacheConfiguration conf) {
        this.conf = conf;
    }

    public void verify() {
        validateStep(STEP_NONE);
    }

    private void validateStep(int step) {
        assertTrue(id + ": next= " + nextStep + ", step= " + step, (nextStep & step) == step);
    }

    void validate() {
        validateStep(STEP_NONE);
    }

    public class Terminatation {
        volatile Error er;

        volatile RuntimeException re;
    }
    public boolean isInTerminatationStatus() {
        return currentStep == STEP_TERMINATE;
    }
    public class Initialization {

        volatile Error er;

        volatile RuntimeException re;

        public void assertFailed() {
            assertTrue(er != null || re != null);
        }
        public boolean failed() {
            return er != null || re != null;
        }
        public boolean isInInitializedStatus() {
            return currentStep == STEP_INITIALIZE;
        }

        public void wasRun() {
            assert (step.get() > 1);
        }

    }

    @Override
    public void terminated() {
        String msg = id + ": terminated called (but never finished, AssertionError???)";
        try {
            validateStep(STEP_TERMINATE);
            currentStep = STEP_TERMINATE;
            nextStep = STEP_NONE;
            if (next != null && !next.initialization().failed()) {
                assertTrue(next.isInTerminatationStatus());
            }
            try {
                decorator.terminated();
                msg = id + ": terminated sucessfully";
            } catch (RuntimeException e) {
                termination.re = e;
                msg = id + ": terminated failed (" + e.getClass().getSimpleName() + ")";
                throw e;
            }
        } finally {
            context.logging.add(msg);
        }
    }
}

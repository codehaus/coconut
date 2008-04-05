package org.codehaus.cake.test.tck;

import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;

import org.codehaus.cake.container.Container;
import org.codehaus.cake.container.ContainerConfiguration;
import org.codehaus.cake.service.executor.ExecutorsService;
import org.codehaus.cake.test.container.util.ThreadServiceTestHelper;
import org.codehaus.cake.util.Clock.DeterministicClock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public class AbstractTCKTest<C extends Container, T extends ContainerConfiguration> extends Assert {
    protected C c;

    protected T conf;

    protected DeterministicClock clock;

    protected ThreadServiceTestHelper threadHelper;

    private volatile Throwable failure;

    private volatile String failText;

    @Before
    public void setupConf() {
        failure = null;
        failText = null;
        clock = new DeterministicClock();
        clock.setTimestamp(10);
        clock.setRelativeTime(1000);
        conf = newConfiguration();
        conf.setClock(clock);
        c = newContainer();
    }

    @After
    public final void noFailures() throws Throwable {
        // exceptionHandler.assertCleared();
        if (failText != null) {
            throw new AssertionError(failText);
        }
        if (failure != null) {
            if (failure instanceof AssertionFailedError) {
                throw failure;
            }
            failure = null;
            failure.printStackTrace();
            throw new AssertionError("Test failed");
        }
    }
    /**
     * Await all loads that currently active.
     */
    protected void awaitFinishedThreads() {
        if (threadHelper != null) {
            threadHelper.awaitAllIdle();
        }
    }
    protected void failed(Throwable cause) {
        this.failure = cause;
        if (cause instanceof Error) {
            throw (Error) cause;
        } else if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        }
    }

    protected void failed(String text) {
        this.failText = text;
    }

    public AbstractTCKTest awaitTermination() {
        try {
            long start = System.nanoTime();
            assertTrue(c.awaitTermination(10, TimeUnit.SECONDS));
            long finish = System.nanoTime();
            if (finish - start > 1000000) {
                // System.out.println(finish - start);
                // new Exception().printStackTrace();
            }
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
        return this;
    }

    public void checkLazystart() {
        assertTrue(c.isStarted());
    }

    protected void shutdownAndAwaitTermination() {
        c.shutdown();
        awaitTermination();
    }

    protected final ExecutorsService withExecutors() {
        return c.getService(ExecutorsService.class);
    }

    protected final C newContainer() {
        c = (C) TckUtil.newContainer(conf);
        return c;
    }

    protected final T newConfigurationClean() {
        conf = (T) TckUtil.newConfiguration();
        return conf;
    }

    protected T newConfiguration() {
        newConfigurationClean();
        return conf;
    }
}

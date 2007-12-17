/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

/**
 * An unsynchronized implementation of {@link CacheServiceManagerService}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnsynchronizedCacheServiceManager extends AbstractCacheServiceManager {

    /** The current state of the service manager. */
    private RunState status = RunState.NOTRUNNING;

    public UnsynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<?>> classes) {
        super(cache, helper, conf, classes);
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return isTerminated();
    }

    /** {@inheritDoc} */
    public boolean lazyStart(boolean failIfShutdown) {
        if (status != RunState.RUNNING) {
            checkStartupException();
            if (status == RunState.STARTING) {
                throw new IllegalStateException(
                        "Cannot invoke this method from CacheLifecycle.start(Map services), should be invoked from CacheLifecycle.started(Cache c)");
            } else if (status == RunState.NOTRUNNING) {
                doStart();
            } else if (failIfShutdown && status.isShutdown()) {
                throw new IllegalStateException("Cache has been shutdown");
            }
            return status == RunState.RUNNING;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    RunState getRunState() {
        return status;
    }

    /** {@inheritDoc} */
    @Override
    void setRunState(RunState state) {
        this.status = state;
    }

    /** {@inheritDoc} */
    @Override
    void shutdown(boolean shutdownNow) {
        /*
         * We do not differentiate between SHUTDOWN and STOP, since unsynchronous services
         * are always fully terminated in CacheLifecycle.shutdown method
         */
        // STARTING
        // RUNNING
        // SHUTDOWN,
        // STOP - ignore
        // TERMINATED; - ignore
        // st
        if (status == RunState.NOTRUNNING) {
            doTerminate();
        } else if (status == RunState.RUNNING) {
            setRunState(shutdownNow ? RunState.STOP : RunState.SHUTDOWN);
            cache.clear();
            initiateShutdown();
            doTerminate();
        } else if (status == RunState.STARTING && super.startupException != null) {
            // only called from within the startup routine
            setRunState(RunState.SHUTDOWN);
            initiateShutdown();
            doTerminate();
        }
    }
}

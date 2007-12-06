/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnsynchronizedCacheServiceManager extends AbstractPicoBasedCacheServiceManager {

    private RunState status = RunState.NOTRUNNING;

    public UnsynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
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
    public void shutdown() {
        if (status == RunState.NOTRUNNING) {
            doTerminate();
            status = RunState.TERMINATED;
        } else if (status == RunState.RUNNING) {
            getCache().clear();
            initiateShutdown();
            doTerminate();
        } else if (status == RunState.STARTING && super.startupException != null) {
            initiateShutdown();
            doTerminate();
        }
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        shutdown();// synchronous shutdown
    }

    public void shutdownServiceAsynchronously(Runnable service) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    RunState getRunState() {
        return status;
    }

    void setRunState(RunState state) {
        this.status = state;
    }
}

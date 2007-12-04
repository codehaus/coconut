/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.management.ManagedLifecycle;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnsynchronizedCacheServiceManager extends AbstractPicoBasedCacheServiceManager {

    private final LinkedList<ManagedLifecycle> managedObjects = new LinkedList<ManagedLifecycle>();

    private RuntimeException startupException;

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
    public <T> T getServiceFromCache(Class<T> serviceType) {
        lazyStart(false);
        return getService(serviceType);
    }

    /** {@inheritDoc} */
    public boolean lazyStart(boolean failIfShutdown) {
        if (status != RunState.RUNNING) {
            if (startupException != null) {
                throw startupException;
            } else if (status == RunState.STARTING) {
                throw new IllegalStateException(
                        "Cannot invoke this method from CacheLifecycle.start(Map services), should be invoked from CacheLifecycle.started(Cache c)");
            } else if (status == RunState.NOTRUNNING) {
                doStart();
            } else if (failIfShutdown && status.isShutdown()) {
                throw new IllegalStateException("Cache has been shutdown");
            }
            // else if status==STARTING=throw illegalStateException()
            return status == RunState.RUNNING;
        }
        return true;
    }

    /** {@inheritDoc} */
    public void shutdown() {
        if (status == RunState.NOTRUNNING) {
            status = RunState.TERMINATED;
        } else if (status == RunState.RUNNING) {
            getCache().clear();
            status = RunState.SHUTDOWN;
            List<ServiceHolder> shutdown = new ArrayList<ServiceHolder>(services);
            Collections.reverse(shutdown);
            for (ServiceHolder si : shutdown) {
                shutdownService(si);
            }
            tryTerminate();
        }
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        shutdown();// synchronous shutdown
    }

    public void shutdownServiceAsynchronously(Runnable service) {
        throw new UnsupportedOperationException();
    }

    private void doStart() {
        status = RunState.STARTING;
        startServices();
        try {

            // register mbeans
            CacheManagementService cms = (CacheManagementService) publicServices
                    .get(CacheManagementService.class);
            if (cms != null) {
                managedObjects.addAll(ServiceManagerUtil.initializeManagedObjects(container));
                for (ManagedLifecycle si : managedObjects) {
                    si.manage(cms);
                }
            }
            status = RunState.RUNNING;
            // started
            for (ServiceHolder si : services) {
                si.started(getCache());
            }
            InternalCacheListener icl = getInternalService(InternalCacheListener.class);
            icl.afterStart(getCache());
        } catch (RuntimeException re) {
            startupException = new CacheException("Could not start cache", re);
            status = RunState.COULD_NOT_START;
            doTerminate(false);
            throw startupException;
        } catch (Error er) {
            startupException = new CacheException("Could not start cache", er);
            status = RunState.COULD_NOT_START;
            ces.terminated(tryTerminateServices());
            throw er;
        }
    }

    private void startServices() {
        CacheServiceManagerService wrapped = ServiceManagerUtil.wrapService(this);
        for (ServiceHolder si : services) {
            try {
                si.start(wrapped);
            } catch (RuntimeException re) {
                startupException = new CacheException("Could not start the cache", re);
                final CacheConfiguration conf = (CacheConfiguration) container
                        .getComponentInstance(CacheConfiguration.class);
                ces.cacheStartFailed(conf, getCache().getClass(), si.getService(), re);
                status = RunState.COULD_NOT_START;
                tryShutdownServices();
                doTerminate(false);
                throw startupException;
            } catch (Error er) {
                startupException = new CacheException("Could not start the cache", er);
                status = RunState.COULD_NOT_START;
                throw er;
            }
        }
    }

    private Map<CacheLifecycle, RuntimeException> tryShutdownServices() {
        Map<CacheLifecycle, RuntimeException> m = new HashMap<CacheLifecycle, RuntimeException>();

        List<ServiceHolder> l = new ArrayList<ServiceHolder>(services);
        Collections.reverse(l);
        for (ServiceHolder sh : l) {
            if (sh.isStarted()) {
                try {
                    sh.shutdown();
                } catch (RuntimeException e) {
                    m.put(sh.getService(), e);
                }
            }
            if (!m.isEmpty()) {
                ces.cacheShutdownFailed(getCache(), m);
            }
        }
        return m;
    }

    protected void doTerminate(boolean isInitializing) {
        if (status != RunState.TERMINATED) {
            if (status != RunState.COULD_NOT_START) {
                status = RunState.TERMINATED;
            }
            ces.terminated(tryTerminateServices());
        }
    }


    protected void shutdownService(ServiceHolder service) {
        service.shutdown();
    }

    protected void tryTerminate() {
        doTerminate(false);
    }

    /** {@inheritDoc} */
    RunState getRunState() {
        return status;
    }
}

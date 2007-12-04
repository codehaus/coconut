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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

public class SynchronizedCacheServiceManager extends AbstractPicoBasedCacheServiceManager {

    private volatile Executor e;

    private final ExecutorService es = Executors.newCachedThreadPool();

    private final Queue<Future> futures = new ConcurrentLinkedQueue<Future>();

    private final ReentrantLock mainLock = new ReentrantLock();

    private final LinkedList<ManagedLifecycle> managedObjects = new LinkedList<ManagedLifecycle>();

    private final Object mutex;

    private RuntimeException startupException;

    private RunState status = RunState.NOTRUNNING;

    /**
     * Wait condition to support awaitTermination.
     */
    private final Condition termination = mainLock.newCondition();

    public SynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        super(cache, helper, conf, classes);
        this.mutex = cache;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;) {
                if (super.isTerminated())
                    return true;
                if (nanos <= 0)
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
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

    public void shutdown() {
        synchronized (mutex) {
            mainLock.lock();
            try {
                try {
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
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                if (!futures.isEmpty()) {
                    ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1, 0L,
                            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
                    tpe.execute(new Runnable() {
                        public void run() {
                            try {
                                while (!futures.isEmpty()) {
                                    Future f = futures.poll();
                                    try {
                                        f.get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } finally {
                                doTerminate(false);
                            }
                        }
                    });
                    tpe.shutdown();
                } else {
                    doTerminate(false);
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    public void shutdownNow() {
        synchronized (mutex) {
            shutdown();
            for (ServiceHolder sh : super.services) {
                sh.shutdownNow();
            }
        }
    }

    public void shutdownService(final ServiceHolder service) {
        final AtomicInteger canSubmit = new AtomicInteger();
        e = new Executor() {
            public void execute(final Runnable command) {
                if (canSubmit.compareAndSet(0, 1)) {
                    service.future = es.submit(new Runnable() {
                        public void run() {
                            command.run();
                        }
                    });
                    futures.add(service.future);
                } else {
                    throw new IllegalStateException();
                }
            }
        };
        service.shutdown();
        canSubmit.set(2);
    }

    public void shutdownServiceAsynchronously(Runnable service2) {
        if (e == null) {
            throw new IllegalStateException("cannot shutdown now");
        }
        e.execute(service2);
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
        mainLock.lock();
        try {
            if (status != RunState.TERMINATED) {
                if (status != RunState.COULD_NOT_START) {
                    status = RunState.TERMINATED;
                }
                ces.terminated(tryTerminateServices());
            }
            termination.signalAll();
        } finally {
            mainLock.unlock();
        }
    }

    /** {@inheritDoc} */
    RunState getRunState() {
        return status;
    }

}

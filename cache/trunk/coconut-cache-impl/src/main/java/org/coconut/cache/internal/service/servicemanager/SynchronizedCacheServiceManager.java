/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;

public class SynchronizedCacheServiceManager extends AbstractPicoBasedCacheServiceManager {

    private volatile Executor e;

    private final ExecutorService es = Executors.newCachedThreadPool();

    private final Queue<Future> futures = new ConcurrentLinkedQueue<Future>();

    private final Object mutex;

    private volatile RunState status = RunState.NOTRUNNING;

    private final CountDownLatch terminationLatch = new CountDownLatch(1);

    public SynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        super(cache, helper, conf, classes);
        this.mutex = cache;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return terminationLatch.await(timeout, unit);
    }

    /** {@inheritDoc} */
    public boolean lazyStart(boolean failIfShutdown) {
        for (;;) {
            RunState state = getRunState();
            if (state != RunState.RUNNING) {
                checkStartupException();
                if (state == RunState.STARTING) {
                    throw new IllegalStateException(
                            "Cannot invoke this method from CacheLifecycle.start(Map services), should be invoked from CacheLifecycle.started(Cache c)");
                } else if (state == RunState.NOTRUNNING) {
                    synchronized (mutex) {
                        if (getRunState() == RunState.NOTRUNNING) {
                            doStart();
                        }
                    }
                } else if (failIfShutdown && state.isShutdown()) {
                    throw new IllegalStateException("Cache has been shutdown");
                } else {
                    return state == RunState.RUNNING;
                }
            } else {
                return true;
            }
        }
    }

    public void shutdown() {
        synchronized (mutex) {
            if (status == RunState.NOTRUNNING) {
                doTerminate();
            } else if (status == RunState.RUNNING) {
                getCache().clear();
            }
            if (status == RunState.RUNNING || status == RunState.STARTING) {
                initiateShutdown();
            }

            if (!futures.isEmpty()) {
                ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
                tpe.execute(new Runnable() {
                    public void run() {
                        try {
                            while (!futures.isEmpty()) {
                                Future f = futures.poll();
                                try {
                                    f.get();
                                } catch (InterruptedException e) {
                                    //ignore???
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        } finally {
                            doTerminate();
                        }
                    }
                });
                tpe.shutdown();
            } else {
                doTerminate();
            }
        }
    }

    public void shutdownNow() {
        synchronized (mutex) {
            if (status == RunState.NOTRUNNING) {
                status = RunState.TERMINATED;
            } else if (status == RunState.RUNNING) {
                shutdown();
            }
            if (status == RunState.SHUTDOWN) {
                initiateShutdownNow();
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

    protected void doTerminate() {
        synchronized (mutex) {
            try {
                super.doTerminate();
            } finally {
                terminationLatch.countDown();
            }
        }
    }

    /** {@inheritDoc} */
    RunState getRunState() {
        return status;
    }

    void setRunState(RunState state) {
        this.status = state;
    }
}

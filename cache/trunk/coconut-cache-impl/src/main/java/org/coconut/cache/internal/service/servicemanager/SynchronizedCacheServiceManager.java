/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

/**
 * An synchronized implementation of {@link CacheServiceManagerService}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SynchronizedCacheServiceManager extends AbstractCacheServiceManager {

    /** Executor responsible for shutting down services asynchronously. */
    private final ExecutorService shutdownServiceExecutor = Executors.newCachedThreadPool();

    /** A list of shutdown futures. */
    private final Queue<ServiceHolder> shutdownFutures = new ConcurrentLinkedQueue<ServiceHolder>();

    /** The cache mutex to synchronize on. */
    private final Object mutex;

    /** The current state of the service manager. */
    private volatile RunState runState = RunState.NOTRUNNING;

    /** CountDownLatch used for signalling termination. */
    private final CountDownLatch terminationLatch = new CountDownLatch(1);

    public SynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        super(cache, helper, conf, classes);
        this.mutex = cache;
    }

    /** {@inheritDoc} */
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
                            doStart(true);
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

    void shutdown(boolean shutdownNow) {
        synchronized (mutex) {
            RunState runState = this.runState;
            if ((runState == RunState.SHUTDOWN && !shutdownNow)
                    || (runState == RunState.STOP && shutdownNow)
                    || (runState == RunState.TERMINATED)) {
                return;
            } else if (runState == RunState.NOTRUNNING) {
                doTerminate();
                return;
            }
            boolean shutdown = (shutdownNow && runState == RunState.RUNNING) || !shutdownNow;
            if (shutdown) {
                if (runState == RunState.RUNNING) {
                    cache.clear();
                }
                if (runState == RunState.RUNNING || runState == RunState.STARTING) {
                    setRunState(shutdownNow ? RunState.STOP : RunState.SHUTDOWN);
                    initiateShutdown();
                }

                if (!shutdownFutures.isEmpty()) {
                    // java 5 bug, cannot use Executors.
                    ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1, 0L,
                            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
                    tpe.execute(new Runnable() {
                        public void run() {
                            try {
                                while (!shutdownFutures.isEmpty()) {
                                    try {
                                        shutdownFutures.peek().future.get();
                                        shutdownFutures.poll();
                                    } catch (InterruptedException e) {
                                        // ignore???
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
                    shutdownServiceExecutor.shutdown();
                    tpe.shutdown();
                } else {
                    doTerminate();
                    return;
                }
            }
            if (shutdownNow) {
                setRunState(RunState.STOP);
                initiateShutdownNow(shutdownFutures);
            }
        }
    }

// else if (runState == RunState.STARTING) {
// throw new IllegalStateException(
// "Cannot invoke this method from CacheLifecycle.start(Map services), should be invoked
// from CacheLifecycle.started(Cache c)");
// }

    /** {@inheritDoc} */
    @Override
    void shutdownService(final ServiceHolder service) throws Exception{
        final AtomicInteger canSubmit = new AtomicInteger();
        CacheLifecycle.Shutdown cs = new CacheLifecycle.Shutdown() {
            public void shutdownAsynchronously(Callable<?> callable) {
                if (canSubmit.compareAndSet(0, 1)) {
                    service.future = shutdownServiceExecutor.submit(callable);
                    shutdownFutures.add(service);
                } else {
                    throw new IllegalStateException();
                }
            }
        };
        service.shutdown(cs);// service can call shutdownServiceAsynchronously now
        canSubmit.set(2); // but not now
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    RunState getRunState() {
        return runState;
    }

    /** {@inheritDoc} */
    @Override
    void setRunState(RunState state) {
        this.runState = state;
    }
}

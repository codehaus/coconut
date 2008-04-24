/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.service.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.forkjoin.ForkJoinExecutor;
import org.codehaus.cake.forkjoin.ForkJoinPool;

/**
 * This class is reponsible for creating instances of {@link ExecutorService},
 * {@link ScheduledExecutorService} and {@link ForkJoinExecutor}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheWorkerManager.java 479 2007-11-27 13:40:08Z kasper $
 */
public class ExecutorsManager {

    /** Default scheduled executor service. */
    private volatile ScheduledExecutorService defaultScheduledExecutorService;
    /** Default executor service. */
    private volatile ExecutorService defaultExecutorService;

    /** Default fork join executor. */
    private volatile ForkJoinPool defaultForkJoinExecutor;

    /** Lock for on-demand initialization of executors */
    private final Object poolLock = new Object();
    private boolean isShutdown;

    private void checkState() {
        if (isShutdown) {
            throw new IllegalStateException("This service has been shutdown");
        }
    }

    ExecutorService defaultExecutorService() {
        ExecutorService s = defaultExecutorService;
        if (s == null) {
            synchronized (poolLock) {
                checkState();
                s = defaultExecutorService;
                if (s == null) {
                    s = Executors.newCachedThreadPool();
                }
            }
        }
        return s;
    }

    ScheduledExecutorService defaultScheduledExecutorService() {
        ScheduledExecutorService s = defaultScheduledExecutorService;
        if (s == null) {
            synchronized (poolLock) {
                checkState();
                s = defaultScheduledExecutorService;
                if (s == null) {
                    s = Executors.newSingleThreadScheduledExecutor();
                }
            }
        }
        return s;
    }

    ForkJoinExecutor defaultForkJoinExecutor() {
        ForkJoinPool p = defaultForkJoinExecutor; // double-check
        if (p == null) {
            synchronized (poolLock) {
                checkState();
                p = defaultForkJoinExecutor;
                if (p == null) {
                    // use ceil(7/8 * ncpus)
                    int nprocs = Runtime.getRuntime().availableProcessors();
                    int nthreads = nprocs - (nprocs >>> 3);
                    defaultForkJoinExecutor = p = new ForkJoinPool(nthreads);
                }
            }
        }
        return p;
    }

    public void shutdown() {
        synchronized (poolLock) {
            isShutdown = true;
            if (defaultExecutorService != null) {
                defaultExecutorService.shutdown();
            }
            if (defaultScheduledExecutorService != null) {
                defaultScheduledExecutorService.shutdown();
            }
            if (defaultForkJoinExecutor != null) {
                defaultForkJoinExecutor.shutdown();
            }
        }
    }

    public void shutdownNow() {
        synchronized (poolLock) {
            isShutdown = true;
            if (defaultExecutorService != null) {
                defaultExecutorService.shutdownNow();
            }
            if (defaultScheduledExecutorService != null) {
                defaultScheduledExecutorService.shutdownNow();
            }
            if (defaultForkJoinExecutor != null) {
                defaultForkJoinExecutor.shutdownNow();
            }
        }
    }

    // cache loader executor
    // call getExecutorService(CacheLoader cl)
    // call getExecutorService(CacheLoaderService)
    // call getExecutorService(Cache c)
    // call getExecutorService(DEFAULT)

    /**
     * Returns a {@link ExecutorService} that can be used to asynchronously execute tasks for the
     * specified service.
     * 
     * @param service
     *            the service for which an ExecutorService should be returned
     * @param attributes
     *            a map of attributes that is passed to the concrete implementation of the executor
     *            manager
     * @return a ExecutorService that can be used to asynchronously execute tasks for the specified
     *         service
     */
    public ExecutorService getExecutorService(Object service, AttributeMap attributes) {
        return defaultExecutorService();
    }

    /**
     * Returns a {@link ForkJoinExecutor} that can be used to asynchronously execute tasks for the
     * specified service.
     * 
     * @param service
     *            the service for which an ForkJoinExecutor should be returned
     * @param attributes
     *            a map of attributes that is passed to the concrete implementation of the executor
     *            manager
     * @return a ForkJoinExecutor that can be used to asynchronously execute tasks for the specified
     *         service
     */
    public ForkJoinExecutor getForkJoinExecutor(Object service, AttributeMap attributes) {
        return defaultForkJoinExecutor();
    }

    /**
     * Returns a {@link ScheduledExecutorService} that can be used to asynchronously schedule tasks
     * for the specified service.
     * 
     * @param service
     *            the service for which an ScheduledExecutorService should be returned
     * @param attributes
     *            a map of attributes that is passed to the concrete implementation of the executor
     *            manager
     * @return a ScheduledExecutorService that can be used to asynchronously schedule tasks for the
     *         specified service
     */
    public ScheduledExecutorService getScheduledExecutorService(Object service,
            AttributeMap attributes) {
        return defaultScheduledExecutorService();
    }

    public static ExecutorsManager from(ScheduledExecutorService ses) {
        ExecutorsManager em = new ExecutorsManager();
        synchronized (em.poolLock) {
            em.defaultScheduledExecutorService = ses;
        }
        return em;
    }

    public static ExecutorsManager from(ExecutorService es) {
        ExecutorsManager em = new ExecutorsManager();
        synchronized (em.poolLock) {
            em.defaultExecutorService = es;
        }
        return em;
    }

    public static ExecutorsManager from(ForkJoinPool e) {
        ExecutorsManager em = new ExecutorsManager();
        synchronized (em.poolLock) {
            em.defaultForkJoinExecutor = e;
        }
        return em;
    }
}

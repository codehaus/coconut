/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.threading;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.service.threading.CacheServiceExecutor;
import org.coconut.cache.service.threading.CacheExecutorConfiguration;
import org.coconut.cache.service.threading.CacheExecutorFactory;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class NoThreadingCacheService extends AbstractInternalCacheService implements
        InternalCacheThreadingService, CacheExecutorFactory {

    /**
     * @param name
     */
    public NoThreadingCacheService() {
        super(CacheExecutorConfiguration.SERVICE_NAME);
    }

    /**
     * @see org.coconut.cache.internal.service.threading.InternalCacheThreadingService#isActive()
     */
    public boolean isActive() {
        return false;
    }

    /**
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    public void execute(Runnable command) {
        command.run();// ??
    }

    public CacheServiceExecutor createCacheExecutor(Class<?> service) {
        return new SameThreadCacheExecutor();
    }

    static class SameThreadCacheExecutor extends CacheServiceExecutor {

        @Override
        public ExecutorService createExecutorService() {
            return new ExecutorService() {

                public boolean awaitTermination(long timeout, TimeUnit unit)
                        throws InterruptedException {
                    return false;
                }

                public <T> List<Future<T>> invokeAll(
                        Collection<? extends Callable<T>> tasks)
                        throws InterruptedException {
                    return null;
                }

                public <T> List<Future<T>> invokeAll(
                        Collection<? extends Callable<T>> tasks, long timeout,
                        TimeUnit unit) throws InterruptedException {
                    return null;
                }

                public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
                        throws InterruptedException, ExecutionException {
                    return null;
                }

                public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                        long timeout, TimeUnit unit) throws InterruptedException,
                        ExecutionException, TimeoutException {
                    return null;
                }

                public boolean isShutdown() {
                    return false;
                }

                public boolean isTerminated() {
                    return false;
                }

                public void shutdown() {}

                public List<Runnable> shutdownNow() {
                    return null;
                }

                public <T> Future<T> submit(Callable<T> task) {
                    return null;
                }

                public Future<?> submit(Runnable task) {
                    task.run();
                    return null;
                }

                public <T> Future<T> submit(Runnable task, T result) {
                    return null;
                }

                public void execute(Runnable command) {}};
        }

        @Override
        public ScheduledExecutorService createScheduledExecutorService() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void executeDedicated(Runnable r) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void shutdown() {}

    }

    public CacheServiceExecutor getExecutor(Class<?> service) {
        return new SameThreadCacheExecutor();
    }
}

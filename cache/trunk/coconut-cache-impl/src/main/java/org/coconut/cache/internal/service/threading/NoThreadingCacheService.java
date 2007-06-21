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
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.service.threading.CacheServiceThreadManager;
import org.coconut.cache.service.threading.CacheExecutorConfiguration;
import org.coconut.cache.service.threading.CacheThreadManager;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class NoThreadingCacheService extends AbstractInternalCacheService implements
        InternalCacheThreadingService, CacheThreadManager {

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

    public CacheServiceThreadManager createCacheExecutor(Class<?> service) {
        return new SameThreadCacheExecutor();
    }

    static class SameThreadCacheExecutor extends CacheServiceThreadManager {

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
                    FutureTask<T> ft=new FutureTask<T>(task);
                    ft.run();
                    return ft;
                }

                public Future<?> submit(Runnable task) {
                    FutureTask<?> ft=new FutureTask(task,null);
                    ft.run();
                    return ft;
                }

                public <T> Future<T> submit(Runnable task, T result) {
                    return null;
                }

                public void execute(Runnable command) {
                    command.run();
                }};
        }

        @Override
        public ScheduledExecutorService createScheduledExecutorService() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void executeDedicated(Runnable r) {
            throw new UnsupportedOperationException();
        }

    }

    public CacheServiceThreadManager getExecutor(Class<?> service) {
        return new SameThreadCacheExecutor();
    }
}

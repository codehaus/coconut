/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheServices;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheServiceManagerUtil {

    public static void main(String[] args) {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();

        final Cache c = null;

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        CacheServices.servicemanager(c).registerService(
                CacheServiceManagerUtil.wrapExecutorService(ses, "Daily Cache Clearing"));
        ses.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                c.clear();
            }
        }, 0, 60 * 60 * 24, TimeUnit.SECONDS);

        CacheServiceManagerUtil.registerSingleThreadSchedulingService(c,
                "Daily Cache Clearing", new Runnable() {
                    public void run() {
                        c.clear();
                    }
                }, 0, 60 * 60 * 24, TimeUnit.SECONDS);
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after the given
     * initial delay, and subsequently with the given delay between the termination of one
     * execution and the commencement of the next. If any execution of the task encounters
     * an exception, subsequent executions are suppressed. Otherwise, the task will only
     * terminate via cancellation or termination of the executor.
     * 
     * @param command
     *            the task to execute
     * @param initialDelay
     *            the time to delay first execution
     * @param delay
     *            the delay between the termination of one execution and the commencement
     *            of the next
     * @param unit
     *            the time unit of the initialDelay and delay parameters
     * @return a ScheduledFuture representing pending completion of the task, and whose
     *         <tt>get()</tt> method will throw an exception upon cancellation
     * @throws RejectedExecutionException
     *             if the task cannot be scheduled for execution
     * @throws NullPointerException
     *             if command is null
     * @throws IllegalArgumentException
     *             if delay less than or equal to zero
     */
    public static ScheduledFuture<?> registerSingleThreadSchedulingService(Cache<?, ?> c,
            String name, Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        CacheServices.servicemanager(c).registerService(
                CacheServiceManagerUtil.wrapExecutorService(ses, "Daily Cache Clearing"));
        return ses.scheduleWithFixedDelay(command, initialDelay, 60 * 60 * 24,
                TimeUnit.SECONDS);
    }

    public static WrappedExecutorService wrapExecutorService(ExecutorService service) {
        return wrapExecutorService(service, "ExecutorService");
    }

    public static WrappedExecutorService wrapExecutorService(ExecutorService service,
            String name) {
        return new WrappedExecutorService(service, name);
    }

    public static class WrappedExecutorService extends AbstractCacheService implements
            ExecutorService {

        private final ExecutorService service;

        WrappedExecutorService(ExecutorService service, String name) {
            super(name);
            if (service == null) {
                throw new NullPointerException("service is null");
            } else if (name == null) {
                throw new NullPointerException("name is null");
            }
            this.service = service;
        }

        /**
         * @see java.util.concurrent.ExecutorService#awaitTermination(long,
         *      java.util.concurrent.TimeUnit)
         */
        public boolean awaitTermination(long timeout, TimeUnit unit)
                throws InterruptedException {
            return service.awaitTermination(timeout, unit);
        }

        /**
         * @param command
         * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
         */
        public void execute(Runnable command) {
            service.execute(command);
        }

        /**
         * @param <T>
         * @param tasks
         * @param timeout
         * @param unit
         * @return
         * @throws InterruptedException
         * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection, long,
         *      java.util.concurrent.TimeUnit)
         */
        public List invokeAll(Collection tasks, long timeout, TimeUnit unit)
                throws InterruptedException {
            // we can't generify this method if we want to
            // be able to compile both on j2se 1.5.0 and javase 6
            // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6267833
            return service.invokeAll(tasks, timeout, unit);
        }

        /**
         * @param <T>
         * @param tasks
         * @return
         * @throws InterruptedException
         * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection)
         */
        public List invokeAll(Collection tasks) throws InterruptedException {
            return service.invokeAll(tasks);
        }

        /**
         * @param <T>
         * @param tasks
         * @param timeout
         * @param unit
         * @return
         * @throws InterruptedException
         * @throws ExecutionException
         * @throws TimeoutException
         * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection, long,
         *      java.util.concurrent.TimeUnit)
         */
        public Object invokeAny(Collection tasks, long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return service.invokeAny(tasks, timeout, unit);
        }

        /**
         * @param <T>
         * @param tasks
         * @return
         * @throws InterruptedException
         * @throws ExecutionException
         * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection)
         */
        public Object invokeAny(Collection tasks) throws InterruptedException,
                ExecutionException {
            return service.invokeAny(tasks);
        }

        /**
         * @return
         * @see java.util.concurrent.ExecutorService#isShutdown()
         */
        public boolean isShutdown() {
            return service.isShutdown();
        }

        /**
         * @return
         * @see java.util.concurrent.ExecutorService#isTerminated()
         */
        public boolean isTerminated() {
            return service.isTerminated();
        }

        /**
         * @see java.util.concurrent.ExecutorService#shutdown()
         */
        public void shutdown() {
            service.shutdown();
        }

        /**
         * @return
         * @see java.util.concurrent.ExecutorService#shutdownNow()
         */
        public List<Runnable> shutdownNow() {
            return service.shutdownNow();
        }

        /**
         * @param <T>
         * @param task
         * @return
         * @see java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
         */
        public <T> Future<T> submit(Callable<T> task) {
            return service.submit(task);
        }

        /**
         * @param <T>
         * @param task
         * @param result
         * @return
         * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable,
         *      java.lang.Object)
         */
        public <T> Future<T> submit(Runnable task, T result) {
            return service.submit(task, result);
        }

        /**
         * @param task
         * @return
         * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable)
         */
        public Future<?> submit(Runnable task) {
            return service.submit(task);
        }

        /**
         * @see org.coconut.cache.service.servicemanager.CacheLifecycle#shutdown(org.coconut.cache.Cache)
         */
        public void shutdown(Cache<?, ?> ignore) {
            service.shutdown();
        }
    }
}

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
public final class CacheServiceManagerUtil {

    /** Cannot instantiate. */
    private CacheServiceManagerUtil() {}

    /**
     * Creates and executes a periodic action that becomes enabled first after the given
     * initial delay (FROM CACHE START), and subsequently with the given delay between the termination of one
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
    /**
     * @param c
     *            ss
     * @param name
     *            ss
     * @param command
     *            ss
     * @param initialDelay
     *            ss
     * @param delay
     *            ss
     * @param unit
     *            ss
     * @return ss
     */
    public static ScheduledFuture<?> registerSingleThreadSchedulingService(CacheConfiguration<?, ?> c,
            String name, Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        c.serviceManager().addService(
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

        /** The {@link ExecutorService} we are wrapping. */
        private final ExecutorService service;

        /**
         * @param service
         *            the ExecutorService to wrap
         * @param name
         *            the name of the service
         */
        WrappedExecutorService(ExecutorService service, String name) {
            super(name);
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        /**
         * {@inheritDoc}
         */
        public boolean awaitTermination(long timeout, TimeUnit unit)
                throws InterruptedException {
            return service.awaitTermination(timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        public void execute(Runnable command) {
            service.execute(command);
        }

        /**
         * {@inheritDoc}
         */
        public List invokeAll(Collection tasks, long timeout, TimeUnit unit)
                throws InterruptedException {
            // we can't generify this method if we want to
            // be able to compile both on j2se 1.5.0 and javase 6
            // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6267833
            return service.invokeAll(tasks, timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        public List invokeAll(Collection tasks) throws InterruptedException {
            return service.invokeAll(tasks);
        }

        /**
         * {@inheritDoc}
         */
        public Object invokeAny(Collection tasks, long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return service.invokeAny(tasks, timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        public Object invokeAny(Collection tasks) throws InterruptedException,
                ExecutionException {
            return service.invokeAny(tasks);
        }

        /**
         * {@inheritDoc}
         */
        public boolean isShutdown() {
            return service.isShutdown();
        }

        /**
         * {@inheritDoc}
         */
        public boolean isTerminated() {
            return service.isTerminated();
        }

        /**
         * {@inheritDoc}
         */
        public void shutdown() {
            service.shutdown();
        }

        /**
         * {@inheritDoc}
         */
        public List<Runnable> shutdownNow() {
            return service.shutdownNow();
        }

        /**
         * {@inheritDoc}
         */
        public <T> Future<T> submit(Callable<T> task) {
            return service.submit(task);
        }

        /**
         * {@inheritDoc}
         */
        public <T> Future<T> submit(Runnable task, T result) {
            return service.submit(task, result);
        }

        /**
         * {@inheritDoc}
         */
        public Future<?> submit(Runnable task) {
            return service.submit(task);
        }

        /**
         * {@inheritDoc}
         */
        public void shutdown(Cache<?, ?> ignore) {
            service.shutdown();
        }
    }
}

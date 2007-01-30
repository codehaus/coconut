/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core.util;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ThreadUtils {

    public final static Executor SAME_THREAD_EXECUTOR = new SameThreadExecutor();


    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link #call}.
     */
    private static Callable NULL_CALLABLE = new NullCallable();

    
    static class SameThreadExecutor implements Executor, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -6365439666830575122L;

        /**
         * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
         */
        public void execute(Runnable command) {
            command.run();
        }
    }

    /**
     * Creates an Executor that uses a single worker thread operating off an
     * unbounded queue, and uses the provided ThreadFactory to create a new
     * thread when needed. Unlike the otherwise equivalent
     * <tt>newFixedThreadPool(1, threadFactory)</tt> the returned executor is
     * guaranteed not to be reconfigurable to use additional threads.
     * 
     * @param threadFactory
     *            the factory to use when creating new threads
     * @return the newly created single-threaded Executor
     * @throws NullPointerException
     *             if threadFactory is null
     */
    public static ExecutorService newSingleDaemonThreadExecutor() {
        return Executors.newSingleThreadExecutor(daemonThreadFactory());
    }

    /**
     * Creates a single-threaded executor that can schedule commands to run
     * after a given delay, or to execute periodically. (Note however that if
     * this single thread terminates due to a failure during execution prior to
     * shutdown, a new one will take its place if needed to execute subsequent
     * tasks.) Tasks are guaranteed to execute sequentially, and no more than
     * one task will be active at any given time. Unlike the otherwise
     * equivalent <tt>newScheduledThreadPool(1)</tt> the returned executor is
     * guaranteed not to be reconfigurable to use additional threads.
     * 
     * @return the newly created scheduled executor
     */
    public static ScheduledExecutorService newSingleDaemonThreadScheduledExecutor() {
        return Executors.newSingleThreadScheduledExecutor(daemonThreadFactory());
    }

    /**
     * Returns a default thread factory used to create new threads. This factory
     * creates all new threads used by an Executor in the same
     * {@link ThreadGroup}. If there is a {@link java.lang.SecurityManager},
     * it uses the group of {@link System#getSecurityManager}, else the group
     * of the thread invoking this <tt>defaultThreadFactory</tt> method. Each
     * new thread is created as a daemon thread with priority set to the smaller
     * of <tt>Thread.NORM_PRIORITY</tt> and the maximum priority permitted in
     * the thread group. New threads have names accessible via
     * {@link Thread#getName} of <em>pool-N-deamon-thread-M</em>, where
     * <em>N</em> is the sequence number of this factory, and <em>M</em> is
     * the sequence number of the thread created by this factory.
     * 
     * @return a thread factory
     */
    public static ThreadFactory daemonThreadFactory() {
        return new DaemonThreadFactory();
    }

    static class DaemonThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);

        final ThreadGroup group;

        final AtomicInteger threadNumber = new AtomicInteger(1);

        final String namePrefix;

        DaemonThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            namePrefix = "pool-" + poolNumber.getAndIncrement() + "-daemon-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (!t.isDaemon())
                t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
    

    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link java.util.concurrent.Callable#call}.
     */
    @SuppressWarnings("unchecked")
    public static <V> Callable<V> nullCallable() {
        return NULL_CALLABLE;
    }
    

    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link #call}.
     */
    final static class NullCallable<V> implements Callable<V>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 4869209484084557763L;

        /** {@inheritDoc} */
        public V call() {
            return null;
        }
    }

}

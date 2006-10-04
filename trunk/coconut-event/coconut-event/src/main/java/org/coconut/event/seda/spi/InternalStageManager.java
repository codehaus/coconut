/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.spi;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class InternalStageManager {
    /**
     * Current total pool size, updated only while holding mainLock but volatile
     * to allow concurrent readability even during updates.
     */
    private volatile int poolSize;

    /**
     * Tracks largest attained pool size.
     */
    private int largestPoolSize;

    /**
     * Set containing all worker threads in the container.
     */
    final HashSet<ThreadWorker> allWorkers = new HashSet<ThreadWorker>();

    /**
     * Permission for checking shutdown
     */
    private static final RuntimePermission shutdownPerm = new RuntimePermission(
            "modifyThread");

    /**
     * Lifecycle state
     */
    volatile RunState runState = RunState.NEW;

    /**
     * Lock held on updates to poolSize, corePoolSize, maximumPoolSize, and
     * workers set.
     */
    final ReentrantLock mainLock = new ReentrantLock();

    /**
     * Wait condition to support awaitTermination
     */
    private final Condition termination = mainLock.newCondition();

    /**
     * Factory for new threads.
     */
    private volatile ThreadFactory threadFactory = Executors.defaultThreadFactory();

    private boolean shutdownedCalled;

    /**
     * Get the next task for a worker thread to run.
     * 
     * @return the task
     * @throws InterruptedException
     *             if interrupted while waiting for task
     */

    /**
     * Returns the thread factory used to create new threads.
     * 
     * @return the current thread factory
     * @see #setThreadFactory
     */
    public ThreadFactory getDefaultThreadFactory() {
        return threadFactory;
    }

    /**
     * Sets the thread factory used to create new threads.
     * 
     * @param threadFactory
     *            the new thread factory
     * @throws NullPointerException
     *             if threadFactory is null
     * @see #getThreadFactory
     */
    public void setDefaultThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory is null");
        }
        this.threadFactory = threadFactory;
    }

    /**
     * Wake up all threads that might be waiting for tasks.
     */
    public void interruptIdleWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (ThreadWorker w : allWorkers)
                w.interruptIfIdle();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * @param threadFactory
     * @return
     */
    public ThreadWorker addThreadWorker(ThreadFactory threadFactory) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            Worker w = new Worker();
            w.firstTask = getTask(w);
            Thread t = threadFactory.newThread(w);
            if (t != null) {
                w.thread = t;
                allWorkers.add(w);
                int nt = ++poolSize;
                if (nt > largestPoolSize)
                    largestPoolSize = nt;
            }
            return w;
        } finally {
            mainLock.unlock();
        }

    }

    /**
     * Returns the largest number of threads that have ever simultaneously been
     * in the pool.
     * 
     * @return the number of threads
     */
    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return largestPoolSize;
        } finally {
            mainLock.unlock();
        }
    }

    protected void workerStarted(ThreadWorker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            assert runState == RunState.RUNNING;
            allWorkers.add(w);
        } finally {
            mainLock.unlock();
        }
    }

    protected void workerRemoved(ThreadWorker w) {

    }

    protected abstract boolean isDone();
    
    /**
     * Perform bookkeeping for a terminated worker thread.
     * 
     * @param w
     *            the worker
     */
    protected void workerDone(ThreadWorker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            allWorkers.remove(w);
            workerRemoved(w);
            if (allWorkers.size() > 0)
                return;

            // Else, this is the last thread. Deal with potential shutdown.

            RunState state = runState;
            assert state != RunState.TERMINATED;

            if (state != RunState.STOP) {
                // If there are queued tasks but no threads, create
                // replacement thread. We must create it initially
                // idle to avoid orphaned tasks in case addThread
                // fails. This also handles case of delayed tasks
                // that will sometime later become runnable.
                if (!isDone()) {
                    ThreadWorker tw=addThreadWorker(getDefaultThreadFactory());
                    if (tw != null)
                        tw.getThread().start();
                }

                // check sink queue, which contains entries of the type
                // stage,Object (event)

                // Otherwise, we can exit without replacement
                if (state == RunState.RUNNING)
                    return;
            }

            // Either state is STOP, or state is SHUTDOWN and there is
            // no work to do. So we can terminate.
            termination.signalAll();
            runState = RunState.TERMINATED;
            // fall through to call terminate() outside of lock.
        } finally {
            mainLock.unlock();
        }

        assert runState == RunState.TERMINATED;
        terminated();
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are
     * executed, but no new tasks will be accepted. Invocation has no additional
     * effect if already shut down.
     * 
     * @throws SecurityException
     *             if a security manager exists and shutting down this
     *             ExecutorService may manipulate threads that the caller is not
     *             permitted to modify because it does not hold
     *             {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>,
     *             or the security manager's <tt>checkAccess</tt> method
     *             denies access.
     */
    public void shutdown() {
        // Fail if caller doesn't have modifyThread permission. We
        // explicitly check permissions directly because we can't trust
        // implementations of SecurityManager to correctly override
        // the "check access" methods such that our documented
        // security policy is implemented.
        SecurityManager security = System.getSecurityManager();
        if (security != null)
            java.security.AccessController.checkPermission(shutdownPerm);

        boolean fullyTerminated = false;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            tryShutdown();
            boolean wasShutdown = false;
            try {
                if (allWorkers.size() > 0) {
                    // Check if caller can modify worker threads. This
                    // might not be true even if passed above check, if
                    // the SecurityManager treats some threads specially.
                    if (security != null) {
                        for (ThreadWorker w : allWorkers)
                            security.checkAccess(w.getThread());
                    }

                    RunState state = runState;
                    // don't override shutdownNow
                    if (state == RunState.RUNNING)
                        runState = RunState.SHUTDOWN;

                    try {
                        for (ThreadWorker w : allWorkers)
                            w.interruptIfIdle();
                    } catch (SecurityException se) {
                        // If SecurityManager allows above checks, but
                        // then unexpectedly throws exception when
                        // interrupting threads (which it ought not do),
                        // back out as cleanly as we can. Some threads may
                        // have been killed but we remain in non-shutdown
                        // state.
                        runState = state;
                        throw se;
                    }
                } else { // If no workers, trigger full termination now
                    fullyTerminated = true;
                    runState = RunState.TERMINATED;
                    termination.signalAll();
                }
                wasShutdown = !shutdownedCalled
                        && (runState == RunState.SHUTDOWN || runState == RunState.STOP || runState == RunState.TERMINATED);
            } finally {
                shutdowned(wasShutdown);
                shutdownedCalled |= wasShutdown;
            }
        } finally {
            mainLock.unlock();
        }
        if (fullyTerminated)
            terminated();
    }

    /**
     * Attempts to stop all actively executing stages, halts the processing of
     * pending events, and returns a list of the events that were awaiting
     * execution.
     * <p>
     * There are no guarantees beyond best-effort attempts to stop processing
     * actively executing events. This implementation cancels tasks via
     * {@link Thread#interrupt}, so any task that fails to respond to
     * interrupts may never terminate.
     * 
     * @return list of tasks that never commenced execution
     * @throws SecurityException
     *             if a security manager exists and shutting down this
     *             StageManager may manipulate threads that the caller is not
     *             permitted to modify because it does not hold
     *             {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>,
     *             or the security manager's <tt>checkAccess</tt> method
     *             denies access.
     */
    public void shutdownNow() {
        // Almost the same code as shutdown()
        SecurityManager security = System.getSecurityManager();
        if (security != null)
            java.security.AccessController.checkPermission(shutdownPerm);

        boolean fullyTerminated = false;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            tryShutdown();
            if (allWorkers.size() > 0) {
                if (security != null) {
                    for (ThreadWorker w : allWorkers)
                        security.checkAccess(w.getThread());
                }

                RunState state = runState;
                if (state != RunState.TERMINATED)
                    runState = RunState.STOP;
                try {
                    for (ThreadWorker w : allWorkers)
                        w.interruptNow();
                } catch (SecurityException se) {
                    // TODO we better remove this
                    // No way to undo shutdowned()
                    // Perhaps we make a try/
                    runState = state; // back out;
                    throw se;
                }
            } else { // If no workers, trigger full termination now
                fullyTerminated = true;
                runState = RunState.TERMINATED;
                termination.signalAll();
            }
        } finally {
            mainLock.unlock();
        }
        if (fullyTerminated)
            terminated();
    }

    public boolean isShutdown() {
        RunState state = runState;
        return state != RunState.RUNNING && state != RunState.NEW;
    }

    /**
     * Returns true if this executor is in the process of terminating after
     * <tt>shutdown</tt> or <tt>shutdownNow</tt> but has not completely
     * terminated. This method may be useful for debugging. A return of
     * <tt>true</tt> reported a sufficient period after shutdown may indicate
     * that submitted tasks have ignored or suppressed interruption, causing
     * this executor not to properly terminate.
     * 
     * @return true if terminating but not yet terminated.
     */
    public boolean isTerminating() {
        return runState == RunState.STOP;
    }

    public boolean isTerminated() {
        return runState == RunState.TERMINATED;
    }

    /**
     * @see org.coconut.event.seda.StageManager#awaitTermination(long,
     *      java.util.concurrent.TimeUnit)
     */
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;) {
                if (runState == RunState.TERMINATED)
                    return true;
                if (nanos <= 0)
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
    }

    public void start() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (getState() == RunState.NEW) {
                runState = RunState.RUNNING;
                started();
            } else if (getState() != RunState.RUNNING) {
                throw new IllegalStateException(
                        "Manager terminated started, cannot restart manager");
            }
        } finally {
            mainLock.unlock();
        }
    }

    protected void started() {

    }

    protected void tryShutdown() {

    }

    protected void shutdowned(boolean wasShutdown) {
    }

    /* Statistics */

    /**
     * Returns the current number of threads in the pool.
     * 
     * @return the number of threads
     */
    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return allWorkers.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the approximate number of threads that are actively executing
     * tasks.
     * 
     * @return the number of threads
     */
    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (ThreadWorker w : allWorkers) {
                if (w.isActive())
                    ++n;
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Gets the next task for a worker thread to run.
     * 
     * @return the task
     */
    protected abstract Runnable getTask(ThreadWorker w);

    /**
     * Method invoked when the StageManager has terminated. Default
     * implementation does nothing. Note: To properly nest multiple overridings,
     * subclasses should generally invoke <tt>super.terminated</tt> within
     * this method.
     */
    protected void terminated() {
    }

    /**
     * Method invoked prior to executing the given Runnable in the given thread.
     * This method is invoked by thread <tt>t</tt> that will execute task
     * <tt>r</tt>, and may be used to re-initialize ThreadLocals, or to
     * perform logging.
     * <p>
     * This implementation does nothing, but may be customized in subclasses.
     * Note: To properly nest multiple overridings, subclasses should generally
     * invoke <tt>super.beforeExecute</tt> at the end of this method.
     * 
     * @param t
     *            the thread that will run task r.
     * @param r
     *            the task that will be executed.
     */
    protected void beforeExecute(ThreadWorker w, Runnable r) {
    }

    /**
     * Method invoked upon completion of execution of the given Runnable. This
     * method is invoked by the thread that executed the task. If non-null, the
     * Throwable is the uncaught <tt>RuntimeException</tt> or <tt>Error</tt>
     * that caused execution to terminate abruptly.
     * <p>
     * <b>Note:</b> When actions are enclosed in tasks (such as
     * {@link FutureTask}) either explicitly or via methods such as
     * <tt>submit</tt>, these task objects catch and maintain computational
     * exceptions, and so they do not cause abrupt termination, and the internal
     * exceptions are <em>not</em> passed to this method.
     * <p>
     * This implementation does nothing, but may be customized in subclasses.
     * Note: To properly nest multiple overridings, subclasses should generally
     * invoke <tt>super.afterExecute</tt> at the beginning of this method.
     * 
     * @param r
     *            the runnable that has completed.
     * @param t
     *            the exception that caused termination, or null if execution
     *            completed normally.
     */
    protected void afterExecute(ThreadWorker w, Runnable r, Throwable t) {
    }

    /**
     * Worker threads
     */
    private class Worker implements ThreadWorker {

        /**
         * The runLock is acquired and released surrounding each task execution.
         * It mainly protects against interrupts that are intended to cancel the
         * worker thread from instead interrupting the task being run.
         */
        private final ReentrantLock runLock = new ReentrantLock();

        /**
         * Initial task to run before entering run loop
         */
        private Runnable firstTask;

        /**
         * Per thread completed task counter; accumulated into
         * completedTaskCount upon termination.
         */
        volatile long completedTasks;

        private volatile Object attachment;

        /**
         * Thread this worker is running in. Acts as a final field, but cannot
         * be set until thread is created.
         */
        Thread thread;

        public Thread getThread() {
            return thread;
        }

        public boolean isActive() {
            return runLock.isLocked();
        }

        /**
         * Interrupts thread if not running a task.
         */
        public void interruptIfIdle() {
            final ReentrantLock runLock = this.runLock;
            if (runLock.tryLock()) {
                try {
                    thread.interrupt();
                } finally {
                    runLock.unlock();
                }
            }
        }

        /**
         * Interrupts thread even if running a task.
         */
        public void interruptNow() {
            thread.interrupt();
        }

        /**
         * Runs a single task between before/after methods.
         */
        private void runTask(Runnable task) {
            final ReentrantLock runLock = this.runLock;
            runLock.lock();
            try {
                Thread.interrupted(); // clear interrupt status on entry
                // Abort now if immediate cancel. Otherwise, we have
                // committed to run this task.
                if (runState == RunState.STOP)
                    return;

                boolean ran = false;
                beforeExecute(this, task);
                try {
                    task.run();
                    ran = true;
                    afterExecute(this, task, null);
                    ++completedTasks;
                } catch (RuntimeException ex) {
                    if (!ran)
                        afterExecute(this, task, ex);
                    // Else the exception occurred within
                    // afterExecute itself in which case we don't
                    // want to call it again.
                    throw ex;
                }
            } finally {
                runLock.unlock();
            }
        }

        /**
         * Main run loop
         */
        public void run() {
            try {
                Runnable task = firstTask;
                firstTask = null;
                while (task != null || (task = getTask(this)) != null) {
                    runTask(task);
                    task = null; // unnecessary but can help GC
                }
            } finally {
                workerDone(this);
            }
        }

        /**
         * @see org.coconut.event.sedaold.defaults.ThreadWorker#attach(java.lang.Object)
         */
        public Object setAttachment(Object ob) {
            Object previous = attachment;
            attachment = ob;
            return previous;
        }

        /**
         * @see org.coconut.event.sedaold.defaults.ThreadWorker#attachment()
         */
        public Object getAttachment() {
            return attachment;
        }
    }

    public ReentrantLock getMainLock() {
        return mainLock;
    }

    public RunState getState() {
        return runState;
    }

    /**
     * An AbstractStageManager can be in one of the following states:
     * <ul>
     * <li>{@link #NEW}<br>
     * A manager that has not yet been started is in this state. </li>
     * <li>{@link #RUNNING}<br>
     * A manager actively processing events is in this state. </li>
     * <li>{@link #SHUTDOWN}<br>
     * A thread that is blocked waiting for a monitor lock is in this state.
     * </li>
     * <li>{@link #STOP}<br>
     * A thread that is blocked waiting for a monitor lock is in this state.
     * </li>
     * <li>{@link #TERMINATED}<br>
     * A thread that is waiting indefinitely for another thread to perform a
     * particular action is in this state. </li>
     * </ul>
     * <p>
     * A manager can be in only one state at a given point in time.
     * 
     * @see AbstractStageManager#getState
     */
    public static enum RunState {
        NEW, RUNNING, SHUTDOWN, STOP, TERMINATED;
    }

}

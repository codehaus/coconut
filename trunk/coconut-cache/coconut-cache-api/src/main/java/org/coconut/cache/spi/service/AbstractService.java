/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractService {

    /**
     * Permission for checking shutdown
     */
    private final RuntimePermission shutdownPerm;

    /**
     * Lifecycle state
     */
    volatile RunState runState = RunState.NOT_STARTED;

    /**
     * Lock held on updates to poolSize, corePoolSize, maximumPoolSize, and
     * workers set.
     */
    final ReentrantLock mainLock = new ReentrantLock();

    /**
     * Wait condition to support awaitTermination
     */
    private final Condition termination = mainLock.newCondition();

    private boolean shutdownedCalled;

    public AbstractService() {
        shutdownPerm = null;
    }

    public AbstractService(RuntimePermission shutdownPerm) {
        this.shutdownPerm = shutdownPerm;
    }

    public boolean isShutdown() {
        RunState state = runState;
        return state != RunState.RUNNING && state != RunState.NOT_STARTED;
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

    protected void started() {

    }

    protected boolean tryShutdown() {
        return true;
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
                if (tryShutdown()) {
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

    protected void shutdownNow() {
        // Almost the same code as shutdown()
        SecurityManager security = System.getSecurityManager();
        if (security != null)
            java.security.AccessController.checkPermission(shutdownPerm);

        boolean fullyTerminated = false;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (tryShutdown()) {
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

    /**
     * Method invoked when the StageManager has terminated. Default
     * implementation does nothing. Note: To properly nest multiple overridings,
     * subclasses should generally invoke <tt>super.terminated</tt> within
     * this method.
     */
    protected void terminated() {
    }

    protected void shutdowned(boolean wasShutdown) {
    }

    protected boolean tryStart() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (getState() == RunState.NOT_STARTED) {
                runState = RunState.RUNNING;
                started();
                return true;
            } else if (getState() != RunState.RUNNING) {
                throw new IllegalStateException(
                        "Service termination started, cannot restart service");
            }
        } finally {
            mainLock.unlock();
        }
        return false;
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

    protected ReentrantLock getMainLock() {
        return mainLock;
    }

    protected RunState getState() {
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
        NOT_STARTED, RUNNING, SHUTDOWN, STOP, TERMINATED;
    }
}

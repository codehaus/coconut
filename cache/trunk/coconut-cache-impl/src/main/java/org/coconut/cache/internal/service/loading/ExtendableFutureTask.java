/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Based on future task. But is easy to override.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class ExtendableFutureTask<V> implements Runnable, Future<V> {
    /** Synchronization control for FutureTask */
    private final Sync sync = new Sync();

    public boolean isCancelled() {
        return sync.innerIsCancelled();
    }

    public boolean isDone() {
        return sync.innerIsDone();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return sync.innerCancel(mayInterruptIfRunning);
    }

    /**
     * @throws CancellationException
     *             {@inheritDoc}
     */
    public V get() throws InterruptedException, ExecutionException {
        return sync.innerGet();
    }

    /**
     * @throws CancellationException
     *             {@inheritDoc}
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return sync.innerGet(unit.toNanos(timeout));
    }

    /**
     * Protected method invoked when this task transitions to state
     * <tt>isDone</tt> (whether normally or via cancellation). The default
     * implementation does nothing. Subclasses may override this method to
     * invoke completion callbacks or perform bookkeeping. Note that you can
     * query status inside the implementation of this method to determine
     * whether this task has been cancelled.
     */
    protected void done() {
    }

    /**
     * Sets the result of this Future to the given value unless this future has
     * already been set or has been cancelled. This method is invoked internally
     * by the <tt>run</tt> method upon successful completion of the
     * computation.
     * 
     * @param v
     *            the value
     */
    protected void set(V v) {
        sync.innerSet(v);
    }

    /**
     * Causes this future to report an <tt>ExecutionException</tt> with the
     * given throwable as its cause, unless this Future has already been set or
     * has been cancelled. This method is invoked internally by the <tt>run</tt>
     * method upon failure of the computation.
     * 
     * @param t
     *            the cause of failure
     */
    protected void setException(Throwable t) {
        sync.innerSetException(t);
    }

    /**
     * Sets this Future to the result of its computation unless it has been
     * cancelled.
     */
    public void run() {
        sync.innerRun();
    }

    abstract V call() throws Exception;

    /**
     * Executes the computation without setting its result, and then resets this
     * Future to initial state, failing to do so if the computation encounters
     * an exception or is cancelled. This is designed for use with tasks that
     * intrinsically execute more than once.
     * 
     * @return true if successfully run and reset
     */
    protected boolean runAndReset() {
        return sync.innerRunAndReset();
    }

    /**
     * Synchronization control for FutureTask. Note that this must be a
     * non-static inner class in order to invoke the protected <tt>done</tt>
     * method. For clarity, all inner class support methods are same as outer,
     * prefixed with "inner". Uses AQS sync state to represent run status
     */
    private final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -7828117401763700385L;

        /** State value representing that task is ready to run */
        private static final int READY = 0;

        /** State value representing that task is running */
        private static final int RUNNING = 1;

        /** State value representing that task ran */
        private static final int RAN = 2;

        /** State value representing that task was cancelled */
        private static final int CANCELLED = 4;

        /** The result to return from get() */
        private V result;

        /** The exception to throw from get() */
        private Throwable exception;

        /**
         * The thread running task. When nulled after set/cancel, this indicates
         * that the results are accessible. Must be volatile, to ensure
         * visibility upon completion.
         */
        private volatile Thread runner;

        private boolean ranOrCancelled(int state) {
            return (state & (RAN | CANCELLED)) != 0;
        }

        /**
         * Implements AQS base acquire to succeed if ran or cancelled
         */
        protected int tryAcquireShared(int ignore) {
            return innerIsDone() ? 1 : -1;
        }

        /**
         * Implements AQS base release to always signal after setting final done
         * status by nulling runner thread.
         */
        protected boolean tryReleaseShared(int ignore) {
            runner = null;
            return true;
        }

        boolean innerIsCancelled() {
            return getState() == CANCELLED;
        }

        boolean innerIsDone() {
            return ranOrCancelled(getState()) && runner == null;
        }

        V innerGet() throws InterruptedException, ExecutionException {
            acquireSharedInterruptibly(0);
            if (getState() == CANCELLED)
                throw new CancellationException();
            if (exception != null)
                throw new ExecutionException(exception);
            return result;
        }

        V innerGet(long nanosTimeout) throws InterruptedException, ExecutionException,
                TimeoutException {
            if (!tryAcquireSharedNanos(0, nanosTimeout))
                throw new TimeoutException();
            if (getState() == CANCELLED)
                throw new CancellationException();
            if (exception != null)
                throw new ExecutionException(exception);
            return result;
        }

        void innerSet(V v) {
            for (;;) {
                int s = getState();
                if (s == RAN)
                    return;
                if (s == CANCELLED) {
                    // aggressively release to set runner to null,
                    // in case we are racing with a cancel request
                    // that will try to interrupt runner
                    releaseShared(0);
                    return;
                }
                if (compareAndSetState(s, RAN)) {
                    result = v;
                    releaseShared(0);
                    done();
                    return;
                }
            }
        }

        void innerSetException(Throwable t) {
            for (;;) {
                int s = getState();
                if (s == RAN)
                    return;
                if (s == CANCELLED) {
                    // aggressively release to set runner to null,
                    // in case we are racing with a cancel request
                    // that will try to interrupt runner
                    releaseShared(0);
                    return;
                }
                if (compareAndSetState(s, RAN)) {
                    exception = t;
                    releaseShared(0);
                    done();
                    return;
                }
            }
        }

        boolean innerCancel(boolean mayInterruptIfRunning) {
            for (;;) {
                int s = getState();
                if (ranOrCancelled(s))
                    return false;
                if (compareAndSetState(s, CANCELLED))
                    break;
            }
            if (mayInterruptIfRunning) {
                Thread r = runner;
                if (r != null)
                    r.interrupt();
            }
            releaseShared(0);
            done();
            return true;
        }

        void innerRun() {
            if (!compareAndSetState(READY, RUNNING))
                return;
            try {
                runner = Thread.currentThread();
                if (getState() == RUNNING) // recheck after setting thread
                    set(call());
                else
                    releaseShared(0); // cancel
            } catch (Throwable ex) {
                setException(ex);
            }
        }

        boolean innerRunAndReset() {
            if (!compareAndSetState(READY, RUNNING))
                return false;
            try {
                runner = Thread.currentThread();
                if (getState() == RUNNING)
                    call(); // don't set result
                runner = null;
                return compareAndSetState(RUNNING, READY);
            } catch (Throwable ex) {
                setException(ex);
                return false;
            }
        }
    }
}
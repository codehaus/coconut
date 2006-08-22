/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.aio.AioFuture;
import org.coconut.core.Callback;
import org.coconut.core.Colored;
import org.coconut.core.Offerable;


/**
 * A cancellable asynchronous computation. This class provides a base
 * implementation of {@link Future}, with methods to start and cancel a
 * computation, query to see if the computation is complete, and retrieve the
 * result of the computation. The result can only be retrieved when the
 * computation has completed; the <tt>get</tt> method will block if the
 * computation has not yet completed. Once the computation has completed, the
 * computation cannot be restarted or cancelled.
 * <p>
 * A <tt>FutureTask</tt> can be used to wrap a {@link Callable}or
 * {@link java.lang.Runnable}object. Because <tt>FutureTask</tt> implements
 * <tt>Runnable</tt>, a <tt>FutureTask</tt> can be submitted to an
 * {@link Executor}for execution.
 * <p>
 * In addition to serving as a standalone class, this class provides
 * <tt>protected</tt> functionality that may be useful when creating
 * customized task classes.
 * 
 * @version $Id$
 */
public abstract class AioFutureTask<V, T> implements Runnable, AioFuture<V, T>, Callable<V> {
    /** Synchronization control for FutureTask */
    private final Sync sync;

    private final Executor e;
    private final Offerable< ? super T> o;
    private volatile Offerable< ? super T> newOfferable;

    private volatile Callback<V> callback;
    private volatile Executor newExecutor;

    /**
     * Creates a <tt>FutureTask</tt> that will upon running, execute the given
     * <tt>Callable</tt>.
     * 
     * @param callable the callable task
     * @throws NullPointerException if callable is null
     */
    public AioFutureTask(Executor e, Offerable< ? super T> o) {
        this.e = e;
        this.o = o;
        sync = new Sync(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    public V call() throws Exception {
        throw new IllegalStateException("Call not allowed");
    }

    public boolean isCancelled() {
        return sync.innerIsCancelled();
    }

    public boolean isDone() {
        return sync.innerIsDone();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return sync.innerCancel(mayInterruptIfRunning);
    }

    public V get() throws InterruptedException, ExecutionException {
        return sync.innerGet();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
        TimeoutException {
        return sync.innerGet(unit.toNanos(timeout));
    }
    public V getIO() throws IOException {
        try {
            return get();
        } catch (InterruptedException e) {
            throw new InterruptedIOException(e.getMessage());
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException)
                throw (RuntimeException) cause;
            else {
                throw new IllegalStateException("Unknown exception", cause);
            }
        }
    }
    /*
     * (non-Javadoc)
     * 
     * @see coconut.aio.AioFuture#getIO(long, java.util.concurrent.TimeUnit)
     */
    public V getIO(long timeout, TimeUnit unit) throws IOException, TimeoutException {
        try {
            return get(timeout, unit);
        } catch (InterruptedException e) {
            throw new InterruptedIOException(e.getMessage());
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException)
                throw (RuntimeException) cause;
            else {
                throw new IllegalStateException("Unknown exception", cause);
            }
        }
    }
    /*
     * (non-Javadoc)
     * 
     * @see coconut.aio.AioFuture#setDestination(coconut.core.Offerable)
     */
    public void setDest(Offerable< ? super T> offerable) {
        this.newOfferable = offerable;
        sync.checkState();
    } /*
         * (non-Javadoc)
         * 
         * @see coconut.core.CallbackFuture#setCallback(java.util.concurrent.Executor,
         *      coconut.core.Callback)
         */
    public void setCallback(Executor executor, Callback<V> callback) {
        newExecutor = executor;
        this.callback = callback;
        sync.checkState();
    }
    public void setCallback(Callback<V> callback) {
        if (e == null)
            throw new IllegalStateException("No default executor specified");
        this.callback = callback;
        sync.checkState();
    }

    /**
     * Protected method invoked when this task transitions to state
     * <tt>isDone</tt> (whether normally or via cancellation). The default
     * implementation does nothing. Subclasses may override this method to
     * invoke completion callbacks or perform bookkeeping. Note that you can
     * query status inside the implementation of this method to determine
     * whether this task has been cancelled.
     */
    protected void doneSet(final Object v) {
        if (o != null) {
            o.offer((T) this);
        }
        final Offerable< ? super T> offer = newOfferable;
        if (offer != null) {
            offer.offer((T) this);
            newOfferable = null;
        }
        final Callback c = callback;

        if (c != null) {
            Executor exec = newExecutor;
            Runnable r = new Runnable() {
                public void run() {
                    c.completed(v);
                }
            };
            if (exec != null)
                exec.execute(r);
            else
                e.execute(r);
            callback = null;
        }

    }
    protected abstract void deliverFailure(Offerable< ? super T> o, Throwable t);

    protected abstract int getColor();
    protected void doneException(final Throwable t) {

        if (o != null) {
            deliverFailure(o, t);
            // o.offer(this);
        }
        final Offerable offer = newOfferable;
        if (offer != null) {
            deliverFailure(offer, t);
            newOfferable = null;
        }

        final Callback c = callback;

        if (c != null) {
            Executor exec = newExecutor;
            Runnable r = new ColoredRunnable() {
                public void run() {
                    c.failed(t);
                }
                public int getColor() {
                    return getColor();
                }
            };
            if (exec != null)
                exec.execute(r);
            else
                e.execute(r);
            callback = null;
        }

    }
    /**
     * Sets the result of this Future to the given value unless this future has
     * already been set or has been cancelled.
     * 
     * @param v the value
     */
    protected void set(V v) {
        sync.innerSet(v);
    }

    /**
     * Causes this future to report an <tt>ExecutionException</tt> with the
     * given throwable as its cause, unless this Future has already been set or
     * has been cancelled.
     * 
     * @param t the cause of failure.
     */
    protected void setException(Throwable t) {
        sync.innerSetException(t);
    }

    /**
     * Sets this Future to the result of computation unless it has been
     * cancelled.
     */
    public void run() {
        sync.innerRun();
    }

    /**
     * Synchronization control for FutureTask. Note that this must be a
     * non-static inner class in order to invoke the protected <tt>done</tt>
     * method. For clarity, all inner class support methods are same as outer,
     * prefixed with "inner". Uses AQS sync state to represent run status
     */
    private final class Sync extends AbstractQueuedSynchronizer {
        /** State value representing that task is running */
        private static final int RUNNING = 1;
        /** State value representing that task ran */
        private static final int RAN = 2;
        /** State value representing that task was cancelled */
        private static final int CANCELLED = 4;

        /** The underlying callable */
        private final Callable<V> callable;
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

        private final Lock lock = new ReentrantLock();

        Sync(Callable<V> callable) {
            this.callable = callable;
        }

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

        void checkState() {
            int s = getState();
            if (ranOrCancelled(s)) {
                lock.lock();
                try {
                    if (innerIsCancelled()) {
                        doneException(new InterruptedIOException("operation was cancelled"));
                    } else if (exception != null)
                        doneException(exception);
                    else {
                        doneSet(result);
                    }
                } finally {
                    lock.unlock();
                }
            }

        }

        void innerSet(V v) {
            for (;;) {
                int s = getState();
                if (ranOrCancelled(s))
                    return;
                if (compareAndSetState(s, RAN))
                    break;
            }
            result = v;
            releaseShared(0);
            doneSet(v);
        }

        void innerSetException(Throwable t) {
            for (;;) {
                int s = getState();
                if (ranOrCancelled(s))
                    return;
                if (compareAndSetState(s, RAN))
                    break;
            }
            exception = t;
            result = null;
            releaseShared(0);
            checkState();
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
            checkState();
            return true;
        }

        void innerRun() {
            if (!compareAndSetState(0, RUNNING))
                return;
            try {
                runner = Thread.currentThread();
                innerSet(callable.call());
            } catch (Throwable ex) {
                innerSetException(ex);
            }
        }

        boolean innerRunAndReset() {
            if (!compareAndSetState(0, RUNNING))
                return false;
            try {
                runner = Thread.currentThread();
                callable.call(); // don't set result
                runner = null;
                return compareAndSetState(RUNNING, 0);
            } catch (Throwable ex) {
                innerSetException(ex);
                return false;
            }
        }
    }

    private interface ColoredRunnable extends Colored, Runnable {

    }
}

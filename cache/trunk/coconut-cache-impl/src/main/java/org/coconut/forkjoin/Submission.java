/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package org.coconut.forkjoin;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Adapter class to allow tasks submitted to a pool to act as Futures.
 * Methods are implemented in the same way as in the RecursiveTask
 * class, but with extra bookkeeping and signalling to cover three
 * kinds of adaptation:
 *
 * (1) Unlike internal fork/join processing, get() must block if the
 * caller is a normal thread (not FJ worker thread). We use a simpler
 * variant of the mechanics used in FutureTask, but bypass them and
 * use helping joins if the caller is itself a ForkJoinWorkerThread.
 *
 * (2) Regular Futures encase RuntimeExceptions within
 * ExecutionExeptions, while internal tasks just throw them directly,
 * so these must be trapped and wrapped.
 *
 * (3) External submissions are tracked for the sake of managing
 * worker threads. The pool submissionStarting and submissionCompleted
 * methods perform the associated bookkeeping. This requires some care
 * with cancellation and early termination -- the completion signal
 * can be issued only if a start signal ever was.
 *
 */
final class Submission<V> extends ForkJoinTask<V> implements Future<V> {
    
    // Status values for sync. We need to keep track of RUNNING status
    // just to make sure callbacks to pool are balanced.
    static final int INITIAL = 0;
    static final int RUNNING = 1;
    static final int DONE    = 2;

    /**
     * Stripped-down variant of FutureTask.sync
     */
    static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        public int tryAcquireShared(int acquires) {
            return getState() == DONE? 1 : -1;
        }

        public boolean tryReleaseShared(int releases) { return true; }
        public void reset() { setState(INITIAL); }
        public boolean isDone() { return getState() == DONE; }

        public boolean transitionToRunning() {
            return compareAndSetState(INITIAL, RUNNING);
        }

        /** Set status to DONE, release waiters, and return old state */
        public int transitionToDone() {
            for (;;) {
                int c = getState();
                if (c == DONE || compareAndSetState(c, DONE)) {
                    releaseShared(0);
                    return c;
                }
            }
        }
    }

    private final ForkJoinTask<V> task;
    private final ForkJoinPool pool;
    private final Sync sync;
    private volatile V result;

    Submission(ForkJoinTask<V> t, ForkJoinPool p) {
        t.setStolen(); // All submitted tasks treated as stolen
        task = t;
        pool = p;
        sync = new Sync();
    }

    /**
     * Transition sync and notify pool.that task finished, only if it
     * was initially notified that task started.
     */
    private void complete() {
        if (sync.transitionToDone() == RUNNING)
            pool.submissionCompleted();
    }
    
    protected V compute() {
        try {
            V ret = null;
            if (sync.transitionToRunning()) {
                pool.submissionStarting();
                ret = task.forkJoin();
            } // else was cancelled, so result doesn't matter
            return ret; 
        } finally {
            complete();
        }
    }

    /**
     * ForkJoinTask version of cancel
     */
    public void cancel() {
        try {
            // Don't bother trying to cancel if already done
            if (getException() == null && !sync.isDone()) {
                // avoid recursive call to cancel
                setDoneExceptionally(new CancellationException());
                task.cancel();
            }
        } finally {
            complete();
        }
    }

    /**
     * Future version of cancel
     */
    public boolean cancel(boolean ignore) {
        this.cancel();
        return isCancelled();
    }

    public V get() throws InterruptedException, ExecutionException {
        // If caller is FJ worker, help instead of block, but fall
        // through.to acquire, to preserve Submission sync guarantees
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread)
            quietlyJoin(); 
        sync.acquireSharedInterruptibly(1);
        return task.reportAsFutureResult();
    }

    public V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        long nanos = unit.toNanos(timeout);
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            if(!((ForkJoinWorkerThread)t).doTimedJoinTask(this, nanos))
                throw new TimeoutException();
            //  Preserve Submission sync guarantees
            sync.acquireSharedInterruptibly(1);
        }
        else if (!sync.tryAcquireSharedNanos(1, nanos))
            throw new TimeoutException();
        return task.reportAsFutureResult();
    }

    /**
     * Interrupt-less get for ForkJoinPool.invoke
     */
    public V awaitInvoke() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread)
            quietlyJoin(); 
        sync.acquireShared(1);
        return task.reportAsForkJoinResult();
    }

    public void finish(V result) { 
        try {
            this.result = result;
            setDone();
        } finally {
            complete();
        }
    }

    public void finishExceptionally(Throwable ex) {
        try {
            setDoneExceptionally(ex);
            task.setDoneExceptionally(ex);
        } finally {
            complete();
        }
    }

    public V forkJoin() {
        V v = null;
        if (exception == null) {
            try {
                result = v = compute();
            } catch(Throwable rex) {
                finishExceptionally(rex);
            }
        }
        Throwable ex = setDone();
        if (ex != null)
            rethrowException(ex);
        return v;
    }

    public Throwable exec() {
        if (exception == null) {
            try {
                result = compute();
            } catch(Throwable rex) {
                return setDoneExceptionally(rex);
            }
        }
        return setDone();
    }

    public V rawResult() { 
        return result; 
    }

    public void reinitialize() { // Of dubious value.
        result = null;
        sync.reset();
        super.reinitialize();
    }

}



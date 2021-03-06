/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package jsr166y.forkjoin;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import sun.misc.Unsafe;
import java.lang.reflect.*;

/**
 * A thread that is internally managed by a ForkJoinPool to execute
 * ForkJoinTasks. This class additionally provides public
 * <tt>static</tt> methods accessing some basic scheduling and
 * execution mechanics for the <em>current</em>
 * ForkJoinWorkerThread. These methods may be invoked only from within
 * other ForkJoinTask computations. Attempts to invoke in other
 * contexts result in exceptions or errors including
 * ClassCastException.  These methods enable construction of
 * special-purpose task classes, as well as specialized idioms
 * occasionally useful in ForkJoinTask processing.
 *
 * <p>The form of supported static methods reflects the fact that
 * worker threads may access and process tasks obtained in any of
 * three ways. In preference order: <em>Local</em> tasks are processed
 * in LIFO (newest first) order. <em>Stolen</em> tasks are obtained
 * from other threads in FIFO (oldest first) order, only if there are
 * no local tasks to run.  <em>Submissions</em> form a FIFO queue
 * common to the entire pool, and are started only if no other
 * work is available.
 *
 * <p> This class is subclassable solely for the sake of adding
 * functionality -- there are no overridable methods dealing with
 * scheduling or execution. However, you can override initialization
 * and termination cleanup methods surrounding the main task
 * processing loop.  If you do create such a subclass, you will also
 * need to supply a custom ForkJoinWorkerThreadFactory to use it in a
 * ForkJoinPool.
 *
 */
public class ForkJoinWorkerThread extends Thread {
    /**
     * The pool this thread works in.
     */
    final ForkJoinPool pool;

    /**
     * Pool-wide sync barrier, cached from pool upon construction
     */
    private final PoolBarrier poolBarrier;

    /**
     * Pool-wide count of active workers, cached from pool upon
     * construction
     */
    private final AtomicInteger activeWorkerCounter;

    /**
     * Run state of this worker, set only by pool (except for termination)
     */
    private final RunState runState;

    /**
     * Each thread's work-stealing queue is represented via the
     * resizable "queue" array plus base and sp indices.
     * Work-stealing queues are special forms of Deques that support
     * only three of the four possible end-operations -- push, pop,
     * and steal (aka dequeue), and only do so under the constraints
     * that push and pop are called only from the owning thread, while
     * steal may be called from other threads.  The work-stealing
     * queue here uses a variant of the algorithm described in
     * "Dynamic Circular Work-Stealing Deque" by David Chase and Yossi
     * Lev, SPAA 2005. For an explanation, read the paper.
     * (http://research.sun.com/scalable/pubs/index.html). The main
     * differences here stem from ensuring that queue slots
     * referencing popped and stolen tasks are cleared, as well as
     * spin/wait control. (Also. method and variable names differ.)
     *
     * The length of the queue array must always be a power of
     * two. Even though these queues don't usually become all that
     * big, the initial size must be large enough to counteract cache
     * contention effects across multiple queues.  Currently, they are
     * initialized upon construction.  However, all queue-related
     * methods except pushTask are written in a way that allows them
     * to instead be lazily allocated and/or disposed of when empty.
     */
    private ForkJoinTask<?>[] queue;

    private static final int INITIAL_CAPACITY = 1 << 13;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * Index (mod queue.length) of least valid queue slot, which is
     * always the next position to steal from if nonempty.  Updated
     * only via casBase().
     */
    private volatile long base;

    /**
     * Index (mod queue.length-1) of next queue slot to push to
     * or pop from
     */
    private volatile long sp;

    /**
     * Number of steals, just for monitoring purposes, 
     */
    private volatile long fullStealCount;

    /**
     * The last event count waited for
     */
    private long eventCount;

    /**
     * Number of steals, transferred to fullStealCount at syncs
     */
    private int stealCount; 

    /**
     * Cached from pool
     */
    private int poolSize;

    /**
     * Index of this worker in pool array. Set once by pool before running.
     */
    private int poolIndex;

    /**
     * Seed for random number generator for choosing steal victims
     */
    private int randomVictimSeed;

    /**
     * Number of scans (calls to getTask() or variants) since last
     * successfully getting a task.  Always zero (== "active" status)
     * when busy executing tasks.  Incremented after a failed scan to
     * help control spins and maintain active worker count. On
     * transition from or to sero, the pool's activeWorkerCounter must
     * be adjusted.  Must be zero when executing tasks, and BEFORE
     * stealing a submission.  To avoid continual flickering and
     * contention, this is done only if worker queues appear to be
     * non-empty.
     */
    private int scans;

    /**
     * Seed for juRandom. Kept with worker fields to minimize
     * cacheline sharing
     */
    long juRandomSeed;

    /**
     * Exported random numbers
     */
    final JURandom juRandom;

    // Transient state indicators, used as argument, not field values.

    /**
     * Signifies that the current thread is not currently executing
     * any task. Used only from mainLoop.
     */
    private static final int IDLING   = 0;

    /**
     * Signifies that the current thread is helping while waiting for
     * another task to be joined.
     */
    private static final int JOINING  = 1;

    /**
     * Signifies that the current thread is willing to run a task, but
     * cannot block if none are available.
     */
    private static final int POLLING  = 2;

    /**
     * Threshold for checking with PoolBarrier and possibly blocking
     * when idling threads cannot find work. This value must be at
     * least 3 for scan control logic to work.
     * 
     * Scans for work (see getTask()) are nearly read-only, and likely
     * to be much faster than context switches that may occur within
     * syncs. So there's no reason to make this value especially
     * small. On the other hand, useless scans waste CPU time, and
     * syncs may help wake up other threads that may allow further
     * progress. So the value should not be especially large either.
     */
    private static final int IDLING_SCANS_PER_SYNC = 32; 
    
    /**
     * Threshold for checking with PoolBarrier and possibly blocking
     * when joining threads cannot find work. This value must be at
     * least IDLING_SCANS_PER_SYNC.
     *
     * Unlike idling threads, joining threads "know" that they will be
     * able to continue at some point. However, we still force them to
     * sometimes sync to reduce wasted CPU time in the face of
     * programmer errors as well as to cope better when some worker
     * threads are making very slow progress.
     */
    private static final int JOINING_SCANS_PER_SYNC = 1024; 


    /**
     * Generator for per-thread randomVictimSeeds
     */
    private static final Random randomSeedGenerator = new Random();

    // Methods called only by pool

    final void setWorkerPoolIndex(int i) { 
        poolIndex = i;
    }

    final int getWorkerPoolIndex() {
        return poolIndex;
    }

    final void setPoolSize(int ps) {
        poolSize = ps;
    }

    final long getWorkerStealCount() {
        return fullStealCount;
    }

    final RunState getRunState() {
        return runState;
    }

    final int getQueueSize() {
        long n = sp - base;
        return n < 0? 0 : (int)n; // suppress momentarily negative values
    }

    /**
     * Reports true if this thread has no tasks and has unsuccessfully
     * tried to obtain a task to run.
     */
    final boolean workerIsIdle() {
        return  base >= sp && scans != 0; // ensure volatile read first
    }

    // Construction and lifecycle methods

    /**
     * Creates a ForkJoinWorkerThread operating in the given pool.
     * @param pool the pool this thread works in
     * @throws NullPointerException if pool is null;
     */
    protected ForkJoinWorkerThread(ForkJoinPool pool) {
        if (pool == null)
            throw new NullPointerException();
        this.pool = pool;
        this.activeWorkerCounter = pool.getActiveWorkerCounter();
        this.poolBarrier = pool.getPoolBarrier();
        this.poolSize = pool.getPoolSize();
        this.juRandomSeed = randomSeedGenerator.nextLong();
        int rseed = randomSeedGenerator.nextInt();
        this.randomVictimSeed = (rseed == 0)? 1 : rseed; // must be nonzero
        this.juRandom = new JURandom();
        this.runState = new RunState();
        this.queue = new ForkJoinTask<?>[INITIAL_CAPACITY];
    }

    /**
     * This method is required to be public, but should never be
     * called explicitly. It executes the main run loop to execute
     * ForkJoinTasks.
     */
    public void run() {
        Throwable exception = null;
        try {
            onStart();
            while (runState.isRunning()) {
                ForkJoinTask<?> t = getTask();
                if (t == null)
                    onEmptyScan(IDLING);
                else
                    t.exec();
            }
            clearLocalTasks();
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            onTermination(exception);
        }
    }

    /**
     * Initializes internal state after construction but before
     * processing any tasks. If you override this method, you must
     * invoke super.onStart() at the beginning of the method.
     * Initialization requires care: Most fields must have legal
     * default values, to ensure that attempted accesses from other
     * threads work correctly even before this thread starts
     * processing tasks.
     */
    protected void onStart() {
    }

    /**
     * Perform cleanup associated with termination of this worker
     * thread.  If you override this method, you must invoke
     * super.onTermination at the end of the overridden method.
     * 
     * @param exception the exception causing this thread to abort due
     * to an unrecoverable error, or null if completed normally.
     */
    protected void onTermination(Throwable exception) {
        try {
            if (scans == 0) {
                scans = 1;
                activeWorkerCounter.decrementAndGet();
            }
            cancelTasks();
            runState.transitionToTerminated();
        } finally {
            pool.workerTerminated(this, exception);
        }
    }

    /**
     * Primary method for getting a task to run.  Tries local, then
     * stolen, then submitted tasks.
     * @return a task or null if none
     */
    private ForkJoinTask<?> getTask() {
        ForkJoinTask<?> popped, stolen;
        if ((popped = popTask()) != null)
            return popped;
        else if ((stolen = getStolenTask()) != null)
            return stolen;
        else
            return getSubmission();
    }

    /**
     * Actions to take on failed getTask, depending on calling
     * context.
     * @param context: IDLING, JOINING, or POLLING
     */
    private void onEmptyScan(int context) {
        int s = scans;
        if (s == 0) { // inactivate
            scans = 1;
            activeWorkerCounter.decrementAndGet();
        }
        else if (s <= IDLING_SCANS_PER_SYNC ||
                 (s <= JOINING_SCANS_PER_SYNC && context == JOINING))
            scans = s + 1;
        else {
            scans = 1; // reset scans to 1 (not 0) on resumption
            // transfer steal counts so pool can read
            int sc = stealCount; 
            if (sc != 0) {
                stealCount = 0;
                if (sc < 0)
                    sc = Integer.MAX_VALUE; // wraparound
                fullStealCount += sc;
            }

            if (runState.isAtLeastStopping()) { 
                // abort if joining; else return so caller can check state
                if (context == JOINING) 
                    throw new CancellationException(); 
            }
            else if (context == POLLING)
                Thread.yield();
            else
                eventCount = poolBarrier.sync(this, eventCount);
        }
    }

    // Main work-stealing queue methods

    /*
     * Resizes queue
     */
    private void growQueue() {
        ForkJoinTask<?>[] oldQ = queue;
        int oldSize = oldQ.length;
        int newSize = oldSize << 1;
        if (newSize > MAXIMUM_CAPACITY)
            throw new RejectedExecutionException("Queue capacity exceeded");
        ForkJoinTask<?>[] newQ = new ForkJoinTask<?>[newSize];
        int oldMask = oldSize - 1;
        int newMask = newSize - 1;
        long s = sp;
        for (long i = base; i < s; ++i)
            newQ[((int)i) & newMask] = oldQ[((int)i) & oldMask];
        sp = s; // need volatile write here just to force ordering
        queue = newQ;
    }

    /**
     * Pushes a task. Also wakes up other workers if queue was
     * previously empty.  Called only by current thread.
     * @param x the task
     */
    final void pushTask(ForkJoinTask<?> x) {
        long s = sp;
        ForkJoinTask<?>[] q = queue;
        int mask = q.length - 1;
        q[((int)s) & mask] = x;
        sp = s + 1;
        if ((s -= base) >= mask - 1)
            growQueue();
        else if (s <= 0)
            poolBarrier.signal();
    }

    /**
     * Returns a popped task, or null if empty.  Called only by
     * current thread.
     */
    private ForkJoinTask<?> popTask() {
        ForkJoinTask<?> x = null;
        long s = sp - 1;
        ForkJoinTask<?>[] q = queue;
        if (q != null) {
            int idx = ((int)s) & (q.length-1);
            x = q[idx];
            sp = s;
            q[idx] = null; 
            long b = base;
            if (s <= b) {
                if (s < b || !casBase(b, ++b)) // note ++b side effect
                    x = null;  // lost race vs steal
                sp = b;
            }
        }
        return x;
    }

    /**
     * Specialized version of popTask to pop only if
     * topmost element is the given task.
     * @param task the task to match, null OK (but never matched)
     */
    private boolean popIfNext(ForkJoinTask<?> task) {
        if (task != null) {
            long s = sp - 1;
            ForkJoinTask<?>[] q = queue;
            if (q != null) { 
                int idx = ((int)s) & (q.length-1);
                if (q[idx] == task) {
                    sp = s;
                    q[idx] = null;
                    long b = base;
                    if (s > b)
                        return true;
                    if (s == b && casBase(b, ++b)) {
                        sp = b;
                        return true;
                    }
                    sp = b;
                }
            }
        }
        return false;
    }
    
    /**
     * Tries to take a task from the base of the queue.  Returns null
     * upon contention.
     * @return a task, or null if none
     */
    private ForkJoinTask<?> tryStealTask() {
        ForkJoinTask<?>[] q;
        long b = base;
        if (b < sp && (q = queue) != null) {
            int k = ((int)b) & (q.length-1);
            ForkJoinTask<?> t = q[k];
            if (t != null && casBase(b, b+1)) {
                clearSlot(q, k, t);
                t.setStolen();
                return t;
            }
        }
        return null;
    }

    /**
     * Tries to find a worker with a non-empty queue.  Starts at a
     * random index of workers array, and probes workers until finding
     * one with non-empty queue or finding that all are empty.  This
     * doesn't require a very high quality generator, but also not a
     * crummy one.  It uses Marsaglia xorshift to generate first n
     * probes. If these are empty, it resorts to full incremental
     * circular traversal.
     * @return a worker with (currently) non-empty queue, or null if none
     */
    private ForkJoinWorkerThread randomVictim() {
        int r = randomVictimSeed;
        ForkJoinWorkerThread victim = null;
        ForkJoinWorkerThread[] ws = pool.getWorkers();
        if (ws != null) {
            int n = ws.length;
            int remaining = n << 1;
            int idx = 0;
            do {
                if (remaining-- >= n) {
                    // avoid % for power of 2
                    idx = (n & (n-1)) == 0? (r & (n-1)) : ((r >>> 1) % n);
                    r ^= r <<   1;
                    r ^= r >>>  3;
                    r ^= r <<  10;
                }
                else if (++idx >= n)
                    idx = 0;
                ForkJoinWorkerThread v = ws[idx];
                if (v != null && v.base < v.sp) {
                    victim = v;
                    break;
                }
            } while (remaining >= 0);
        }
        randomVictimSeed = r;
        return victim;
    }

    /**
     * Tries to steal a task from a random worker.  
     * @return a task, or null if none
     */
    private ForkJoinTask<?> getStolenTask() {
        ForkJoinWorkerThread v;
        while ((v = randomVictim()) != null) {
            if (scans != 0) {
                // Rescan if taken while trying to activate.
                AtomicInteger awc = activeWorkerCounter;
                do {
                    int c = awc.get();
                    if (awc.compareAndSet(c, c+1)) {
                        scans = 0;
                        break;
                    }
                } while (v.base < v.sp);
            }
            if (scans == 0) {
                ForkJoinTask<?> t = v.tryStealTask();
                if (t != null) {
                    ++stealCount;
                    return t;
                }
            }
        }
        return null;
    }

    // misc utilities

    /**
     * Tries to get a submission. Same basic logic as getStolenTask
     * @return a task, or null if none
     */
    private ForkJoinTask<?> getSubmission() {
        ForkJoinPool.SubmissionQueue sq = pool.getSubmissionQueue();
        while (sq.isApparentlyNonEmpty()) {
            if (scans != 0) {
                AtomicInteger awc = activeWorkerCounter;
                int c = awc.get();
                if (awc.compareAndSet(c, c+1)) 
                    scans = 0;
            }
            if (scans == 0) {
                ForkJoinTask<?> t = sq.poll();
                if (t != null)
                    return t;
            }
        }
        return null;
    }

    /**
     * Returns next task to pop. Called only by current thread.
     */
    private ForkJoinTask<?> peekTask() {
        ForkJoinTask<?>[] q = queue;
        long s = sp - 1;
        if (q == null || s <= base)
            return null;
        else
            return q[((int)s) & (q.length-1)];
    }

    /**
     * Run or cancel all local tasks on exit from main.  Exceptions
     * will cause aborts that will eventually trigger cancelTasks.
     */
    private void clearLocalTasks() {
        for (;;) {
            ForkJoinTask<?> t = popTask();
            if (t == null)
                break;
            if (runState.isAtLeastStopping())
                t.cancel();
            else
                t.exec();
        }
    }

    /*
     * Remove (via steal) and cancel all tasks in queue.  Can be
     * called from any thread.
     */
    final void cancelTasks() {
        while (base < sp) {
            ForkJoinTask<?> t = tryStealTask();
            if (t != null) // avoid exceptions due to cancel()
                t.setDoneExceptionally(new CancellationException());
        }
    }

    // Public methods on current thread
    
    /**
     * Returns the pool hosting the current task execution.
     * @return the pool
     */
    public static ForkJoinPool getPool() {
        return ((ForkJoinWorkerThread)(Thread.currentThread())).pool;
    }

    /**
     * Returns the index number of the current worker thread in its
     * pool.  The return value is in the range
     * <tt>0...getPool().getPoolSize()-1</tt>.  This method may be
     * useful for applications that track status or collect results
     * per-worker rather than per-task.
     * @return the index number.
     */
    public static int getPoolIndex() {
        return ((ForkJoinWorkerThread)(Thread.currentThread())).poolIndex;
    }

    /**
     * Returns the number of tasks waiting to be run by the current
     * worker thread. This value may be useful for heuristic decisions
     * about whether to fork other tasks.
     * @return the number of tasks
     */
    public static int getLocalQueueSize() {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        long n = w.sp - w.base;
        return n < 0? 0 : (int)n;
    }

    /**
     * Returns an estimate of how many more locally queued tasks there
     * are than idle worker threads that might steal them.  This value
     * may be useful for heuristic decisions about whether to fork
     * other tasks.
     * @return the number of tasks, which is negative if there are
     * fewer tasks than idle workers
     */
    public static int getEstimatedSurplusTaskCount() {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        return (int)(w.sp - w.base) - 
            (w.poolSize - w.activeWorkerCounter.get());
    }

    /**
     * Returns, but does not remove or execute, the next task locally
     * queued for execution by the current worker thread. There is no
     * guarantee that this task will be the next one actually returned
     * or executed from other polling or execution methods.
     * @return the next task or null if none
     */
    public static ForkJoinTask<?> peekLocalTask() {
        return ((ForkJoinWorkerThread)(Thread.currentThread())).peekTask();
    }

    /**
     * Removes and returns, without executing, the next task queued
     * for execution in the current worker thread's local queue.
     * @return the next task to execute, or null if none
     */
    public static ForkJoinTask<?> pollLocalTask() {
        return ((ForkJoinWorkerThread)(Thread.currentThread())).popTask();
    }

    /**
     * Removes and returns, without executing, the given task from the
     * queue hosting current execution only if it would be the next
     * task executed by the current worker.  Among other usages, this
     * method may be used to bypass task execution during
     * cancellation.
     * @param task the task
     * @return true if removed
     */
    public static boolean removeIfNextLocalTask(ForkJoinTask<?> task) {
        return ((ForkJoinWorkerThread)(Thread.currentThread())).popIfNext(task);
    }

    /**
     * Execute the next task locally queued by the current worker, if
     * one is available.
     * @return true if a task was run; a false return indicates
     * that no task was available.
     */
    public static boolean executeLocalTask() {
        ForkJoinTask<?> t = 
            ((ForkJoinWorkerThread)(Thread.currentThread())).popTask();
        if (t == null) 
            return false;
        t.exec();
        return true;
    }

    /**
     * Removes and returns, without executing, the next task available
     * for execution by the current worker thread, which may be a
     * locally queued task, one stolen from another worker, or a pool
     * submission.
     * @return the next task to execute, or null if none
     */
    public static ForkJoinTask<?> pollTask() {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        ForkJoinTask<?> t = w.getTask();
        if (t == null)
            w.onEmptyScan(POLLING);
        return t;
    }

    /**
     * Helps this program complete by processing a local, stolen or
     * submitted task, if one is available.  This method may be useful
     * when several tasks are forked, and only one of them must be
     * joined, as in:
     * <pre>
     *   while (!t1.isDone() &amp;&amp; !t2.isDone()) 
     *     ForkJoinWorkerThread.executeTask();
     * </pre> 
     * Similarly, you can help process tasks until all computations
     * complete via 
     * <pre>
     *   while(ForkJoinWorkerThread.executeTask() || 
     *         !ForkJoinWorkerThread.getPool().isQuiescent()) 
     *      ;
     * </pre>
     *
     * @return true if a task was run; a false return indicates
     * that no task was available.
     */
    public static boolean executeTask() {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        ForkJoinTask<?> t = w.getTask();
        if (t == null) {
            w.onEmptyScan(POLLING);
            return false;
        }
        t.exec();
        return true;
    }

    // per-worker exported random numbers

    /**
     * A workalike for java.util.Random, but specialized 
     * for exporting to users of worker threads.
     */
    final class JURandom { // non-static, use worker seed
        // Guarantee same constants as java.util.Random
        final static long Multiplier = 0x5DEECE66DL;
        final static long Addend = 0xBL;
        final static long Mask = (1L << 48) - 1;
        
        int next(int bits) {
            long next = (juRandomSeed * Multiplier + Addend) & Mask;
            juRandomSeed = next;
            return (int)(next >>> (48 - bits));
        }

        int nextInt() {
            return next(32);
        }
        
        int nextInt(int n) {
            if (n <= 0)
                throw new IllegalArgumentException("n must be positive");
            int bits = next(31);
            if ((n & -n) == n) 
                return (int)((n * (long)bits) >> 31);
            
            for (;;) {
                int val = bits % n;
                if (bits - val + (n-1) >= 0)
                    return val;
                bits = next(31);
            }
        }

        long nextLong() {
            return ((long)(next(32)) << 32) + next(32);
        }

        
        long nextLong(long n) {
            if (n <= 0)
                throw new IllegalArgumentException("n must be positive");
            long offset = 0;
            while (n >= Integer.MAX_VALUE) { // randomly pick half range
                int bits = next(2); // 2nd bit for odd vs even split
                long half = n >>> 1;
                long nextn = ((bits & 2) == 0)? half : n - half;
                if ((bits & 1) == 0)
                    offset += n - nextn;
                n = nextn;
            }
            return offset + nextInt((int)n);
        }
        
        
        double nextDouble() {
            return (((long)(next(26)) << 27) + next(27))
                / (double)(1L << 53);
        }        
    }

    /**
     * Returns a random integer using a per-worker random
     * number generator with the same properties as
     * {@link java.util.Random#nextInt}
     * @return the next pseudorandom, uniformly distributed {@code int}
     *         value from this worker's random number generator's sequence
     */
    public static int nextRandomInt() {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        return w.juRandom.nextInt();
    }

    /**
     * Returns a random integer using a per-worker random
     * number generator with the same properties as
     * {@link java.util.Random#nextInt(int)}
     * @param n the bound on the random number to be returned.  Must be
     *	      positive.
     * @return the next pseudorandom, uniformly distributed {@code int}
     *         value between {@code 0} (inclusive) and {@code n} (exclusive)
     *         from this worker's random number generator's sequence
     * @throws IllegalArgumentException if n is not positive
     */
    public static int nextRandomInt(int n) {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        return w.juRandom.nextInt(n);
    }

    /**
     * Returns a random long using a per-worker random
     * number generator with the same properties as
     * {@link java.util.Random#nextLong}
     * @return the next pseudorandom, uniformly distributed {@code long}
     *         value from this worker's random number generator's sequence
     */
    public static long nextRandomLong() {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        return w.juRandom.nextLong();
    }

    /**
     * Returns a random integer using a per-worker random
     * number generator with the same properties as
     * {@link java.util.Random#nextInt(int)}
     * @param n the bound on the random number to be returned.  Must be
     *	      positive.
     * @return the next pseudorandom, uniformly distributed {@code int}
     *         value between {@code 0} (inclusive) and {@code n} (exclusive)
     *         from this worker's random number generator's sequence
     * @throws IllegalArgumentException if n is not positive
     */
    public static long nextRandomLong(long n) {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        return w.juRandom.nextLong(n);
    }

    /**
     * Returns a random double using a per-worker random
     * number generator with the same properties as
     * {@link java.util.Random#nextDouble}
     * @return the next pseudorandom, uniformly distributed {@code double}
     *         value between {@code 0.0} and {@code 1.0} from this
     *         worker's random number generator's sequence
     */
    public static double nextRandomDouble() {
        ForkJoinWorkerThread w = 
            (ForkJoinWorkerThread)(Thread.currentThread());
        return w.juRandom.nextDouble();
    }

    // Package-local support for core ForkJoinTask methods

    /**
     * Called only from ForkJoinTask, upon completion
     * of stolen or cancelled task. 
     */
    static void signalTaskCompletion() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread)
            ((ForkJoinWorkerThread)t).poolBarrier.signal();
        // else external cancel -- OK not to signal
    }

    /**
     * Implements ForkJoinTask.join
     */
    final <T> T doJoinTask(ForkJoinTask<T> joinMe) {
        for (;;) {
            Throwable ex = joinMe.exception;
            if (ex != null)
                ForkJoinTask.rethrowException(ex);
            if (joinMe.status < 0)
                return joinMe.rawResult();
            ForkJoinTask<?> t;
            if ((t = getTask()) == null)
                onEmptyScan(JOINING);
            else
                t.exec();
        }
    }

    /**
     * Implements ForkJoinTask.quietlyJoin
     */
    final Throwable doQuietlyJoinTask(ForkJoinTask<?> joinMe) {
        for (;;) {
            Throwable ex = joinMe.exception;
            if (ex != null)
                return ex;
            if (joinMe.status < 0)
                return null;
            ForkJoinTask<?> t;
            if ((t = getTask()) == null)
                onEmptyScan(JOINING);
            else
                t.exec();
        }
    }

    /**
     * Timeout version of join for Submissions.
     * Returns false if timed out before complated
     */
    final boolean doTimedJoinTask(ForkJoinTask<?> joinMe, long nanos) {
        long startTime = System.nanoTime();
        for (;;) {
            if (joinMe.exception != null || joinMe.status < 0)
                return true;
            if (nanos - (System.nanoTime() - startTime) <= 0)
                return false;
            ForkJoinTask<?> t;
            if ((t = getTask()) == null)
                onEmptyScan(JOINING);
            else
                t.exec();
        }
    }
    
    final void doForkJoin(RecursiveAction t1, RecursiveAction t2) {
        int touch = t1.status + t2.status; // force null pointer check
        pushTask(t2);
        Throwable ex1 = t1.exec();
        Throwable ex2 = popIfNext(t2)? t2.exec() : doQuietlyJoinTask(t2);
        if (ex2 != null)
            ForkJoinTask.rethrowException(ex2);
        else if (ex1 != null)
            ForkJoinTask.rethrowException(ex1);
    }


    // Temporary Unsafe mechanics for preliminary release
 
    private static Unsafe getUnsafe() {
        try {
            if (ForkJoinWorkerThread.class.getClassLoader() != null) {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                return (Unsafe)f.get(null);
            }
            else
                return Unsafe.getUnsafe();
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize intrinsics",
                                       e);
        }
    }

    private static final Unsafe _unsafe = getUnsafe();
    private static final long baseOffset;
    private static final long arrayBase;
    private static final int arrayShift;
    
    private static int computeArrayShift() {
        int s = _unsafe.arrayIndexScale(ForkJoinTask[].class);
        if ((s & (s-1)) != 0)
            throw new Error("data type scale not a power of two");
        return 31 - Integer.numberOfLeadingZeros(s);
    }

    static {
        try {
            baseOffset = _unsafe.objectFieldOffset
                (ForkJoinWorkerThread.class.getDeclaredField("base"));
            arrayBase = _unsafe.arrayBaseOffset(ForkJoinTask[].class);
            arrayShift = computeArrayShift();
        } catch (Exception ex) { throw new Error(ex); }
    }
    
    private final boolean casBase(long cmp, long val) {
        return _unsafe.compareAndSwapLong(this, baseOffset, cmp, val);
    }
    
    private static final void clearSlot(ForkJoinTask[] array, int i, 
                                        ForkJoinTask expect) {
        _unsafe.compareAndSwapObject(array, 
                                     arrayBase + ((long)i << arrayShift),
                                     expect, null);
    }
}

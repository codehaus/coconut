/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.coconut.core.Clock;
import org.coconut.core.Transformer;
import org.coconut.event.seda.spi.AbstractManagedStage;
import org.coconut.event.seda.spi.AbstractManagedStageManager;
import org.coconut.event.seda.spi.ThreadWorker;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class LinearPipeline extends AbstractManagedStageManager {

    static int threads = 160;

    private final static Clock clock = Clock.NANO_CLOCK;

    private StageImpl[] stages; // guarded by mainLock

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    private final BlockingQueue initial;

    /**
     * Creates a new simple pipeline.
     * 
     * @param c
     * @param t
     */
    public LinearPipeline(Transformer... t) {
        this(false, t);
    }

    public LinearPipeline(boolean useInitialQueue, Transformer... t) {
        if (t == null) {
            throw new NullPointerException("t is null");
        }
        if (t.length > 0) {
            stages = new StageImpl[t.length];
            stages[0] = new StageImpl(0, "Producer", t[0]);
            for (int i = 1; i < t.length; i++) {
                stages[i] = new StageImpl(i, "Stage-" + i, t[i]);
            }
        }
        initial = useInitialQueue ? new LinkedBlockingQueue() : null;
    }

    public BlockingQueue<?> getInitialQueue() {
        return initial;
    }

    public Stage addStage(String name, Transformer t) {
        Lock mainLock = getMainLock();
        mainLock.lock();
        try {
            if (getState() != RunState.NEW) {
                throw new IllegalStateException(
                        "Manager already started, stages must be added before start");
            }
            StageImpl[] old = stages;
            stages = new StageImpl[stages.length + 1];
            System.arraycopy(old, 0, stages, 0, old.length);
            stages[stages.length - 1] = new StageImpl(stages.length, name, t);
            return stages[stages.length - 1];
        } finally {
            mainLock.unlock();
        }
    }

    protected boolean stopWithNull() {
        return true;
    }

    protected EventContext produce() {
        EventContext o = stages[0].processc(new EventContext(
                new long[stages.length]));
        return o;
    }

    @Override
    public void start() {
        Lock mainLock = getMainLock();
        mainLock.lock();
        try {
            if (getState() == RunState.NEW) {
                super.start();
                for (int i = 0; i < threads; i++) {
                    addThreadWorker(getDefaultThreadFactory()).getThread()
                            .start();
                }
            } else if (getState() != RunState.RUNNING) {
                throw new IllegalStateException(
                        "Manager terminated started, cannot restart manager");
            }
        } finally {
            mainLock.unlock();
        }

    }

    /**
     * @see org.coconut.event.sedaold.StageManager#getStages()
     */
    public List<? extends AbstractManagedStage> getStages() {
        return Arrays.asList(stages);
    }

    /**
     * @see org.coconut.event.sedaold.StageManager#lock()
     */
    public Lock lock() {
        return rwLock.writeLock();
    }

    /**
     * @see org.coconut.event.sedaold.defaults.SameThreadStageManager#getTask()
     */
    @Override
    protected Runnable getTask(ThreadWorker w) {
        if (isShutdown()) {
            return null;
        }
        Runnable r = new Runnable() {
            public void run() {
                while (!isShutdown()) {
                    rwLock.readLock().lock();
                    try {
                        EventContext o = produce();
                        if (o == null) {
                            break;
                            // notify others
                        }
                        for (int i = 1; i < stages.length; i++) {
                            o = stages[i].processc(o);
                            // if (o == null) {
                            // break;
                            // }
                            if (Thread.interrupted()) {
                                // do check
                            }
                        }
                    } finally {
                        rwLock.readLock().unlock();
                    }
                }
            }
        };
        return r;
    }

    static class EventContext {

        Object o;

        long[] times;

        EventContext(long[] times) {
            this.times = times;
        }

        /**
         * @see org.coconut.event.seda.spi.EventContext#setObject(T)
         */
        public void setObject(Object t) {
            o = t;
        }

        /**
         * @see org.coconut.event.seda.spi.EventContext#getObject()
         */
        public Object getObject() {
            return o;
        }

    }

    protected void eventProcessed(Object o, long time) {

    }

    public interface PipelineStage extends Stage {
        Transformer<?, ?> getTransformer();

        int getIndex();

        PipelineStage getPreviousStage();

        PipelineStage getNextStage();
    }

    AtomicLong al = new AtomicLong();

    ConcurrentLinkedQueue<EventContext> clq = new ConcurrentLinkedQueue<EventContext>();

    void profile(EventContext ec) {
        // System.out.println(ec.times[ec.times.length - 1] - ec.times[0]);
        al.incrementAndGet();
        clq.add(ec);
    }

    public static String toString(long[] a, long origion) {
        if (a == null)
            return "null";
        if (a.length == 0)
            return "[]";

        StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(a[0] - origion);

        for (int i = 1; i < a.length; i++) {
            buf.append(", ");
            buf.append(a[i] - origion);
        }

        buf.append("]");
        return buf.toString();
    }

    public void terminated() {
        System.out.println(al.get());
        int size = clq.size();
        long total = 0;
        long[] l = new long[size];
        for (int i = 0; i < size; i++) {
            EventContext ec = clq.poll();
            if (ec == null) {
                System.out.println("null at" + i);
            }
            l[i] = ec.times[ec.times.length - 1] - ec.times[0];
            total += l[i];
            if (i % 100 == 0) {
                System.out.println(toString(ec.times, ec.times[0]));
            }
            ec.times = null;
        }
        if (!clq.isEmpty()) {
            throw new IllegalStateException();
        }

        Arrays.sort(l);
        System.out.println("Average: " + (total / size));
        System.out.println("Best: " + l[0]);
        System.out.println("Median " + l[(l.length - 1) / 2]);
        System.out.println("90th " + l[((l.length - 1) / 10) * 9]);
        System.out.println("Worst: " + l[l.length - 1]);
    }

    final class StageImpl extends AbstractManagedStage implements PipelineStage /*
                                                                                 * ,
                                                                                 * EventProcessor
                                                                                 */
    {

        final int index;

        final Transformer t;

        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        final BlockingQueue bq = new LinkedBlockingQueue();

        public StageImpl(int index, String name, Transformer t) {
            super(name);
            this.t = t;
            this.index = index;
        }

        /**
         * @see org.coconut.event.sedaold.Stage#readLock()
         */
        public Lock readLock() {
            return lock.readLock();
        }

        /**
         * @see org.coconut.event.sedaold.Stage#writeLock()
         */
        public Lock writeLock() {
            return lock.writeLock();
        }

        boolean isLast() {
            return index == stages.length - 1;
        }

        EventContext processc(EventContext o) {
            readLock().lock();
            try {
                long startCpuTime = currentCpuTime();
                long startUserTime = currentUserTime();
                long start = System.nanoTime();
                process(o);
                long cpuUse = currentCpuTime() - startCpuTime;
                long userTime = currentUserTime() - startUserTime;
                long finish = System.nanoTime();
                addWallClockTime(finish - start);
                addCpuTime(cpuUse);
                addUserTime(userTime);
                o.times[index] = finish;
                if (isLast()) {
                    eventDone(o, finish);
                }
                return o;
            } finally {
                readLock().unlock();
            }
        }

        private void eventDone(EventContext ec, long finishTime) {
            ec.setObject(null); // help gc
            profile(ec);
        }

        Object[] processMany(Object[] o) {
            readLock().lock();
            try {
                int tt = o.length;
                Object[] result = new Object[tt];
                int failures = 0;
                long startCpuTime = currentCpuTime();
                long startUserTime = currentUserTime();
                long start = clock.relativeTime();
                for (int i = 0; i < tt; i++) {
                    try {
                        result[i] = t.transform(o[i]);
                    } catch (RuntimeException re) {
                        failures++;
                        result[i] = stageFailed(this, o[i], re);
                    }
                }
                addProcessed(tt - failures);
                if (failures > 0) {
                    addFailures(failures);
                }
                long finishCpuTime = currentCpuTime();
                long finishUserTime = currentUserTime();
                long finish = clock.relativeTime();
                addWallClockTime(finish - start);
                addCpuTime(finishCpuTime - startCpuTime);
                addUserTime(finishUserTime - startUserTime);
                return result;
            } finally {
                readLock().unlock();
            }
        }

        /**
         * @see org.coconut.event.seda.spi.AbstractManagedStage#getActiveThreadCount()
         */
        @Override
        public int getActiveThreadCount() {
            return lock.getReadLockCount();
        }

        /**
         * @see org.coconut.event.seda.spi.AbstractManagedStage#getIncoming()
         */
        @Override
        public AbstractManagedStage[] getIncoming() {
            if (index == 0) {
                return new AbstractManagedStage[0];
            } else {
                return new AbstractManagedStage[] { stages[index - 1] };
            }
        }

        /**
         * @see org.coconut.event.seda.LinearPipeline.PipelineStage#getTransformer()
         */
        public Transformer<?, ?> getTransformer() {
            return t;
        }

        /**
         * @see org.coconut.event.seda.LinearPipeline.PipelineStage#getIndex()
         */
        public int getIndex() {
            return index;
        }

        /**
         * @see org.coconut.event.seda.LinearPipeline.PipelineStage#getPreviousStage()
         */
        public PipelineStage getPreviousStage() {
            return index == 0 ? null : stages[index - 1];
        }

        /**
         * @see org.coconut.event.seda.LinearPipeline.PipelineStage#getNextStage()
         */
        public PipelineStage getNextStage() {
            return index == stages.length - 1 ? null : stages[index + 1];
        }

        /**
         * @see org.coconut.event.seda.spi.EventProcessor#process(org.coconut.event.seda.spi.EventContext)
         */
        public void process(EventContext event) {
            try {
                event.setObject(t.transform(event.getObject()));
                incrementProcessed();
            } catch (RuntimeException re) {
                incrementFailed();
                event.setObject(stageFailed(this, event.getObject(), re));
            }
        }

        /**
         * @see org.coconut.event.seda.spi.EventProcessor#process(org.coconut.event.seda.spi.EventContext[])
         */
        public void process(EventContext[] events) {
            int failures = 0;
            for (int i = 0; i < events.length; i++) {
                EventContext event = events[i];
                try {
                    event.setObject(t.transform(event.getObject()));
                } catch (RuntimeException re) {
                    failures++;
                    event.setObject(stageFailed(this, event.getObject(), re));
                }
            }
            addProcessed(events.length - failures);
            if (failures > 0) {
                addFailures(failures);
            }
        }
    }
}

// protected Object produceNewElement(Transformer<?, ?> origin) {
// return origin.transform(null);
// }
//
// protected List produceNewElements(Transformer<?, ?> origin, int size) {
// if (origin instanceof BatchedTransformer<?, ?>) {
// BatchedTransformer bt = (BatchedTransformer) origin;
// return bt.transformAll(new ArrayList(size));
// } else {
// ArrayList al = new ArrayList(size);
// for (int i = 0; i < size; i++) {
// al.add(produceNewElement(origin));
// }
// return al;
// }
// }

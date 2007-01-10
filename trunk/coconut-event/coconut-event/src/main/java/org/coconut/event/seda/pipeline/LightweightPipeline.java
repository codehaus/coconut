/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.coconut.concurrent.CapacityArrayQueue;
import org.coconut.concurrent.CapacityQueue;
import org.coconut.core.Offerable;
import org.coconut.core.Transformer;
import org.coconut.event.Sink;
import org.coconut.event.SinkException;
import org.coconut.event.Stage;
import org.coconut.event.seda.spi.InternalStageManager;
import org.coconut.event.seda.spi.ThreadWorker;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LightweightPipeline extends AbstractLinearPipeline {

    private final InternalManager im = new InternalManager();

    private final CopyOnWriteArrayList<TransformerStage> stages = new CopyOnWriteArrayList<TransformerStage>();

    private final ReentrantLock workLock = new ReentrantLock();

    public LightweightPipeline() {
        super();
    }

    public LightweightPipeline(boolean useInputQueue, BlockingQueue<?> outputQueue) {
        super(useInputQueue, outputQueue);
    }

    /**
     * @see org.coconut.event.seda.pipeline.LinearPipeline#addStage(java.lang.String,
     *      org.coconut.core.Transformer)
     */
    public Stage addStage(String name, Transformer<?, ?> transformer) {
        if (transformer == null) {
            throw new NullPointerException("transformer is null");
        }
        final ReentrantLock mainLock = im.getMainLock();
        mainLock.lock();
        try {
            if (im.getState() != InternalStageManager.RunState.NOT_STARTED) {
                throw new IllegalStateException("Pipeline has already been started");
            }
            if (getStage(name) != null) {
                throw new IllegalArgumentException("Stage with the specified name,"
                        + name + ", has already been registered");
            }
            TransformerStage s = new TransformerStage(name, transformer);
            stages.add(s);
            return s;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * @see org.coconut.event.seda.pipeline.LinearPipeline#getStages()
     */
    public List<? extends Stage> getStages() {
        return new ArrayList<Stage>(stages);
    }

    /**
     * @see org.coconut.event.seda.StageManager#awaitTermination(long,
     *      java.util.concurrent.TimeUnit)
     */
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return im.awaitTermination(timeout, unit);
    }

    /**
     * @see org.coconut.event.seda.StageManager#isShutdown()
     */
    public boolean isShutdown() {
        return im.isShutdown();
    }

    /**
     * @see org.coconut.event.seda.StageManager#isTerminated()
     */
    public boolean isTerminated() {
        return im.isTerminated();
    }

    /**
     * @see org.coconut.event.seda.StageManager#lock()
     */
    public Lock lock() {
        return workLock;
    }

    /**
     * @see org.coconut.event.seda.StageManager#shutdown()
     */
    public void shutdown() {
        im.shutdown();
    }

    /**
     * @see org.coconut.event.seda.StageManager#start()
     */
    public void start() {
        // refactor this if add methods to remove stages
        if (getStages().size() == 0) {
            throw new IllegalStateException(
                    "Cannot start pipeline without any registered stages");
        }
        im.start();
    }

    class TransformerStage extends AbstractStage {

        final CapacityQueue abq = new CapacityArrayQueue(1);

        private final Transformer t;

        /**
         * @param name
         */
        public TransformerStage(String name, Transformer t) {
            super(name);
            this.t = t;
        }

        Object processEvent(Object e) {
            try {
                Object result = t.transform(e);
                if (result == null) {
                    throw new NullPointerException("event was null");
                }
                return result;
            } catch (RuntimeException re) {
                NextOfferable o = new NextOfferable(this);
                handleFailure(this, e, re, o);
                return o.element;
            }
        }

        boolean processAndEnqueueEvent(Object e) throws InterruptedException {
            Object o = processEvent(e);
            if (o != null) {
                abq.put(o);
            }
            return o != null;
        }

//        /**
//         * @see org.coconut.event.Sink#send(java.lang.Object)
//         */
//        public void send(Object o) throws SinkException {
//            // TODO Auto-generated method stub
//
//        }
//
//        /**
//         * @see org.coconut.core.Offerable#offer(java.lang.Object)
//         */
//        public boolean offer(Object element) {
//            try {
//                abq.put(element);
//                return true;
//            } catch (InterruptedException e) {
//                return false;
//            }
//        }
    }

    protected void handleFailure(Stage s, Object event, Throwable t, Sink stage) {
        System.err.println("Event: " + event + " failed to process on stage " + s);
        System.err.println("With the following exception");
        t.printStackTrace();
    }

    protected void unhandledException(Stage s, Throwable t) {
        System.err.println("The stage " + s + " failed with the following exception");
    }

    protected int initialThreadCount() {
        return getStages().size();
    }

    private boolean isDone() {
        return isShutdown()
                && (LightweightPipeline.this.inputQueue == null || LightweightPipeline.this.inputQueue
                        .size() == 0);
    }

    /**
     * @see org.coconut.event.sedaold.defaults.SameThreadStageManager#getTask()
     */
    Runnable getTask(ThreadWorker w) {
        if (isDone()) {
            return null;
        }
        final TransformerStage[] s = this.stages.toArray(new TransformerStage[stages
                .size()]);
        final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        Runnable r = new Runnable() {
            public void run() {
                while (!isDone()) {
                    rwLock.readLock().lock();
                    try {
                        Object o = null;
                        if (LightweightPipeline.this.inputQueue != null) {
                            while (o == null) {
                                try {
                                    o = LightweightPipeline.this.inputQueue.poll(1,
                                            TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                }
                                if (o == null && isDone()) {
                                    return;
                                }
                            }
                        }
                        for (int i = 0; i < stages.size(); i++) {
                            o = s[i].processEvent(o);
                        }
                        if (o != null && LightweightPipeline.this.outputQueue != null) {
                            LightweightPipeline.this.outputQueue.add(o);
                        }
                    } finally {
                        rwLock.readLock().unlock();
                    }
                }
            }
        };
        return r;
    }

    class InternalManager extends InternalStageManager {

        /**
         * @see org.coconut.event.seda.spi.ServiceDutte#getTask(org.coconut.event.seda.spi.ThreadWorker)
         */
        @Override
        protected Runnable getTask(ThreadWorker w) {
            return LightweightPipeline.this.getTask(w);
        }

        /**
         * @see org.coconut.event.seda.spi.ServiceDutte#started()
         */
        @Override
        protected void started() {
            for (int i = 0; i < initialThreadCount(); i++) {
                addThreadWorker(getDefaultThreadFactory()).getThread().start();
            }
        }

        @Override
        protected void tryShutdown() {
            LightweightPipeline.this.inputQueue.closeQueue();
        }

        /**
         * @see org.coconut.event.seda.spi.ServiceDutte#isDone()
         */
        @Override
        protected boolean isDone() {
            return LightweightPipeline.this.isDone();
        }
    }

    class NextOfferable implements Offerable, Sink {

        Object element;

        TransformerStage s;

        NextOfferable(TransformerStage s) {
            this.s = s;
        }

        /**
         * @see org.coconut.core.Offerable#offer(java.lang.Object)
         */
        public boolean offer(Object element) {
            if (element == null) {
                unhandledException(s, new NullPointerException("cannot handle null"));
            }
            this.element = element;
            return true;
        }

        /**
         * @see org.coconut.event.Sink#send(java.lang.Object)
         */
        public void send(Object element) throws SinkException {
            offer(element);
        }
    }
}

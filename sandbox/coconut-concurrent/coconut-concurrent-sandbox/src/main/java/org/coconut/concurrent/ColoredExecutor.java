/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.concurrent;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.coconut.core.Colored;

/**
 * A Colored Executor. Used for serializing execution of Runnables by wrapping
 * an existing Executor.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class ColoredExecutor implements Executor {
    /* The number of slots available for Runnables */
    private final int concurrencyLevel;
    /* Used for generating random color values */
    private final Random random = new Random();
    /* The slots where each command is put into */
    private final ColorSlot[] slots;
    /* The executor we are wrapping */
    private final Executor executor;
    /* How we treat "uncolored" commands, true -> color=0 false -> color=random */
    private final boolean isStrict;

    /**
     * Constructs a new ColoredExecutor. Runnables that are submitted to this
     * executor and does not implement the Colored interface are given a random
     * color.
     * 
     * @param executor
     *            the executor to wrap
     */
    public ColoredExecutor(Executor executor) {
        this(executor, false);
    }

    /**
     * Constructs a new ColoredExecutor.
     * 
     * @param executor
     *            the executor to wrap
     * @param strict
     *            if <tt>true</tt> all "uncolored" commands are given the same
     *            color, if false all "uncolored" commands are given a random
     *            color
     */
    public ColoredExecutor(Executor executor, boolean strict) {
        this(executor, strict, 100);
    }

    /**
     * Constructs a new ColoredExecutor.
     * 
     * @param executor
     *            the executor to wrap
     * @param strict
     *            if <tt>true</tt> all "uncolored" commands are given the same
     *            color, if false all "uncolored" commands are given a random
     *            color
     * @param concurrencyLevel
     *            the number of slots used
     */
    public ColoredExecutor(Executor executor, boolean strict, int concurrencyLevel) {
        if (executor == null)
            throw new NullPointerException();
        this.concurrencyLevel = concurrencyLevel;
        this.executor = executor;
        this.isStrict = strict;

        slots = new ColorSlot[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            slots[i] = new ColorSlot();
        }
    }
    /**
     * Execute a Runnable with a specific color
     * 
     * @param runnable
     * @param color
     */
    public void execute(final Runnable command, final int color) {
        if (command == null)
            throw new NullPointerException("command");

        innerExecute(command, hash(color));
    }
    /**
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    public void execute(final Runnable command) {
        if (command == null)
            throw new NullPointerException("command");

        if (command instanceof Colored) {
            innerExecute(command, hash(((Colored) command).getColor()));
        } else if (isStrict) {
            innerExecute(command, 0);
        } else {
            innerExecute(command, random.nextInt());
        }
    }
    public void release(final Runnable command) {
        if (command instanceof MyRunnable) {
            ((MyRunnable) command).release();
        } else
            throw new IllegalArgumentException("Runnable not created by a ColoredExecutor");

    }
    //only package access for testing
    void innerExecute(final Runnable command, final int hashedColor) {
        ColorSlot slot = slots[Math.abs(hashedColor) % concurrencyLevel];
        slot.queue.add(new MyRunnable(command, slot));
        slot.trySubmitNext();
    }
    /**
     * Hash the color, uses same hash as java.util.*
     */
    private static int hash(int color) {
        int h = color;
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        return h;
    }
    private static class MyRunnable implements Runnable {

        private final AtomicBoolean isDone = new AtomicBoolean(false);
        private final Runnable command;
        private final ColorSlot slot;
        MyRunnable(Runnable command, ColorSlot slot) {
            this.command = command;
            this.slot = slot;
        }
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            //System.out.println("running" + isDone.get());
            if (isDone.compareAndSet(false,true)) {
                try {
                    command.run();
                } finally {
                    slot.submitted.set(false);
                    slot.trySubmitNext();
                }
            } 
        }
        //used for cancellation of tasks
        private void release() {
            if (isDone.compareAndSet(true, false)) {
                slot.submitted.set(false);
                slot.trySubmitNext();
            }
        }

    }
    private class ColorSlot {
        private final Queue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();
        private final AtomicBoolean submitted = new AtomicBoolean();
        private void trySubmitNext() {
            if (submitted.compareAndSet(false, true)) {
                Runnable r = queue.poll();
                if (r != null) {
                    //TODO this sucks
                    executor.execute(r); //TODO do something about exceptions
                }
                else
                    submitted.set(false);
            }
        }
    }

}
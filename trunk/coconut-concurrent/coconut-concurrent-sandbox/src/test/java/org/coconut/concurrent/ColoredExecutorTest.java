/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.Colored;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class ColoredExecutorTest extends AbstractConcurrentTestCase {

    public void testNullExceptionConstructor() {
        try {
            new ColoredExecutor(null);
        } catch (NullPointerException npe) {
            return;
        }
        shouldThrow();
    }
    public void testNullExceptionExecuteColor() {
        try {
            Executor ee = Executors.newSingleThreadExecutor();
            ColoredExecutor e = new ColoredExecutor(ee);
            e.execute(null, 0);
        } catch (NullPointerException npe) {
            return;
        }
        shouldThrow();
    }

    public void testNullExceptionExecute() {
        try {
            Executor ee = Executors.newSingleThreadExecutor();
            Executor e = new ColoredExecutor(ee);
            e.execute(null);
        } catch (NullPointerException npe) {
            return;
        }
        shouldThrow();
    }

    public void testSimpleExecute() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        ExecutorService es = Executors.newSingleThreadExecutor();
        Executor e = new ColoredExecutor(es);
        Runnable r = new Runnable() {
            public void run() {
                latch.countDown();
            }
        };
        e.execute(r);
        waitOnLatchShort(latch);
        joinPool(es);
    }

    public void testConcurrentInnerExecute() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(20000);
        ExecutorService es = Executors.newFixedThreadPool(66);
        ColoredExecutor e = new ColoredExecutor(es, true, 100);
        final Random r = new Random(0);
        final AtomicLong[] slots = new AtomicLong[100];
        for (int i = 0; i < 100; i++)
            slots[i] = new AtomicLong(0);
        //clean up test
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 100; j++) {
                final int slot = j;
                e.innerExecute(new Runnable() {
                    public void run() {
                        long threadId = Thread.currentThread().getId();
                        slots[slot] = new AtomicLong(threadId);
                        int doWhat = r.nextInt(15);
                        if (doWhat < 10)
                            Thread.yield();
                        else if (doWhat < 13)
                            compute2(slot * doWhat);
                        else {
                            try {
                                Thread.sleep(15);
                            } catch (InterruptedException ie) {
                                //ignore
                            }
                        }
                        if (slots[slot].get() != threadId)
                            fail("interleaved");
                        else
                            latch.countDown();
                    }
                }, j);
            }

        }
        waitOnLatchVeryLong(latch);
        joinPool(es);
    }

    public void testConcurrentExecuteColor() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(20000);
        ExecutorService es = Executors.newFixedThreadPool(66);
        ColoredExecutor e = new ColoredExecutor(es, true, 100);
        final Random r = new Random(0);
        final AtomicLong[] slots = new AtomicLong[100];
        for (int i = 0; i < 100; i++)
            slots[i] = new AtomicLong(0);
        //clean up test
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 100; j++) {
                final int slot = j;
                e.execute(new Runnable() {
                    public void run() {
                        long threadId = Thread.currentThread().getId();
                        slots[slot] = new AtomicLong(threadId);
                        int doWhat = r.nextInt(15);
                        if (doWhat < 10)
                            Thread.yield();
                        else if (doWhat < 13)
                            compute2(slot * doWhat);
                        else {
                            try {
                                Thread.sleep(15);
                            } catch (InterruptedException ie) {
                                //ignore
                            }
                        }
                        if (slots[slot].get() != threadId)
                            fail("interleaved");
                        else
                            latch.countDown();
                    }
                }, j);
            }

        }
        waitOnLatchVeryLong(latch);
        joinPool(es);
    }

    public void testConcurrentExecute() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(20000);
        ExecutorService es = Executors.newFixedThreadPool(66);
        ColoredExecutor e = new ColoredExecutor(es, true, 100);
        final Random r = new Random(0);
        final AtomicLong[] slots = new AtomicLong[100];
        for (int i = 0; i < 100; i++)
            slots[i] = new AtomicLong(0);
        //clean up test
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 100; j++) {
                final int slot = j;
                e.execute(new ColoredRunnable(new Runnable() {
                    public void run() {
                        long threadId = Thread.currentThread().getId();
                        slots[slot] = new AtomicLong(threadId);
                        int doWhat = r.nextInt(15);
                        if (doWhat < 10)
                            Thread.yield();
                        else if (doWhat < 13)
                            compute2(slot * doWhat);
                        else {
                            try {
                                Thread.sleep(15);
                            } catch (InterruptedException ie) {
                                //ignore
                            }
                        }
                        if (slots[slot].get() != threadId)
                            fail("interleaved");
                        else
                            latch.countDown();
                    }
                }, j));
            }

        }
        waitOnLatchVeryLong(latch);
        joinPool(es);
    }

    static class ColoredRunnable implements Runnable, Colored {
        private final int color;
        private final Runnable runnable;
        public ColoredRunnable(Runnable runnable, int color) {
            this.runnable = runnable;
            this.color = color;
        }
        public void run() {
            runnable.run();
        }
        public int getColor() {
            return color;
        }
    }
}
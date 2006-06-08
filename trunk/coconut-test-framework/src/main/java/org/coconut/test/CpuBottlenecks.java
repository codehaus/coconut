/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CpuBottlenecks {

    static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    static {
        // warm up loop, not sure this has any effect
        int x = 1;
        for (int i = 0; i < 10000; i++) {
            long start = System.nanoTime();
            x = compute(x);
        }
    }

    public static void main(String[] args) {
        int x = 234234;
        for (int i = 0; i < 100000; i++) {
            x = compute(x);
        }
        long start = threadBean.getCurrentThreadCpuTime();
        for (int i = 0; i < 10000000; i++) {
            for (int j = 0; j < 1197; j++) {
                x = compute(x);
            }
        }
        long finish = threadBean.getCurrentThreadCpuTime();
        System.out.println(finish - start);
    }

    /**
     * Computes a linear congruential random number a random number of times.
     */
    static int compute(int x) {
        int loops = (x >>> 4) & 7;
        while (loops-- > 0) {
            x = (x * 2147483647) % 16807;
        }
        return x;
    }

    public final static class Cpu3 extends AdaptiveBottleNeck {

        public Cpu3(long millies) {
            this(millies, TimeUnit.MILLISECONDS);
        }

        Cpu3(long cpuTime, TimeUnit unit) {
            super(unit.toNanos(cpuTime));
        }

        /**
         * @see org.coconut.event.testutil.CpuBottlenecks.AdaptiveBottleNeck#time()
         */
        @Override
        long time() {
            return System.nanoTime();
        }

        void innerRun(long delay) {
            long now = System.nanoTime();
            int mindlessCalc = (int) (now ^ (now >>> 32));
            for (int i = 0; i < 1000; i++) {
                compute(mindlessCalc);
            }
            long finish = System.nanoTime();
            double avg = ((double) 1000) / (finish - now);
            while (now + delay > finish) {
                long prevFinish = finish;
                double count = avg * (delay - (finish - now)) / 2;
                int t = Math.max(10, (int) count);
                for (int i = 0; i < t; i++) {
                    compute(mindlessCalc);
                }
                finish = System.nanoTime();
                avg = ((double) t) / (finish - prevFinish);
            }
        }
    }

    final static class MathWorker implements Runnable {

        private final long iterations;

        MathWorker(long iterations) {
            this.iterations = iterations;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            int x = 234234;
            for (int i = 0; i < iterations; i++) {
                //1280
                for (int j = 0; j < 147; j++) {
                    x = compute(x);
                }
            }
        }

    }

    final static class CpuSystemWorker extends AdaptiveBottleNeck {

        CpuSystemWorker(long millies) {
            this(millies, TimeUnit.MILLISECONDS);
        }

        CpuSystemWorker(long cpuTime, TimeUnit unit) {
            super(unit.toNanos(cpuTime));
        }

        /**
         * @see org.coconut.event.testutil.CpuBottlenecks.AdaptiveBottleNeck#time()
         */
        @Override
        long time() {
            return threadBean.getCurrentThreadCpuTime();
        }

        public void innerRun(long cpuTime) {
            long start = threadBean.getCurrentThreadCpuTime();
            long now = start;
            while (start + cpuTime > now) {
                now = threadBean.getCurrentThreadCpuTime();
            }
        }
    }

    public final static class Cpu2 extends AdaptiveBottleNeck {

        public Cpu2(long millies) {
            this(millies, TimeUnit.MILLISECONDS);
        }

        Cpu2(long cpuTime, TimeUnit unit) {
            super(unit.toNanos(cpuTime));
        }

        public void innerRun(long cpuTime) {
            long start = System.nanoTime();
            long now = start;
            while (start + cpuTime > now) {
                now = System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
                System.nanoTime();
            }
        }

        /**
         * @see org.coconut.event.testutil.CpuBottlenecks.AdaptiveBottleNeck#time()
         */
        @Override
        long time() {
            return System.nanoTime();
        }
    }

    abstract static class AdaptiveBottleNeck implements Runnable {
        private final long delay;

        private final AtomicLong totalRuns = new AtomicLong();

        private final AtomicLong totalTime = new AtomicLong();

        public AdaptiveBottleNeck(long delay) {
            this.delay = delay;
        }

        abstract long time();

        public void run() {
            // we could replace this by a PID controller eventually
            if (delay != 0) {
                long adjust = 0;
                long runs = totalRuns.get();
                if (runs > 0) {
                    adjust = (runs + 1) * delay - totalTime.get() - 5000000;
                }
                long now = time();
                innerRun(delay + adjust);
                long finish = time();
                totalTime.addAndGet(finish - now);
                totalRuns.incrementAndGet();
            }

        }

        abstract void innerRun(long time);

    }

    public final static class Cpu1 implements Runnable {
        private final long cpuTime;

        private final AtomicLong counts = new AtomicLong();

        private final AtomicLong time = new AtomicLong();

        public Cpu1(long millies) {
            this(millies, TimeUnit.MILLISECONDS);
        }

        Cpu1(long cpuTime, TimeUnit unit) {
            this.cpuTime = unit.toNanos(cpuTime);
        }

        public void run() {
            if (cpuTime != 0) {
                long now = System.nanoTime();
                long adjust = 0;
                if (counts.get() > 0) {
                    adjust = (counts.get() + 1) * cpuTime - time.get()
                            - 5000000;
                }
                long totalTime = cpuTime + adjust;

                int mindlessCalc = (int) (now ^ (now >>> 32));
                for (int i = 0; i < 1000; i++) {
                    compute(mindlessCalc);
                }
                long finish = System.nanoTime();
                double avg = ((double) 1000) / (finish - now);
                while (now + totalTime > finish) {
                    long prevFinish = finish;
                    double count = avg * (totalTime - (finish - now)) / 2;
                    int t = Math.max(10, (int) count);
                    for (int i = 0; i < t; i++) {
                        compute(mindlessCalc);
                    }
                    finish = System.nanoTime();
                    avg = ((double) t) / (finish - prevFinish);
                }
                time.addAndGet(finish - now);
                counts.incrementAndGet();
            }
        }
    }

    // final static class CpuWorkerGaussian implements Runnable {
    //
    // }
    //
    // final static class CpuWorkerExponential implements Runnable {
    //
    // }

    public final static class Sleeper implements Runnable {
        private final long sleep;

        public Sleeper(long millies) {
            this(millies, TimeUnit.MILLISECONDS);
        }

        Sleeper(long time, TimeUnit unit) {
            this.sleep = unit.toNanos(time);
        }

        public void run() {
            if (sleep != 0) {
                long start = System.nanoTime();
                long remaining = sleep;
                while (remaining > 0) {
                    try {
                        TimeUnit.NANOSECONDS.sleep(sleep);
                    } catch (InterruptedException e) {
                        // just for tests, okay to use system.out
                        System.err.println("Thread was interrupted, ignoring");
                    }
                    remaining = sleep - (System.nanoTime() - start);
                }
            }
        }
    }

    public static Runnable cpuTime(long millies) {
        return cpuTime(millies, TimeUnit.MILLISECONDS);
    }

    public static Runnable cpuTime(long time, TimeUnit unit) {
        return new CpuSystemWorker(time, unit);
    }

    public static Runnable math(long iterations) {
        return new MathWorker(iterations);
    }

    public static Runnable sleep(long millies) {
        return sleep(millies, TimeUnit.MILLISECONDS);
    }

    public static Runnable sleep(long time, TimeUnit unit) {
        return new Sleeper(time, unit);
    }

    public static void sleepNow(long millies) {
        sleep(millies, TimeUnit.MILLISECONDS).run();
    }

    public static void sleepNow(long time, TimeUnit unit) {
        new Sleeper(time, unit).run();
    }

    public enum CpuWorkType {
        WALL_CLOCK, CPU_SYSTEM_MODE, CPU_USER_MODE
    }

}

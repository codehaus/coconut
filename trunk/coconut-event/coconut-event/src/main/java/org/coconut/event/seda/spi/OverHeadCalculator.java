/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.spi;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class OverHeadCalculator {
    private final static ThreadMXBean threadInfo = ManagementFactory
            .getThreadMXBean();

    public static void main(String[] args) throws InterruptedException {
        // calc(1);
        // calc(10);
        // calc(100);
        // calc(100);
        // calc(100);
        // calc(100);
        // calc(100);
        // calc(100);
        // calc(1000);
        // calc(10000);
        // calc(100000);
        // calc2(10000);
        // Thread.sleep(50);
        // calc(100000);
        // calc2(10000);
        // calc(100);
        // calc(10);
        // calc(100);
        // calc(10);
        // System.out.println(calc());
        // System.out.println(calc());
        // System.out.println(calc());
        // System.out.println(calc());
        // System.out.println(calc());
        // System.out.println(calc());
        // System.out.println(calc());
        // System.out.println(calc());
//        System.out.println(nanoTimeOverhead);
//        System.out.println(cpuTimeOverhead);
//        System.out.println(userTimeOverhead);
    }


    // public static void dd(boolean print) {
    //
    // long overhead = System.nanoTime();
    // long oo = System.nanoTime();
    // overhead = oo - overhead;
    // long now = System.nanoTime();
    // long startCpuTime = threadInfo.getCurrentThreadCpuTime();
    // long startUserTime = threadInfo.getCurrentThreadUserTime();
    // long now1 = System.nanoTime();
    // ThreadInfo inf = threadInfo.getThreadInfo(1, 0);
    // long boo = inf.getWaitedTime();
    // long now2 = System.nanoTime();
    // if (print) {
    // System.out.println((now1 - now - overhead) + " "
    // + (now2 - now1 - overhead));
    // System.out.println(boo);
    // }
    // }

    // public static void main(String[] args) throws InterruptedException {
    // threadInfo.setThreadContentionMonitoringEnabled(true);
    // for (int i = 0; i < 10000; i++) {
    // dd(false);
    // }
    // Thread.sleep(40);
    // for (int i = 0; i < 10000; i++) {
    // dd(false);
    // }
    // // System.out.println(Thread.currentThread()
    // // .getId());
    // dd(true);
    // }
    // private static volatile long count;


    public static void calc(int amount) {
        long start = System.nanoTime();
        for (int i = 0; i < amount; i++) {
            System.nanoTime();
        }
        long finish = System.nanoTime();
        System.out.println("Time for " + amount + " "
                + (((double) finish - start) / amount));
    }

    public static void calc2(int amount) {
        long start = System.nanoTime();
        for (int i = 0; i < amount; i++) {
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
        long finish = System.nanoTime();
        System.out.println("Time for " + amount + " "
                + (((double) finish - start) / amount));
    }
}

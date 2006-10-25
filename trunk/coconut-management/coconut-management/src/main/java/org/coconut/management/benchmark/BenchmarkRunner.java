/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.coconut.internal.util.tabular.TabularFormatter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class BenchmarkRunner {
    private final static ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();

    public static void run(Benchmark b, int iterations) throws Exception {
        mxBean.setThreadCpuTimeEnabled(true);
        b.warmup();
        long cpuUsage = mxBean.getCurrentThreadCpuTime();
        long userTime = mxBean.getCurrentThreadUserTime();
        long now = System.nanoTime();
        b.benchmark(iterations);
        long finish = System.nanoTime();
        long cpuUsageAfter = mxBean.getCurrentThreadCpuTime();
        long userTimeAfter = mxBean.getCurrentThreadUserTime();
        System.out.println("Total time:" + TabularFormatter.formatTime(finish - now));
        System.out.println("CPU time:"
                + TabularFormatter.formatTime(cpuUsageAfter - cpuUsage));
        System.out.println("User time:"
                + TabularFormatter.formatTime(userTimeAfter - userTime));

        System.out.println("Per iteration time:"
                + TabularFormatter.formatTime(((double) finish - now) / iterations));
    }
}

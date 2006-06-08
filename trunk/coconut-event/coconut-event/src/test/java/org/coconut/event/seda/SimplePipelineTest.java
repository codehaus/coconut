/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.Transformer;
import org.coconut.core.Transformers;
import org.coconut.event.seda.management.StatisticsUtil;
import org.coconut.test.CpuBottlenecks;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SimplePipelineTest {

    private final static ThreadMXBean threadInfo = ManagementFactory
            .getThreadMXBean();

    public static void main2(String[] args) {

        LinearPipeline sp = new LinearPipeline(c(1), s(1), s(1), s(1), s(1));

        BlockingQueue bq = sp.getInitialQueue();

    }

    public static void main(String[] args) throws Exception {

        threadInfo.setThreadContentionMonitoringEnabled(true);
        // doIt(1);
        // doIt(2);
        // doIt(4);
        // doIt(8);
        doIt(4);
        // doIt(32);
        // doIt(64);
        // doIt(128);
        // doIt(256);
        // doIt(512);
        // SimplePipeline sp = new SimplePipeline(c, newT(1), newT(1), newT(1),
        // newT(1), newT(1), newT(1));
        // new FetchStage(),

    }

    private static void doIt(int threads) throws InterruptedException {
        final AtomicLong tot = new AtomicLong();
        Transformer c = new Transformer() {
            public Object transform(Object ignore) {
                return System.nanoTime();
            }
        };
        Transformer f = new Transformer() {
            public Object transform(Object ignore) {
                long total = System.nanoTime() - ((Long) ignore);
                tot.addAndGet(total);
                return ignore;
            }
        };
        LinearPipeline.threads = threads;
        System.out.println("Threads :" + threads);
        Transformer no = Transformers.noTransformer();
        //LinearPipeline sp = new LinearPipeline(c, c(1), c(2), s(50), c(1), c(1));
        //LinearPipeline sp = new LinearPipeline(c, cc(450), cc(250), cc(800), cc(500), no);
        LinearPipeline sp = new LinearPipeline(c, m(45), m(25), m(80), m(50), no);
        // LinearPipeline sp = new LinearPipeline(c, c(1), new FetchStage(),
        // s(1), s(1), s(1));
        // sp.addStage("Last", s(1));
        // sp.getActiveCount()
        // sp.registerJMX("org.coconut.event.seda:type=Pipeline");
        sp.start();
        //Thread.sleep(12000);
        Thread.sleep(6000);
        Thread.sleep(6000);
        sp.shutdown();
        sp.awaitTermination(1000, TimeUnit.SECONDS);

        Stages.printUsage(sp.getInfo(), System.out);
        DecimalFormat df=new DecimalFormat("#0.0000");
        System.out.println("Overhead (%) for CpuTime: " + df.format(100*StatisticsUtil.getCpuOverhead(sp.getInfo())) + " %");
        // System.out.println(TabularFormatter.formatTime(tot.get()
        // / in.getEventsAccepted());
        System.out.println(sp.awaitTermination(100, TimeUnit.SECONDS));
    }

    static Transformer cc(int time) {
        return new RunnableTransformer(CpuBottlenecks.cpuTime(time,TimeUnit.MICROSECONDS));
    }
    static Transformer c(int time) {
        return new RunnableTransformer(CpuBottlenecks.cpuTime(time));
    }
    static Transformer m(int time) {
        return new RunnableTransformer(CpuBottlenecks.math(time));
    }

    static Transformer s(int time) {
        return new RunnableTransformer(CpuBottlenecks.sleep(time));
    }

    public static class RunnableTransformer implements Transformer {

        private final Runnable r;

        /**
         * @see org.coconut.core.Transformer#transform(F)
         */
        public Object transform(Object from) {
            r.run();
            return from;
        }

        public RunnableTransformer(Runnable r) {
            this.r = r;
        }

    }
}

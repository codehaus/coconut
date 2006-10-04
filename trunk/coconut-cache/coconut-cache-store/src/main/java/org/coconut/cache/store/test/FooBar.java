/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.store.test;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.coconut.apm.benchmark.Benchmark;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class FooBar implements Benchmark {

    // BlockingQueue bq = new LinkedBlockingQueue(10000000);

    BlockingQueue bq = new ArrayBlockingQueue(10000000);

    ArrayList al = new ArrayList(1000000);

    public static void main(String[] args) throws Exception {
        FooBar bar = new FooBar();
        bar.warmup();
        Thread.sleep(1000);
        bar.benchmark(1);
        long start = System.nanoTime();
        bar.benchmark(10);
        long finish = System.nanoTime();
        System.out.println( ((double) (finish - start)) /10000000);
    }

    public void doit() {

    }

    /**
     * @see org.coconut.cache.benchmark.Benchmark#benchmark(int)
     */
    public void benchmark(int iterations) throws Exception {
        Object o = new Object();
        for (int j = 0; j < iterations; j++) {
            for (int i = 0; i < 1000000; i++) {
                bq.offer(new ItemLoaded());
            }
            bq.drainTo(al);
            al.clear();
        }
    }

    /**
     * @see org.coconut.cache.benchmark.Benchmark#getDescription()
     */
    public String getDescription() {
        return null;
    }

    /**
     * @see org.coconut.cache.benchmark.Benchmark#warmup()
     */
    public void warmup() throws Exception {
        benchmark(5);
    }

    /**
     * @see org.coconut.core.Named#getName()
     */
    public String getName() {
        return null;
    }
    
    static class ItemLoaded {
        private Object key;
        private Object value;
        
    }
}

/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.cache.defaults.memory.UnlimitedCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MemoryTestRunner {
    public final static int[] DEFAULT_ITERATIONS = new int[] { 1, 10, 100, 1000, 10000,
            100000 };

    // 
    private static final Runtime RUNTIME = Runtime.getRuntime();

    public static void main(String[] args) throws Exception {
        MemoryTestRunner t = new MemoryTestRunner();
        System.out
                .println("|| JDK Implementations \\\\ || 1 Object || 10 Objects || 100 Objects || 1.000 Objects || 10.000 Objects || 100.000 Objects || Elements/MB ||");
        t.test(new MapMemoryOverheadTest(ConcurrentHashMap.class));
        t.test(new MapMemoryOverheadTest(HashMap.class));
        t.test(new MapMemoryOverheadTest(Hashtable.class));
        t.test(new MapMemoryOverheadTest(LinkedHashMap.class));
        t.test(new MapMemoryOverheadTest(TreeMap.class));
        System.out
                .println("|| Coconut Cache Implementations \\\\ || || || || || || || ||");
        t.test(new MapMemoryOverheadTest(UnlimitedCache.class));
        System.out.println("|| Other Cache Implementations \\\\ || || || || || || || ||");
        t.test(new EHcacheMemoryOverheadTest());
        t.test(new JBossMemoryOverheadTest());
        t.test(new JCSOverheadTest());
        t.test(new OSCacheMemoryOverTest());
        t.test(new WhirlyCacheMemoryOverheadTest());
    }

    public int test(MemoryOverheadTest map, int iterations) throws Exception {
        final int count = 1;
        runGC();
        new MemoryTestResult(1, iterations).run(map);
        usedMemory();
        runGC();
        MemoryTestResult t = new MemoryTestResult(count, iterations);
        runGC();
        t.run(map);
        float memUsage = t.memoryUse; // Take an after heap snapshot:
        final int size = Math.round(memUsage / count);
        return size;
    }

    void test(MemoryOverheadTest tt) throws Exception {
        int[] scores = new int[DEFAULT_ITERATIONS.length];
        for (int i = 0; i < DEFAULT_ITERATIONS.length; i++) {
            int iter = DEFAULT_ITERATIONS[i];
            scores[i] = test(tt, iter);
        }
        System.out.println(toConfluenceString(tt.toString(), scores, DEFAULT_ITERATIONS));
    }

    private String toConfluenceString(String name, int[] scores, int[] iterations) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        sb.append(name);
        sb.append(" \\\\ |");
        int s = 0;
        for (int i = 0; i < scores.length; i++) {
            s = (int) ((double) scores[i] / iterations[i]);
            sb.append(" ");
            sb.append(s);
            sb.append(" |");
        }
        sb.append(" ");
        sb.append((int) ((2 << 20) / (double) s));
        sb.append(" |");
        return sb.toString();
    }

    private static void runGC() throws Exception {
        for (int r = 0; r < 4; ++r) {
            long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
            for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
                RUNTIME.runFinalization();
                RUNTIME.gc();
                Thread.sleep(50);

                usedMem2 = usedMem1;
                usedMem1 = usedMemory();
            }
        }
    }

    private static long usedMemory() {
        return RUNTIME.totalMemory() - RUNTIME.freeMemory();
    }
}

/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark;

import com.opensymphony.oscache.base.algorithm.UnlimitedCache;
import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheConfiguration;
import com.whirlycott.cache.CacheManager;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MemoryOverheadCalculator {
    // 6144, 6145
    public final static int[] DEFAULT_ITERATIONS = new int[] { 1, 10, 100, 1000, 10000,
            100000 };

    // OSCache only takes string keys
    private final static String[] keys = new String[100000];

    private final static Integer[] values = new Integer[100000];

    private static final Runtime RUNTIME = Runtime.getRuntime();

    static {
        for (int i = 0; i < values.length; i++) {
            keys[i] = Integer.toString(i);
            values[i] = Integer.valueOf(i);
        }
        fixLogger("com.whirlycott.cache.CacheDecorator");
        fixLogger("com.whirlycott.cache.CacheManager");
        fixLogger("com.whirlycott.cache.policy.LFUMaintenancePolicy");
        fixLogger("com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache");
        fixLogger("com.opensymphony.oscache.base.Cache");
    }

    private static void fixLogger(String logger) {
        Logger.getLogger(logger).setLevel(Level.OFF);
        // Logger.getLogger(logger).setLevel(Level.DEBUG);
        // ConsoleAppender ap = new ConsoleAppender();
        // ap.setWriter(new PrintWriter(System.err));
        // ap.setLayout(new SimpleLayout());
        // Logger.getLogger(logger).addAppender(ap);
    }

    public static void main(String[] args) throws Exception {
        MemoryOverheadCalculator t = new MemoryOverheadCalculator();
        // t.test(new DoNothingTester());

        System.out
                .println("|| JDK Implementations \\\\ || 1 Object || 10 Objects || 100 Objects || 1.000 Objects || 10.000 Objects || 100.000 Objects || Elements/MB ||");
         t.test(new Ref(ConcurrentHashMap.class));
         t.test(new Ref(HashMap.class));
         t.test(new Ref(Hashtable.class));
         t.test(new Ref(LinkedHashMap.class));
         t.test(new Ref(TreeMap.class));
         System.out.println("|| Coconut Cache Implementations \\\\ || || || || || || || ||");
         t.test(new Ref(UnlimitedCache.class));
         System.out.println("|| Other Cache Implementations \\\\ || || || || || || || ||");
        t.test(new OSCacheTester<Integer, Integer>());
        t.test(new WhirlyCacheTester<Integer, Integer>());
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

    public int test(OverHeadTester map, int iterations) throws Exception {
        final int count = 1;
        runGC();
        new TestResult(1, iterations).run(map);
        usedMemory();
        runGC();
        TestResult t = new TestResult(count, iterations);
        runGC();
        t.run(map);
        float memUsage = t.memoryUse; // Take an after heap snapshot:
        final int size = Math.round(memUsage / count);
        return size;
    }

    private void test(OverHeadTester tt) throws Exception {
        int[] scores = new int[DEFAULT_ITERATIONS.length];
        for (int i = 0; i < DEFAULT_ITERATIONS.length; i++) {
            int iter = DEFAULT_ITERATIONS[i];
            scores[i] = test(tt, iter);
        }
        System.out.println(toConfluenceString(tt, scores, DEFAULT_ITERATIONS));
    }

    private String toConfluenceString(OverHeadTester o, int[] scores, int[] iterations) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        sb.append(o);
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

    public interface OverHeadTester {
        void test(TestResult result) throws Exception;
    }

    public class TestResult {
        private long startMemory;

         long memoryUse;

        private int iterations;

        Object[] refs;

        TestResult(int testCounts, int iterations) {
            refs = new Object[testCounts];
            this.iterations = iterations;
        }

        public int getIterations() {
            return iterations;
        }

        public int getTotal() {
            return refs.length;
        }

        <T> T get(int index) {
            return (T) refs[index];
        }

        void set(int index, Object o) {
            refs[index] = o;
        }

        public void run(OverHeadTester o) throws Exception {
            runGC();
            o.test(this);
            refs = null;
            runGC();
        }

        public void start() throws Exception {
            startMemory = usedMemory();
        }

        public void stop() throws Exception {
            runGC();
            Thread.sleep(100);
            runGC();
            Thread.sleep(100);
            memoryUse = usedMemory() - startMemory;
        }
    }

    static class OSCacheTester<K, V> implements OverHeadTester {

        /**
         * @see org.coconut.cache.policy.concurrent.OverHeadTester#create(int)
         */
        public void test(TestResult runs) throws Exception {
            runs.start();
            for (int i = 0; i < runs.getTotal(); i++) {
                runs.set(i, new com.opensymphony.oscache.base.Cache(true, false, false));
            }
            final int iterations = runs.getIterations();
            for (int i = 0; i < runs.getTotal(); i++) {
                com.opensymphony.oscache.base.Cache c = runs.get(i);
                for (int j = 0; j < iterations; j++) {
                    c.putInCache(keys[j], values[j]);
                }
            }
            runs.stop();
        }

        public String toString() {
            return "OSCache 2.3.2 ("
                    + com.opensymphony.oscache.base.Cache.class.getCanonicalName()
                            .toString() + ")";
        }
    }

    static class WhirlyCacheTester<K, V> implements OverHeadTester {

        /**
         * @see org.coconut.cache.policy.concurrent.OverHeadTester#create(int)
         */
        public void test(TestResult runs) throws Exception {
            runs.start();
            CacheConfiguration conf = new CacheConfiguration();
            String name = "WhirlyCache " + System.nanoTime();
            conf.setName(name);
            conf.setBackend("com.whirlycott.cache.impl.ConcurrentHashMapImpl");
            conf.setMaxSize(runs.getIterations());
            conf.setTunerSleepTime(Integer.MAX_VALUE);
            conf.setPolicy("com.whirlycott.cache.policy.FIFOMaintenancePolicy");
            for (int i = 0; i < runs.getTotal(); i++) {
                runs.set(i, CacheManager.getInstance().createCache(conf));
            }
            final int iterations = runs.getIterations();
            for (int i = 0; i < runs.getTotal(); i++) {
                Cache c = runs.get(i);
                for (int j = 0; j < iterations; j++) {
                    c.store(keys[j], values[j]);
                }
            }
            runs.stop();
            for (String s : CacheManager.getInstance().getCacheNames()) {
                CacheManager.getInstance().destroy(s);
            }
        }

        public String toString() {
            return "WhirlyCache 1.0.1 (" + Cache.class.getCanonicalName().toString()
                    + ")";
        }
    }

    static class DoNothingTester implements OverHeadTester {

        /**
         * @see org.coconut.cache.policy.concurrent.OverHeadTester#create(int)
         */
        public void test(TestResult runs) throws Exception {
            runs.start();
            for (int i = 0; i < runs.getTotal(); i++) {
                runs.set(i, new HashMap());
            }
            final int iterations = runs.getIterations();
            for (int i = 0; i < runs.getTotal(); i++) {
                Map<String, Integer> c = runs.get(i);
                for (int j = 0; j < iterations; j++) {
                    c.put(keys[j], values[j]);
                }
                runs.set(i, null);
            }
            runs.stop();
        }

        public String toString() {
            return "0 bytes used";
        }
    }

    static class Ref implements OverHeadTester {

        private final Class<? extends Map> c;

        /**
         * @param c
         */
        public Ref(final Class<? extends Map> c) {
            this.c = c;
        }

        /**
         * @see org.coconut.cache.policy.concurrent.OverHeadTester#create(int)
         */
        public void test(TestResult runs) throws Exception {
            runs.start();
            for (int i = 0; i < runs.getTotal(); i++) {
                runs.set(i, c.newInstance());
            }
            final int iterations = runs.getIterations();
            for (int i = 0; i < runs.getTotal(); i++) {
                Map<String, Integer> c = runs.get(i);
                for (int j = 0; j < iterations; j++) {
                    c.put(keys[j], values[j]);
                }
            }
            runs.stop();
        }

        public String toString() {
            return c.getCanonicalName().toString();
        }
    }
}

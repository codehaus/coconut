/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.concurrent;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.cache.defaults.memory.UnsynchronizedCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MemoryOverheadTester {
    public interface OverHeadTester<K, V> {
        Map<K, V> create(int runs) throws Exception ;
    }

    static class Put1000 implements OverHeadTester<Integer, Integer> {

        private final OverHeadTester<Integer, Integer> tester;

        /**
         * @param tester
         */
        public Put1000(final OverHeadTester<Integer, Integer> tester) {
            this.tester = tester;
        }

        /**
         * @see org.coconut.cache.policy.concurrent.OverHeadTester#create(int)
         */
        public Map<Integer, Integer> create(int runs) throws Exception {
            Map<Integer, Integer> m = tester.create(runs);
            for (int i = 0; i < runs; i++) {
                m.put(ints[i], ints[i]);
            }
            return m;
        }
    }

    static class Ref<K, V> implements OverHeadTester<K, V> {

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
        public Map<K, V> create(int runs) throws Exception {
            return c.newInstance();
        }

        public String toString() {
            return c.getCanonicalName().toString();
        }
    }

    // 6144, 6145

    public final static int[] DEFAULT_ITERATIONS = new int[] { 1, 10, 100, 1000, 10000,
            100000 };

    private final static Integer[] ints = new Integer[100000];

    private static final Runtime RUNTIME = Runtime.getRuntime();

    static {
        for (int i = 0; i < ints.length; i++) {
            ints[i] = Integer.valueOf(i);
        }
    }

    public static void main(String[] args) throws Exception {
        MemoryOverheadTester t = new MemoryOverheadTester();
        System.out
                .println("|| JDK Implementations \\\\ || 1 Object || 10 Objects || 100 Objects || 1.000 Objects || 10.000 Objects || 100.000 Objects ||");
        t.test(HashMap.class);
        t.test(Hashtable.class);
        t.test(LinkedHashMap.class);
        t.test(TreeMap.class);
        t.test(ConcurrentHashMap.class);
        System.out.println("|| Coconut Cache Implementations \\\\ || || || || || || ||");
        t.test(UnsynchronizedCache.class);
        System.out.println("|| Other Cache Implementations \\\\ || || || || || || ||");

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

    public int test(OverHeadTester<Integer, Integer> map, int iterations)
            throws Exception {
        runGC();
        usedMemory();
        map.create(iterations);
        runGC();
        long memUsageBefore = usedMemory();
        int count = 10;
        Object[] refs = new Object[count];
        for (int i = 0; i < count; ++i) {
            refs[i] = map.create(iterations);
        }
        runGC();
        long memUsageAfter = usedMemory(); // Take an after heap snapshot:
        final int size = Math.round(((float) (memUsageAfter - memUsageBefore)) / count);
        for (int i = 0; i < count; ++i)
            refs[i] = null;
        refs = null;
        return size;
    }

    private void test(Class<? extends Map> c) throws Exception {
        OverHeadTester<Integer, Integer> tt = new Ref<Integer, Integer>(c);
        int[] scores = new int[DEFAULT_ITERATIONS.length];
        for (int i = 0; i < DEFAULT_ITERATIONS.length; i++) {
            int iter = DEFAULT_ITERATIONS[i];
            scores[i] = test(new Put1000(tt), iter);
        }
        System.out.println(toConfluenceString(tt, scores, DEFAULT_ITERATIONS));
    }

    private String toConfluenceString(OverHeadTester o, int[] scores, int[] iterations) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        sb.append(o);
        sb.append(" \\\\ |");
        for (int i = 0; i < scores.length; i++) {
            int s = (int) ((double) scores[i] / iterations[i]);
            sb.append(" ");
            sb.append(s);
            sb.append(" |");
        }
        return sb.toString();
    }

}

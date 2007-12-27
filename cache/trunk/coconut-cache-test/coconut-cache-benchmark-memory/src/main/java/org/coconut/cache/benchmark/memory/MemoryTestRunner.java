/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.cache.defaults.SynchronizedCache;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.ehcache.EHCacheAdapterFactory;
import org.coconut.cache.test.adapter.jbosscache.JbossCacheAdapterFactory;
import org.coconut.cache.test.adapter.jcs.JCSCacheAdapterFactory;
import org.coconut.cache.test.adapter.oscache.OSCacheAdapterFactory;
import org.coconut.cache.test.adapter.whirlycache.WhirlyCacheAdapterFactory;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class MemoryTestRunner {
    public final static int[] DEFAULT_ITERATIONS = new int[] { 1, 10, 100, 1000, 10000 /*, 100000*/};

    // public final static int[] DEFAULT_ITERATIONS = new int[] { 1, 10, 100, 1000, 10000,
    // 100000 /* ,1000000 */};

    public static void main(String[] args) throws Exception {
        MemoryTestRunner t = new MemoryTestRunner();
        System.out
                .println("|| JDK Implementations \\\\ || 1 Object || 10 Objects || 100 Objects || 1.000 Objects || 10.000 Objects || 100.000 Objects || Elements/MB ||");
        t.test(new MapMemoryOverheadTest(ConcurrentHashMap.class));
        t.test(new MapMemoryOverheadTest(HashMap.class));
        t.test(new MapMemoryOverheadTest(Hashtable.class));
        t.test(new MapMemoryOverheadTest(LinkedHashMap.class));
        t.test(new MapMemoryOverheadTest(TreeMap.class));
        System.out.println("|| Coconut Cache Implementations \\\\ || || || || || || || ||");
        t.test(new MapMemoryOverheadTest(UnsynchronizedCache.class));
        t.test(new MapMemoryOverheadTest(SynchronizedCache.class));
        System.out.println("|| Other Cache Implementations \\\\ || || || || || || || ||");
        t.test(new EHCacheAdapterFactory());
        t.test(new JbossCacheAdapterFactory());
        t.test(new JCSCacheAdapterFactory());
        t.test(new OSCacheAdapterFactory());
        t.test(new WhirlyCacheAdapterFactory());
    }
    void test(CacheAdapterFactory tt) throws Exception {
        test(new MapMemoryOverheadTest(tt));
    }
    void test(MapMemoryOverheadTest tt) throws Exception {
        long[] scores = new long[DEFAULT_ITERATIONS.length];
        for (int i = 0; i < DEFAULT_ITERATIONS.length; i++) {
            int iter = DEFAULT_ITERATIONS[i];
            scores[i] = tt.test2(iter);
        }
        System.out.println(toConfluenceString(tt.toString(), scores, DEFAULT_ITERATIONS));
    }

    private String toConfluenceString(String name, long[] scores, int[] iterations) {
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

}

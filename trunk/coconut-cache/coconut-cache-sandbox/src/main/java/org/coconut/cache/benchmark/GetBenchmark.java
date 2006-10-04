/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.apm.benchmark.Benchmark;
import org.coconut.apm.benchmark.BenchmarkRunner;
import org.coconut.apm.benchmark.SimpelRandom;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class GetBenchmark implements Benchmark {

    private final Map<Integer, Integer> map;

    private final int size;

    private final Integer[] keyValues;

    private final SimpelRandom random;

    public GetBenchmark(Map<Integer, Integer> map, int size) {
        if (map == null) {
            throw new NullPointerException("map is null");
        } else if (size < 0) {
            throw new IllegalArgumentException();
        }
        this.size = size;
        random = new SimpelRandom(size);
        keyValues = new Integer[size];
        for (int i = 0; i < size; i++) {
            keyValues[i] = i;
        }
        this.map = map;
    }

    private void getAmount(final int amount) {
        for (int i = 0; i < amount; i++) {
            Integer v = keyValues[random.next(size)];
            map.get(v);
        }

    }

    public static void main(String[] args) throws Exception {
        Map<Integer, Integer> m;
        m = new ConcurrentHashMap<Integer, Integer>();
        // m = new LinkedHashMap<Integer, Integer>();
        // m = new UnsafePocketCache<Integer, Integer>((ValueLoader)
        // PocketCaches
        // .nullLoader(), 10000);
        // m = new SoftReferenceCache<Integer, Integer>((ValueLoader)
        // PocketCaches
        // .nullLoader(), 5000);
        GetBenchmark g = new GetBenchmark(m, 1000000);
        BenchmarkRunner.run(g, 10000000);
    }

    /**
     * @see org.coconut.cache.benchmark.Benchmark#getDescription()
     */
    public String getDescription() {
        return "Benchmarks the get() method of a Map";
    }

    public void benchmark(int iterations) {
        getAmount(iterations);
    }

    /**
     * @see org.coconut.cache.benchmark.Benchmark#warmup()
     */
    public void warmup() throws Exception {
        for (int i = 0; i < size; i++) {
            Integer v = keyValues[i];
            map.put(v, v);
        }
        getAmount(size * 10);
    }

    /**
     * @see org.coconut.core.Named#getName()
     */
    public String getName() {
        return "map.get() benchmark";
    }
}

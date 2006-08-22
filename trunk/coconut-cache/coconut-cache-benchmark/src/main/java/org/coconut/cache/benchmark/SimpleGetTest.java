/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.benchmark;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.memory.UnlimitedCache;
import org.coconut.cache.policy.Policies;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class SimpleGetTest {

    public static void main(String[] args) {
        final int size = 1000000;
        Integer[] ints = new Integer[size];
        for (int i = 0; i < size; i++) {
            ints[i] = i;
        }
        List shuffleMe = Arrays.asList(ints);
        Collections.shuffle(shuffleMe);
        Integer[] shuffled = (Integer[]) shuffleMe.toArray();
        CacheConfiguration cc = CacheConfiguration.newConf();
        cc.eviction().setPolicy(Policies.newLRU()).setMaximumCapacity(100000);

        Map map;
        map = new UnlimitedCache(cc);
        //map = new HashMap();
        // map = CacheBuilder.create().addCache(Policies.newFIFO(),
        // 10000).build();
        // map = new ConcurrentHashMap();
        for (int i = 0; i < size; i++) {
            map.put(ints[i], map);
        }
        long start = System.nanoTime();

        for (int i = 0; i < size; i++) {
            map.get(shuffled[i]);
        }
        long end = System.nanoTime();
        long duration = end - start;
        long millies = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS);
        System.out.println(millies);
    }

}

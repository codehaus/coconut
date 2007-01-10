/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.benchmark;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.memory.UnsynchronizedCache;
import org.coconut.cache.policy.Policies;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        int tests = 1000 * 1000;
        CacheConfiguration<Integer, Object> conf = CacheConfiguration.newConf();
        conf.eviction().setMaximumCapacity(10000);
        conf.eviction().setPolicy(Policies.newLRU());
        //conf.setPolicy(Policies.newMRU());
        Map<Integer, Object> uc = new UnsynchronizedCache<Integer, Object>(conf);
        // Map<Integer, String> uc = new HashMap<Integer, String>();

        String foo = "dfdf";
        long timer = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            uc.put(i, foo);
        }
        long finist = System.nanoTime();
        System.out.println(TimeUnit.MILLISECONDS.convert(finist - timer,
                TimeUnit.NANOSECONDS));

        Thread.sleep(200000);

        System.out.println("bye");
        //Collections
    }
}

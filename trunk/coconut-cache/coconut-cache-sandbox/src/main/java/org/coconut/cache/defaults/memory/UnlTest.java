/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults.memory;

import static org.coconut.filter.ComparisonFilters.between;
import static org.coconut.filter.ComparisonFilters.greatherThen;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheFilters;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.tck.util.IntegerToStringLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnlTest {
    public static void maing(String[] args) {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.newConf();
        cc.backend().setBackend(new IntegerToStringLoader());
        UnsynchronizedCache uc = new UnsynchronizedCache(cc);

        uc.loadAsync(3);
        System.out.println(uc.get(5));
        System.out.println(uc.getHitStat());
        // 
    }

    public static void main(String[] args) throws InterruptedException {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.newConf();
        cc.backend().setBackend(new IntegerToStringLoader());
        cc.eviction().setMaximumSize(10000);
        cc.eviction().setPolicy(Policies.newLRU());
        UnsynchronizedCache uc = new UnsynchronizedCache(cc);
        for (int i = 0; i < 1000; i++) {
            uc.put(i, "" + i);
        }

        for (int i = 0; i < 1000000; i++) {
            uc.get(i % 13030);
        }
        Thread.sleep(500);
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            uc.get(i % 12323);
            uc.put(i % 19999, i % 19999);
        }
        System.out.println(uc.size());
        System.out.println(System.nanoTime() - start);
        System.out.println(uc.getHitStat());
        System.out.println(uc.getGroup());
    }

    public static void mains(String[] args) {
        Cache<Integer, String> c = new UnsynchronizedCache<Integer, String>();
        for (int i = 0; i < 256; i++) {
            c.put(i, Integer.toHexString(i));
        }
//        for (CacheEntry entry : CacheFilters.queryByKey(c, between(40, 45))) {
//            System.out.println(entry);
//        }
//        System.out.println("------");
//
//        for (CacheEntry entry : CacheFilters.queryByValue(c, greatherThen("f5"))) {
//            System.out.println(entry);
//        }

    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.harness;

import java.util.HashSet;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.policy.paging.RandomPolicy;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.spi.ReplacementPolicy;
import org.coconut.core.EventProcessor;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class HarnessTest extends CacheHarnessBundle {

    private static int last = (int) (System.currentTimeMillis() ^ (System.nanoTime() >>> 32));

    int CACHE_SIZE = 50000;

    @Test
    public void testPutExpireEventBus() {
        CacheConfiguration conf = newConf();
        conf.event();
        conf.eviction().setMaximumSize(CACHE_SIZE);
        ReplacementPolicy rp = new RandomPolicy();
        conf.eviction().setPolicy(rp);
        final HashSet<Integer> ha = new HashSet<Integer>();
        Cache<Integer, Integer> cc = (Cache) newCache(conf);
        cc.getService(CacheEventService.class).subscribe(
                new EventProcessor<CacheEvent>() {
                    public void process(CacheEvent event) {
                        if (event instanceof CacheEntryEvent.ItemRemoved) {
                            CacheEntryEvent.ItemRemoved e = (ItemRemoved) event;
                            // System.out.println(e.getKey());
                            assertTrue(ha.remove(e.getKey()));
                        }
                    }
                });
        for (int i = 0; i < CACHE_SIZE; i++) {
            int ia = nextInt();
            cc.put(ia, ia);
            ha.add(ia);
        }
        assertEquals(CACHE_SIZE, cc.size());
        start();
        long iters = 0;
        while (!isDone()) {
            iters++;
            for (int i = 0; i < 1000; i++) {
                int ia = nextInt();
                cc.put(ia, ia);
                ha.add(ia);
                assertEquals(CACHE_SIZE, cc.size());
            }
        }
        cc.getService(CacheEventService.class).unsubscribeAll();
        for (Integer i : ha) {
            assertEquals(i, cc.remove(i));
        }
        assertEquals(0, cc.size());
        printTime();
        System.out.println(iters);
    }

    @Test
    public void testPutExpire() {
        CacheConfiguration conf = newConf();
        conf.eviction().setMaximumSize(CACHE_SIZE);
        ReplacementPolicy rp = new RandomPolicy();
        conf.eviction().setPolicy(rp);

        HashSet<Integer> ha = new HashSet<Integer>();
        Cache<Integer, Integer> cc = (Cache) newCache(conf);
        for (int i = 0; i < CACHE_SIZE; i++) {
            int ia = nextInt();
            cc.put(ia, ia);
            ha.add(ia);
        }
        assertEquals(CACHE_SIZE, cc.size());
        start();
        long iters = 0;
        while (!isDone()) {
            iters++;
            for (int i = 0; i < 1000; i++) {
                int ia = nextInt();
                cc.put(ia, ia);
                assertEquals(CACHE_SIZE, cc.size());
            }
        }
        printTime();
        System.out.println(iters);
    }


    static int nextInt() {
        int y = last;
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        last = y;
        return y;
    }

}

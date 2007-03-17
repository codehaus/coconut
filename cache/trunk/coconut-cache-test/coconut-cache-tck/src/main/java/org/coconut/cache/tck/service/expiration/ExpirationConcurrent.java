/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionUtils.M1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.CountdownLatchLoader;
import org.coconut.core.Clock;

public class ExpirationConcurrent extends ExpirationTestBundle {

    /**
     * Test the Strict policy.
     * 
     * @throws InterruptedException
     */
    public void testStrict() throws InterruptedException {
        long s = 1024;
        int gets = 0;
        int failures = 0;
        for (;;) {
            gets = 0;
            CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
            Cache<Integer, String> c = newCache(cc.serviceExpiration().c().setClock(
                    Clock.DEFAULT_CLOCK));
            long earlyStart = Clock.DEFAULT_CLOCK.relativeTime();
            service.put(M1.getKey(), M1.getValue(), s, TimeUnit.NANOSECONDS);
            long lateStart = Clock.DEFAULT_CLOCK.relativeTime();
            for (;;) {
                long earlyNow = Clock.DEFAULT_CLOCK.relativeTime();
                String value = c.get(M1.getKey());
                long lateNow = Clock.DEFAULT_CLOCK.relativeTime();
                if (value != null) {
                    assertEquals(M1.getValue(), value);
                    assertFalse(earlyNow - lateStart > s);
                    if (gets++ >= 5) {
                        if (failures == 0) {
                            s = 1;
                            break;
                        }
                        return;
                    }
                } else {
                    assertTrue(lateNow - earlyStart > s);
                    s = s * 2;
                    failures++;
                    break;
                }
            }
        }
    }

    public void testStrictThreaded() throws InterruptedException {
        final AtomicLong counts = new AtomicLong();
        CountdownLatchLoader loader = CountdownLatchLoader.integerToStringLoader(1);
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        final Cache<Integer, String> c = newCache(cc.serviceLoading().setBackend(loader).c()
                .setClock(Clock.DEFAULT_CLOCK));

        service.put(M1.getKey(), "ZXCW", 1, TimeUnit.NANOSECONDS);

        // assert c.peek(M1.getValue()) ==null

        Runnable r = new Runnable() {
            public void run() {
                assertEquals(M1.getValue(), c.get(M1.getKey()));
                counts.incrementAndGet();
            }
        };
        int threads = 10;
        Thread[] t = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            t[i] = new Thread(r);
            t[i].start();
        }
        Thread.sleep(50);
        assertEquals(0, counts.get());
        loader.countDown();
        for (Thread th : t) {
            th.join();
        }
        assertEquals(threads, counts.get());
        assertEquals(1, loader.getNumberOfLoads());
    }

    public void testLazyThreaded() throws InterruptedException {
        final AtomicLong counts = new AtomicLong();
        CountdownLatchLoader loader = CountdownLatchLoader.integerToStringLoader(1);
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        final Cache<Integer, String> c = newCache(cc.serviceLoading().setBackend(loader).c()
                .setClock(Clock.DEFAULT_CLOCK));

        service.put(M1.getKey(), "ZXCW", 1, TimeUnit.NANOSECONDS);

        // assert c.peek(M1.getValue()) ==null
        Runnable r = new Runnable() {
            public void run() {
                String str = c.get(M1.getKey());
                assertTrue(str.equals("ZXCW") || str.equals(M1.getValue()));
                counts.incrementAndGet();
            }
        };
        Runnable r1 = new Runnable() {
            public void run() {
                assertEquals("ZXCW", c.get(M1.getKey()));
            }
        };
        int threads = 10;
        Thread[] t = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            t[i] = new Thread(r);
            t[i].start();
        }
        Thread.sleep(30);
        assertTrue(counts.get() >= threads - 1);
        loader.countDown();
        for (Thread th : t) {
            th.join();
        }
        assertEquals(threads, counts.get());
        assertEquals(1, loader.getNumberOfLoads());
    }

}

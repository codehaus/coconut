/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.cache.CacheLoader;


public class CountdownLatchLoader implements CacheLoader<Integer, String> {

    private final AtomicLong loads = new AtomicLong();

    private final AtomicLong loadAlls = new AtomicLong();

    private final CountDownLatch latch;

    private final CountDownLatch beforeLoad;

    private final CacheLoader<Integer, String> loader;

    public CountdownLatchLoader(CacheLoader<Integer, String> loader, int counts) {
        this(loader, counts, 0);
    }

    public CountdownLatchLoader(CacheLoader<Integer, String> loader,
            int counts, int beforeLoads) {
        this.latch = new CountDownLatch(counts);
        this.loader = loader;
        this.beforeLoad = new CountDownLatch(beforeLoads);
    }

    public String load(Integer key) throws Exception {
        beforeLoad.countDown();
        latch.await();
        loads.incrementAndGet();
        return loader.load(key);
    }

    public Map<Integer, String> loadAll(Collection<? extends Integer> keys)
            throws Exception {
        beforeLoad.countDown();
        latch.await();
        loadAlls.incrementAndGet();
        return loader.loadAll(keys);
    }

    public void countDown() {
        latch.countDown();
    }
    
    public CountDownLatch beforeLoad() {
        return beforeLoad;
    }

    public static CountdownLatchLoader integerToStringLoader(int counts) {
        return new CountdownLatchLoader(new IntegerToStringLoader(), counts);
    }

    public long getNumberOfLoads() {
        return loads.get();
    }

    public long getNumberOfLoadAlls() {
        return loadAlls.get();
    }
    
}

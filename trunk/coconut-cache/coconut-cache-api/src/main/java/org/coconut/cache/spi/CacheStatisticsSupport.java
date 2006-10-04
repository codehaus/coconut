/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.ArrayList;
import java.util.Collection;

import org.coconut.apm.monitor.LongCounter;
import org.coconut.apm.monitor.LongSamplingCounter;
import org.coconut.cache.Cache;
import org.coconut.cache.Caches;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class CacheStatisticsSupport {


    // number of clears, number of loads, loaded elements,
    // number of queries, number of removes, number of statistics resets
    // number of added,  number of new elements

    private LongCounter clearCount;

    private LongCounter evictCount;

    // last clear
    // lastEvict
    // lastReset
    private LongCounter numberOfLoads;

    private LongCounter numberOfRemoves;

    private LongCounter numberOfStatisticsReset;

    
    public final static String CACHE_HIT_COUNTER = "hits";

    public final static String CACHE_MISS_COUNTER = "misses";

    public final static String CACHE_EXPIRED_COUNTER = "items expired";

    public final static String CACHE_EVICTED_COUNTER = "items evicted";

    public final static String CACHE_EVICT_INVOCATION_COUNTER = "evict() invocations";

    public final static String CACHE_CLEAR_COUNTER = "cache clear counter";

    public final static String CACHE_LOAD_COUNTER = "cache load hits";

    public final static String CACHE_HIT_TIME_COUNTER = "cache hit time";

    private final LongCounter hits;

    private final LongCounter misses;

    private final LongCounter expired;

    private final LongCounter evicted;

    private final LongCounter callsToEvict;

    private final LongSamplingCounter evictTime;

    private final LongSamplingCounter hitTime;

    private final LongSamplingCounter missTime;

    private LongSamplingCounter putTime;
    
    CacheStatisticsSupport(boolean isConcurrent) {
        hits = LongCounter.newConcurrent(CACHE_HIT_COUNTER,
                "number of cache hits (since start or last reset)");
        misses = LongCounter.newConcurrent(CACHE_MISS_COUNTER,
                "number of cache misses (since start or last reset)");
        expired = LongCounter.newConcurrent(CACHE_EXPIRED_COUNTER,
                "number of items that has expired (since start or last reset)");
        evicted = LongCounter.newConcurrent(CACHE_EVICTED_COUNTER,
                "number of items that has been evicted (since start or last reset)");
        evictTime = new LongSamplingCounter("evict time (nano)");
        hitTime = new LongSamplingCounter("evict time (nano)");
        missTime = new LongSamplingCounter("evict time (nano)");
        callsToEvict = LongCounter.newConcurrent(CACHE_EVICT_INVOCATION_COUNTER,
                "invocations of evict() on the cache (since start or last reset)");
    }

    public static CacheStatisticsSupport createConcurrent() {
        return new CacheStatisticsSupport(true);
    }

    public long evictStarted() {
        return System.nanoTime();
    }

    public void evictStopped(long start) {
        long time = System.nanoTime() - start;
        evictTime.report(time);
        callsToEvict.incrementAndGet();
    }

    public long getStarted() {
        return System.nanoTime();
    }

    public void getHitStopped(long start) {
        long time = System.nanoTime() - start;
        hitTime.report(time);
        hits.incrementAndGet();
    }

    public void getMissStopped(long start) {
        long time = System.nanoTime() - start;
        missTime.report(time);
        misses.incrementAndGet();
    }

    public void evictTimed(long nanos) {

    }

    public void hitsIncrement() {
        hits.incrementAndGet();
    }

    public void missesIncrement() {
        hits.incrementAndGet();
    }

    public void evictedIncrement() {
        evicted.incrementAndGet();
    }

    public void expiredIncrement() {
        evicted.incrementAndGet();
    }

    public void expiredIncrement(int count) {
        evicted.addAndGet(count);
    }

    public void reset() {
        hits.reset();
        misses.reset();
    }

    public Cache.HitStat getHitStat() {
        return Caches.newImmutableHitStat(hits.get(), misses.get());
    }

    public Collection<Object> getMetrics() {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(hits);
        list.add(misses);
        return list;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hits: ");
        sb.append(hits);
        sb.append("\nmisses");
        sb.append(misses);
        sb.append("\nhit time: ");
        sb.append(hitTime);
        sb.append("\nmiss time: ");
        sb.append(missTime);

        return sb.toString();
    }
}

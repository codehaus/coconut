/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.ArrayList;
import java.util.Collection;

import org.coconut.apm.monitor.DateSampler;
import org.coconut.apm.monitor.LongCounter;
import org.coconut.apm.monitor.LongSamplingCounter;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.Caches;
import org.coconut.core.Clock;

/**
 * possible to disable certain statistics
 * <p>
 * synchronous, concurrent, unsynchornized versions
 * <p>
 * do we want to provide cache entries?
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class CacheStatisticsSupport<K, V> {

    // number of loads, loaded elements, number of queries,
    // number of added, number of new elements

    public final static String CACHE_CLEAR_COUNTER = "cache clear counter";

    public final static String CACHE_CLEAR_LASTTIME = "cache clear counter";

    public final static String CACHE_CLEAR_TIMER = "cache clear counter";

    public final static String CACHE_EVICT_COUNTER = "cache evicts";

    public final static String CACHE_EVICT_LASTTIME = "cache clear counter";

    public final static String CACHE_EVICT_TIMER = "duration of cache evicts";

    public final static String CACHE_RESET_COUNTER = "cache evicts";

    public final static String CACHE_RESET_LASTTIME = "cache clear counter";

    public final static String ENTRY_EVICTED_COUNTER = "items evicted";

    public final static String ENTRY_EVICTED_TIMER = "items evicted";

    public final static String ENTRY_EXPIRED_COUNTER = "items expired";

    public final static String ENTRY_HIT_COUNTER = "cache hits";

    public final static String ENTRY_HIT_TIMER = "cache hit time";

    public final static String ENTRY_MISS_COUNTER = "misses";

    public final static String ENTRY_MISS_TIMER = "misses";

    public final static String ENTRY_PUT_COUNTER = "misses";

    public final static String ENTRY_PUT_TIMER = "misses";

    public final static String ENTRY_REMOVE_COUNTER = "misses";

    public final static String ENTRY_REMOVE_TIMER = "misses";

    public static CacheStatisticsSupport createConcurrent() {
        return new CacheStatisticsSupport(true);
    }

    /* Cache Statistics */
    private final LongCounter cacheClearCount;

    private final DateSampler cacheClearLast;

    private final LongSamplingCounter cacheClearTime;

    private final LongCounter cacheEvictCount;

    private final DateSampler cacheEvictLast;

    private final LongSamplingCounter cacheEvictTime;

    private final LongCounter cacheStatisticsResetCount;

    private final DateSampler cacheStatisticsResetLast;

    /* Cache Entry Statistics */

    private final LongCounter entryEvictedCount;

    private final LongSamplingCounter entryEvictedTime;

    private final LongCounter entryExpiredCount;

    private final LongCounter entryGetHitCount;

    private final LongSamplingCounter entryGetHitTime;

    private final LongCounter entryGetMissCount;

    private final LongSamplingCounter entryGetMissTime;

    private final LongCounter entryPutCount;

    private final LongSamplingCounter entryPutTime;

    private final LongCounter entryRemoveCount;

    private final LongSamplingCounter entryRemoveTime;

    CacheStatisticsSupport(boolean isConcurrent) {
        Clock c = Clock.MILLI_CLOCK;
        // cache counters

        // invocations of evict() on the cache (since start or last reset)
        cacheEvictCount = LongCounter.newConcurrent(CACHE_EVICT_COUNTER,
                getDesc(CACHE_EVICT_COUNTER));
        cacheEvictLast = new DateSampler(CACHE_EVICT_LASTTIME,
                getDesc(CACHE_EVICT_LASTTIME), c);
        cacheEvictTime = new LongSamplingCounter(CACHE_EVICT_TIMER,
                getDesc(CACHE_EVICT_TIMER));

        cacheClearCount = LongCounter.newConcurrent(CACHE_CLEAR_COUNTER,
                getDesc(CACHE_CLEAR_COUNTER));
        cacheClearLast = new DateSampler(CACHE_CLEAR_LASTTIME,
                getDesc(CACHE_CLEAR_LASTTIME), c);
        cacheClearTime = new LongSamplingCounter(CACHE_CLEAR_TIMER,
                getDesc(CACHE_CLEAR_TIMER));

        cacheStatisticsResetCount = LongCounter.newConcurrent(CACHE_RESET_COUNTER,
                getDesc(CACHE_RESET_COUNTER));
        cacheStatisticsResetLast = new DateSampler(CACHE_RESET_LASTTIME,
                getDesc(CACHE_RESET_LASTTIME), c);

        // entry counters

        entryEvictedCount = LongCounter.newConcurrent(ENTRY_EVICTED_COUNTER,
                getDesc(ENTRY_EVICTED_COUNTER));
        entryEvictedTime = new LongSamplingCounter(ENTRY_EVICTED_TIMER,
                getDesc(ENTRY_EVICTED_TIMER));
        entryExpiredCount = LongCounter.newConcurrent(ENTRY_EXPIRED_COUNTER,
                getDesc(ENTRY_EXPIRED_COUNTER));

        entryGetHitCount = LongCounter.newConcurrent(ENTRY_HIT_COUNTER,
                getDesc(ENTRY_HIT_COUNTER));
        entryGetHitTime = new LongSamplingCounter(ENTRY_HIT_TIMER,
                getDesc(ENTRY_HIT_TIMER));

        entryGetMissCount = LongCounter.newConcurrent(ENTRY_MISS_COUNTER,
                getDesc(ENTRY_MISS_COUNTER));
        entryGetMissTime = new LongSamplingCounter(ENTRY_MISS_TIMER,
                getDesc(ENTRY_MISS_TIMER));

        entryPutCount = LongCounter.newConcurrent(ENTRY_PUT_COUNTER,
                getDesc(ENTRY_PUT_COUNTER));
        entryPutTime = new LongSamplingCounter(ENTRY_PUT_TIMER, getDesc(ENTRY_PUT_TIMER));

        entryRemoveCount = LongCounter.newConcurrent(ENTRY_REMOVE_COUNTER,
                getDesc(ENTRY_REMOVE_COUNTER));
        entryRemoveTime = new LongSamplingCounter(ENTRY_REMOVE_TIMER,
                getDesc(ENTRY_REMOVE_TIMER));
    }

    public long cacheClearStart(Cache<K, V> cache) {
        return System.nanoTime();
    }

    public long cacheClearStop(Cache<K, V> cache, long start) {
        long time = System.nanoTime() - start;
        cacheClearLast.run();
        cacheClearTime.report(time);
        cacheClearCount.incrementAndGet();
        return time;
    }

    public long cacheEvictStart(Cache<K, V> cache) {
        return System.nanoTime();
    }

    public long cacheEvictStop(Cache<K, V> cache, long start) {
        long time = System.nanoTime() - start;
        cacheEvictLast.run();
        cacheEvictTime.report(time);
        cacheEvictCount.incrementAndGet();
        return time;
    }

    public void cacheReset() {
        cacheStatisticsResetLast.run();
        entryGetHitCount.reset();
        entryGetMissCount.reset();
        // TODO reset others;
        cacheStatisticsResetCount.incrementAndGet();
    }

    public long entryEvictedStart(CacheEntry<K, V> entry) {
        return System.nanoTime();
    }

    public long entryEvictedStop(CacheEntry<K, V> entry, long start) {
        long time = System.nanoTime() - start;
        entryEvictedTime.report(time);
        entryEvictedCount.incrementAndGet();
        return time;
    }

    public void entryExpired() {
        entryExpiredCount.incrementAndGet();
    }

    public void entryExpired(int count) {
        entryExpiredCount.addAndGet(count);
    }

    public long entryGetStart() {
        return System.nanoTime();
    }

    public long entryGetStop(CacheEntry<K, V> entry, long start, boolean wasHit) {
        long time = System.nanoTime() - start;
        if (wasHit) {
            entryGetHitTime.report(time);
            entryGetHitCount.incrementAndGet();
        } else {
            entryGetMissTime.report(time);
            entryGetMissCount.incrementAndGet();
        }
        return time;
    }

    public long entryRemoveStart() {
        return System.nanoTime();
    }

    public long entryRemoveStop(long start) {
        long time = System.nanoTime() - start;
        entryRemoveTime.report(time);
        entryRemoveCount.incrementAndGet();
        return time;
    }

    public long entryPutStart() {
        return System.nanoTime();
    }

    public long entryPutStop(long start) {
        long time = System.nanoTime() - start;
        entryPutTime.report(time);
        entryPutCount.incrementAndGet();
        return time;
    }

    public Cache.HitStat getHitStat() {
        return Caches
                .newImmutableHitStat(entryGetHitCount.get(), entryGetMissCount.get());
    }

    public Collection<Object> getMetrics() {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(entryGetHitCount);
        list.add(entryGetMissCount);
        return list;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hits: ");
        sb.append(entryGetHitCount);
        sb.append("\nmisses");
        sb.append(entryGetMissCount);
        sb.append("\nhit time: ");
        sb.append(entryGetHitTime);
        sb.append("\nmiss time: ");
        sb.append(entryGetMissTime);

        return sb.toString();
    }

    private String getDesc(String key) {
        return Ressources.getString(CacheStatisticsSupport.class, key);
    }
}

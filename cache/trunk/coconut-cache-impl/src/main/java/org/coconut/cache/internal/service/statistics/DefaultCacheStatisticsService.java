/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.internal.service.service.InternalCacheServiceManager;
import org.coconut.cache.internal.service.util.Resources;
import org.coconut.cache.service.statistics.CacheHitStat;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.core.Clock;
import org.coconut.management.ManagedGroup;
import org.coconut.management.annotation.ManagedAttribute;

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
public final class DefaultCacheStatisticsService<K, V> extends
        AbstractInternalCacheService implements 
        CacheStatisticsService {

    // number of loads, loaded elements, number of queries,
    // number of added, number of new elements

    public final static String CACHE_CLEAR_COUNTER = "Cache clear counter";

    public final static String CACHE_CLEAR_LASTTIME = "Cache clear counter";

    public final static String CACHE_CLEAR_TIMER = "Cache clear counter";

    public final static String CACHE_EVICT_COUNTER = "Cache evicts";

    public final static String CACHE_EVICT_LASTTIME = "Cache clear counter";

    public final static String CACHE_EVICT_TIMER = "duration of cache evicts";

    public final static String CACHE_RESET_COUNTER = "Cache evicts";

    public final static String CACHE_RESET_LASTTIME = "Cache clear counter";

    public final static String ENTRY_EVICTED_COUNTER = "items evicted";

    public final static String ENTRY_EVICTED_TIMER = "items evicted";

    public final static String ENTRY_EXPIRED_COUNTER = "items expired";

    public final static String ENTRY_HIT_COUNTER = "Cache hits";

    public final static String ENTRY_HIT_TIMER = "Cache hit time";

    public final static String ENTRY_MISS_COUNTER = "Cache misses";

    public final static String ENTRY_MISS_TIMER = "Cache miss time";

    public final static String ENTRY_PUT_COUNTER = "Caches puts";

    public final static String ENTRY_PUT_TIMER = "Cache puts time";

    public final static String ENTRY_REMOVE_COUNTER = "Cache removes";

    public final static String ENTRY_REMOVE_TIMER = "Cache remove times";

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

    private final AtomicLong entryGetHitSizeCount = new AtomicLong();

    private final AtomicDouble entryGetHitCostCount = new AtomicDouble();

    private final LongSamplingCounter entryGetHitTime;

    private final LongCounter entryGetMissCount;

    private final AtomicLong entryGetMissSizeCount = new AtomicLong();

    private final AtomicDouble entryGetMissCostCount = new AtomicDouble();

    private final LongSamplingCounter entryGetMissTime;

    private final LongCounter entryPutCount;

    private final LongSamplingCounter entryPutTime;

    private final LongCounter entryRemoveCount;

    private final LongSamplingCounter entryRemoveTime;

    public DefaultCacheStatisticsService(InternalCacheServiceManager manager,
            CacheConfiguration<K, V> conf) {
        super("statistics");
        Clock c = Clock.DEFAULT_CLOCK;
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

    volatile long started;

    public long beforeCacheClear(Cache<K, V> cache) {
        return System.nanoTime();
    }

    public void afterCacheClear(Cache<K, V> cache, long start, int size, long capacity,
            Collection<? extends CacheEntry<K, V>> removed) {
        long time = System.nanoTime() - start;
        // TODO what about removed?
        cacheClearLast.run();
        cacheClearTime.report(time);
        cacheClearCount.incrementAndGet();
    }

    /**
     * @see org.coconut.cache.service.servicemanager.AbstractCacheService#initialize(org.coconut.cache.CacheConfiguration,
     *      java.util.Map)
     */
    @Override
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        serviceMap.put(CacheStatisticsService.class, this);
    }

    public long beforeCacheEvict(Cache<K, V> cache) {
        return System.nanoTime();
    }

    public void afterCacheEvict(Cache<K, V> cache, long started, int size,
            int previousSize, long capacity, long previousCapacity,
            Collection<? extends CacheEntry<K, V>> evicted,
            Collection<? extends CacheEntry<K, V>> expired) {
        long time = System.nanoTime() - started;
        cacheEvictLast.run();
        cacheEvictTime.report(time);
        cacheEvictCount.incrementAndGet();
        entryEvictedCount.addAndGet(evicted.size());
        entryExpiredCount.addAndGet(expired.size());
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

    public long beforeGet(Cache<K, V> cache, K key) {
        return System.nanoTime();
    }

    public void afterGet(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, K key,
            CacheEntry<K, V> prev, CacheEntry<K, V> newE, boolean isExpired) {
        long time = System.nanoTime() - started;

        // keep statistics about null loads

        // we might also want to keep statistics about byte hit count..
        // and cost hit count...
        // total hit cost / total miss cost
        boolean isHit = !isExpired && prev != null;

        if (prev == null) {
            if (newE == null) {

            } else {

            }
        }
        // TODO fix
        if (isHit) {
            entryGetHitTime.report(time);
            entryGetHitCount.incrementAndGet();
            double cost = prev.getCost();
            entryGetHitCostCount.addAndGet(cost);
            long size = prev.getSize();
            entryGetHitSizeCount.addAndGet(size);
        } else {
            entryGetMissTime.report(time);
            entryGetMissCount.incrementAndGet();
            // if (entry != null) {
            // double cost = entry.getCost();
            // entryGetMissCostCount.addAndGet(cost);
            // long size = entry.getSize();
            // entryGetMissSizeCount.addAndGet(size);
            // }
        }
    }

    public long beforeRemove(Cache<K, V> cache, Object key) {
        return System.nanoTime();
    }

    public void afterRemove(Cache<K, V> cache, long start, CacheEntry<K, V> removed) {
        long time = System.nanoTime() - start;
        entryRemoveTime.report(time);
        entryRemoveCount.incrementAndGet();
    }

    public long beforeReplace(Cache<K, V> cache) {
        return System.nanoTime();
    }

    public void afterReplace(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evicted, CacheEntry<K, V> oldEntry,
            CacheEntry<K, V> newEntry) {
        long time = System.nanoTime() - started;
        entryPutTime.report(time);
        entryPutCount.incrementAndGet();
    }

    public long beforePut(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return System.nanoTime();
    }

    public long beforePut(Cache<K, V> cache, Object key, Object value) {
        return System.nanoTime();
    }

    public long beforePutAll(Cache<K, V> cache,
            Collection<? extends CacheEntry<K, V>> entries) {
        return System.nanoTime();
    }

    public void afterPutAll(Cache<K, V> cache, long start,
            Collection<? extends CacheEntry<K, V>> entries,
            Collection<? extends CacheEntry<K, V>> entries2,
            Collection<? extends CacheEntry<K, V>> entries3) {
        long time = System.nanoTime() - start;
        entryPutTime.report(time);
        entryPutCount.addAndGet(entries.size());
    }

    public long entryPutStop(long start) {
        long time = System.nanoTime() - start;
        entryPutTime.report(time);
        entryPutCount.incrementAndGet();
        return time;
    }

    public CacheHitStat getHitStat() {
        return new CacheHitStat(entryGetHitCount.get(), entryGetMissCount.get());
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
        return Resources.lookup(DefaultCacheStatisticsService.class, key.toLowerCase());
    }

    public void addTo(ManagedGroup dg) {
        ManagedGroup m = dg.addChild("Statistics", "");

        ManagedGroup general = m.addChild("General", "");
        general.add(cacheStatisticsResetCount);
        general.add(cacheStatisticsResetLast);
        general.add(entryExpiredCount);

        ManagedGroup clear = m.addChild("Clear", "");
        clear.add(cacheClearCount);
        clear.add(cacheClearLast);
        clear.add(cacheClearTime);

        ManagedGroup eviction = m.addChild("Eviction", "");
        eviction.add(cacheEvictCount);
        eviction.add(cacheEvictLast);
        eviction.add(cacheEvictTime);
        eviction.add(entryEvictedCount);
        eviction.add(entryEvictedTime);

        ManagedGroup access = m.addChild("Access",
                "Statistics regarding access to the cache");
        access.add(entryGetHitCount);
        access.add(entryGetMissCount);
        access.add(entryGetHitTime);
        access.add(entryGetMissTime);
        access.add(new CacheRatio());

        ManagedGroup put = m.addChild("Put", "");
        put.add(entryPutCount);
        put.add(entryPutTime);

        ManagedGroup remove = m.addChild("Remove", "");
        remove.add(entryRemoveCount);
        remove.add(entryRemoveTime);
    }

    public class CacheRatio {
        @ManagedAttribute(defaultValue = "cache hit ratio")
        public double getHitRatio() {
            long hits = entryGetHitCount.get();
            long misses = entryGetMissCount.get();
            final long sum = hits + misses;
            if (sum == 0) {
                return Float.NaN;
            }
            return ((float) hits) / sum;
        }
    }

    public class CacheRatioSize {
        @ManagedAttribute(defaultValue = "cache hit ratio")
        public double getHitRatio() {
            long hits = entryGetHitSizeCount.get();
            long misses = entryGetMissSizeCount.get();
            final long sum = hits + misses;
            if (sum == 0) {
                return Float.NaN;
            }
            return ((float) hits) / sum;
        }
    }

    public class CacheRatioCost {
        @ManagedAttribute(defaultValue = "cache hit ratio")
        public double getHitRatio() {
            double hits = entryGetHitCostCount.get();
            double misses = entryGetMissCostCount.get();
            final double sum = hits + misses;
            if (sum == 0) {
                return Float.NaN;
            }
            return hits / sum;
        }
    }

    public boolean needElementsAfterClear() {
        // TODO Auto-generated method stub
        return false;
    }


    public void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            CacheEntry<K, V> oldEntry, CacheEntry<K, V> newEntry) {
    // TODO Auto-generated method stub

    }


    public void afterTrimToSize(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries) {
    // TODO Auto-generated method stub

    }


    public long beforePutAll(Cache<K, V> cache, Map<? extends K, ? extends V> map) {
        // TODO Auto-generated method stub
        return 0;
    }


    public long beforeReplace(Cache<K, V> cache, K key, V oldValue, V newValue) {
        // TODO Auto-generated method stub
        return 0;
    }


    public long beforeTrimToSize(Cache<K, V> cache) {
        // TODO Auto-generated method stub
        return 0;
    }


    public void doStart() {
        started = System.currentTimeMillis();
    }


    public void shutdown(Executor callback) {

    }

    public void resetStatistics() {
        cacheReset();
    }
}

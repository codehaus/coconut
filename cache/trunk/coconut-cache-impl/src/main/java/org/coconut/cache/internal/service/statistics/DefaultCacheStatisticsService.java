/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.spi.Resources;
import org.coconut.cache.internal.service.statistics.LongCounter.ConcurrentLongCounter;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.statistics.CacheHitStat;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.core.Clock;
import org.coconut.internal.util.AtomicDouble;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;

/**
 * possible to disable certain statistics
 * <p>
 * synchronous, concurrent, unsynchornized versions
 * <p>
 * do we want to provide cache entries?
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class DefaultCacheStatisticsService<K, V> extends AbstractCacheLifecycle implements
        CacheStatisticsService, ManagedLifecycle {

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

    private final LongSamplingCounter cacheEvictTime;

    private final LongCounter cacheStatisticsResetCount;

    private final DateSampler cacheStatisticsResetLast;

    private final LongCounter entryEvictedCount;

    /* Cache Entry Statistics */

    private final LongSamplingCounter entryEvictedTime;

    private final LongCounter entryExpiredCount;

    private final AtomicDouble entryGetHitCostCount = new AtomicDouble();

    private final ConcurrentLongCounter entryGetHitCount;

    private final AtomicLong entryGetHitSizeCount = new AtomicLong();

    private final LongSamplingCounter entryGetHitTime;

    private final AtomicDouble entryGetMissCostCount = new AtomicDouble();

    private final LongCounter entryGetMissCount;

    private final AtomicLong entryGetMissSizeCount = new AtomicLong();

    private final LongSamplingCounter entryGetMissTime;

    private final LongCounter entryPutCount;

    private final LongSamplingCounter entryPutTime;

    private final LongCounter entryRemoveCount;

    private final LongSamplingCounter entryRemoveTime;

    volatile long started;

    public DefaultCacheStatisticsService() {
        Clock c = Clock.DEFAULT_CLOCK;
        // cache counters

        // invocations of evict() on the cache (since start or last reset)
        cacheEvictCount = LongCounter.newConcurrent(CACHE_EVICT_COUNTER,
                getDesc(CACHE_EVICT_COUNTER));
        cacheEvictTime = new LongSamplingCounter(CACHE_EVICT_TIMER, getDesc(CACHE_EVICT_TIMER));

        cacheClearCount = LongCounter.newConcurrent(CACHE_CLEAR_COUNTER,
                getDesc(CACHE_CLEAR_COUNTER));
        cacheClearLast = new DateSampler(CACHE_CLEAR_LASTTIME, getDesc(CACHE_CLEAR_LASTTIME), c);
        cacheClearTime = new LongSamplingCounter(CACHE_CLEAR_TIMER, getDesc(CACHE_CLEAR_TIMER));

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

        entryGetHitCount = LongCounter.newConcurrent(ENTRY_HIT_COUNTER, getDesc(ENTRY_HIT_COUNTER));
        entryGetHitTime = new LongSamplingCounter(ENTRY_HIT_TIMER, getDesc(ENTRY_HIT_TIMER));

        entryGetMissCount = LongCounter.newConcurrent(ENTRY_MISS_COUNTER,
                getDesc(ENTRY_MISS_COUNTER));
        entryGetMissTime = new LongSamplingCounter(ENTRY_MISS_TIMER, getDesc(ENTRY_MISS_TIMER));

        entryPutCount = LongCounter.newConcurrent(ENTRY_PUT_COUNTER, getDesc(ENTRY_PUT_COUNTER));
        entryPutTime = new LongSamplingCounter(ENTRY_PUT_TIMER, getDesc(ENTRY_PUT_TIMER));

        entryRemoveCount = LongCounter.newConcurrent(ENTRY_REMOVE_COUNTER,
                getDesc(ENTRY_REMOVE_COUNTER));
        entryRemoveTime = new LongSamplingCounter(ENTRY_REMOVE_TIMER, getDesc(ENTRY_REMOVE_TIMER));
    }

// public void addTo(ManagedGroup dg) {
// ManagedGroup m = dg.addChild("Statistics", "");
//
// ManagedGroup general = m.addChild("General", "");
// general.add(cacheStatisticsResetCount);
// general.add(cacheStatisticsResetLast);
// general.add(entryExpiredCount);
//
// ManagedGroup clear = m.addChild("Clear", "");
// clear.add(cacheClearCount);
// clear.add(cacheClearLast);
// clear.add(cacheClearTime);
//
// ManagedGroup eviction = m.addChild("Eviction", "");
// eviction.add(cacheEvictCount);
// eviction.add(cacheEvictLast);
// eviction.add(cacheEvictTime);
// eviction.add(entryEvictedCount);
// eviction.add(entryEvictedTime);
//
// ManagedGroup access = m.addChild("Access", "Statistics regarding access to the cache");
// access.add(entryGetHitCount);
// access.add(entryGetMissCount);
// access.add(entryGetHitTime);
// access.add(entryGetMissTime);
// // access.add(new CacheRatio());
//
// ManagedGroup put = m.addChild("Put", "");
// put.add(entryPutCount);
// put.add(entryPutTime);
//
// ManagedGroup remove = m.addChild("Remove", "");
// remove.add(entryRemoveCount);
// remove.add(entryRemoveTime);
// }

    public void afterCacheClear(Cache<K, V> cache, long start,
            Collection<? extends CacheEntry<K, V>> removed, long capacity) {
        long time = getTimeStamp() - start;
        // TODO what about removed?
        cacheClearLast.run();
        cacheClearTime.report(time);
        cacheClearCount.incrementAndGet();
    }

// public void afterCacheEvict(Cache<K, V> cache, long started, int size, int
// previousSize,
// long capacity, long previousCapacity, Collection<? extends CacheEntry<K, V>> evicted,
// Collection<? extends CacheEntry<K, V>> expired) {
// long time = System.nanoTime() - started;
// cacheEvictLast.run();
// cacheEvictTime.report(time);
// cacheEvictCount.incrementAndGet();
// entryEvictedCount.addAndGet(evicted.size());
// entryExpiredCount.addAndGet(expired.size());
// }

// public void afterGet(Cache<K, V> cache, long started,
// Collection<? extends CacheEntry<K, V>> evictedEntries, K key, CacheEntry<K, V> prev,
// CacheEntry<K, V> newE, boolean isExpired) {
// long time = System.nanoTime() - started;
//
// // keep statistics about null loads
//
// // we might also want to keep statistics about byte hit count..
// // and cost hit count...
// // total hit cost / total miss cost
// boolean isHit = !isExpired && prev != null;
//
// if (prev == null) {
// if (newE == null) {
//
// } else {
//
// }
// }
// // TODO fix
// if (isHit) {
// entryGetHitTime.report(time);
// entryGetHitCount.incrementAndGet();
// double cost = prev.getCost();
// entryGetHitCostCount.addAndGet(cost);
// long size = prev.getSize();
// entryGetHitSizeCount.addAndGet(size);
// } else {
// entryGetMissTime.report(time);
// entryGetMissCount.incrementAndGet();
// // if (entry != null) {
// // double cost = entry.getCost();
// // entryGetMissCostCount.addAndGet(cost);
// // long size = entry.getSize();
// // entryGetMissSizeCount.addAndGet(size);
// // }
// }
// }

    public void afterHit(Cache<K, V> cache, long started, K key, CacheEntry<K, V> entry) {
        long time = getTimeStamp() - started;
        entryGetHitTime.report(time);
        entryGetHitCount.incrementAndGet();
        double cost = entry.getCost();
        entryGetHitCostCount.addAndGet(cost);
        long size = entry.getSize();
        entryGetHitSizeCount.addAndGet(size);
    }

    public void afterMiss(Cache<K, V> cache, long started, K key, CacheEntry<K, V> previousEntry,
            CacheEntry<K, V> newEntry, boolean isExpired) {

        long time = getTimeStamp() - started;
        entryGetMissTime.report(time);
        entryGetMissCount.incrementAndGet();
    }

    public void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, CacheEntry<K, V> oldEntry,
            CacheEntry<K, V> newEntry) {}

    public void afterPutAll(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Map<CacheEntry<K, V>, CacheEntry<K, V>> newPrevEntries) {}

    public void afterRemove(Cache<K, V> cache, long start, CacheEntry<K, V> removed) {
        long time = getTimeStamp() - start;
        entryRemoveTime.report(time);
        entryRemoveCount.incrementAndGet();
    }

    public void afterRemoveAll(Cache<K, V> cache, long start, Collection<CacheEntry<K, V>> removed) {
        long time = System.nanoTime() - start;
        entryRemoveTime.report(time);
        entryRemoveCount.addAndGet(removed.size());
    }

// public void afterReplace(Cache<K, V> cache, long started,
// Collection<? extends CacheEntry<K, V>> evicted, CacheEntry<K, V> oldEntry,
// CacheEntry<K, V> newEntry) {
// long time = System.nanoTime() - started;
// entryPutTime.report(time);
// entryPutCount.incrementAndGet();
// }

    public void afterTrimCache(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, int previousSize, int newSize,
            long previousVolume, long newVolume) {}

    public long beforeCacheClear(Cache<K, V> cache) {
        return getTimeStamp();
    }

    public long beforeGet(Cache<K, V> cache, K key) {
        return getTimeStamp();
    }

    public long beforeGetAll(Cache<K, V> cache, Collection<? extends K> keys) {
        return getTimeStamp();
    }

    public void afterGetAll(Cache<K, V> cache, long started, Object[] keys,
            CacheEntry<K, V>[] entries, boolean[] isHit, boolean[] isExpired,
            Map<K, V> loadedEntries) {
        int hits = 0;
        for (boolean element : isHit) {
            if (element) {
                hits++;
            }
        }
        entryGetHitCount.addAndGet(hits);
        entryGetMissCount.addAndGet(isHit.length - hits);
    }

    public long beforePut(Cache<K, V> cache, Object key, Object value) {
        return getTimeStamp();
    }

    public long beforePutAll(Cache<K, V> cache, Map<? extends K, ? extends V> t,
            Map<? extends K, AttributeMap> attributes) {
        return getTimeStamp();
    }

    public long beforeRemove(Cache<K, V> cache, Object key) {
        return getTimeStamp();
    }

    public long beforeRemoveAll(Cache<K, V> cache, Collection keys) {
        return getTimeStamp();
    }

    public long beforeTrim(Cache<K, V> cache, int size, long volume) {
        return getTimeStamp();
    }

    public void cacheReset() {
        cacheStatisticsResetLast.run();
        entryGetHitCount.reset();
        entryGetMissCount.reset();
        // TODO reset others;
        cacheStatisticsResetCount.incrementAndGet();
    }

    public CacheHitStat getHitStat() {
        return new CacheHitStat(entryGetHitCount.get(), entryGetMissCount.get());
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheStatisticsService.class, StatisticsUtils.wrapService(this));
    }

    /** {@inheritDoc} */
    public void manage(ManagedGroup parent) {
        ManagedGroup g = parent.addChild(CacheStatisticsConfiguration.SERVICE_NAME,
                "Cache Statistics attributes and operations");
        g.add(StatisticsUtils.wrapMXBean(this));
    }

    public void resetStatistics() {
        cacheReset();
    }

// public String toString() {
// StringBuilder sb = new StringBuilder();
// sb.append("hits: ");
// sb.append(entryGetHitCount);
// sb.append("\nmisses");
// sb.append(entryGetMissCount);
// sb.append("\nhit time: ");
// sb.append(entryGetHitTime);
// sb.append("\nmiss time: ");
// sb.append(entryGetMissTime);
//
// return sb.toString();
// }

    private String getDesc(String key) {
        return Resources.lookup(DefaultCacheStatisticsService.class, key.toLowerCase());
    }

    @Override
    public String toString() {
        return "Statistics Service";
    }

    long getTimeStamp() {
        return 0;
    }

    @Override
    public void started(Cache<?, ?> cache) {
        started = System.currentTimeMillis();
    }
}

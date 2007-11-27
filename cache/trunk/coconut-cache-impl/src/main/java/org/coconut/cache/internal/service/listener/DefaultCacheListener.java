/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.listener;

import java.util.Collection;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.event.InternalCacheEventService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;

public class DefaultCacheListener<K, V> extends AbstractCacheLifecycle implements
        InternalCacheListener<K, V> {

    private final DefaultCacheStatisticsService<K, V> statistics;

    private final InternalCacheEventService<K, V> event;

    public DefaultCacheListener(DefaultCacheStatisticsService<K, V> statistics,
            InternalCacheEventService<K, V> event) {
        this.statistics = statistics;
        this.event = event;
    }

    public void afterCacheClear(Cache<K, V> cache, long timestamp,
            Collection<? extends CacheEntry<K, V>> entries, long previousVolume) {
        statistics.afterCacheClear(cache, timestamp, entries, previousVolume);
        event.afterCacheClear(cache, timestamp, entries, previousVolume);
    }

    public long beforeCacheClear(Cache<K, V> cache) {
        return statistics.beforeCacheClear(cache);
    }

    public void afterRemoveAll(Cache<K, V> cache, long start, Collection<? extends K> keys,
            Collection<CacheEntry<K, V>> removed) {
        statistics.afterRemoveAll(cache, start, removed);
        event.afterRemoveAll(cache, start, removed);
    }

    public long beforeRemoveAll(Cache<K, V> cache, Collection<? extends K> keys) {
        return statistics.beforeRemoveAll(cache, keys);
    }

    public void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            AbstractCacheEntry<K, V> oldEntry, AbstractCacheEntry<K, V> newEntry, boolean fromLoader) {
        statistics.afterPut(cache, started, evictedEntries, oldEntry, newEntry);
        if (newEntry != null) {
            event.afterPut(cache, started, evictedEntries, oldEntry, newEntry);
        }
    }

    public long beforePut(Cache<K, V> cache, K key, V value, boolean fromLoader) {
        return statistics.beforePut(cache, key, value);
    }

    public long beforePutAll(Cache<K, V> cache, Map<? extends K, ? extends V> map,
            Map<? extends K, AttributeMap> attributes, boolean fromLoader) {
        return statistics.beforePutAll(cache, map, attributes);
    }

    public void afterPutAll(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> newPrevEntries,
            boolean fromLoader) {
        statistics.afterPutAll(cache, started, evictedEntries, newPrevEntries);
        event.afterPutAll(cache, started, evictedEntries, newPrevEntries);

    }

    public void afterCachePurge(Cache<K, V> cache,long start, 
            Collection<? extends CacheEntry<K, V>> purgedEntries, int previousSize,
            long previousVolume, int newSize, long newVolume) {
        event.afterPurge(cache, purgedEntries);
    }

    public void afterTrimCache(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, int previousSize, int newSize,
            long previousVolume, long newVolume) {
        statistics.afterTrimCache(cache, started, evictedEntries, previousSize, newSize,
                previousVolume, newVolume);
        event.afterTrimCache(cache, started, evictedEntries, previousSize, newSize, previousVolume,
                newVolume);
    }

    public long beforeCachePurge(Cache<K, V> cache) {
        return 0;
    }

    public long beforeTrim(Cache<K, V> cache, int newSize, long newVolume) {
        return statistics.beforeTrim(cache, newSize, newVolume);
    }

    public long beforeRemove(Cache<K, V> cache, Object key, Object value) {
        return statistics.beforeRemove(cache, key);
    }

    public void afterRemove(Cache<K, V> cache, long started, CacheEntry<K, V> entry) {
        statistics.afterRemove(cache, started, entry);
        event.afterRemove(cache, started, entry);
    }

    public long beforeReplace(Cache<K, V> cache, K key, V value) {
        return statistics.beforePut(cache, key, value);
    }

    public void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            AbstractCacheEntry<K, V> oldEntry, AbstractCacheEntry<K, V> newEntry) {
        statistics.afterPut(cache, started, evictedEntries, oldEntry, newEntry);
        if (newEntry != null) {
            event.afterPut(cache, started, evictedEntries, oldEntry, newEntry);
        }
    }

    public long beforeGet(Cache<K, V> cache, K key) {
        return statistics.beforeGet(cache, key);
    }

    public void afterHit(Cache<K, V> cache, long started, K key, CacheEntry<K, V> entry) {
        statistics.afterHit(cache, started, key, entry);
    }

    public void afterMiss(Cache<K, V> cache, long started, K key, CacheEntry<K, V> previousEntry,
            CacheEntry<K, V> newEntry, boolean isExpired) {
        statistics.afterMiss(cache, started, key, previousEntry, newEntry, isExpired);
    }

    public void dexpired(Cache<K, V> cache, long started, CacheEntry<K, V> entry) {
        event.dexpired(cache, started, entry);
    }

    public void afterStart(Cache<K, V> cache) {
        event.afterStart(cache);
    }

}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.listener;

import java.util.Collection;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntry;
import org.coconut.cache.internal.service.event.InternalCacheEventService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;

public class DefaultCacheListener<K, V> implements InternalCacheListener<K, V> {

    private final DefaultCacheStatisticsService<K, V> statistics;

    private final InternalCacheEventService<K, V> event;

    public DefaultCacheListener(DefaultCacheStatisticsService<K, V> statistics) {
        this.statistics = statistics;
        this.event = null;
    }

    public DefaultCacheListener(DefaultCacheStatisticsService<K, V> statistics,
            InternalCacheEventService<K, V> event) {
        this.statistics = statistics;
        this.event = event;
    }

    public void afterCacheClear(Cache<K, V> cache, long timestamp,
            Collection<? extends CacheEntry<K, V>> entries, long previousVolume) {
        statistics.afterCacheClear(cache, timestamp, entries, previousVolume);
        if (event != null) {
            event.afterCacheClear(cache, timestamp, entries, previousVolume);
        }
    }

    public long beforeCacheClear(Cache<K, V> cache) {
        return statistics.beforeCacheClear(cache);
    }

    public void afterRemoveAll(Cache<K, V> cache, long start, Collection<? extends K> keys,
            Collection<CacheEntry<K, V>> removed) {
        statistics.afterRemoveAll(cache, start, removed);
        if (event != null) {
            event.afterRemoveAll(cache, start, removed);
        }
    }

    public long beforeRemoveAll(Cache<K, V> cache, Collection<? extends K> keys) {
        return statistics.beforeRemoveAll(cache, keys);
    }

    public void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            InternalCacheEntry<K, V> oldEntry, InternalCacheEntry<K, V> newEntry, boolean fromLoader) {
        statistics.afterPut(cache, started, evictedEntries, oldEntry, newEntry);
        if (event != null) {
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
            Map<InternalCacheEntry<K, V>, InternalCacheEntry<K, V>> newPrevEntries,
            boolean fromLoader) {
        statistics.afterPutAll(cache, started, evictedEntries, (Map) newPrevEntries);
        if (event != null) {
            event.afterPutAll(cache, started, evictedEntries, newPrevEntries);
        }
    }

    public void afterCachePurge(Cache<K, V> cache, long start,
            Collection<? extends CacheEntry<K, V>> purgedEntries, int previousSize,
            long previousVolume, int newSize, long newVolume) {
        if (event != null) {
            event.afterPurge(cache, purgedEntries);
        }
    }

    public void afterTrimCache(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, int previousSize, int newSize,
            long previousVolume, long newVolume) {
        statistics.afterTrimCache(cache, started, evictedEntries, previousSize, newSize,
                previousVolume, newVolume);
        if (event != null) {
            event.afterTrimCache(cache, started, evictedEntries, previousSize, newSize,
                    previousVolume, newVolume);
        }
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
        if (event != null) {
            event.afterRemove(cache, started, entry);
        }
    }

    public long beforeReplace(Cache<K, V> cache, K key, V value) {
        return statistics.beforePut(cache, key, value);
    }

    public void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            InternalCacheEntry<K, V> oldEntry, InternalCacheEntry<K, V> newEntry) {
        statistics.afterPut(cache, started, evictedEntries, oldEntry, newEntry);
        if (event != null && newEntry != null) {
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
        if (event != null) {
            event.dexpired(cache, started, entry);
        }
    }

    public void afterStart(Cache<K, V> cache) {
        if (event != null) {
            event.afterStart(cache);
        }
    }

    public long beforeGetAll(Cache<K, V> cache, Collection<? extends K> keys) {
        return statistics.beforeGetAll(cache, keys);
    }

    public void afterGetAll(Cache<K, V> cache, long started, Object[] keys,
            CacheEntry<K, V>[] entries, boolean[] isHit, boolean[] isExpired,
            Map<K, V> loadedEntries) {
        statistics.afterGetAll(cache, started, keys, entries, isHit, isExpired, loadedEntries);
    }

    @Override
    public String toString() {
        return "Listener Service";
    }

    public void afterStop(Cache<K, V> cache) {
        if (event != null) {
            event.afterStop(cache);
        }
    }
}

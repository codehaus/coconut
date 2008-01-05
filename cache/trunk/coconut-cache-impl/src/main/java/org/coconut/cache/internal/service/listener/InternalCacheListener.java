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

/**
 * @param <K>
 * @param <V>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface InternalCacheListener<K, V> {
    /**
     * Called after the specified cache was cleared.
     *
     * @param cache
     *            the cache that was cleared
     * @param timestamp
     *            the timestamp that was returned by {@link #beforeCacheClear(Cache)}
     * @param entries
     *            the entries that was removed
     * @param previousVolume
     *            the previous volume of the cache
     */
    void afterCacheClear(Cache<K, V> cache, long timestamp,
            Collection<? extends CacheEntry<K, V>> entries, long previousVolume);

    void afterCachePurge(Cache<K, V> cache, long start,
            Collection<? extends CacheEntry<K, V>> purgedEntries, int previousSize,
            long previousVolume, int newSize, long newVolume);

    void afterGetAll(Cache<K, V> cache, long started, Object[] keys, CacheEntry<K, V>[] entries,
            boolean[] isHit, boolean[] isExpired, Map<K, V> loadedEntries);

    void afterHit(Cache<K, V> cache, long started, K key, CacheEntry<K, V> entry);

    void afterMiss(Cache<K, V> cache, long started, K key, CacheEntry<K, V> previousEntry,
            CacheEntry<K, V> newEntry, boolean isExpired);

    void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            InternalCacheEntry<K, V> oldEntry, InternalCacheEntry<K, V> newEntry);

    void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            InternalCacheEntry<K, V> oldEntry, InternalCacheEntry<K, V> newEntry, boolean fromLoader);

    void afterPutAll(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Map<InternalCacheEntry<K, V>, InternalCacheEntry<K, V>> newPrevEntries,
            boolean fromLoader);

    void afterRemove(Cache<K, V> cache, long started, CacheEntry<K, V> entry);

    void afterRemoveAll(Cache<K, V> cache, long start, Collection<? extends K> keys,
            Collection<CacheEntry<K, V>> removed);

    void afterStart(Cache<K, V> cache);

    void afterStop(Cache<K, V> cache);

    void afterTrimCache(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, int previousSize, int newSize,
            long previousVolume, long newVolume);

    /**
     * Called before the cache was cleared.
     *
     * @param cache
     *            the cache that was cleared
     * @return a timestamp
     */
    long beforeCacheClear(Cache<K, V> cache);

    long beforeCachePurge(Cache<K, V> cache);

    long beforeGet(Cache<K, V> cache, K key);

    long beforeGetAll(Cache<K, V> cache, Collection<? extends K> keys);

    long beforePut(Cache<K, V> cache, K key, V value, boolean fromLoader);

    long beforePutAll(Cache<K, V> cache, Map<? extends K, ? extends V> map,
            Map<? extends K, AttributeMap> attributes, boolean fromLoader);

    long beforeRemove(Cache<K, V> cache, Object key, Object value);

    long beforeRemoveAll(Cache<K, V> cache, Collection<? extends K> keys);

    long beforeReplace(Cache<K, V> cache, K key, V value);

    long beforeTrim(Cache<K, V> cache, int newSize, long newVolume);

    void dexpired(Cache<K, V> cache, long started, CacheEntry<K, V> entry);

}

package org.coconut.cache.internal.service.listener;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.core.AttributeMap;

/**
 * @param <K>
 * @param <V>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface InternalCacheListener<K, V> {
    /**
     * Called before the cache was cleared.
     * 
     * @param cache
     *            the cache that was cleared
     * @return a timestamp
     */
    long beforeCacheClear(Cache<K, V> cache);

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

    long beforeRemoveAll(Cache<K, V> cache, Collection<? extends K> keys);

    void afterRemoveAll(Cache<K, V> cache, long start, Collection<? extends K> keys,
            Collection<CacheEntry<K, V>> removed);

    long beforePut(Cache<K, V> cache, K key, V value, boolean fromLoader);

    void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            AbstractCacheEntry<K, V> oldEntry, AbstractCacheEntry<K, V> newEntry, boolean fromLoader);

    long beforePutAll(Cache<K, V> cache, Map<? extends K, ? extends V> map,
            Map<? extends K, AttributeMap> attributes, boolean fromLoader);

    void afterPutAll(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> newPrevEntries,
            boolean fromLoader);

    long beforeCachePurge(Cache<K, V> cache);

    void afterCachePurge(Cache<K, V> cache, long start, Collection<? extends CacheEntry<K, V>> purgedEntries,
            int previousSize, long previousVolume, int newSize, long newVolume);

    long beforeTrim(Cache<K, V> cache, int newSize, long newVolume);

    void afterTrimCache(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, int previousSize, int newSize,
            long previousVolume, long newVolume);
    
    long beforeRemove(Cache<K, V> cache, Object key, Object value);
    
    void afterRemove(Cache<K, V> cache, long started, CacheEntry<K, V> entry);
    
    long beforeReplace(Cache<K, V> cache, K key, V value);
    
    void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            AbstractCacheEntry<K, V> oldEntry, AbstractCacheEntry<K, V> newEntry);
    
    long beforeGet(Cache<K, V> cache, K key);
    
    void afterHit(Cache<K, V> cache, long started, K key, CacheEntry<K, V> entry);
    
    void afterMiss(Cache<K, V> cache, long started, K key, CacheEntry<K, V> previousEntry,
            CacheEntry<K, V> newEntry, boolean isExpired);

    void dexpired(Cache<K, V> cache, long started, CacheEntry<K, V> entry);
    
}

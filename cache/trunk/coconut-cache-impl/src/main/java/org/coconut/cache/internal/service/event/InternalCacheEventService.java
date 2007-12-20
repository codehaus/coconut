/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.service.event.CacheEventService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface InternalCacheEventService<K, V> extends CacheEventService<K, V> {
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

    void afterPurge(Cache<K, V> cache, Collection<? extends CacheEntry<K, V>> expired);

    void dexpired(Cache<K, V> cache, long started, CacheEntry<K, V> entry);

    void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            AbstractCacheEntry<K, V> oldEntry, AbstractCacheEntry<K, V> newEntry);

    void afterPutAll(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> newPrevEntries);

    void afterRemove(Cache<K, V> cache, long started, CacheEntry<K, V> entry);

    void afterRemoveAll(Cache<K, V> cache, long started, Collection<CacheEntry<K, V>> entries);

//    void afterReplace(Cache<K, V> cache, long started,
//            Collection<? extends CacheEntry<K, V>> evictedEntries, CacheEntry<K, V> oldEntry,
//            CacheEntry<K, V> newEntry);

    void afterTrimCache(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, int previousSize, int newSize,
            long previousVolume, long newVolume);

    void afterStart(Cache<K, V> cache);
    void afterStop(Cache<K, V> cache);
    
}

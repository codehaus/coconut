/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.joinpoint;

import java.util.Collection;
import java.util.List;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface AfterCacheOperation<K, V> {

    /**
     * Called after the specified cache was cleared.
     * 
     * @param cache
     *            the cache that was cleared
     * @param started
     *            the time at which the clear was started
     * @param previousSize
     *            the previous size of the cache
     * @param previousCapacity
     *            the previous capacity of the cache
     * @param entries
     *            the entries that was removed. (<tt>null</tt> is allowed)
     */
    void afterCacheClear(Cache<K, V> cache, long started, int previousSize,
            long previousCapacity, Collection<? extends CacheEntry<K, V>> entries);

    void afterCacheEvict(Cache<K, V> cache, long started, int size, int previousSize,
            long capacity, long previousCapacity,
            Collection<? extends CacheEntry<K, V>> evicted,
            Collection<? extends CacheEntry<K, V>> expired);

    void afterGet(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, K key,
            CacheEntry<K, V> prev, CacheEntry<K, V> newEntry, boolean isExpired);

    void afterPut(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            CacheEntry<K, V> oldEntry, CacheEntry<K, V> newEntry);

    void afterPutAll(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Collection<? extends CacheEntry<K, V>> prev,
            Collection<? extends CacheEntry> added);

    void afterRemove(Cache<K, V> cache, long started, CacheEntry<K, V> entry);

    void afterReplace(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            CacheEntry<K, V> oldEntry, CacheEntry<K, V> newEntry);

    void afterTrimToSize(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries);

}

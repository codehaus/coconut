/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import java.util.Collection;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalEventService<K, V> {

    /**
     * Returns whether or not remove events will be raised when the cache is
     * cleared.
     */
    boolean isRemoveEventsFromClear();

    /**
     * The specified cache was cleared.
     * <p>
     * IMPORTANT: the specified entries parameter should only be
     * <tt>non-null</tt> if {@link #isRemoveEventsFromClear()} returns
     * <tt>true</tt>;
     * 
     * @param cache
     *            the cache that was cleared
     * @param size
     *            the previous size of the cache
     * @param capacity
     *            the previous capacity of the cache
     * @param entries
     *            the entries that was removed.
     */
    void cacheCleared(Cache<K, V> cache, int size, long capacity,
            Iterable<? extends CacheEntry<K, V>> entries);

    /**
     * The specified CacheEntry was evicted from the cache because space was
     * needed and the configured replament policy decided that this elements had
     * least chance of being requested in the future.
     * <p>
     * Normally called from {@link Cache#evict()},
     * {@link Cache#put(Object, Object, long, java.util.concurrent.TimeUnit)} or
     * {@link Cache#putAll(java.util.Map, long, java.util.concurrent.TimeUnit)}.
     * Or when a value is loaded from a configured cache loader from
     * {@link Cache#get(Object)}, {@link Cache#getAll(java.util.Collection)},
     * {@link Cache#getEntry(Object)}.
     * <p>
     * {@link org.coconut.cache.spi.AbstractCache#putEntries(java.util.Collection)},
     * {@link org.coconut.cache.spi.AbstractCache#putEntry(CacheEntry)}, ...
     * and many more
     * 
     * @param cache
     *            the cache from where the entry was evicted
     * @param entry
     *            the entry that was evicted
     */
    void entryEvicted(Cache<K, V> cache, CacheEntry<K, V> entry);

    /**
     * @param cache
     *            the cache from where the entries was evicted
     * @param list
     *            the list of entries that was evicted
     */
    void entriesEvicted(Cache<K, V> cache, Collection<? extends CacheEntry<K, V>> list);

    void expired(Cache<K, V> cache, Collection<? extends CacheEntry<K, V>> list);

    void removed(Cache<K, V> cache, CacheEntry<K, V> entry);

    void put(Cache<K, V> cache, CacheEntry<K, V> newEntry, CacheEntry<K, V> prev,
            boolean wasAccepted);

    void expiredAndGet(Cache<K, V> cache, K key, CacheEntry<K, V> entry);

    void getHit(Cache<K, V> cache, CacheEntry<K, V> entry);

    void getAndLoad(Cache<K, V> cache, K key, CacheEntry<K, V> entry);
}

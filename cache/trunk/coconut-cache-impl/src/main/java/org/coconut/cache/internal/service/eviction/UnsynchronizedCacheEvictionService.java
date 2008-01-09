/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.coconut.attribute.Attributes;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.servicemanager.AbstractCacheServiceManager;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;

/**
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class UnsynchronizedCacheEvictionService<K, V, T extends CacheEntry<K, V>> extends
        AbstractEvictionService<K, V, T> {

    final InternalCacheListener listener;

    // @SuppressWarnings("unchecked")
    public UnsynchronizedCacheEvictionService(MemoryStore<K, V> ms, InternalCacheListener listener,
            AbstractCacheEntryFactoryService<K, V> factory) {
        super(ms, factory);
        this.listener = listener;
    }

    @Override
    void trimCache(int toSize, long toVolume) {
        long started = listener.beforeTrim(toSize, toVolume);

        // manager.lazyStart(true);
        // int size = map.size();
        // long volume = map.volume();
        // List<CacheEntry<K, V>> l = map.trimCache(toSize, toVolume);

        // listener.afterTrimCache(started, l, size, map.size(), volume, map.volume());
    }

}

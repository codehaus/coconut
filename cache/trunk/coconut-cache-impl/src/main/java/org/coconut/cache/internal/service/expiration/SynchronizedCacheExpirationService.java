package org.coconut.cache.internal.service.expiration;

import java.util.Collections;
import java.util.List;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.InternalCacheEntry;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.memorystore.MemoryStore;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.core.Clock;

public class SynchronizedCacheExpirationService<K, V> extends AbstractCacheExpirationService<K, V> {

    private final MemoryStore<K, V> map;

    private final Clock clock;

    private final Cache cache;

    private final InternalCacheListener<K, V> listener;

    public SynchronizedCacheExpirationService(Cache cache, Clock clock, MemoryStore<K, V> entryMap,
            InternalCacheListener<K, V> listener, CacheConfiguration<K, V> conf,
            InternalCache<K, V> helper, CacheExpirationConfiguration<K, V> confExpiration,
            InternalCacheEntryService attributeFactory) {
        super(helper, confExpiration, attributeFactory);
        this.listener = listener;
        this.cache = cache;
        this.clock = clock;
        this.map = entryMap;
    }

    public void purgeExpired() {
        List<InternalCacheEntry<K, V>> expired = Collections.EMPTY_LIST;
        long start = listener.beforeCachePurge();
        long timestamp = clock.timestamp();
        int size = 0;
        int newSize = 0;
        long volume = 0;
        long newVolume = 0;

        synchronized (cache) {
            size = map.size();
            volume = map.volume();
           // expired = map.purgeExpired(timestamp);
            newSize = map.size();
            newVolume = map.volume();
        }

        listener.afterCachePurge(start, expired, size, volume, newSize, newVolume);
    }

}

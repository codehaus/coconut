package org.coconut.cache.internal.service.expiration;

import java.util.Collection;

import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.InternalCacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.memorystore.MemoryStore;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.core.Clock;

public class UnsynchronizedCacheExpirationService<K, V> extends
        AbstractCacheExpirationService<K, V> {

    private final MemoryStore<K, V> map;

    private final Clock clock;

    private final InternalCacheListener<K, V> listener;

    public UnsynchronizedCacheExpirationService(Clock clock, MemoryStore<K, V> entryMap,
            InternalCacheListener<K, V> listener, InternalCache<K, V> helper,
            CacheExpirationConfiguration<K, V> confExpiration,
            InternalCacheEntryService attributeFactory) {
        super(helper, confExpiration, attributeFactory);
        this.listener = listener;
        this.clock = clock;
        this.map = entryMap;
    }

    public void purgeExpired() {
        long start = listener.beforeCachePurge();
        long timestamp = clock.timestamp();
        int size = map.size();
        long volume = map.volume();

        Collection<InternalCacheEntry<K, V>> expired = (Collection) map.withFilter(null)
                .removeAll().asList();
        listener.afterCachePurge(start, expired, size, volume, map.size(), map.volume());
    }
}

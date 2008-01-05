package org.coconut.cache.internal.service.expiration;

import java.util.Collections;
import java.util.List;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.SynchronizedCache;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.entry.InternalCacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.core.Clock;

public class SynchronizedCacheExpirationService<K, V> extends DefaultCacheExpirationService<K, V> {

    private final EntryMap<K, V> map;

    private final Clock clock;

    private final Cache cache;

    private final InternalCacheListener<K, V> listener;

    public SynchronizedCacheExpirationService(Cache cache, Clock clock, EntryMap<K, V> entryMap,
            InternalCacheListener<K, V> listener, CacheConfiguration<K, V> conf,
            InternalCacheSupport<K, V> helper, CacheExpirationConfiguration<K, V> confExpiration,
            InternalCacheEntryService attributeFactory) {
        super(conf, helper, confExpiration, attributeFactory);
        this.listener = listener;
        this.cache = cache;
        this.clock = clock;
        this.map = entryMap;
    }

    @Override
    public void purgeExpired() {
        List<InternalCacheEntry<K, V>> expired = Collections.EMPTY_LIST;
        long start = listener.beforeCachePurge(cache);
        long timestamp = clock.timestamp();
        int size = 0;
        int newSize = 0;
        long volume = 0;
        long newVolume = 0;

        synchronized (cache) {
            size = map.peekSize();
            volume = map.volume();
            expired = map.purgeExpired(timestamp);
            newSize = map.peekSize();
            newVolume = map.volume();
        }

        listener.afterCachePurge(cache, start, expired, size, volume, newSize, newVolume);
    }

}

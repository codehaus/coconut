package org.coconut.cache.internal.memory;

import org.coconut.cache.internal.service.cache.InternalCache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;

public class DefaultEvictableMemoryStore<K, V> extends UnlimitedSequentialMemoryStore<K, V> {

    public DefaultEvictableMemoryStore(InternalCache<K, V> cache, AbstractCacheEntryFactoryService e) {
        super(cache, e);
    }

}

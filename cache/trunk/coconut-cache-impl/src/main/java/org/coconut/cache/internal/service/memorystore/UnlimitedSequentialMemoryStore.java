package org.coconut.cache.internal.service.memorystore;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;

public class UnlimitedSequentialMemoryStore<K, V> extends AbstractSequentialMemoryStore<K, V> {
    AbstractCacheEntryFactoryService e;

    public UnlimitedSequentialMemoryStore(Cache<K, V> cache,
            AbstractCacheEntryFactoryService e) {
        super(cache);
        threshold = (int) (16 * loadFactor);
        table = new ChainingEntry[16];
        this.e = e;
    }

    ChainingEntry<K, V> created(K key, V value, AttributeMap attributes) {
        ChainingEntry<K, V> entry = e.createEntry(key, value, attributes, null);
        return entry;
    }

    ChainingEntry<K, V> updated(ChainingEntry<K, V> old, K key, V value, AttributeMap attributes) {
        ChainingEntry<K, V> entry = e.createEntry(key, value, attributes, old);
        return entry;
    }
}

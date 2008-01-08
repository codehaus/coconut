package org.coconut.cache.internal.memory;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.internal.service.cache.InternalCache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;

public class DefaultSequentialMemoryStore<K, V> extends AbstractSequentialMemoryStore<K, V> {
    AbstractCacheEntryFactoryService e;

    public DefaultSequentialMemoryStore(InternalCache<K, V> cache, AbstractCacheEntryFactoryService e) {
        super(cache);
        threshold = (int) (16 * loadFactor);
        table = new ChainingEntry[16];
        this.e = e;
    }

    ChainingEntry<K, V> created(K key, V value, AttributeMap attributes) {
        return e.createEntry(key, value, attributes, null);
    }

    void deleted(ChainingEntry<K, V> entry, boolean isEvicted) {}

    ChainingEntry<K, V> updated(ChainingEntry<K, V> old, K key, V value, AttributeMap attributes) {
        return e.createEntry(key, value, attributes, old);
    }
}

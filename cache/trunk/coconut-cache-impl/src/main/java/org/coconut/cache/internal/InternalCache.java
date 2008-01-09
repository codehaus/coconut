package org.coconut.cache.internal;

import java.util.Collection;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;

public interface InternalCache<K, V> extends Cache<K,V> {

    CacheEntry<K, V> put(K key, V value, AttributeMap attributes);

    void putAllWithAttributes(Map<K, Map.Entry<V, AttributeMap>> data);

    boolean removeEntries(Collection<?> entries);

    boolean lazyStart();
    
    void lazyStartFailIfShutdown();
}

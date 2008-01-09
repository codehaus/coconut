package org.coconut.cache.internal.service.memorystore;

import java.util.Collection;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.internal.forkjoin.ParallelArray;
import org.coconut.operations.Ops.Predicate;

public interface MemoryStore<K, V> extends MemoryStoreWithFilter<K, V> {
    CacheEntry<K, V> get(Object key);

    int getMaximumSize();

    long getMaximumVolume();

    ParallelArray<CacheEntry<K, V>> trim();

    ParallelArray<CacheEntry<K, V>> trimTo(int size, long volume);

    Map.Entry<CacheEntry<K, V>, CacheEntry<K, V>> put(K key, V value, AttributeMap attributes,
            boolean OnlyIfAbsent);

    Map<CacheEntry<K, V>, CacheEntry<K, V>> putAllWithAttributes(
            Map<K, Map.Entry<V, AttributeMap>> data);

    CacheEntry<K, V> remove(Object key);

    CacheEntry<K, V> remove(Object key, Object value);

    ParallelArray<CacheEntry<K, V>> removeAll(Collection entries);

    CacheEntry<K, V> removeAny(Predicate<? super CacheEntry<K, V>> selector);

    ParallelArray<CacheEntry<K, V>> removeEntries(Collection entries);

    CacheEntry<K, V> removeValue(Object value);

    ParallelArray<CacheEntry<K, V>> removeValues(Collection entries);

    void setMaximumSize(int size);

    void setMaximumVolume(long volume);

    long volume();

}

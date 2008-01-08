package org.coconut.cache.internal.memory;

import java.util.Collection;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.internal.forkjoin.ParallelArray;
import org.coconut.operations.Ops.Predicate;

public interface MemoryStore<K, V> extends Iterable<CacheEntry<K, V>>, MemoryStoreWithFilter<K, V> {
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
    // ParallelArray<CacheEntry<K, V>> all();

    void setMaximumSize(int size);
    void setMaximumVolume(long volume);
    long volume();

    // ParallelArray<CacheEntry<K, V>> all(Class<? super CacheEntry> elementType);

    // void apply(Procedure<? super CacheEntry<K, V>> procedure);

    // <T> MemoryStoreWithMapping<T> withMapping(Mapper<? super CacheEntry<K, V>, ?
    // extends T> mapper);

    // int size();

    // CacheEntry<K, V> any();

    // void clear();

    // Set<Map.Entry<K, V>> entrySet();

    // Collection<V> values();

    // Set<K> keySet();

    // ParallelArray<CacheEntry<K, V>> removeAll();

    // MemoryStoreWithMapping<K> withKeys();

    // MemoryStoreWithMapping<V> withValues();

// MemoryStoreWithFilter<K, V > withFilter(Predicate<? super CacheEntry<K, V>> selector);
//
// MemoryStoreWithFilter<K, V> withFilterOnAttributes(Predicate<? super AttributeMap>
// selector);
//
// MemoryStoreWithFilter<K, V> withFilterOnKeys(Predicate<? super K> selector);
//
// MemoryStoreWithFilter<K, V> withFilterOnValues(Predicate<? super V> selector);

}

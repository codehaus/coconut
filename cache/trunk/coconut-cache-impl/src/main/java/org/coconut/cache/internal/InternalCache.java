package org.coconut.cache.internal;

import java.util.Collection;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

public interface InternalCache<K, V> {

    <T> void apply(Predicate<? super CacheEntry<K, V>> selector,
            Mapper<? super CacheEntry<K, V>, T> mapper, Procedure<T> procedure);

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    boolean isSynchronized();
    void clear();

    boolean isEmpty();

    boolean retainAll(Mapper pre, Predicate selector, Collection<?> c);

    long getVolume();

    boolean removeValue(Object value);

    int size();

    boolean removeValues(Collection<?> values);

    V remove(Object key);

    boolean removeEntries(Collection<?> entries);

    V peek(K key);

    boolean removeKeys(Collection<?> keys);

    boolean remove(Object key, Object value);

    void clearView(Mapper pre, Predicate p);

    CacheEntry<K, V> put(K key, V value, AttributeMap attributes);

    void putAllWithAttributes(Map<K, Map.Entry<V, AttributeMap>> data);

    Object getMutex();

// void putAll(Map<? extends K, ? extends V> keyValues, Map<? extends K, AttributeMap>
// attributes);

}

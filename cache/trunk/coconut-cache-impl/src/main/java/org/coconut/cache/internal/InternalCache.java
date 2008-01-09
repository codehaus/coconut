package org.coconut.cache.internal;

import java.util.Collection;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;

public interface InternalCache<K, V> {

    void clear();

    void clearView(Mapper pre, Predicate p);

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    Object getMutex();

    long getVolume();

    boolean isEmpty();

    boolean isSynchronized();

    V peek(K key);

    CacheEntry<K, V> put(K key, V value, AttributeMap attributes);

    void putAllWithAttributes(Map<K, Map.Entry<V, AttributeMap>> data);

    V remove(Object key);

    boolean remove(Object key, Object value);

    boolean removeEntries(Collection<?> entries);

    boolean removeKeys(Collection<?> keys);

    boolean removeValue(Object value);

    boolean removeValues(Collection<?> values);

    @Deprecated
    boolean retainAll(Mapper pre, Predicate selector, Collection<?> c);

    int size();
}

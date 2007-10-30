package org.coconut.cache.internal.service.loading;

import java.util.Map;

import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.core.AttributeMap;

public interface LoadSupport<K, V> {

    void load(K key, AttributeMap attributes);

    void loadAll(AttributeMap attributes, boolean force);

    void loadAll(Map<K, AttributeMap> attributes);

    /**
     * Called whenever a cacheloader has asynchronously loaded an element.
     * 
     * @param key
     *            the
     * @param value
     *            the value that was loaded, possible <code>null</code>
     * @param attributes
     */
    AbstractCacheEntry<K, V> valueLoaded(K key, V value, AttributeMap attributes);

    void valuesLoaded(Map<? extends K, ? extends V> values,
            Map<? extends K, AttributeMap> keys);

}

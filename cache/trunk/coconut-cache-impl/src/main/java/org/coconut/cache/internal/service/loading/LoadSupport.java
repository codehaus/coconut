/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Map;

import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.core.AttributeMap;

public interface LoadSupport<K, V> {

    /**
     * @param key
     *            the key to load
     * @param attributes
     *            a map of attributes
     */
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

    Map<K, AbstractCacheEntry<K, V>> valuesLoaded(Map<? extends K, ? extends V> values,
            Map<? extends K, AttributeMap> keys);

}

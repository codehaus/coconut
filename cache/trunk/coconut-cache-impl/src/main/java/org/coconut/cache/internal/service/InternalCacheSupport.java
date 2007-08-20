/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.core.AttributeMap;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheSupport<K, V> {

    /**
     * Called whenever a cacheloader has asynchronously loaded an element.
     * 
     * @param key
     *            the
     * @param value
     *            the value that was loaded, possible <code>null</code>
     * @param attributes
     */
    void valueLoaded(K key, V value, AttributeMap attributes);

    void valuesLoaded(Map<? extends K, ? extends V> values,
            Map<? extends K, AttributeMap> keys);

    int removeAll(Collection<? extends K> collection);

    int removeAllFiltered(Filter<? super CacheEntry<K, V>> filter);

    Object getMutex();

    V put(K key, V value, AttributeMap attributes);

    void putAll(Map<? extends K, ? extends V> keyValues,
            Map<? extends K, AttributeMap> attributes);

    boolean isValid(K key);

    Collection<? extends K> filterKeys(Filter<? super CacheEntry<K, V>> filter);

    Collection<? extends CacheEntry<K, V>> filter(Filter<? super CacheEntry<K, V>> filter);

    void trimToVolume(long capacity);

    void trimToSize(int size);
}

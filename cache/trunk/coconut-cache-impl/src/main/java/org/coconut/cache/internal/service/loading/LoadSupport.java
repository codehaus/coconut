/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.service.loading.CacheLoadingService;

/**
 * This implementation of {@link CacheLoadingService} needs the cache implementation to
 * support this interface.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this service
 * @param <V>
 *            the type of mapped values
 */
public interface LoadSupport<K, V> {

    /**
     * @param key
     *            the key to load
     * @param attributes
     *            a map of attributes
     */
    void load(K key, AttributeMap attributes);

    void loadAll(AttributeMap attributes, boolean force);

    void loadAll(Map< ? extends K,  ? extends AttributeMap> attributes);

    /**
     * Called whenever a cacheloader has loaded an element.
     * 
     * @param key
     *            the key for which the value should be added
     * @param value
     *            the value that was loaded, possible <code>null</code>
     * @param attributes
     *            a list of attributes for the element
     * @return the AbstractCacheEntry that was added to the cache or <code>null</code>
     *         if it was not added.
     */
    AbstractCacheEntry<K, V> valueLoaded(K key, V value, AttributeMap attributes);

    Map<K, AbstractCacheEntry<K, V>> valuesLoaded(Map<? extends K, ? extends V> values,
            Map<? extends K, AttributeMap> keys);

}

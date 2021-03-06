/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.operations.Ops.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this service
 * @param <V>
 *            the type of mapped values
 */
public interface InternalCacheLoadingService<K, V> extends CacheLoadingService<K, V> {

    /**
     * Returns the refresh predicate for this service.
     *
     * @return the refresh predicate for this service
     */
    Predicate<CacheEntry<K, V>> getRefreshPredicate();

    CacheEntry<K, V> loadAndAddToCache(K key, AttributeMap attributes, boolean isSynchronous);

    /**
     * Asynchronously load the value for the specified key and AttributeMap.
     *
     * @param key
     *            the key for which to asynchronously load a value
     * @param attributes
     *            a map of attributes to parse to the cache loader
     */
    void loadAsync(K key, AttributeMap attributes);

    void loadAsyncAll(Map<? extends K, ? extends AttributeMap> mapsWithAttributes);

    /**
     * @param entry
     * @return
     */
    CacheEntry<K, V> loadBlocking(K key, AttributeMap attributes);

    /**
     * @param entry
     * @return
     */
    Map<K, V> loadBlockingAll(Map<? extends K, ? extends AttributeMap> keys);
}

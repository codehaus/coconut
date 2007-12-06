/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;

public class UnsynchronizedCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V> {
    private final InternalCacheEntryService attributeFactory;

    public UnsynchronizedCacheLoaderService(InternalCacheEntryService attributeFactory,
            InternalCacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf, final LoadSupport<K, V> cache) {
        super(loadConf, attributeFactory, exceptionService, cache);
        this.attributeFactory = attributeFactory;
    }

    /** {@inheritDoc} */
    public void loadAllAsync(Map<? extends K, ? extends AttributeMap> mapsWithAttributes) {
        Collection<UnsynchronizedCacheLoaderCallback<K, V>> col = new ArrayList<UnsynchronizedCacheLoaderCallback<K, V>>(
                mapsWithAttributes.size());
        for (Map.Entry<? extends K, ? extends AttributeMap> e : mapsWithAttributes.entrySet()) {
            UnsynchronizedCacheLoaderCallback<K, V> callback = new UnsynchronizedCacheLoaderCallback<K, V>(
                    e.getKey(), e.getValue());
            col.add(callback);
        }
        try {
            getLoader().loadAll(col);
        } catch (RuntimeException re) {
            throw re;// check
        }
        // all are done in sync version
        Map<K, V> keyValues = new HashMap<K, V>();
        Map<K, AttributeMap> keyAttributes = new HashMap<K, AttributeMap>();
        for (UnsynchronizedCacheLoaderCallback<K, V> callback : col) {
            if (!callback.isDone()) {
                throw new RuntimeException();
            }
            V result = callback.getResult();
            if (callback.getCause() != null) {
                result = getExceptionHandler().getHandler().loadingFailed(
                        getExceptionHandler().createContext(), getLoader(), callback.getKey(),
                        callback.getAttributes(), callback.getCause());
            }
            keyValues.put(callback.getKey(), result);
            keyAttributes.put(callback.getKey(), callback.getAttributes());
        }
        loadSupport.valuesLoaded(keyValues, keyAttributes);
    }

    /** {@inheritDoc} */
    public AbstractCacheEntry<K, V> loadBlocking(K key, AttributeMap attributes) {
        AttributeMap map = attributeFactory.createMap(attributes);
        return loadAndAddToCache(key, map, false);
    }

}

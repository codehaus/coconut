/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.loading.CacheLoaderCallback;
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
            getExceptionHandler().fatalRuntimeException(
                    "CacheLoader.loadAll() failed with runtime exception", re);
            for (Iterator<UnsynchronizedCacheLoaderCallback<K, V>> iterator = col.iterator(); iterator
                    .hasNext();) {
                getExceptionHandler().fatalRuntimeException(
                        "As a result, load of value was never completed [key = "
                                + iterator.next().getKey() + "]");
                iterator.remove();
            }
        }
        // all callbacks should be done in unsync version
        Map<K, V> keyValues = new HashMap<K, V>();
        Map<K, AttributeMap> keyAttributes = new HashMap<K, AttributeMap>();
        for (UnsynchronizedCacheLoaderCallback<K, V> callback : col) {
            if (!callback.isDone()) {
                getExceptionHandler().fatalRuntimeException(
                        "CacheLoader.loadAll() failed to complete load, completed() or failed() was never called for '"
                                + CacheLoaderCallback.class.getSimpleName() + "' [key = "
                                + callback.getKey() + "]", new RuntimeException());
            } else {
                V result = callback.getResult();
                if (callback.getCause() != null) {
                    result = getExceptionHandler().getHandler().loadingLoadValueFailed(
                            getExceptionHandler().createContext(
                                    callback.getCause(),
                                    "Could not load value [key = " + callback.getKey() + ", attributes = "
                                            + callback.getAttributes() + "]"), getLoader(), callback.getKey(),
                            callback.getAttributes());
                }
                keyValues.put(callback.getKey(), result);
                keyAttributes.put(callback.getKey(), callback.getAttributes());
            }
        }
        loadSupport.valuesLoaded(keyValues, keyAttributes);
    }

    /** {@inheritDoc} */
    public AbstractCacheEntry<K, V> loadBlocking(K key, AttributeMap attributes) {
        AttributeMap map = attributeFactory.createMap(attributes);
        return loadAndAddToCache(key, map, false);
    }

}

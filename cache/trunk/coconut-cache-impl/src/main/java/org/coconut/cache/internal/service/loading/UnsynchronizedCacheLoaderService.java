/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
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
    @Override
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
            StringBuilder sb = new StringBuilder();
            sb.append("CacheLoader.loadAll() failed");
            List<UnsynchronizedCacheLoaderCallback<K, V>> missing = LoadingUtils
                    .findAndRemoveCallbacks(col);
            if (missing.size() > 0) {
                sb
                        .append("\nAs a result, load of the following keys was never completed [key(s)={");
                for (int i = 0; i < missing.size(); i++) {
                    sb.append("'");
                    sb.append(missing.get(i).getKey());
                    sb.append("'");
                    if (i != missing.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("}]");
            }
            getExceptionHandler().fatal(sb.toString(), re);
        }
        // all callbacks should be done in unsync version
        Map<K, V> keyValues = new HashMap<K, V>();
        Map<K, AttributeMap> keyAttributes = new HashMap<K, AttributeMap>();
        for (UnsynchronizedCacheLoaderCallback<K, V> callback : col) {
            if (!callback.isDone()) {
                getExceptionHandler().fatal(
                        "CacheLoader.loadAll() failed to complete load, completed() or failed() was never called for '"
                                + CacheLoaderCallback.class.getSimpleName() + "' [loader ="
                                + getLoader() + ", key = " + callback.getKey() + "]");
            } else {
                V v = callback.getResult();
                if (callback.getCause() != null) {
                    v = getExceptionHandler().loadFailed(callback.getCause(), getLoader(),
                            callback.getKey(), callback.getAttributes());
                }
                keyValues.put(callback.getKey(), v);
                keyAttributes.put(callback.getKey(), callback.getAttributes());
            }
        }
        loadSupport.valuesLoaded(keyValues, keyAttributes);
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> loadBlocking(K key, AttributeMap attributes) {
        AttributeMap map = attributeFactory.createMap(attributes);
        return loadAndAddToCache(key, map, false);
    }

}

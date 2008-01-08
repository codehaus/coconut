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
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.servicemanager.AbstractCacheServiceManager;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoader.LoaderCallback;
import org.coconut.internal.util.CollectionUtils.SimpleImmutableEntry;

public class UnsynchronizedCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V> {
    private final InternalCacheEntryService attributeFactory;

    private final InternalCache c;

    private final AbstractCacheServiceManager icsm;

    public UnsynchronizedCacheLoaderService(AbstractCacheServiceManager icsm,
            InternalCache c, InternalCacheEntryService attributeFactory,
            InternalCacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf, final InternalCache<K, V> cache) {
        super(loadConf, attributeFactory, exceptionService, cache);
        this.attributeFactory = attributeFactory;
        this.icsm = icsm;
      //  this.map = map;
        this.c = c;
    }

    /** {@inheritDoc} */
    @Override
    void doLoadAll(Map<? extends K, ? extends AttributeMap> attributes) {
        Map<K, AttributeMap> keys = new HashMap<K, AttributeMap>();

        if (!icsm.lazyStart(false)) {
            return;
        }
       // map.needsLoad(keys, attributes);
        forceLoadAll(keys);
    }

    /** {@inheritDoc} */
    @Override
    public void doLoad(K key, AttributeMap attributes) {
     //   if (icsm.lazyStart(false) && map.needsLoad(key)) {
            forceLoad(key, attributes);
     //   }
    }

    /** {@inheritDoc} */
    @Override
    public void loadAll(AttributeMap attributes, boolean force) {
        final Map<K, AttributeMap> keys;
        if (!icsm.lazyStart(false)) {
            return;
        }
        if (force) {
   //         keys = Attributes.toMap(new ArrayList(map.keySet(c)), attributes);
        } else {
   //         keys = map.whoNeedsLoading(attributes);
        }

   //     forceLoadAll(keys);
    }

    /** {@inheritDoc} */
    @Override
    public void loadAsyncAll(Map<? extends K, ? extends AttributeMap> mapsWithAttributes) {
        loadBlockingAll(mapsWithAttributes);
    }

    /** {@inheritDoc} */
    public Map<K, V> loadBlockingAll(Map<? extends K, ? extends AttributeMap> keys) {
        Collection<UnsynchronizedCacheLoaderCallback<K, V>> col = new ArrayList<UnsynchronizedCacheLoaderCallback<K, V>>(
                keys.size());
        for (Map.Entry<? extends K, ? extends AttributeMap> e : keys.entrySet()) {
            AttributeMap map = attributeFactory.createMap(e.getValue());
            UnsynchronizedCacheLoaderCallback<K, V> callback = new UnsynchronizedCacheLoaderCallback<K, V>(
                    e.getKey(), map);
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
        Map<K, Map.Entry<? extends V, AttributeMap>> keyValues = new HashMap<K, Map.Entry<? extends V, AttributeMap>>();
        Map<K, V> result = new HashMap<K, V>();
        for (UnsynchronizedCacheLoaderCallback<K, V> callback : col) {
            if (!callback.isDone()) {
                getExceptionHandler().fatal(
                        "CacheLoader.loadAll() failed to complete load, completed() or failed() was never called for '"
                                + LoaderCallback.class.getSimpleName() + "' [loader ="
                                + getLoader() + ", key = " + callback.getKey() + "]");
            } else {
                V v = callback.getResult();
                if (callback.getCause() != null) {
                    v = getExceptionHandler().loadFailed(callback.getCause(), getLoader(),
                            callback.getKey(), callback.getAttributes());
                }
                keyValues.put(callback.getKey(), new SimpleImmutableEntry(v, callback
                        .getAttributes()));
                result.put(callback.getKey(), v);
            }
        }
        internal.putAllWithAttributes((Map) keyValues);
        return result;
    }

    /** {@inheritDoc} */
    public void loadAsync(K key, AttributeMap attributes) {
        loadBlocking(key, attributes);// Load blocking as default
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> loadBlocking(K key, AttributeMap attributes) {
        return loadAndAddToCache(key, attributeFactory.createMap(attributes), false);
    }

}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.internal.service.expiration.AbstractCacheExpirationService;
import org.coconut.cache.internal.service.threading.InternalCacheThreadingService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.internal.spi.ExtendedExecutorRunnable;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.core.Clock;
import org.coconut.core.Transformer;
import org.coconut.core.AttributeMaps.DefaultAttributeMap;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheLoaderService<K, V> implements InternalCacheLoadingService<K, V> {

    private final CacheHelper<K, V> cache;

    private final Clock clock;

    private final CacheErrorHandler<K, V> errorHandler;

    private final AbstractCacheExpirationService<K, V> expirationService;

    private final IsValidEntry isValid = new IsValidEntry();

    private final CacheLoader<? super K, ? extends V> loader;

    private long reloadExpirationTime;

    private Filter<CacheEntry<K, V>> reloadFilter;

    private final InternalCacheThreadingService threadManager;

    /**
     * @param clock
     * @param errorHandler
     * @param loader
     * @param threadManager
     * @param cache
     * @param asyncLoader
     */
    public DefaultCacheLoaderService(final Clock clock,
            final CacheErrorHandler<K, V> errorHandler,
            final CacheLoader<? super K, ? extends V> loader,
            final InternalCacheThreadingService threadManager,
            AbstractCacheExpirationService<K, V> expirationService,
            final CacheHelper<K, V> cache) {
        this.clock = clock;
        this.errorHandler = errorHandler;
        this.loader = loader;
        this.threadManager = threadManager;
        this.cache = cache;
        this.expirationService = expirationService;
    }

    /**
     * @see org.coconut.cache.internal.service.loading.InternalCacheLoadingService#canLoad()
     */
    public boolean canLoad() {
        return true;
    }

    /*
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object)
     */
    public Future<?> load(K key) {
        return load(key, new DefaultAttributeMap());
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public Future<?> load(K key, AttributeMap attributes) {
        LoadValueRunnable lvr = new LoadValueRunnable<K, V>(this, loader, key, attributes);
        threadManager.execute(lvr);
        return lvr;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadAll(java.util.Collection)
     */
    public Future<?> loadAll(Collection<? extends K> keys) {
        return loadAll(AttributeMaps.createMap(keys));
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadAll(java.util.Map)
     */
    public Future<?> loadAll(Map<K, AttributeMap> mapsWithAttributes) {
        LoadValuesRunnable lvr = new LoadValuesRunnable<K, V>(this, loader,
                mapsWithAttributes);
        FutureTask<V> ft = new FutureTask<V>(lvr, null);
        threadManager.execute(ft);
        return ft;
    }

    /**
     * @see org.coconut.cache.internal.service.loading.InternalCacheLoadingService#loadBlocking(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public V loadBlocking(CacheLoader<? super K, ? extends V> loader, K key,
            AttributeMap attributes) {
        V v = null;
        try {
            v = loader.load(key, attributes);
        } catch (Exception e) {
            v = errorHandler.loadFailed(loader, key, attributes, false, e);
        }
        return v;
    }

    /**
     * @see org.coconut.cache.internal.service.loading.InternalCacheLoadingService#loadBlocking(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public Map<? super K, ? extends V> loadAllBlocking(Map<? extends K, AttributeMap> keys) {
        return loadAllBlocking(loader, keys);
    }

    /**
     * @see org.coconut.cache.internal.service.loading.InternalCacheLoadingService#loadBlocking(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public Map<? super K, ? extends V> loadAllBlocking(
            CacheLoader<? super K, ? extends V> loader,
            Map<? extends K, AttributeMap> keys) {
        Map<? super K, ? extends V> map = null;
        try {
            map = loader.loadAll(keys);
        } catch (Exception e) {
            map = errorHandler.loadAllFailed(loader, keys, false, e);
        }
        return map;
    }

    /**
     * @see org.coconut.cache.internal.service.loading.InternalCacheLoadingService#loadBlocking(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public V loadBlocking(K key, AttributeMap attributes) {
        return loadBlocking(loader, key, attributes);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadIfMissing(java.util.Collection)
     */
    public Future<?> forceLoadAll(Collection<? extends K> keys) {
        return forceLoadAll(AttributeMaps.createMap(keys));
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadIfMissing(java.util.Map)
     */
    public Future<?> forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
        // defensive copy
        Map<K, AttributeMap> map = new HashMap<K, AttributeMap>(mapsWithAttributes);
        Collection<K> keys = cache.filterEntries(
                (Collection) mapsWithAttributes.keySet(), isValid);
        // TODO what about needs reload???
        map.keySet().removeAll(keys);
        return loadAll(map);
    }

    public boolean needsReload(CacheEntry<K, V> entry) {
        long reloadAheadTime = reloadExpirationTime;
        if (reloadAheadTime < 0
                || entry.getExpirationTime() == CacheExpirationService.NEVER_EXPIRE) {
            return false;
        }
        Filter<CacheEntry<K, V>> filter = reloadFilter;
        if (filter != null && filter.accept(entry)) {
            return true;
        }
        long refTime = entry.getLastUpdateTime() + reloadAheadTime;
        return clock.isPassed(refTime);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#reloadAll()
     */
    public Future<?> forceLoadAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#reloadAll(org.coconut.core.AttributeMap)
     */
    public Future<?> forceLoadAll(AttributeMap aatributes) {
        throw new UnsupportedOperationException();
    }

    public void reloadIfNeeded(CacheEntry<K, V> entry) {
        if (needsReload(entry)) {
            load(entry.getKey());
        }
    }

    class IsValidEntry implements Filter<CacheEntry> {

        /**
         * @see org.coconut.filter.Filter#accept(java.lang.Object)
         */
        public boolean accept(CacheEntry element) {
            return element == null ? false : !expirationService.isExpired(element);
        }

    }

    static class LoadValueRunnable<K, V> extends ExtendableFutureTask implements
            ExtendedExecutorRunnable.LoadKey<K> {
        private final AttributeMap attributes;

        private final K key;

        // we migth at some time allow for loading from other then default
        private final CacheLoader<? super K, ? extends V> loader;

        private final DefaultCacheLoaderService<K, V> loaderService;

        /**
         * @param loader
         * @param key
         * @param callback
         */
        LoadValueRunnable(final DefaultCacheLoaderService<K, V> loaderService,
                final CacheLoader<? super K, ? extends V> loader, final K key,
                AttributeMap attributes) {
            if (loader == null) {
                throw new NullPointerException("loader is null");
            } else if (key == null) {
                throw new NullPointerException("key is null");
            }
            this.loader = loader;
            this.key = key;
            this.loaderService = loaderService;
            this.attributes = attributes;
        }

        /**
         * @see org.coconut.cache.internal.spi.ExtendedExecutorRunnable.LoadKey#getAttributeMap()
         */
        public AttributeMap getAttributeMap() {
            return attributes;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader.LoadKeyRunnable#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public Object call() {
            V v = null;
            try {
                v = loader.load(key, attributes);
            } catch (Exception e) {
                v = loaderService.errorHandler.loadFailed(loader, key, attributes, true,
                        e);
            }
            if (v != null) {
                loaderService.cache.valueLoaded(key, v, attributes);
            }
            return null;
        }
    }

    static class LoadValuesRunnable<K, V> implements ExtendedExecutorRunnable.LoadKeys<K> {
        private final Map<? extends K, AttributeMap> keysWithAttributes;

        private final CacheLoader<? super K, ? extends V> loader;

        private final DefaultCacheLoaderService<K, V> loaderService;

        /**
         * @param loader
         * @param key
         * @param callback
         */
        LoadValuesRunnable(final DefaultCacheLoaderService<K, V> loaderService,
                final CacheLoader<? super K, ? extends V> loader,
                Map<? extends K, AttributeMap> keysWithAttributes) {
            if (loader == null) {
                throw new NullPointerException("loader is null");
            } else if (keysWithAttributes == null) {
                throw new NullPointerException("key is null");
            }
            this.loader = loader;
            this.keysWithAttributes = keysWithAttributes;
            this.loaderService = loaderService;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader.LoadKeyRunnable#getKey()
         */
        public Map<? extends K, AttributeMap> getKeys() {
            return keysWithAttributes;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Map<? super K, ? extends V> map = null;
            try {
                map = loader.loadAll(keysWithAttributes);
            } catch (Exception e) {
                map = loaderService.errorHandler.loadAllFailed(loader,
                        keysWithAttributes, true, e);
            }
            if (map != null && map.size() > 0) {
                loaderService.cache.valuesLoaded(map, keysWithAttributes);
            }
        }
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter)
     */
    public Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter, org.coconut.core.AttributeMap)
     */
    public Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter, AttributeMap defaultAttributes) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter, org.coconut.core.Transformer)
     */
    public Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter, Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoad(java.lang.Object)
     */
    public Future<?> forceLoad(K key) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoad(java.lang.Object, org.coconut.core.AttributeMap)
     */
    public Future<?> forceLoad(K key, AttributeMap attributes) {
        // TODO Auto-generated method stub
        return null;
    }
}
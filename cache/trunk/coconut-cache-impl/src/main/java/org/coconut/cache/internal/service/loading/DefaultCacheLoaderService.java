/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.threading.InternalCacheThreadingService;
import org.coconut.cache.internal.service.util.ExtendableFutureTask;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.internal.spi.ExtendedExecutorRunnable;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V> {

    final CacheHelper<K, V> cache;

    private final Clock clock;

    private final CacheExceptionService<K, V> errorHandler;

    private final DefaultCacheExpirationService<K, V> expirationService;

    private final CacheLoader<? super K, ? extends V> loader;

    private long reloadExpirationTime;

    private final Filter<CacheEntry<K, V>> reloadFilter;

    private final Executor loadExecutor;

    /**
     * @param clock
     * @param errorHandler
     * @param loader
     * @param threadManager
     * @param cache
     * @param asyncLoader
     */
    public DefaultCacheLoaderService(final Clock clock,
            InternalCacheAttributeService attributeFactory,
            CacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf,
            final InternalCacheThreadingService threadManager,
            DefaultCacheExpirationService<K, V> expirationService,
            final CacheHelper<K, V> cache) {
        super(attributeFactory, cache);
        this.errorHandler = exceptionService;
        this.clock = clock;
        this.loader = loadConf.getLoader();
        this.loadExecutor = threadManager.getExecutor(CacheLoadingService.class)
                .createExecutorService();
        this.reloadExpirationTime = getDefaultTimeToRefresh(loadConf);
        this.reloadFilter = loadConf.getRefreshFilter();
        this.cache = cache;
        this.expirationService = expirationService;
    }

    Future<?> doLoad(K key, AttributeMap attributes) {
        LoadValueRunnable lvr = new LoadValueRunnable<K, V>(this, loader, key, attributes);
        loadExecutor.execute(lvr);
        return lvr;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadAll(java.util.Map)
     */
    Future<?> doLoad(Map<? extends K, AttributeMap> mapsWithAttributes) {
        LoadValuesRunnable lvr = new LoadValuesRunnable<K, V>(this, loader,
                mapsWithAttributes);
        FutureTask<V> ft = new FutureTask<V>(lvr, null);
        loadExecutor.execute(ft);
        return ft;
    }

    /**
     * @see org.coconut.cache.internal.service.loading.InternalCacheLoadingService#loadBlocking(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    private V loadBlocking(CacheLoader<? super K, ? extends V> loader, K key,
            AttributeMap attributes) {
        if (loader == null) {
            return null;
        }
        V v = null;
        try {
            v = loader.load(key, attributes);
        } catch (Exception e) {
            v = errorHandler.getExceptionHandler().loadFailed(
                    errorHandler.createContext(), loader, key, attributes, false, e);
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
        Map<K, V> map = new HashMap<K, V>();
        for (Map.Entry<? extends K, AttributeMap> entry : keys.entrySet()) {
            map.put(entry.getKey(), loadBlocking(entry.getKey(), entry.getValue()));
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

    public boolean needsReload(AbstractCacheEntry<K, V> entry) {
        if (loader == null) {
            return false;
        }
        if (reloadFilter != null && reloadFilter.accept(entry)) {
            return true;
        }
        long expTime = entry.getRefreshTime();
        return expTime == CacheExpirationService.NEVER_EXPIRE ? false : clock
                .isPassed(expTime);

    }

    public void reloadIfNeeded(AbstractCacheEntry<K, V> entry) {
        if (needsReload(entry)) {
            load(entry.getKey());
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
        public AttributeMap getAttributes() {
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
                v = loaderService.errorHandler.getExceptionHandler().loadFailed(
                        loaderService.errorHandler.createContext(), loader, key,
                        attributes, true, e);
            }
            loaderService.cache.valueLoaded(key, v, attributes);
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
                map = null; // loader.loadAll(keysWithAttributes);
            } catch (Exception e) {
// map = loaderService.errorHandler.loadAllFailed(loader,
// keysWithAttributes, true, e);
            }
            if (map != null && map.size() > 0) {
                loaderService.cache.valuesLoaded(map, keysWithAttributes);
            }
        }
    }

    public boolean isDummy() {
        return false;
    }

    @Override
    public long innerGetRefreshTime() {
        return reloadExpirationTime;
    }

    public long getDefaultTimeToRefresh(TimeUnit unit) {
        return 0;
    }

    public void setDefaultTimeToRefresh(long timeToLive, TimeUnit unit) {}

}
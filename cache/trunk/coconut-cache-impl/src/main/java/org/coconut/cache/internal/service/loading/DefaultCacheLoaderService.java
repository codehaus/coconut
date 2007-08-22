/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.InternalCacheSupport;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
import org.coconut.cache.internal.service.threading.InternalCacheThreadingService;
import org.coconut.cache.internal.service.util.ExtendableFutureTask;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache containing this service
 * @param <V>
 *            the type of mapped values
 */
public class DefaultCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V>
        implements ManagedObject {

    final InternalCacheSupport<K, V> cache;

    /** A clock used to check if an entry needs to be refreshed. */
    private final Clock clock;

    private final CacheExceptionService<K, V> errorHandler;

    private final CacheLoader<? super K, ? extends V> loader;

    private final Executor loadExecutor;

    private final Filter<CacheEntry<K, V>> reloadFilter;

    public DefaultCacheLoaderService(final Clock clock,
            InternalCacheAttributeService attributeFactory,
            CacheExceptionService<K, V> exceptionService,
            CacheLoadingConfiguration<K, V> loadConf,
            final InternalCacheThreadingService threadManager,
            final InternalCacheSupport<K, V> cache) {
        super(attributeFactory, cache);
        this.errorHandler = exceptionService;
        this.clock = clock;
        this.loader = loadConf.getLoader();
        this.loadExecutor = threadManager.getExecutor(CacheLoadingService.class)
                .createExecutorService();
        attributeFactory.update().setTimeToFreshNanos(
                LoadingUtils.getInitialTimeToRefresh(loadConf));
        this.reloadFilter = loadConf.getRefreshFilter();
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerServices(Map<Class<?>, Object> serviceMap) {
        if (loader != null) {
            serviceMap.put(CacheLoadingService.class, LoadingUtils.wrapService(this));
        }
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public Map<? super K, ? extends V> loadAllBlocking(Map<? extends K, AttributeMap> keys) {
        return loadAllBlocking(loader, keys);
    }

    /**
     * {@inheritDoc}
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

    /**
     * @see org.coconut.cache.internal.service.loading.InternalCacheLoadingService#loadBlocking(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    private V loadBlocking(CacheLoader<? super K, ? extends V> loader, K key,
            AttributeMap attributes) {
        V v = null;
        if (loader != null) {
            try {
                v = loader.load(key, attributes);
            } catch (Exception e) {
                v = errorHandler.getExceptionHandler().loadFailed(
                        errorHandler.createContext(), loader, key, attributes, true, e);
            }
        }
        return v;
    }

    /** {@inheritDoc} */
    public void manage(ManagedGroup parent) {
        if (loader != null) {
            ManagedGroup g = parent.addChild(CacheLoadingConfiguration.SERVICE_NAME,
                    "Cache Loading attributes and operations");
            g.add(LoadingUtils.wrapMXBean(this));
        }
    }

    /** {@inheritDoc} */
    void doLoad(K key, AttributeMap attributes) {
        LoadValueRunnable lvr = new LoadValueRunnable<K, V>(this, loader, key, attributes);
        loadExecutor.execute(lvr);
    }

    /** {@inheritDoc} */
    void doLoad(Map<? extends K, AttributeMap> mapsWithAttributes) {
        LoadValuesRunnable lvr = new LoadValuesRunnable<K, V>(this, loader,
                mapsWithAttributes);
        FutureTask<V> ft = new FutureTask<V>(lvr, null);
        loadExecutor.execute(ft);
    }

    static class LoadValueRunnable<K, V> extends ExtendableFutureTask {
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
         * {@inheritDoc}
         */
        public Object call() {
            V v = null;
            try {
                v = loader.load(key, attributes);
            } catch (Exception e) {
                v = loaderService.errorHandler.getExceptionHandler().loadFailed(
                        loaderService.errorHandler.createContext(), loader, key,
                        attributes, false, e);
            }
            loaderService.cache.valueLoaded(key, v, attributes);
            return null;
        }

        public K getKey() {
            return key;
        }
    }

    static class LoadValuesRunnable<K, V> implements Runnable {
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

        public Map<? extends K, AttributeMap> getKeys() {
            return keysWithAttributes;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            for (Map.Entry<? extends K, AttributeMap> entry : keysWithAttributes.entrySet()) {
                K key = entry.getKey();
                AttributeMap attributes = entry.getValue();
                V v = null;
                try {
                    v = loader.load(key, attributes);
                } catch (Exception e) {
                    v = loaderService.errorHandler.getExceptionHandler().loadFailed(
                            loaderService.errorHandler.createContext(), loader, key,
                            attributes, false, e);
                }
                loaderService.cache.valueLoaded(key, v, attributes);
            }

        }
    }

}

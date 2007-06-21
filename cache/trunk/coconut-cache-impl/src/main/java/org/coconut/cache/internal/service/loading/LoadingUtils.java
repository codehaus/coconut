package org.coconut.cache.internal.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.management.ManagementUtils.DelegatedCacheMXBean;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.core.AttributeMap;
import org.coconut.core.Transformer;
import org.coconut.filter.Filter;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

public class LoadingUtils {
    public static <K, V> CacheLoadingService<K, V> wrapService(
            CacheLoadingService<K, V> service) {
        return new DelegatedCacheLoadingService<K, V>(service);
    }

    public static CacheMXBean wrapMXBean(Cache<?, ?> service) {
        return new DelegatedCacheMXBean(service);
    }

    /**
     * <p>
     * Must be a public class to allow reflection.
     */
    public static final class DelegatedCacheLoadingMXBean implements CacheLoadingMXBean {
        private final CacheLoadingService<?, ?> service;

        public DelegatedCacheLoadingMXBean(CacheLoadingService<?, ?> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        @ManagedAttribute(description = "The default time to live for cache entries in milliseconds")
        public long getDefaultTimeToRefreshMs() {
            return service.getDefaultTimeToRefresh(TimeUnit.MILLISECONDS);
        }

        public void setDefaultTimeToRefreshMs(long timeToLiveMs) {
            service.setDefaultTimeToRefresh(timeToLiveMs, TimeUnit.MILLISECONDS);
        }

        @ManagedOperation(description = "reload all mappings")
        public void reloadAll() {
            service.reloadAll();
        }
    }

    public static final class DelegatedCacheLoadingService<K, V> implements
            CacheLoadingService<K, V> {
        private final CacheLoadingService<K, V> delegate;

        /**
         * @param service
         */
        public DelegatedCacheLoadingService(CacheLoadingService<K, V> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.delegate = service;
        }

        public void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
                AttributeMap defaultAttributes) {
            delegate.filteredLoad(filter, defaultAttributes);
        }

        public void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
                Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer) {
            delegate.filteredLoad(filter, attributeTransformer);
        }

        public void filteredLoad(Filter<? super CacheEntry<K, V>> filter) {
             delegate.filteredLoad(filter);
        }

        public void forceLoad(K key, AttributeMap attributes) {
             delegate.forceLoad(key, attributes);
        }

        public void forceLoad(K key) {
             delegate.forceLoad(key);
        }

        public void forceLoadAll(AttributeMap attributes) {
             delegate.forceLoadAll(attributes);
        }

        public void forceLoadAll(Collection<? extends K> keys) {
             delegate.forceLoadAll(keys);
        }

        public void forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
             delegate.forceLoadAll(mapsWithAttributes);
        }

        public long getDefaultTimeToRefresh(TimeUnit unit) {
             return delegate.getDefaultTimeToRefresh(unit);
        }

        public void load(K key, AttributeMap attributes) {
             delegate.load(key, attributes);
        }

        public void load(K key) {
            delegate.load(key);
        }

        public void loadAll(Collection<? extends K> keys) {
             delegate.loadAll(keys);
        }

        public void loadAll(Map<K, AttributeMap> mapsWithAttributes) {
             delegate.loadAll(mapsWithAttributes);
        }

        public void reloadAll() {
             delegate.reloadAll();
        }

        public void setDefaultTimeToRefresh(long timeToLive, TimeUnit unit) {
            delegate.setDefaultTimeToRefresh(timeToLive, unit);
        }
    }
}

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
         * @param name
         */
        public DelegatedCacheLoadingService(CacheLoadingService<K, V> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.delegate = service;
        }

        public Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter,
                AttributeMap defaultAttributes) {
            return delegate.filteredLoad(filter, defaultAttributes);
        }

        public Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter,
                Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer) {
            return delegate.filteredLoad(filter, attributeTransformer);
        }

        public Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter) {
            return delegate.filteredLoad(filter);
        }

        public Future<?> forceLoad(K key, AttributeMap attributes) {
            return delegate.forceLoad(key, attributes);
        }

        public Future<?> forceLoad(K key) {
            return delegate.forceLoad(key);
        }

        public Future<?> forceLoadAll(AttributeMap attributes) {
            return delegate.forceLoadAll(attributes);
        }

        public Future<?> forceLoadAll(Collection<? extends K> keys) {
            return delegate.forceLoadAll(keys);
        }

        public Future<?> forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
            return delegate.forceLoadAll(mapsWithAttributes);
        }

        public long getDefaultTimeToRefresh(TimeUnit unit) {
            return delegate.getDefaultTimeToRefresh(unit);
        }

        public Future<?> load(K key, AttributeMap attributes) {
            return delegate.load(key, attributes);
        }

        public Future<?> load(K key) {
            return delegate.load(key);
        }

        public Future<?> loadAll(Collection<? extends K> keys) {
            return delegate.loadAll(keys);
        }

        public Future<?> loadAll(Map<K, AttributeMap> mapsWithAttributes) {
            return delegate.loadAll(mapsWithAttributes);
        }

        public Future<?> reloadAll() {
            return delegate.reloadAll();
        }

        public void setDefaultTimeToRefresh(long timeToLive, TimeUnit unit) {
            delegate.setDefaultTimeToRefresh(timeToLive, unit);
        }
    }
}

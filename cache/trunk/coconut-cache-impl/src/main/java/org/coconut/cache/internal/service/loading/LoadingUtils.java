/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Transformer;
import org.coconut.filter.Filter;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class LoadingUtils {

    /** Cannot instantiate. */
    private LoadingUtils() {}

    public static <K, V> CacheLoadingService<K, V> wrapService(
            CacheLoadingService<K, V> service) {
        return new DelegatedCacheLoadingService<K, V>(service);
    }

    public static CacheLoadingMXBean wrapMXBean(CacheLoadingService<?, ?> service) {
        return new DelegatedCacheLoadingMXBean(service);
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

        /**
         * {@inheritDoc}
         */
        @ManagedAttribute(description = "The default time to live for cache entries in milliseconds")
        public long getDefaultTimeToRefreshMs() {
            return service.getDefaultTimeToRefresh(TimeUnit.MILLISECONDS);
        }

        /**
         * {@inheritDoc}
         */
        public void setDefaultTimeToRefreshMs(long timeToLiveMs) {
            service.setDefaultTimeToRefresh(timeToLiveMs, TimeUnit.MILLISECONDS);
        }

        /**
         * {@inheritDoc}
         */
        @ManagedOperation(description = "reload all mappings")
        public void forceLoadAll() {
            service.forceLoadAll();
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

        /**
         * {@inheritDoc}
         */
        public void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
                AttributeMap defaultAttributes) {
            delegate.filteredLoad(filter, defaultAttributes);
        }

        /**
         * {@inheritDoc}
         */
        public void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
                Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer) {
            delegate.filteredLoad(filter, attributeTransformer);
        }

        /**
         * {@inheritDoc}
         */
        public void filteredLoad(Filter<? super CacheEntry<K, V>> filter) {
            delegate.filteredLoad(filter);
        }

        /**
         * {@inheritDoc}
         */
        public void forceLoad(K key, AttributeMap attributes) {
            delegate.forceLoad(key, attributes);
        }

        /**
         * {@inheritDoc}
         */
        public void forceLoad(K key) {
            delegate.forceLoad(key);
        }

        /**
         * {@inheritDoc}
         */
        public void forceLoadAll(AttributeMap attributes) {
            delegate.forceLoadAll(attributes);
        }

        /**
         * {@inheritDoc}
         */
        public void forceLoadAll(Collection<? extends K> keys) {
            delegate.forceLoadAll(keys);
        }

        /**
         * {@inheritDoc}
         */
        public void forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
            delegate.forceLoadAll(mapsWithAttributes);
        }

        /**
         * {@inheritDoc}
         */
        public long getDefaultTimeToRefresh(TimeUnit unit) {
            return delegate.getDefaultTimeToRefresh(unit);
        }

        /**
         * {@inheritDoc}
         */
        public void load(K key, AttributeMap attributes) {
            delegate.load(key, attributes);
        }

        /**
         * {@inheritDoc}
         */
        public void load(K key) {
            delegate.load(key);
        }

        /**
         * {@inheritDoc}
         */
        public void loadAll(Collection<? extends K> keys) {
            delegate.loadAll(keys);
        }

        /**
         * {@inheritDoc}
         */
        public void loadAll(Map<K, AttributeMap> mapsWithAttributes) {
            delegate.loadAll(mapsWithAttributes);
        }

        /**
         * {@inheritDoc}
         */
        public void forceLoadAll() {
            delegate.forceLoadAll();
        }

        /**
         * {@inheritDoc}
         */
        public void setDefaultTimeToRefresh(long timeToLive, TimeUnit unit) {
            delegate.setDefaultTimeToRefresh(timeToLive, unit);
        }
    }

    public static long convertNanosToRefreshTime(long timeToRefreshNanos, TimeUnit unit) {
        return new CacheLoadingConfiguration().setDefaultTimeToRefresh(
                timeToRefreshNanos, TimeUnit.NANOSECONDS).getDefaultTimeToRefresh(unit);
    }

    public static long convertRefreshTimeToNanos(long timeToRefresh, TimeUnit unit) {
        return new CacheLoadingConfiguration().setDefaultTimeToRefresh(timeToRefresh,
                unit).getDefaultTimeToRefresh(TimeUnit.NANOSECONDS);
    }

    public static long getInitialTimeToRefrehs(CacheLoadingConfiguration<?, ?> conf) {
        long tmp = conf.getDefaultTimeToRefresh(TimeUnit.NANOSECONDS);
        if (tmp == 0) {
            tmp = Long.MAX_VALUE;
        }
        return tmp;
    }
}

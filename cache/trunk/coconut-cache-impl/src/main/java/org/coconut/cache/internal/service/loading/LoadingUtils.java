/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMap;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * Various utilities used for the loading service.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class LoadingUtils {

    /** Cannot instantiate. */
    private LoadingUtils() {}

    /**
     * Converts the specified timeToRefresh in nanoseconds to the specified unit. This
     * conversion routine will handle the special meaning of {@link Long#MAX_VALUE}.
     * 
     * @param timeToRefreshNanos
     *            the time in nanoseconds to convert
     * @param unit
     *            the unit to convert to
     * @return the converted value
     * @throws NullPointerException
     *             if the specified unit is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the specified timeToRefreshNanos is negative
     */
    public static long convertNanosToRefreshTime(long timeToRefreshNanos, TimeUnit unit) {
        return new CacheLoadingConfiguration().setDefaultTimeToRefresh(
                timeToRefreshNanos, TimeUnit.NANOSECONDS).getDefaultTimeToRefresh(unit);
    }

    /**
     * Converts the specified timeToRefresh in the specified unit to nanoseconds. This
     * conversion routine will handle the special meaning of {@link Long#MAX_VALUE}.
     * 
     * @param timeToRefresh
     *            the time to convert from
     * @param unit
     *            the unit to convert from
     * @return the converted value
     * @throws NullPointerException
     *             if the specified unit is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the specified interval is negative
     */
    public static long convertRefreshTimeToNanos(long timeToRefresh, TimeUnit unit) {
        return new CacheLoadingConfiguration().setDefaultTimeToRefresh(timeToRefresh,
                unit).getDefaultTimeToRefresh(TimeUnit.NANOSECONDS);
    }

    /**
     * Returns the time to refresh in nanoseconds from the specified cache loading
     * configuration. Currently this is similar to
     * {@link #convertRefreshTimeToNanos(long, TimeUnit)}, however when we get tree
     * caching working 0 will mean that the time to refresh must be inherited from the
     * parent.
     * 
     * @param conf
     *            the CacheLoadingConfiguration to fetch the refresh time from
     * @return the configured refresh time
     */
    public static long getInitialTimeToRefresh(CacheLoadingConfiguration<?, ?> conf) {
        long tmp = conf.getDefaultTimeToRefresh(TimeUnit.NANOSECONDS);
        if (tmp == 0) {
            tmp = Long.MAX_VALUE;
        }
        return tmp;
    }

    /**
     * Wraps a CacheLoadingService in a CacheLoadingMXBean.
     * 
     * @param service
     *            the CacheLoadingService to wrap
     * @return the wrapped CacheLoadingMXBean
     */
    public static CacheLoadingMXBean wrapMXBean(CacheLoadingService<?, ?> service) {
        return new DelegatedCacheLoadingMXBean(service);
    }

    /**
     * Wraps a CacheLoadingService implementation such that only methods from the
     * CacheLoadingService interface is exposed.
     * 
     * @param service
     *            the CacheLoadingService to wrap
     * @return a wrapped service that only exposes CacheLoadingService methods
     */
    public static <K, V> CacheLoadingService<K, V> wrapService(
            CacheLoadingService<K, V> service) {
        return new DelegatedCacheLoadingService<K, V>(service);
    }

    static <K, V> Callable<AbstractCacheEntry<K, V>> loadValue(
            final AbstractCacheLoadingService<K, V> loaderService, final K key,
            AttributeMap attributes) {
        return new LoadValueRunnable<K, V>(loaderService, key, attributes);
    }

    /**
     * A class that exposes a {@link CacheLoadingService} as a {@link CacheLoadingMXBean}.
     */
    public static final class DelegatedCacheLoadingMXBean implements CacheLoadingMXBean {

        /** The CacheLoadingService that is wrapped. */
        private final CacheLoadingService<?, ?> service;

        /**
         * Creates a new DelegatedCacheLoadingMXBean.
         * 
         * @param service
         *            the CacheLoadingService to wrap
         */
        public DelegatedCacheLoadingMXBean(CacheLoadingService<?, ?> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        /** {@inheritDoc} */
        @ManagedOperation(description = "reload all mappings")
        public void forceLoadAll() {
            service.forceLoadAll();
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The default time to live for cache entries in milliseconds")
        public long getDefaultTimeToRefreshMs() {
            return service.getDefaultTimeToRefresh(TimeUnit.MILLISECONDS);
        }

        /** {@inheritDoc} */
        @ManagedOperation(description = "Attempts to reload all entries that are either expired or which needs refreshing")
        public void loadAll() {
            service.loadAll();
        }

        /** {@inheritDoc} */
        public void setDefaultTimeToRefreshMs(long timeToLiveMs) {
            service.setDefaultTimeToRefresh(timeToLiveMs, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * A wrapper class that exposes only the CacheLoadingService methods of a
     * CacheLoadingService implementation.
     */
    public static final class DelegatedCacheLoadingService<K, V> implements
            CacheLoadingService<K, V> {

        /** The CacheLoadingService that is wrapped. */
        private final CacheLoadingService<K, V> delegate;

        /**
         * Creates a wrapped CacheLoadingService from the specified implementation.
         * 
         * @param service
         *            the CacheLoadingService to wrap
         */
        public DelegatedCacheLoadingService(CacheLoadingService<K, V> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.delegate = service;
        }

        /** {@inheritDoc} */
        public void forceLoad(K key) {
            delegate.forceLoad(key);
        }

        /** {@inheritDoc} */
        public void forceLoad(K key, AttributeMap attributes) {
            delegate.forceLoad(key, attributes);
        }

        /** {@inheritDoc} */
        public void forceLoadAll() {
            delegate.forceLoadAll();
        }

        /** {@inheritDoc} */
        public void forceLoadAll(AttributeMap attributes) {
            delegate.forceLoadAll(attributes);
        }

        /** {@inheritDoc} */
        public void forceLoadAll(Collection<? extends K> keys) {
            delegate.forceLoadAll(keys);
        }

        /** {@inheritDoc} */
        public void forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
            delegate.forceLoadAll(mapsWithAttributes);
        }

        /** {@inheritDoc} */
        public long getDefaultTimeToRefresh(TimeUnit unit) {
            return delegate.getDefaultTimeToRefresh(unit);
        }

        /** {@inheritDoc} */
        public void load(K key) {
            delegate.load(key);
        }

        /** {@inheritDoc} */
        public void load(K key, AttributeMap attributes) {
            delegate.load(key, attributes);
        }

        /** {@inheritDoc} */
        public void loadAll() {
            delegate.loadAll();
        }

        /** {@inheritDoc} */
        public void loadAll(AttributeMap attributes) {
            delegate.loadAll(attributes);
        }

        /** {@inheritDoc} */
        public void loadAll(Collection<? extends K> keys) {
            delegate.loadAll(keys);
        }

        /** {@inheritDoc} */
        public void loadAll(Map<K, AttributeMap> mapsWithAttributes) {
            delegate.loadAll(mapsWithAttributes);
        }
        /** {@inheritDoc} */
        public void setDefaultTimeToRefresh(long timeToLive, TimeUnit unit) {
            delegate.setDefaultTimeToRefresh(timeToLive, unit);
        }
    }

    static class LoadValueRunnable<K, V> implements Callable<AbstractCacheEntry<K, V>> {
        private final AttributeMap attributes;

        private final K key;

        private final AbstractCacheLoadingService<K, V> loaderService;

        /**
         * @param loader
         * @param key
         * @param callback
         */
        LoadValueRunnable(final AbstractCacheLoadingService<K, V> loaderService,
                final K key, AttributeMap attributes) {
            if (key == null) {
                throw new NullPointerException("key is null");
            }
            this.key = key;
            this.loaderService = loaderService;
            this.attributes = attributes;
        }

        /** {@inheritDoc} */
        public AbstractCacheEntry<K, V> call() {
            return loaderService.loadAndAddToCache(key, attributes, false);
        }

        public K getKey() {
            return key;
        }
    }

    static class LoadValuesRunnable<K, V> implements Runnable {
        private final Map<? extends K, AttributeMap> keysWithAttributes;

        private final CacheLoader<? super K, ? extends V> loader;

        private final AbstractCacheLoadingService<K, V> loaderService;

        /**
         * @param loader
         * @param key
         * @param callback
         */
        LoadValuesRunnable(final AbstractCacheLoadingService<K, V> loaderService,
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

        /** {@inheritDoc} */
        public void run() {
            for (Map.Entry<? extends K, AttributeMap> entry : keysWithAttributes
                    .entrySet()) {
                K key = entry.getKey();
                AttributeMap attributes = entry.getValue();
                throw new UnsupportedOperationException();
//                loaderService.doLoad(loader, key, attributes, false);
            }
        }
    }
}

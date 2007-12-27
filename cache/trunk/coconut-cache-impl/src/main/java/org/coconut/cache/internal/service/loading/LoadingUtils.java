/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * Various utilities used for the loading service.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class LoadingUtils {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private LoadingUtils() {}

    // /CLOVER:ON
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
        return new CacheLoadingConfiguration().setDefaultTimeToRefresh(timeToRefreshNanos,
                TimeUnit.NANOSECONDS).getDefaultTimeToRefresh(unit);
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
        return new CacheLoadingConfiguration().setDefaultTimeToRefresh(timeToRefresh, unit)
                .getDefaultTimeToRefresh(TimeUnit.NANOSECONDS);
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
        return tmp == 0 ? Long.MAX_VALUE : tmp;
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

    public static <K, V> List<UnsynchronizedCacheLoaderCallback<K, V>> findAndRemoveCallbacks(
            Iterable<UnsynchronizedCacheLoaderCallback<K, V>> i) {
        ArrayList<UnsynchronizedCacheLoaderCallback<K, V>> missing = new ArrayList<UnsynchronizedCacheLoaderCallback<K, V>>();
        for (Iterator<UnsynchronizedCacheLoaderCallback<K, V>> iterator = i.iterator(); iterator
                .hasNext();) {
            UnsynchronizedCacheLoaderCallback<K, V> callback = iterator.next();
            if (!callback.isDone()) {
                missing.add(callback);
                iterator.remove();
            }
        }
        return missing;
    }

    /**
     * Wraps a CacheLoadingService implementation such that only methods from the
     * CacheLoadingService interface is exposed.
     *
     * @param service
     *            the CacheLoadingService to wrap
     * @return a wrapped service that only exposes CacheLoadingService methods
     * @param <K>
     *            the type of keys maintained by this service
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheLoadingService<K, V> wrapService(CacheLoadingService<K, V> service) {
        return new DelegatedCacheLoadingService<K, V>(service);
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
        public long getDefaultTimeToRefreshMs() {
            return service.getDefaultTimeToRefresh(TimeUnit.MILLISECONDS);
        }

        /** {@inheritDoc} */
        @ManagedOperation(description = "Attempts to reload all entries that are either expired or which needs refreshing")
        public void loadAll() {
            service.loadAll();
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The default time to live for cache entries in milliseconds")
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
        public void forceLoadAll(Map<? extends K, ? extends AttributeMap> mapsWithAttributes) {
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
        public void loadAll(Map<? extends K, ? extends AttributeMap> mapsWithAttributes) {
            delegate.loadAll(mapsWithAttributes);
        }

        /** {@inheritDoc} */
        public void setDefaultTimeToRefresh(long timeToLive, TimeUnit unit) {
            delegate.setDefaultTimeToRefresh(timeToLive, unit);
        }
    }
}

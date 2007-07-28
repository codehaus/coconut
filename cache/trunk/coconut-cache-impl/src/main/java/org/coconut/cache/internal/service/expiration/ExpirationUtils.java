/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;
import org.coconut.management.annotation.ManagedAttribute;

/**
 * Various utility classes for expiration service implementation.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class ExpirationUtils {

    /** Cannot instantiate. */
    private ExpirationUtils() {}

    /**
     * Returns the initial time to live in nanoseconds from the specified expiration
     * configuration.
     * 
     * @param conf
     *            the configuration to read the initial time to live from
     * @return the initial time to live in nanoseconds
     */
    public static long getInitialTimeToLiveNS(CacheExpirationConfiguration<?, ?> conf) {
        long tmp = conf.getDefaultTimeToLive(TimeUnit.NANOSECONDS);
        return tmp == 0 ? Long.MAX_VALUE : tmp;
    }

    public static long convertNanosToExpirationTime(long timeToLiveNanos, TimeUnit unit) {
        return new CacheExpirationConfiguration().setDefaultTimeToLive(timeToLiveNanos,
                TimeUnit.NANOSECONDS).getDefaultTimeToLive(unit);
    }

    public static long convertExpirationTimeToNanos(long timeToLive, TimeUnit unit) {
        return new CacheExpirationConfiguration().setDefaultTimeToLive(timeToLive, unit)
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS);
    }

    public static <K, V> boolean isExpired(CacheEntry<K, V> entry, Clock clock,
            Filter<CacheEntry<K, V>> filter) {
        if (filter != null && filter.accept(entry)) {
            return true;
        }
        long expTime = entry.getExpirationTime();
        return expTime == CacheExpirationService.NEVER_EXPIRE ? false : clock
                .isPassed(expTime);
    }

    /**
     * Wraps the specified CacheExpirationService implementation only exposing the methods
     * available in the {@link CacheExpirationService} interface.
     * 
     * @param service
     *            the expiration service we want to wrap
     * @return a wrapped service that only exposes CacheExpirationService methods 
     * @param <K>
     *            the type of keys maintained by the specified service
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheExpirationService<K, V> wrapService(
            CacheExpirationService<K, V> service) {
        return new DelegatedCacheExpirationService<K, V>(service);
    }

    /**
     * Wraps a {@link CacheExpirationService} as a {@link CacheExpirationMXBean}.
     * 
     * @param service
     *            the service to wrap
     * @return a wrapped CacheExpirationMXBean
     */
    public static CacheExpirationMXBean wrapAsMXBean(CacheExpirationService<?, ?> service) {
        return new DelegatedCacheExpirationMXBean(service);
    }

    /**
     * A wrapper class that exposes an ExecutorService as a CacheExpirationMXBean.
     */
    public static class DelegatedCacheExpirationMXBean implements CacheExpirationMXBean {
        /** The CacheExpirationService we are wrapping. */
        private final CacheExpirationService<?, ?> service;

        /**
         * Creates a new DelegatedCacheExpirationMXBean from the specified expiration
         * service.
         * 
         * @param service
         *            the expiration service to wrap
         */
        public DelegatedCacheExpirationMXBean(CacheExpirationService<?, ?> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        /**
         * {@inheritDoc}
         */
        @ManagedAttribute(description = "The default time to live for cache entries in milliseconds")
        public long getDefaultTimeToLiveMs() {
            return service.getDefaultTimeToLive(TimeUnit.MILLISECONDS);
        }

        /**
         * {@inheritDoc}
         */
        public void setDefaultTimeToLiveMs(long timeToLiveMs) {
            service.setDefaultTimeToLive(timeToLiveMs, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * A wrapper class that exposes only the CacheExpirationService methods of an
     * CacheExpirationService implementation.
     */
    public static class DelegatedCacheExpirationService<K, V> implements
            CacheExpirationService<K, V> {
        /** The expiration service we are wrapping. */
        private final CacheExpirationService<K, V> service;

        /**
         * Creates a new DelegatedCacheExpirationService from the specified expiration
         * service.
         * 
         * @param service
         *            the expiration service to wrap
         */
        public DelegatedCacheExpirationService(CacheExpirationService<K, V> service) {
            this.service = service;
        }

        /**
         * {@inheritDoc}
         */
        public int removeAll(Collection<? extends K> keys) {
            return service.removeAll(keys);
        }

        /**
         * {@inheritDoc}
         */
        public int removeFiltered(Filter<? super CacheEntry<K, V>> filter) {
            return service.removeFiltered(filter);
        }

        /**
         * {@inheritDoc}
         */
        public long getDefaultTimeToLive(TimeUnit unit) {
            return service.getDefaultTimeToLive(unit);
        }

        /**
         * {@inheritDoc}
         */
        public V put(K key, V value, long expirationTime, TimeUnit unit) {
            return service.put(key, value, expirationTime, unit);
        }

        /**
         * {@inheritDoc}
         */
        public void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
            service.putAll(t, timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
            service.setDefaultTimeToLive(timeToLive, unit);
        }

    }
}

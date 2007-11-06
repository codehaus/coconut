/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.Clock;
import org.coconut.filter.Predicate;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * Various utility classes for expiration service implementation.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class ExpirationUtils {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private ExpirationUtils() {}
    // /CLOVER:ON

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

    /**
     * Converts the specified timeToLiveNanos.
     * 
     * @param timeToLiveNanos
     * @param unit
     * @return
     * @throws IllegalArgumentException
     *             if the specified timeToLiveNanos is a non positive number
     * @throws NullPointerException
     *             if the specified unit is <tt>null</tt>
     */
    public static long convertNanosToExpirationTime(long timeToLiveNanos, TimeUnit unit) {
        return new CacheExpirationConfiguration().setDefaultTimeToLive(timeToLiveNanos,
                TimeUnit.NANOSECONDS).getDefaultTimeToLive(unit);
    }

    public static long convertExpirationTimeToNanos(long timeToLive, TimeUnit unit) {
        return new CacheExpirationConfiguration().setDefaultTimeToLive(timeToLive, unit)
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS);
    }

    /**
     * Checks if the specified cache entry is expired.
     * 
     * @param entry
     *            the cache entry to check for expiration
     * @param clock
     *            the clock that is used to check the expiration time
     * @param filter
     *            an additional filter that is used to check the expiration status of the
     *            cache entry. This filter may be <code>null</code>
     * @return <code>true</code> if the cache entry is expired, otherwise false
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> boolean isExpired(CacheEntry<K, V> entry, Clock clock,
            Predicate<CacheEntry<K, V>> filter) {
        if (filter != null && filter.evaluate(entry)) {
            return true;
        }
        long expTime = entry.getExpirationTime();
        return expTime == CacheExpirationService.NEVER_EXPIRE ? false : clock.isPassed(expTime);
    }

    /**
     * Wraps the specified CacheExpirationService implementation only exposing the methods
     * available in the {@link CacheExpirationService} interface.
     * 
     * @param expirationService
     *            the expiration service we want to wrap
     * @return a wrapped service that only exposes CacheExpirationService methods
     * @param <K>
     *            the type of keys maintained by the specified service
     * @param <V>
     *            the type of mapped values
     * @throws NullPointerException
     *             if the specified expiration is <code>null</code>
     */
    public static <K, V> CacheExpirationService<K, V> wrapService(
            CacheExpirationService<K, V> expirationService) {
        return new DelegatedCacheExpirationService<K, V>(expirationService);
    }

    /**
     * Wraps a {@link CacheExpirationService} as a {@link CacheExpirationMXBean}.
     * 
     * @param expirationService
     *            the service to wrap
     * @return a wrapped CacheExpirationMXBean
     * @throws NullPointerException
     *             if the specified expiration is <code>null</code>
     */
    public static CacheExpirationMXBean wrapAsMXBean(CacheExpirationService<?, ?> expirationService) {
        return new DelegatedCacheExpirationMXBean(expirationService);
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
         * @param expirationService
         *            the expiration service to wrap
         */
        public DelegatedCacheExpirationMXBean(CacheExpirationService<?, ?> expirationService) {
            if (expirationService == null) {
                throw new NullPointerException("expirationService is null");
            }
            this.service = expirationService;
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The default time to live for cache entries in milliseconds")
        public long getDefaultTimeToLiveMs() {
            return service.getDefaultTimeToLive(TimeUnit.MILLISECONDS);
        }

        /** {@inheritDoc} */
        public void setDefaultTimeToLiveMs(long timeToLiveMs) {
            service.setDefaultTimeToLive(timeToLiveMs, TimeUnit.MILLISECONDS);
        }

        /** {@inheritDoc} */
        @ManagedOperation(description = "Removes all expired items from the cache")
        public void purgeExpired() {
            service.purgeExpired();
        }
    }

    /**
     * A wrapper class that exposes only the CacheExpirationService methods of an
     * CacheExpirationService implementation.
     */
    public static class DelegatedCacheExpirationService<K, V> implements
            CacheExpirationService<K, V> {
        /** The expiration service we are wrapping. */
        private final CacheExpirationService<K, V> delegate;

        /**
         * Creates a new DelegatedCacheExpirationService from the specified expiration
         * service.
         * 
         * @param expirationService
         *            the expiration service to wrap
         */
        public DelegatedCacheExpirationService(CacheExpirationService<K, V> expirationService) {
            if (expirationService == null) {
                throw new NullPointerException("expirationService is null");
            }
            this.delegate = expirationService;
        }

        /** {@inheritDoc} */
        public long getDefaultTimeToLive(TimeUnit unit) {
            return delegate.getDefaultTimeToLive(unit);
        }

        /** {@inheritDoc} */
        public V put(K key, V value, long expirationTime, TimeUnit unit) {
            return delegate.put(key, value, expirationTime, unit);
        }

        /** {@inheritDoc} */
        public void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
            delegate.putAll(t, timeout, unit);
        }

        /** {@inheritDoc} */
        public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
            delegate.setDefaultTimeToLive(timeToLive, unit);
        }

        /** {@inheritDoc} */
        public void purgeExpired() {
            delegate.purgeExpired();
        }
    }
}

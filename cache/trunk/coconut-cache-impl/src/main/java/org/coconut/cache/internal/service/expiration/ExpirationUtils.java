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

public class ExpirationUtils {

    public static long getInitialTimeToLive(CacheExpirationConfiguration<?, ?> conf) {
        long tmp = conf.getDefaultTimeToLive(TimeUnit.NANOSECONDS);
        if (tmp == 0) {
            tmp = Long.MAX_VALUE;
        }
        return tmp;
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

    public static <K, V> CacheExpirationService<K, V> wrapService(
            CacheExpirationService<K, V> service) {
        return new DelegatedCacheExpirationService<K, V>(service);
    }

    public static CacheExpirationMXBean wrapMXBean(CacheExpirationService<?, ?> service) {
        return new DelegatedCacheExpirationMXBean(service);
    }

    /**
     * A wrapper class that exposes only the ExecutorService methods of an implementation.
     */
    public static class DelegatedCacheExpirationMXBean implements CacheExpirationMXBean {
        private final CacheExpirationService<?, ?> service;

        public DelegatedCacheExpirationMXBean(CacheExpirationService<?, ?> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        @ManagedAttribute(description = "The default time to live for cache entries in milliseconds")
        public long getDefaultTimeToLiveMs() {
            return service.getDefaultTimeToLive(TimeUnit.MILLISECONDS);
        }

        public void setDefaultTimeToLiveMs(long timeToLiveMs) {
            service.setDefaultTimeToLive(timeToLiveMs, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * A wrapper class that exposes only the ExecutorService methods of an implementation.
     */
    public static class DelegatedCacheExpirationService<K, V> implements
            CacheExpirationService<K, V> {
        private final CacheExpirationService<K, V> service;

        public DelegatedCacheExpirationService(CacheExpirationService<K, V> service) {
            this.service = service;
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#removeAll(java.util.Collection)
         */
        public int removeAll(Collection<? extends K> keys) {
            return service.removeAll(keys);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#removeAll(org.coconut.filter.Filter)
         */
        public int removeAll(Filter<? extends CacheEntry<K, V>> filter) {
            return service.removeAll(filter);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#getDefaultTimeToLive(java.util.concurrent.TimeUnit)
         */
        public long getDefaultTimeToLive(TimeUnit unit) {
            return service.getDefaultTimeToLive(unit);
        }


        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#put(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
         */
        public V put(K key, V value, long expirationTime, TimeUnit unit) {
            return service.put(key, value, expirationTime, unit);
        }


        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#putAll(java.util.Map, long, java.util.concurrent.TimeUnit)
         */
        public void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
            service.putAll(t, timeout, unit);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#setDefaultTimeToLive(long,
         *      java.util.concurrent.TimeUnit)
         */
        public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
            service.setDefaultTimeToLive(timeToLive, unit);
        }

    }
}

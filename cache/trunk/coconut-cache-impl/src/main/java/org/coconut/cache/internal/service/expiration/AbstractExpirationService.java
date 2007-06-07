/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheServices;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractExpirationService<K, V> extends
        AbstractInternalCacheService implements InternalExpirationService<K, V>,
        CacheExpirationService<K, V> {

    final Clock clock;

    @Override
    public void start(Map<Class<?>, Object> allServices) {
        CacheManagementService cms = (CacheManagementService) allServices
                .get(CacheManagementService.class);
        if (cms != null) {
            cms.getRoot().add(new DelegatedCacheExpirationMXBean(this));
        }
        super.start(allServices);
    }

    private final CacheHelper<K, V> helper;

    @Override
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        serviceMap.put(CacheExpirationService.class,
                new DelegatedCacheExpirationService<K, V>(this));
        serviceMap.put(CacheExpirationMXBean.class, new DelegatedCacheExpirationMXBean(
                this));
    }

    private final AbstractCacheExceptionHandler<K, V> errorHandler;

    public AbstractExpirationService(Clock clock, CacheHelper<K, V> helper,
            CacheExceptionHandlingConfiguration<K, V> exceptionConfiguration) {
        super(CacheExpirationConfiguration.SERVICE_NAME);
        this.clock = clock;
        this.helper = helper;
        this.errorHandler = exceptionConfiguration.getExceptionHandler();
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expire(java.lang.Object)
     */
    public final boolean expire(K key) {
        return helper.expire(key);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(java.util.Collection)
     */
    public final int removeAll(Collection<? extends K> keys) {
        return helper.expireAll(keys);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(org.coconut.filter.Filter)
     */
    public final int removeAll(Filter<? extends CacheEntry<K, V>> filter) {
        return helper.expireAll(filter);
    }

// public void registerServices(Map<Class<?>, Object> map) {
//        
// }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll()
     */
    public final int removeAll() {
        return removeAll((Filter) Filters.trueFilter());
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationMXBean#getDefaultExpirationMs()
     */
    public final long getDefaultTimeToLiveMs() {
        return getDefaultTimeToLive(TimeUnit.MILLISECONDS);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationMXBean#setDefaultTimeout(long)
     */
    public final void setDefaultTimeToLiveMs(long timeToLiveMs) {
        setDefaultTimeToLive(timeToLiveMs, TimeUnit.MILLISECONDS);
    }

    public final V put(K key, V value, long timeToLive, TimeUnit unit) {
        if (timeToLive < 0) {
            throw new IllegalArgumentException("timeToLive must not be negative, was "
                    + timeToLive);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        long ttl = timeToLive;
        if (ttl != CacheExpirationService.NEVER_EXPIRE
                || ttl != CacheExpirationService.NEVER_EXPIRE) {
            ttl = TimeUnit.NANOSECONDS.convert(ttl, unit);
        }
        return doPut(key, value, ttl);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#putAll(java.util.Map,
     *      long, java.util.concurrent.TimeUnit)
     */
    public void putAll(Map<? extends K, ? extends V> t, long timeToLive, TimeUnit unit) {
        if (timeToLive < 0) {
            throw new IllegalArgumentException(
                    "expirationTime must not be negative, was " + timeToLive);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        long ttl = timeToLive;
        if (ttl != CacheExpirationService.NEVER_EXPIRE
                || ttl != CacheExpirationService.DEFAULT_EXPIRATION) {
            ttl = TimeUnit.NANOSECONDS.convert(ttl, unit);
        }
        doPutAll(t, ttl);
    }

    abstract V doPut(K key, V value, long timeToLiveNano);

    abstract void doPutAll(Map<? extends K, ? extends V> t, long timeToLiveNano);

    abstract Filter<?> getExpirationFilter();
    String getExpirationFilterAsString() {
        Object o = getExpirationFilter();
        if (o == null) {
            return "null";
        } else {
            return o.toString();
        }
    }

    final boolean isExpired(CacheEntry<K, V> entry, Filter<CacheEntry<K, V>> filter) {
        if (filter != null && filter.accept(entry)) {
            return true;
        }
        long expTime = entry.getExpirationTime();
        return expTime == CacheExpirationService.NEVER_EXPIRE ? false : clock
                .isPassed(expTime);
    }

    final long readTTLAttribute(K key, AttributeMap attributes) {
        long time = CacheExpirationService.DEFAULT_EXPIRATION;
        if (attributes != null) {
            time = attributes.getLong(CacheAttributes.TIME_TO_LIVE_NS);
            if (time < 0) {
                errorHandler
                        .warning("'The specified attribute map, contained an invalid attribute. "
                                + " Must specify a positive expirationTime, was [expirationTime= "
                                + time + " for key = " + key);
                time = CacheExpirationService.DEFAULT_EXPIRATION;
            }
        }
        return time;
    }

    /**
     * Checks that the specified arguments are valid or throws an runtime exception.
     * 
     * @param timeToLive
     * @param unit
     *            the time unit of the timeToLive argument
     * @throws IllegalArgumentException
     *             if specified timeToLive is a non positive number
     * @throws NullPointerException
     *             if the specified unit is <tt>null</tt>
     */
    static long verifyTTL(long timeToLive, TimeUnit unit) {
        return new CacheExpirationConfiguration().setDefaultTimeToLive(timeToLive, unit)
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS);
    }

    static long ttlToUnit(long ttlNano, TimeUnit unit) {
        if (ttlNano == CacheExpirationService.NEVER_EXPIRE) {
            // don't convert relative to time unit
            return CacheExpirationService.NEVER_EXPIRE;
        } else {
            return unit.convert(ttlNano, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * A wrapper class that exposes only the ExecutorService methods of an implementation.
     */
    static class DelegatedCacheExpirationMXBean implements CacheExpirationMXBean {
        private final AbstractExpirationService<?, ?> service;

        DelegatedCacheExpirationMXBean(AbstractExpirationService<?, ?> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        @ManagedAttribute(description = "The default time to live for cache entries in milliseconds")
        public long getDefaultTimeToLiveMs() {
            return service.getDefaultTimeToLive(TimeUnit.MILLISECONDS);
        }

        @ManagedAttribute(description = "toString() on the defined expiration filter")
        public String getFilterAsString() {
            return service.getExpirationFilterAsString();
        }

        public void setDefaultTimeToLiveMs(long timeToLiveMs) {
            service.setDefaultTimeToLive(timeToLiveMs, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * A wrapper class that exposes only the ExecutorService methods of an implementation.
     */
    static class DelegatedCacheExpirationService<K, V> implements
            CacheExpirationService<K, V> {
        private final CacheExpirationService<K, V> service;

        DelegatedCacheExpirationService(CacheExpirationService<K, V> service) {
            this.service = service;
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll()
         */
        public int removeAll() {
            return service.removeAll();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(java.util.Collection)
         */
        public int removeAll(Collection<? extends K> keys) {
            return service.removeAll(keys);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(org.coconut.filter.Filter)
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
         * @see org.coconut.cache.service.expiration.CacheExpirationService#put(java.lang.Object,
         *      java.lang.Object, long, java.util.concurrent.TimeUnit)
         */
        public V put(K key, V value, long expirationTime, TimeUnit unit) {
            return service.put(key, value, expirationTime, unit);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#putAll(java.util.Map,
         *      long, java.util.concurrent.TimeUnit)
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

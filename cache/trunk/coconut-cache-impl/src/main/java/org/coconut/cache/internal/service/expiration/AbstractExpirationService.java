/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.InternalCacheService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.spi.AbstractCacheService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractExpirationService<K, V> extends AbstractCacheService
        implements InternalCacheService, InternalExpirationService<K, V>,
        CacheExpirationService<K, V> {

    public static final String SERVICE_NAME = "expiration";

     final Clock clock;

    private final CacheHelper<K, V> helper;

    private final AbstractCacheExceptionHandler<K, V> errorHandler;

    public AbstractExpirationService(Clock clock, CacheHelper<K, V> helper,
            AbstractCacheExceptionHandler<K, V> errorHandler) {
        super(SERVICE_NAME);
        this.clock = clock;
        this.helper = helper;
        this.errorHandler = errorHandler;
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

    public void registerServices(Map<Class, Object> map) {
        map.put(CacheExpirationService.class, InternalExpirationUtils.wrap(this));
    }

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
        return doPut(key, value, timeToLive);
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
            time = attributes.getLong(CacheAttributes.TIME_TO_LIVE_NANO);
            if (time < 0) {
                errorHandler
                        .warning("'Must specify a positive expirationTime, was [expirationTime= "
                                + time + " for key = " + key);
                time = CacheExpirationService.DEFAULT_EXPIRATION;
            }
        }
        return time;
    }

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
}

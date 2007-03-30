/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.defaults.DefaultAttributes;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;
import org.coconut.core.AttributeMaps.DefaultAttributeMap;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheExpirationService<K, V> extends
        AbstractCacheExpirationService<K, V> {
    final Cache<K, V> cache;

    private final Clock clock;

    private long defaultExpirationTime;

    private Filter<CacheEntry<K, V>> expirationFilter;

    private final CacheErrorHandler<K, V> errorHandler;

    public DefaultCacheExpirationService(Cache<K, V> cache,
            CacheExpirationConfiguration<K, V> conf, Clock clock,
            CacheErrorHandler<K, V> errorHandler) {
        this.clock = clock;
        this.cache = cache;
        this.defaultExpirationTime = conf.getDefaultTimeToLive(TimeUnit.NANOSECONDS);
        this.expirationFilter = conf.getExpirationFilter();
        this.errorHandler = errorHandler;
    }

    /**
     * @see org.coconut.cache.spi.ExpirationSupport#getExpirationFilter()
     */
    public Filter<CacheEntry<K, V>> getExpirationFilter() {
        return expirationFilter;
    }

    /**
     * @see org.coconut.cache.spi.ExpirationSupport#getExpirationFilter()
     */
    public void setExpirationFilter(Filter<CacheEntry<K, V>> filter) {
        this.expirationFilter = filter;
    }

    /**
     * Returns true if the specified entry is expired, otherwise false.
     * 
     * @param entry
     *            the entry to check for expiration
     * @return true if the entry is expired, otherwise false
     */
    public boolean isExpired(CacheEntry<K, V> entry) {
        Filter<CacheEntry<K, V>> filter = expirationFilter;
        if (filter != null && filter.accept(entry)) {
            return true;
        }
        long expTime = entry.getExpirationTime();
        return expTime == CacheExpirationService.NEVER_EXPIRE ? false : clock
                .isPassed(expTime);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#put(java.lang.Object,
     *      java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    public V put(K key, V value, long timeToLive, TimeUnit unit) {
        if (timeToLive < 0) {
            throw new IllegalArgumentException(
                    "timeToLive must not be negative, was " + timeToLive);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        AttributeMap map = new DefaultAttributeMap();
        map.put(DefaultAttributes.TIME_TO_LIVE_NANO, unit.toNanos(timeToLive));
        return cache.put(key, value, map);// checks for null key+value
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
        AttributeMap map = new DefaultAttributeMap();
        map.put(DefaultAttributes.TIME_TO_LIVE_NANO, unit.toNanos(timeToLive));
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.InternalCacheExpirationService#getExpirationTime(java.lang.Object,
     *      java.lang.Object, org.coconut.cache.service.loading.AttributeMap)
     */
    public long getExpirationTime(K key, V value, AttributeMap attributes) {
        long timeout = defaultExpirationTime;
        if (attributes != null) {
            long time = attributes.getLong(DefaultAttributes.TIME_TO_LIVE_NANO);
            if (time < 0) {
                errorHandler
                        .warning("'Must specify a positive expirationTime was [expirationTime= "
                                + time + " for key = " + key);
            } else if (time > 0) {
                timeout = time;
            }
            //
            // else use defaults
        }
        if (timeout == CacheExpirationService.NEVER_EXPIRE) {
            return CacheExpirationService.NEVER_EXPIRE;
        } else {
            return clock.getDeadlineFromNow(timeout, TimeUnit.NANOSECONDS);
        }

    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#getDefaultTimeout(java.util.concurrent.TimeUnit)
     */
    public long getDefaultTimeToLive(TimeUnit unit) {
        if (defaultExpirationTime == CacheExpirationService.NEVER_EXPIRE) {
            // don't convert relative to time unit
            return CacheExpirationService.NEVER_EXPIRE;
        } else {
            return unit.convert(defaultExpirationTime, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#setDefaultTimeout(long,
     *      java.util.concurrent.TimeUnit)
     */
    public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
        defaultExpirationTime = new CacheExpirationConfiguration().setDefaultTimeToLive(
                timeToLive, unit).getDefaultTimeToLive(TimeUnit.NANOSECONDS);

    }

    // @ManagedAttribute(defaultValue = "Default Expiration Description",
    // description = "The default expiration time of the cache")
    // public String getDefaultExpirationDescription() {
    // long d = getDefaultExpirationTime();
    // if (d == CacheExpirationService.NEVER_EXPIRE) {
    // return "Never expire";
    // }
    // return TabularFormatter.formatTime2(d, TimeUnit.MILLISECONDS);
    // }

}

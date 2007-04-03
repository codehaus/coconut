/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheExpirationService<K, V> extends
        AbstractCacheExpirationService<K, V> {

    private final InternalCacheAttributeService attributeFactory;

    private final Clock clock;

    private long defaultExpirationTime;

    private final CacheExceptionHandler<K, V> errorHandler;

    private Filter<CacheEntry<K, V>> expirationFilter;

    private final CacheHelper<K, V> helper;

    public DefaultCacheExpirationService(CacheHelper<K, V> helper,
            CacheExpirationConfiguration<K, V> conf, Clock clock,
            CacheExceptionHandler<K, V> errorHandler,
            InternalCacheAttributeService attributeFactory) {
        this.helper = helper;
        this.clock = clock;
        this.defaultExpirationTime = conf.getDefaultTimeToLive(TimeUnit.NANOSECONDS);
        this.expirationFilter = conf.getExpirationFilter();
        this.errorHandler = errorHandler;
        this.attributeFactory = attributeFactory;
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expire(java.lang.Object)
     */
    public boolean expire(K key) {
        return helper.expire(key);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(java.util.Collection)
     */
    public int expireAll(Collection<? extends K> keys) {
        return helper.expireAll(keys);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(org.coconut.filter.Filter)
     */
    public int expireAll(Filter<? extends CacheEntry<K, V>> filter) {
        return helper.expireAll(filter);
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
     * @see org.coconut.cache.spi.ExpirationSupport#getExpirationFilter()
     */
    public Filter<CacheEntry<K, V>> getExpirationFilter() {
        return expirationFilter;
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.InternalCacheExpirationService#getExpirationTime(java.lang.Object,
     *      java.lang.Object, org.coconut.cache.service.loading.AttributeMap)
     */
    public long getExpirationTime(K key, V value, AttributeMap attributes) {
        long timeout = defaultExpirationTime;
        if (attributes != null) {
            long time = attributes.getLong(CacheAttributes.TIME_TO_LIVE_NANO);
            if (time < 0) {
                errorHandler
                        .warning("'Must specify a positive expirationTime, was [expirationTime= "
                                + time + " for key = " + key);
            } else if (time > 0) {
                timeout = time;
            }
            // else use defaults
        }
        if (timeout == CacheExpirationService.NEVER_EXPIRE) {
            return CacheExpirationService.NEVER_EXPIRE;
        } else {
            return clock.getDeadlineFromNow(timeout, TimeUnit.NANOSECONDS);
        }

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
            throw new IllegalArgumentException("timeToLive must not be negative, was "
                    + timeToLive);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        AttributeMap map = attributeFactory.createMap();
        map.put(CacheAttributes.TIME_TO_LIVE_NANO, getTime(timeToLive, unit));
        return helper.put(key, value, map);// checks for null key+value
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
        HashMap<K, AttributeMap> attributes = new HashMap<K, AttributeMap>();
        long timeToLiveNano = getTime(timeToLive, unit);
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            K key = entry.getKey();
            AttributeMap map = attributeFactory.createMap();
            map.put(CacheAttributes.TIME_TO_LIVE_NANO, timeToLiveNano);
            attributes.put(key, map);
        }
        helper.putAll(t, attributes);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#setDefaultTimeout(long,
     *      java.util.concurrent.TimeUnit)
     */
    public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
        defaultExpirationTime = new CacheExpirationConfiguration().setDefaultTimeToLive(
                timeToLive, unit).getDefaultTimeToLive(TimeUnit.NANOSECONDS);
    }

    /**
     * @see org.coconut.cache.spi.ExpirationSupport#getExpirationFilter()
     */
    public void setExpirationFilter(Filter<CacheEntry<K, V>> filter) {
        this.expirationFilter = filter;
    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheService#isDummy()
     */
    public boolean isDummy() {
        return false;
    }

    private long getTime(long defaultTimeToLive, TimeUnit unit) {
        if (defaultTimeToLive == CacheExpirationService.DEFAULT_EXPIRATION) {
            return getDefaultTimeToLive(TimeUnit.NANOSECONDS);
        } else if (defaultTimeToLive == CacheExpirationService.NEVER_EXPIRE) {
            return CacheExpirationService.NEVER_EXPIRE;
        } else {
            return unit.convert(defaultTimeToLive, TimeUnit.NANOSECONDS);
        }
    }
}

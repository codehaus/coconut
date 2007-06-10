/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedCacheExpirationService<K, V> extends
        AbstractExpirationService<K, V> {

    private final InternalCacheAttributeService attributeFactory;

    private long defaultTTL;

    private Filter<CacheEntry<K, V>> expirationFilter;

    private final CacheHelper<K, V> helper;

    /**
     * @param cache
     * @param conf
     * @param clock
     * @param errorHandler
     */
    public UnsynchronizedCacheExpirationService(CacheHelper<K, V> helper,
            CacheExpirationConfiguration<K, V> conf, Clock clock,
            CacheExceptionHandlingConfiguration<K, V> exceptionConfiguration,
            InternalCacheAttributeService attributeFactory) {
        super(clock, helper, exceptionConfiguration);
        this.helper = helper;
        defaultTTL = conf.getDefaultTimeToLive(TimeUnit.NANOSECONDS);
        expirationFilter = conf.getExpirationFilter();
        this.attributeFactory = attributeFactory;
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#getDefaultTimeout(java.util.concurrent.TimeUnit)
     */
    public final long getDefaultTimeToLive(TimeUnit unit) {
        return ttlToUnit(defaultTTL, unit);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#getExpirationFilter()
     */
    Filter<CacheEntry<K, V>> getExpirationFilter() {
        return expirationFilter;
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.InternalCacheExpirationService#getExpirationTime(java.lang.Object,
     *      java.lang.Object, org.coconut.cache.service.loading.AttributeMap)
     */
    public long innerGetExpirationTime() {
        return defaultTTL;
        
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.InternalCacheExpirationService#isExpired(org.coconut.cache.CacheEntry)
     */
    public boolean innerIsExpired(CacheEntry<K, V> entry) {
        return isExpired(entry, expirationFilter);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#setDefaultTimeout(long,
     *      java.util.concurrent.TimeUnit)
     */
    public final void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
        defaultTTL = verifyTTL(timeToLive, unit);
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.AbstractCacheExpirationService#doPut(java.lang.Object,
     *      java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    @Override
    final V doPut(K key, V value, long timeToLiveNano) {
        AttributeMap map = attributeFactory.createMap();
        long ttlNano = timeToLiveNano != CacheExpirationService.DEFAULT_EXPIRATION ? timeToLiveNano
                : defaultTTL;
        map.put(CacheAttributes.TIME_TO_LIVE_NS, ttlNano);
        return helper.put(key, value, map);// checks for null key+value
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.AbstractCacheExpirationService#doPutAll(java.util.Map,
     *      long)
     */
    @Override
    void doPutAll(Map<? extends K, ? extends V> t, long timeToLiveNano) {
        HashMap<K, AttributeMap> attributes = new HashMap<K, AttributeMap>();
        long ttlNano = timeToLiveNano != CacheExpirationService.DEFAULT_EXPIRATION ? timeToLiveNano
                : defaultTTL;
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            K key = entry.getKey();
            AttributeMap map = attributeFactory.createMap();
            map.put(CacheAttributes.TIME_TO_LIVE_NS, ttlNano);
            attributes.put(key, map);
        }
        helper.putAll(t, attributes);
    }

}

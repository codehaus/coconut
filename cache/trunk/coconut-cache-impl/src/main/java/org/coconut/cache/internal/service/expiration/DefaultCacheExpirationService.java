/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheHelper;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheExpirationService<K, V> extends AbstractInternalCacheService
        implements CacheExpirationService<K, V> {

    /** Responsible for creating attribute maps. */
    private final InternalCacheAttributeService attributeFactory;

    /** The user specified expiration filter. */
    private final Filter<CacheEntry<K, V>> expirationFilter;

    private final CacheHelper<K, V> helper;

    private final Clock clock;

    public DefaultCacheExpirationService(Clock clock, CacheHelper<K, V> helper,
            CacheExpirationConfiguration<K, V> conf,
            InternalCacheAttributeService attributeFactory) {
        super(CacheExpirationConfiguration.SERVICE_NAME);
        expirationFilter = conf.getExpirationFilter();
        this.clock = clock;
        this.helper = helper;
        this.attributeFactory = attributeFactory;
        attributeFactory.update().setExpirationTimeNanos(
                ExpirationUtils.getInitialTimeToLive(conf));
    }

    @Override
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        serviceMap.put(CacheExpirationService.class, ExpirationUtils.wrapService(this));
    }

    public V put(K key, V value, long timeToLive, TimeUnit unit) {
        if (timeToLive == CacheExpirationService.DEFAULT_EXPIRATION) {
            return helper.put(key, value, AttributeMaps.EMPTY_MAP);
        } else {
            AttributeMap map = attributeFactory.createMap();
            CacheAttributes.setTimeToLive(map, timeToLive, unit);
            return helper.put(key, value, map);// checks for null key+value
        }
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#putAll(java.util.Map,
     *      long, java.util.concurrent.TimeUnit)
     */
    public void putAll(Map<? extends K, ? extends V> t, long timeToLive, TimeUnit unit) {
        if (timeToLive == CacheExpirationService.DEFAULT_EXPIRATION) {
            helper.putAll(t);
        } else {
            HashMap<K, AttributeMap> attributes = new HashMap<K, AttributeMap>();
            for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
                K key = entry.getKey();
                AttributeMap att = attributeFactory.createMap();
                CacheAttributes.setTimeToLive(att, timeToLive, unit);
                attributes.put(key, att);
            }
            helper.putAll(t, attributes);
        }
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#removeAll(java.util.Collection)
     */
    public int removeAll(Collection<? extends K> keys) {
        return helper.expireAll(keys);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#removeAll(org.coconut.filter.Filter)
     */
    public int removeAll(Filter<? extends CacheEntry<K, V>> filter) {
        return helper.expireAll(filter);
    }

    public boolean isExpired(CacheEntry<K, V> entry) {
        return ExpirationUtils.isExpired(entry, clock, expirationFilter);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#getDefaultTimeToLive(java.util.concurrent.TimeUnit)
     */
    public long getDefaultTimeToLive(TimeUnit unit) {
        return ExpirationUtils.convertNanosToExpirationTime(attributeFactory.update()
                .getExpirationTimeNanos(), unit);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#setDefaultTimeToLive(long,
     *      java.util.concurrent.TimeUnit)
     */
    public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
        attributeFactory.update().setExpirationTimeNanos(
                ExpirationUtils.convertExpirationTimeToNanos(timeToLive, unit));
    }

    @Override
    protected void registerMXBeans(ManagedGroup root) {
        ManagedGroup g = root.addChild(CacheExpirationConfiguration.SERVICE_NAME,
                "Cache Expiration attributes and operations");
        g.add(ExpirationUtils.wrapMXBean(this));
    }
}

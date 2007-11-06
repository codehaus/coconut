/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;
import org.coconut.predicate.Predicate;

/**
 * The default implementation of {@link CacheExpirationService}. This implementation can
 * be used in a multi-threaded environment if {@link Cache}, {@link InternalCacheSupport}
 * and {@link AbstractCacheEntryFactoryService} that is specified at construction time are
 * thread-safe.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class DefaultCacheExpirationService<K, V> extends AbstractCacheLifecycle implements
        CacheExpirationService<K, V>, ManagedObject, CompositeService {

    /** Responsible for creating attribute maps. */
    private final InternalCacheEntryService attributeFactory;

    private final Cache<K, V> cache;

    /** The clock used to get the current time. */
    private final Clock clock;

    /** The user specified expiration filter. */
    private final Predicate<CacheEntry<K, V>> expirationFilter;

    private final InternalCacheSupport<K, V> helper;

    public DefaultCacheExpirationService(Cache<K, V> cache, Clock clock,
            InternalCacheSupport<K, V> helper, CacheExpirationConfiguration<K, V> conf,
            InternalCacheEntryService attributeFactory) {
        super(CacheExpirationConfiguration.SERVICE_NAME);
        this.clock = clock;
        this.cache = cache;
        this.helper = helper;
        this.expirationFilter = conf.getExpirationFilter();
        this.attributeFactory = attributeFactory;
        attributeFactory.setExpirationTimeNanos(ExpirationUtils
                .getInitialTimeToLiveNS(conf));
    }

    /** {@inheritDoc} */
    public Collection<?> getChildServices() {
        return Arrays.asList(expirationFilter);
    }

    /** {@inheritDoc} */
    public long getDefaultTimeToLive(TimeUnit unit) {
        return ExpirationUtils.convertNanosToExpirationTime(attributeFactory
                .getExpirationTimeNanos(), unit);
    }

    /** {@inheritDoc} */
    public Predicate<CacheEntry<K, V>> getExpirationFilter() {
        return expirationFilter;
    }

    /** {@inheritDoc} */
    public boolean isExpired(CacheEntry<K, V> entry) {
        return ExpirationUtils.isExpired(entry, clock, expirationFilter);
    }

    /** {@inheritDoc} */
    public void manage(ManagedGroup parent) {
        ManagedGroup g = parent.addChild(CacheExpirationConfiguration.SERVICE_NAME,
                "Cache Expiration attributes and operations");
        g.add(ExpirationUtils.wrapAsMXBean(this));
    }

    /** {@inheritDoc} */
    public void purgeExpired() {
        helper.purgeExpired();
    }

    /** {@inheritDoc} */
    public V put(K key, V value, long timeToLive, TimeUnit unit) {
        if (timeToLive == CacheExpirationService.DEFAULT_EXPIRATION) {
            return cache.put(key, value);
        } else {
            AttributeMap map = attributeFactory.createMap();
            CacheAttributes.setTimeToLive(map, timeToLive, unit);
            return helper.put(key, value, map);// checks for null key+value
        }
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends K, ? extends V> t, long timeToLive, TimeUnit unit) {
        if (timeToLive == CacheExpirationService.DEFAULT_EXPIRATION) {
            cache.putAll(t);
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

    /** {@inheritDoc} */
    @Override
    public void registerServices(Map<Class<?>, Object> serviceMap) {
        serviceMap.put(CacheExpirationService.class, ExpirationUtils.wrapService(this));
    }

    /** {@inheritDoc} */
    public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
        long time = ExpirationUtils.convertExpirationTimeToNanos(timeToLive, unit);
        attributeFactory.setExpirationTimeNanos(
                time == 0 ? Long.MAX_VALUE : time);
    }
}

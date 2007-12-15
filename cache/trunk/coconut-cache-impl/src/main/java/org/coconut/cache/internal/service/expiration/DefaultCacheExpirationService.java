/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.core.Clock;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.coconut.operations.Ops.Predicate;

/**
 * The default implementation of {@link CacheExpirationService}. This implementation can
 * be used in a multi-threaded environment if {@link Cache}, {@link InternalCacheSupport}
 * and {@link AbstractCacheEntryFactoryService} that is specified at construction time are
 * thread-safe.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class DefaultCacheExpirationService<K, V> extends AbstractCacheLifecycle implements
        CacheExpirationService<K, V>, ManagedLifecycle, CompositeService {

    /** Responsible for creating attribute maps. */
    private final InternalCacheEntryService attributeFactory;

    private final Cache<K, V> cache;

    /** The clock used to get the current time. */
    private final Clock clock;

    /** The user specified expiration filter. */
    private final Predicate<CacheEntry<K, V>> expirationFilter;

    private final InternalCacheSupport<K, V> helper;

    public DefaultCacheExpirationService(Cache<K, V> cache, CacheConfiguration<K, V> conf,
            InternalCacheSupport<K, V> helper, CacheExpirationConfiguration<K, V> confExpiration,
            InternalCacheEntryService attributeFactory) {
        this.clock = conf.getClock();
        this.cache = cache;
        this.helper = helper;
        this.expirationFilter = confExpiration.getExpirationFilter();
        this.attributeFactory = attributeFactory;
        attributeFactory.setDefaultTimeToLiveNs(ExpirationUtils
                .getInitialTimeToLiveNS(confExpiration));
    }

    /** {@inheritDoc} */
    public Collection<?> getChildServices() {
        return Arrays.asList(expirationFilter);
    }

    /** {@inheritDoc} */
    public long getDefaultTimeToLive(TimeUnit unit) {
        return ExpirationUtils.convertNanosToExpirationTime(attributeFactory
                .getDefaultTimeToLiveTimeNs(), unit);
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
            TimeToLiveAttribute.set(map, timeToLive, unit);
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
                TimeToLiveAttribute.set(att, timeToLive, unit);
                attributes.put(key, att);
            }
            helper.putAll(t, attributes);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheExpirationService.class, ExpirationUtils.wrapService(this));
    }

    /** {@inheritDoc} */
    public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
        long time = ExpirationUtils.convertExpirationTimeToNanos(timeToLive, unit);
        attributeFactory.setDefaultTimeToLiveNs(time == 0 ? Long.MAX_VALUE : time);
    }

    public String toString() {
        return "Expiration Service";
    }
}

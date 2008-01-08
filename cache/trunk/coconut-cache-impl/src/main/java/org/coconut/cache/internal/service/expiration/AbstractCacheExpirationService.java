/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.internal.util.CollectionUtils;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;

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
public abstract class AbstractCacheExpirationService<K, V> extends AbstractCacheLifecycle implements
        CacheExpirationService<K, V>, ManagedLifecycle {

    /** Responsible for creating attribute maps. */
    private final InternalCacheEntryService entryService;

    /** The user specified expiration filter. */
    private final InternalCache<K, V> cache;

    AbstractCacheExpirationService(InternalCache<K, V> cache,
            CacheExpirationConfiguration<K, V> configuration, InternalCacheEntryService entryService) {
        this.cache = cache;
        this.entryService = entryService;
        entryService.setDefaultTimeToLiveNs(ExpirationUtils.getInitialTimeToLiveNS(configuration));
    }

    /** {@inheritDoc} */
    public long getDefaultTimeToLive(TimeUnit unit) {
        return ExpirationUtils.convertNanosToExpirationTime(entryService
                .getDefaultTimeToLiveTimeNs(), unit);
    }

    /** {@inheritDoc} */
    public void manage(ManagedGroup parent) {
        ManagedGroup g = parent.addChild(CacheExpirationConfiguration.SERVICE_NAME,
                "Cache Expiration attributes and operations");
        g.add(ExpirationUtils.wrapAsMXBean(this));
    }

    /** {@inheritDoc} */
    public V put(K key, V value, long timeToLive, TimeUnit unit) {
        CacheEntry<K, V> prev = cache.put(key, value, TimeToLiveAttribute.singleton(timeToLive, unit));
        return prev == null ? null : prev.getValue();
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends K, ? extends V> t, long timeToLive, TimeUnit unit) {
        AttributeMap am = TimeToLiveAttribute.singleton(timeToLive, unit);
        HashMap map = new HashMap();
        for (Map.Entry me : t.entrySet()) {
            map.put(me.getKey(), new CollectionUtils.SimpleImmutableEntry(me.getValue(), am));
        }
        cache.putAllWithAttributes(map);
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheExpirationService.class, ExpirationUtils.wrapService(this));
    }

    /** {@inheritDoc} */
    public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
        long time = ExpirationUtils.convertExpirationTimeToNanos(timeToLive, unit);
        entryService.setDefaultTimeToLiveNs(time == 0 ? Long.MAX_VALUE : time);
    }

    @Override
    public String toString() {
        return "Expiration Service";
    }
}

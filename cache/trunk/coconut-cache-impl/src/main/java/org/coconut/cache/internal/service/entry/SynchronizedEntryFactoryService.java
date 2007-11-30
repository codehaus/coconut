/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.policy.IsCacheable;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;

public class SynchronizedEntryFactoryService<K, V> extends AbstractCacheEntryFactoryService<K, V> {

    private long defaultExpirationTime;

    private long defaultRefreshTime;

    private final Object mutex;

    private final IsCacheable<K, V> isCacheable;

    public SynchronizedEntryFactoryService(CacheConfiguration<?, ?> conf,
            CacheEvictionConfiguration<K, V> evictionConfiguration,
            InternalCacheExceptionService<K, V> exceptionHandler, Cache<K, V> mutex) {
        super(conf.getClock(), exceptionHandler);
        this.isCacheable = evictionConfiguration.getIsCacheableFilter();
        this.mutex = mutex;
    }

    /** {@inheritDoc} */
    public AbstractCacheEntry<K, V> createEntry(K key, V value, AttributeMap attributes,
            AbstractCacheEntry<K, V> existing) {
        if (attributes == null) {
            attributes = createMap();
        }
        if (isCacheable != null && !isCacheable.isCacheable(key, value, attributes)) {
            return null;
        }
        long expirationTime = getTimeToLive(defaultExpirationTime, key, value, attributes, existing);
        double cost = getCost(key, value, attributes, existing);
        long size = getSize(key, value, attributes, existing);
        long creationTime = getCreationTime(key, value, attributes, existing);
        long lastUpdate = getLastModified(key, value, attributes, existing);
        long hits = getHits(key, value, attributes, existing);
        long refreshTime = getTimeToRefresh(defaultRefreshTime, key, value, attributes, existing);
        SynchronizedCacheEntry<K, V> newEntry = new SynchronizedCacheEntry<K, V>(key, value, cost,
                creationTime, lastUpdate, size, refreshTime, expirationTime, hits);

        if (existing != null) {
            newEntry.setPolicyIndex(existing.getPolicyIndex());
        }
        return newEntry;
    }

    /** {@inheritDoc} */
    public synchronized long getDefaultTimeToLiveTimeNs() {
        synchronized (mutex) {
            return defaultExpirationTime;
        }
    }

    /** {@inheritDoc} */
    public synchronized long getTimeToRefreshNs() {
        synchronized (mutex) {
            return defaultRefreshTime;
        }
    }

    /** {@inheritDoc} */
    public synchronized void setDefaultTimeToLiveNs(long nanos) {
        synchronized (mutex) {
            this.defaultExpirationTime = nanos;
        }
    }

    /** {@inheritDoc} */
    public void setTimeToRefreshNs(long nanos) {
        synchronized (mutex) {
            this.defaultRefreshTime = nanos;
        }
    }
}

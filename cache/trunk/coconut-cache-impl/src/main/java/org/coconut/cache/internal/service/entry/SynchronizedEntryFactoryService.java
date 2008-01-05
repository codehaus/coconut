/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.attribute.DefaultAttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;

public class SynchronizedEntryFactoryService<K, V> extends AbstractCacheEntryFactoryService<K, V> {

    private long defaultExpirationTime = Long.MAX_VALUE;

    private long defaultRefreshTime = Long.MAX_VALUE;

    private final Object mutex;

    private boolean isDisabled;

    public SynchronizedEntryFactoryService(CacheConfiguration<?, ?> conf,
            CacheEvictionConfiguration<K, V> evictionConfiguration,
            InternalCacheExceptionService<K, V> exceptionHandler, Cache<K, V> mutex) {
        super(conf.getClock(),evictionConfiguration, exceptionHandler);
        this.isDisabled = evictionConfiguration.isDisabled();
        this.mutex = mutex;
    }

    /** {@inheritDoc} */
    @Override
    public AbstractCacheEntry<K, V> createEntry(K key, V value, AttributeMap attributes,
            InternalCacheEntry<K, V> existing) {
        if (attributes == null) {
            attributes = createMap();
        }
        long expirationTime = getTimeToLive(defaultExpirationTime, key, value, attributes, existing);
        double cost = getCost(key, value, attributes, existing);
        long size = getSize(key, value, attributes, existing);
        long creationTime = getCreationTime(key, value, attributes, existing);
        long lastUpdate = getLastModified(key, value, attributes, existing);
        long hits = getHits(key, value, attributes, existing);
        long refreshTime = getTimeToRefresh(defaultRefreshTime, key, value, attributes, existing);
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        if (attributes.size() > 0) {
            for (Attribute a : attributes.keySet()) {
                if (!isCacheAttribute(a)) {
                    if (am == Attributes.EMPTY_ATTRIBUTE_MAP) {
                        am = new DefaultAttributeMap();
                    }
                    am.put(a, attributes.get(a));
                }
            }
        }
        SynchronizedCacheEntry<K, V> newEntry = new SynchronizedCacheEntry<K, V>(key, value, cost,
                creationTime, lastUpdate, size, refreshTime, expirationTime, hits, am);
        if (!isCacheable(newEntry)) {
            newEntry.setPolicyIndex(Integer.MIN_VALUE);
            return newEntry;
        }
        if (existing != null) {
            newEntry.setPolicyIndex(((AbstractCacheEntry) existing).getPolicyIndex());
        }
        if (isDisabled) {
            newEntry.setPolicyIndex(Integer.MIN_VALUE);
        }
        return newEntry;
    }

    /** {@inheritDoc} */
    public long getDefaultTimeToLiveTimeNs() {
        synchronized (mutex) {
            return defaultExpirationTime;
        }
    }

    /** {@inheritDoc} */
    public long getTimeToRefreshNs() {
        synchronized (mutex) {
            return defaultRefreshTime;
        }
    }

    /** {@inheritDoc} */
    public void setDefaultTimeToLiveNs(long nanos) {
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

    public boolean isDisabled() {
        synchronized (mutex) {
            return isDisabled;
        }
    }

    public void setDisabled(boolean isDisabled) {
        synchronized (mutex) {
            this.isDisabled = isDisabled;
        }
    }
}

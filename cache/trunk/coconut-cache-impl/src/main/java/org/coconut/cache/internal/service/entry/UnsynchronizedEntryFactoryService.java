/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.policy.IsCacheable;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.core.Clock;

/**
 * This class creates unsynchronized instances of {@link AbstractCacheEntry}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class UnsynchronizedEntryFactoryService<K, V> extends AbstractCacheEntryFactoryService<K, V> {

    /** The default refresh time of newly added entries. */
    private long defaultRefreshTimeNanos = Long.MAX_VALUE;

    /** The default time to live of newly added entries. */
    private long defaultTimeToLiveNanos = Long.MAX_VALUE;

    private final IsCacheable<K, V> isCacheable;

    private boolean isDisabled;

    /**
     * Creates a new UnsynchronizedEntryFactoryService.
     * 
     * @param clock
     *            the clock used for calculating expiration and refresh times
     * @param exceptionService
     *            the cache exception service
     */
    public UnsynchronizedEntryFactoryService(Clock clock,
            CacheEvictionConfiguration<K, V> evictionConfiguration,
            InternalCacheExceptionService<K, V> exceptionService) {
        super(clock, exceptionService);
        this.isDisabled = evictionConfiguration.isDisabled();
        this.isCacheable = evictionConfiguration.getIsCacheableFilter();
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
        long expirationTime = getTimeToLive(defaultTimeToLiveNanos, key, value, attributes,
                existing);
        double cost = getCost(key, value, attributes, existing);
        long size = getSize(key, value, attributes, existing);
        long creationTime = getCreationTime(key, value, attributes, existing);
        long lastUpdate = getLastModified(key, value, attributes, existing);
        long hits = getHits(key, value, attributes, existing);
        long refreshTime = getTimeToRefresh(defaultRefreshTimeNanos, key, value, attributes,
                existing);
        UnsynchronizedCacheEntry<K, V> newEntry = new UnsynchronizedCacheEntry<K, V>(key, value,
                cost, creationTime, lastUpdate, size, refreshTime);
        newEntry.setHits(hits);
        newEntry.setExpirationTime(expirationTime);

        if (existing != null) {
            newEntry.setPolicyIndex(existing.getPolicyIndex());
        }
        if (isDisabled) {
            newEntry.setPolicyIndex(Integer.MIN_VALUE);
        }
        return newEntry;
    }

    /** {@inheritDoc} */
    public long getDefaultTimeToLiveTimeNs() {
        return defaultTimeToLiveNanos;
    }

    /** {@inheritDoc} */
    public long getTimeToRefreshNs() {
        return defaultRefreshTimeNanos;
    }

    /** {@inheritDoc} */
    public void setDefaultTimeToLiveNs(long nanos) {
        this.defaultTimeToLiveNanos = nanos;
    }

    /** {@inheritDoc} */
    public void setTimeToRefreshNs(long nanos) {
        this.defaultRefreshTimeNanos = nanos;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
}

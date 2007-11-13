/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnsynchronizedEntryFactoryService<K, V> extends
        AbstractCacheEntryFactoryService<K, V> {

    private long defaultExpirationTime;

    private long defaultRefreshTime;

    public UnsynchronizedEntryFactoryService(CacheConfiguration<?, ?> conf,
            InternalCacheExceptionService<K, V> exceptionHandler) {
        super(conf.getClock(), exceptionHandler);
    }

    /**
     * @see org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService#createEntry(java.lang.Object,
     *      java.lang.Object, org.coconut.core.AttributeMap,
     *      org.coconut.cache.internal.service.entry.AbstractCacheEntry)
     */
    public AbstractCacheEntry<K, V> createEntry(K key, V value, AttributeMap attributes,
            AbstractCacheEntry<K, V> existing) {
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
        UnsynchronizedCacheEntry<K, V> newEntry = new UnsynchronizedCacheEntry<K, V>(
                this, key, value, cost, creationTime, lastUpdate, size, refreshTime);
        newEntry.setHits(hits);
        newEntry.setExpirationTime(expirationTime);

        if (existing != null) {
            newEntry.setPolicyIndex(existing.getPolicyIndex());
        }
        return newEntry;
    }

    /** {@inheritDoc} */
    public long getExpirationTimeNanos() {
        return defaultExpirationTime;
    }

    /** {@inheritDoc} */
    public long getTimeToRefreshNanos() {
        return defaultRefreshTime;
    }
    
    /** {@inheritDoc} */
    public void setExpirationTimeNanos(long nanos) {
        this.defaultExpirationTime = nanos;
    }
    /** {@inheritDoc} */
    public void setTimeToFreshNanos(long nanos) {
        this.defaultRefreshTime = nanos;
    }
}

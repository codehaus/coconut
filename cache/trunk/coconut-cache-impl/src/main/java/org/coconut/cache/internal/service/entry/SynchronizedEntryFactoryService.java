package org.coconut.cache.internal.service.entry;

import org.coconut.cache.Cache;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;

public class SynchronizedEntryFactoryService<K, V> extends
        AbstractCacheEntryFactoryService<K, V> {

    private long defaultExpirationTime;

    private long defaultRefreshTime;

    private final Object mutex;

    public SynchronizedEntryFactoryService(Clock clock,
            CacheExceptionService<K, V> exceptionHandler, Cache<K, V> mutex) {
        super(clock, exceptionHandler);
        this.mutex = mutex;
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
        long expirationTime = getTimeToLive(defaultExpirationTime, key, value,
                attributes, existing);
        double cost = getCost(key, value, attributes, existing);
        long size = getSize(key, value, attributes, existing);
        long creationTime = getCreationTime(key, value, attributes, existing);
        long lastUpdate = getLastModified(key, value, attributes, existing);
        long hits = getHits(key, value, attributes, existing);
        long refreshTime = getTimeToRefresh(defaultRefreshTime, key, value, attributes,
                existing);
        ConcurrentCacheEntry<K, V> newEntry = new ConcurrentCacheEntry<K, V>(this, key,
                value, cost, creationTime, lastUpdate, size, refreshTime, expirationTime,
                hits);

        if (existing != null) {
            newEntry.setPolicyIndex(existing.getPolicyIndex());
        }
        return newEntry;
    }

    /** {@inheritDoc} */
    public synchronized long getExpirationTimeNanos() {
        synchronized (mutex) {
            return defaultExpirationTime;
        }
    }

    /** {@inheritDoc} */
    public synchronized long getTimeToRefreshNanos() {
        synchronized (mutex) {
            return defaultRefreshTime;
        }
    }

    /** {@inheritDoc} */
    public synchronized void setExpirationTimeNanos(long nanos) {
        synchronized (mutex) {
            this.defaultExpirationTime = nanos;
        }
    }

    /** {@inheritDoc} */
    public void setTimeToFreshNanos(long nanos) {
        synchronized (mutex) {
            this.defaultRefreshTime = nanos;
        }
    }
}
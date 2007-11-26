/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnsynchronizedCacheEntry<K, V> extends AbstractCacheEntry<K, V> {

    long expirationTime;

    long hits;

    long lastAccessTime;

    long refreshTime;

    /**
     * @param key
     * @param value
     * @param cost
     * @param creationTime
     * @param lastUpdateTime
     * @param size
     */
    public UnsynchronizedCacheEntry(K key, V value, double cost, long creationTime,
            long lastUpdateTime, long size, long refreshTime) {
        super(key, value, cost, creationTime, lastUpdateTime, size);
        this.refreshTime = refreshTime;
// this.expirationTime = expirationTime;
// this.hits = hits;
// this.lastAccessTime = lastAccessTime;
    }

    /** {@inheritDoc} */
    public void accessed(InternalCacheEntryService<K, V> service) {
        lastAccessTime = service.getAccessTimeStamp(this);
        hits++;
    }

    /** {@inheritDoc} */
    public long getExpirationTime() {
        return expirationTime;
    }

    /** {@inheritDoc} */
    public long getHits() {
        return hits;
    }

    /** {@inheritDoc} */
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    /** {@inheritDoc} */
    @Override
    public long getRefreshTime() {
        return refreshTime;
    }

    /** {@inheritDoc} */
    @Override
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /** {@inheritDoc} */
    @Override
    public void setHits(long hits) {
        this.hits = hits;
    }
}

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

    private final AbstractCacheEntryFactoryService<K, V> service;

    /**
     * @param key
     * @param value
     * @param cost
     * @param creationTime
     * @param lastUpdateTime
     * @param size
     */
    public UnsynchronizedCacheEntry(AbstractCacheEntryFactoryService<K, V> service,
            K key, V value, double cost, long creationTime, long lastUpdateTime,
            long size, long refreshTime) {
        super(key, value, cost, creationTime, lastUpdateTime, size);
        this.service = service;
        this.refreshTime = refreshTime;
// this.expirationTime = expirationTime;
// this.hits = hits;
// this.lastAccessTime = lastAccessTime;
    }

    public void accessed() {
        lastAccessTime = service.getAccessTimeStamp(this);
        hits++;
    }

    /**
     * @see org.coconut.cache.CacheEntry#getExpirationTime()
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * @see org.coconut.cache.CacheEntry#getHits()
     */
    public long getHits() {
        return hits;
    }

    /**
     * @see org.coconut.cache.CacheEntry#getLastAccessTime()
     */
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * @param expirationTime
     *            the expirationTime to set
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * @param hits
     *            the hits to set
     */
    public void setHits(long hits) {
        this.hits = hits;
    }

    /**
     * @param lastAccessTime
     *            the lastAccessTime to set
     */
    void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public long getRefreshTime() {
        return refreshTime;
    }

}

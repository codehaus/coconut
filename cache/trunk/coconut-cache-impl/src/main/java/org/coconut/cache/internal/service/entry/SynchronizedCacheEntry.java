/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SynchronizedCacheEntry<K, V> extends AbstractCacheEntry<K, V> {

    private final AbstractCacheEntryFactoryService<K, V> service;

    private volatile long expirationTime;

    private volatile long hits;

    private volatile long lastAccessedTime;

    private volatile long refreshTime;

    /**
     * @param key
     * @param value
     * @param cost
     * @param creationTime
     * @param lastUpdateTime
     * @param size
     */
    public SynchronizedCacheEntry(AbstractCacheEntryFactoryService<K, V> service, K key,
            V value, double cost, long creationTime, long lastUpdateTime, long size,
            long refreshTime, long expirationTime, long hits) {
        super(key, value, cost, creationTime, lastUpdateTime, size);
        this.service = service;
        this.refreshTime = refreshTime;
        this.expirationTime = expirationTime;
        this.hits = hits;
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
        return lastAccessedTime;
    }

    public void accessed() {
        lastAccessedTime = service.getAccessTimeStamp(this);
        hits++;
    }

    @Override
    void setExpirationTime(long time) {
        this.expirationTime = time;
    }

    @Override
    void setHits(long hits) {
        this.hits = hits;
    }

    void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    @Override
    public long getRefreshTime() {
        return refreshTime;
    }
}

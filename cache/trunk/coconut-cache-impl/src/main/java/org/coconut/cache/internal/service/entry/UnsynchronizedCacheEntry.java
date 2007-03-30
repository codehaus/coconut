/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedCacheEntry<K, V> extends AbstractCacheEntry<K, V> {

    /**
     * @param key
     * @param value
     * @param cost
     * @param creationTime
     * @param lastUpdateTime
     * @param size
     * @param version
     */
    public UnsynchronizedCacheEntry(AbstractCacheEntryFactoryService<K, V> service, K key,
            V value, double cost, long creationTime, long lastUpdateTime, long size,
            long version) {
        super(key, value, cost, creationTime, lastUpdateTime, size, version);
        this.service = service;
//        this.expirationTime = expirationTime;
//        this.hits = hits;
//        this.lastAccessTime = lastAccessTime;
    }

    private final AbstractCacheEntryFactoryService<K, V> service;

    long expirationTime;

    long hits;

    long lastAccessTime;

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

    public void accessed() {
        lastAccessTime = service.getAccessTimeStamp(this);
        hits++;
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

}

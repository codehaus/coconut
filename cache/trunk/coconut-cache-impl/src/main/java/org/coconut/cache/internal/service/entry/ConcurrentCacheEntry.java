/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ConcurrentCacheEntry<K, V> extends AbstractCacheEntry<K, V> {

    private final AbstractCacheEntryFactoryService<K, V> service;

    private volatile long expirationTime;

    private volatile long hits;

    private volatile long lastAccessedTime;

    /**
     * @param key
     * @param value
     * @param cost
     * @param creationTime
     * @param lastUpdateTime
     * @param size
     */
    public ConcurrentCacheEntry(AbstractCacheEntryFactoryService<K, V> service, K key,
            V value, double cost, long creationTime, long lastUpdateTime, long size) {
        super(key, value, cost, creationTime, lastUpdateTime, size);
        this.service = service;

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
    }

    @Override
    public void setExpirationTime(long time) {
        this.expirationTime = time;
    }

    @Override
    public void setHits(long hits) {
        this.hits = hits;
    }

    @Override
    public long getRefreshTime() {
        throw new UnsupportedOperationException();
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SynchronizedCacheEntry<K, V> extends AbstractCacheEntry<K, V> {

    private final long expirationTime;

    private volatile long hits;

    private volatile long lastAccessedTime;

    private final long refreshTime;

    /**
     * @param key
     * @param value
     * @param cost
     * @param creationTime
     * @param lastUpdateTime
     * @param size
     */
    public SynchronizedCacheEntry(K key, V value, double cost, long creationTime,
            long lastUpdateTime, long size, long refreshTime, long expirationTime, long hits,
            AttributeMap attributes) {
        super(key, value, cost, creationTime, lastUpdateTime, size, attributes);
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

    @Override
    public long getRefreshTime() {
        return refreshTime;
    }

    @Override
    public void setHits(long hits) {
        this.hits = hits;
    }

    @Override
    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessedTime = lastAccessTime;
    }

    public CacheEntry<K, V> safe() {
        return this;
    }
}

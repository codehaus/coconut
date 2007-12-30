/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.attribute.AttributeMap;

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
            long lastUpdateTime, long size, long refreshTime, AttributeMap attributes) {
        super(key, value, cost, creationTime, lastUpdateTime, size, attributes);
        this.refreshTime = refreshTime;
// this.expirationTime = expirationTime;
// this.hits = hits;
// this.lastAccessTime = lastAccessTime;
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

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public void setHits(long hits) {
        this.hits = hits;
    }

    @Override
    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
}

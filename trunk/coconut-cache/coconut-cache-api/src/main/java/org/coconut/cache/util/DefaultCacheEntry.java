/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.util;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.policy.CostSizeObject;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultCacheEntry<K, V> implements CacheEntry<K, V> {

    private final V value;

    private final K key;

    public DefaultCacheEntry(K key, V value) {
        this.value = value;
        this.key = key;
    }

    // /**
    // * {@inheritDoc}
    // */
    // public Map<String, ?> getMetaMap() {
    // return null; // no extended attributes
    // }

    // /**
    // * {@inheritDoc}
    // */
    // public long getTimeToLive(TimeUnit unit) {
    // return Cache.DEFAULT_EXPIRATION;
    // }

    /**
     * {@inheritDoc}
     */
    public double getCost() {
        return CostSizeObject.DEFAULT_COST;
    }

    /**
     * {@inheritDoc}
     */
    public long getSize() {
        return CostSizeObject.DEFAULT_SIZE;
    }

    /**
     * {@inheritDoc}
     */
    public V getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public long getCreationTime() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getExpirationTime() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getHits() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getLastAccessTime() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getLastUpdateTime() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getVersion() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public K getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
}

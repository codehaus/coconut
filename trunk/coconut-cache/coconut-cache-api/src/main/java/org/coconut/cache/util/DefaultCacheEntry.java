/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import java.util.Collections;
import java.util.Map;

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
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        this.value = value;
        this.key = key;
    }

    public static <K, V> CacheEntry<K, V> entryWithExpiration(K key, V value,
            long expiration) {
        return new WithExpiration<K, V>(key, value, expiration);
    }

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

    /**
     * @see org.coconut.cache.CacheEntry#getAttributes()
     */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(Collections.EMPTY_MAP);
    }

    static class WithExpiration<K, V> extends DefaultCacheEntry<K, V> {

        private final long expirationTime;

        /**
         * @param key
         * @param value
         */
        public WithExpiration(K key, V value, long expiration) {
            super(key, value);
            if (expiration < 0) {
                throw new IllegalArgumentException(
                        "expiration must be a positive number, was " + expiration);
            }
            this.expirationTime = expiration;
        }

        /**
         * @see org.coconut.cache.util.DefaultCacheEntry#getExpirationTime()
         */
        @Override
        public long getExpirationTime() {
            return expirationTime;
        }

    }
}

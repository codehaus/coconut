/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultCacheEntry<K, V> implements CacheEntry<K, V> {

    public static final long DEFAULT_CREATION_TIME = 0;

    public static final long DEFAULT_EXPIRATION_TIME = Cache.DEFAULT_EXPIRATION;

    public static final long DEFAULT_HIT_COUNT = -1;

    public static final long DEFAULT_LAST_ACCESS_TIME = 0;

    public static final long DEFAULT_LAST_UPDATE_TIME = 0;

    public static final long DEFAULT_VERSION = 0;

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

    /**
     * {@inheritDoc}
     */
    public double getCost() {
        return DEFAULT_COST;
    }

    /**
     * {@inheritDoc}
     */
    public long getSize() {
        return DEFAULT_SIZE;
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
        return DEFAULT_CREATION_TIME;
    }

    /**
     * {@inheritDoc}
     */
    public long getExpirationTime() {
        return DEFAULT_EXPIRATION_TIME;
    }

    /**
     * {@inheritDoc}
     */
    public long getHits() {
        return DEFAULT_HIT_COUNT;
    }

    /**
     * {@inheritDoc}
     */
    public long getLastAccessTime() {
        return DEFAULT_LAST_ACCESS_TIME;
    }

    /**
     * {@inheritDoc}
     */
    public long getLastUpdateTime() {
        return DEFAULT_LAST_UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    public long getVersion() {
        return DEFAULT_VERSION;
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

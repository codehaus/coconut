/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.spi;

import java.util.concurrent.Semaphore;

import org.coconut.cache.CacheEntry;


public class MutableCacheEntry<K, V> implements CacheEntry<K, V> {

    private final Semaphore loadLock = new Semaphore(1);

    private final K key;

    private volatile V value;

    private final long creationTime;

    private long expirationTime;

    /**
     * Creates a new MutableCacheEntry
     * 
     * @param key
     * @param creationTime
     */
    public MutableCacheEntry(K key, long creationTime) {
        this.key = key;
        this.creationTime = creationTime;
    }

    public MutableCacheEntry(K key, V value, long creationTime) {
        this.key = key;
        this.value = value;
        this.creationTime = creationTime;
    }

    public boolean tryPrepareLoad(boolean doNotServerPrevious) {
        if (loadLock.tryAcquire()) {
            if (doNotServerPrevious) {
                this.value = null;
            }
            return true;
        } else {
            return false;
        }
    }

    public void loadFinished(V value) {
        this.value = value;
        loadLock.release();
    }

    public int hashCode() {
        V value = peek();
        return (key == null ? 0 : key.hashCode())
                ^ (value == null ? 0 : value.hashCode());
    }

    public K getKey() {
        return key;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public long getHits() {
        return 0;
    }

    public long getLastAccessTime() {
        return 0;
    }

    public long getLastUpdateTime() {
        return 0;
    }

    public long getTTL() {
        return 0;
    }

    public long getVersion() {
        return 0;
    }

    public boolean isValid() {
        return value != null;
    }

    public V peek() {
        return value;
    }

    public V getValue() {
        if (value == null) {
            if (loadLock.availablePermits() == 0) {
                try {
                    // TODO: crappy code!!
                    loadLock.acquireUninterruptibly(); // <--wait on loading
                    return value;
                } finally {
                    loadLock.release();
                }
            } else {
                return null; //loader returned null
            }
        } else {
            return value;
        }
    }

    /**
     * @see java.util.Map$Entry#setValue(V)
     */
    public V setValue(final V newValue) {
        if (newValue == null) {
            throw new NullPointerException("newValue is null");
        }
        V v = value;
        value = newValue;
        return v;
    }

    /**
     * @see org.coconut.cache.CacheEntry#getSize()
     */
    public long getSize() {
        return 1;
    }

    /**
     * @see org.coconut.cache.CacheEntry#getCost()
     */
    public double getCost() {
        return 1;
    }

    /**
     * @see org.coconut.cache.CacheEntry#getMisses()
     */
    public long getMisses() {
        // TODO Auto-generated method stub
        return 0;
    }
}

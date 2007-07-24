/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.core.AttributeMap;

/**
 * A basis implementation of the {@link CacheEntry} interface.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCacheEntry<K, V> implements CacheEntry<K, V> {

    public static final long DEFAULT_HIT_COUNT = -1;

    public static final long DEFAULT_LAST_ACCESS_TIME = 0;

    AbstractCacheEntry<K, V> next;

    private final double cost;

    private final long creationTime;

    private final int hash;

    private final K key;

    private final long lastUpdateTime;

    /** the index in cache policy, is -1 if not used or initialized. */
    private int policyIndex = -1;

    private final long size;

    private final V value;

    /**
     * @param key
     * @param value
     */
    AbstractCacheEntry(K key, V value, double cost, long creationTime,
            long lastUpdateTime, long size) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        this.hash = EntryMap.hash(key.hashCode());
        this.key = key;
        this.value = value;
        this.cost = cost;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.size = size;
    }

    public abstract void accessed();

    public void entryRemoved() {

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry e = (Map.Entry) o;
        Object k1 = getKey();
        Object k2 = e.getKey();
        // we keep null checks, might later want to use Map.Entry instead of
        // Entry to compare with
        if (k1 == k2 || (k1 != null && k1.equals(k2))) {
            Object v1 = value;
            Object v2 = e.getValue();
            if (v1 == v2 || (v1 != null && v1.equals(v2)))
                return true;
        }
        return false;
    }

    public AttributeMap getAttributes() {
        return null;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @return the creationTime
     */
    public long getCreationTime() {
        return creationTime;
    }

    public int getHash() {
        return hash;
    }

    public K getKey() {
        return key;
    }

    /**
     * @return the lastUpdateTime
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AbstractCacheEntry<K, V> getNext() {
        return next;
    }

    public int getPolicyIndex() {
        return policyIndex;
    }

    public abstract long getRefreshTime();

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }

    public void incrementHits() {
        setHits(getHits() + 1);
    }

    public abstract void setExpirationTime(long time);

    public abstract void setHits(long hits);

    public void setNext(AbstractCacheEntry<K, V> entry) {
        next = entry;
    }

    public void setPolicyIndex(int index) {
        this.policyIndex = index;
    }

    public V setValue(V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue() + " (policyIndex= " + getPolicyIndex() + ")";
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.attribute.DefaultAttributeMap;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.attribute.common.TimeToRefreshAttribute;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.memorystore.ChainingEntry;
import org.coconut.core.Clock;
import org.coconut.operations.Ops.Predicate;

/**
 * A basis implementation of the {@link CacheEntry} interface.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCacheEntry<K, V> implements ChainingEntry<K, V> {
    private final AttributeMap attributes;

    /** The cost of this cache entry. */
    private final double cost;

    /** The creation time of this cache entry. */
    private final long creationTime;

    /** The hash of the key. */
    private final int hash;

    /** The key of this cache entry. */
    private final K key;

    /** The time this entry was last updated. */
    private final long lastUpdateTime;

    /** the index in cache policy, is -1 if not used or initialized. */
    private int policyIndex = -1;

    /** The size of the cache entry. */
    private final long size;

    /** The value of the cache entry. */
    private final V value;

    /** The next cache entry in the hash map. */
    public AbstractCacheEntry<K, V> next;

    boolean isExpired;

    public AbstractCacheEntry<K, V> next() {
        return next;
    }
    public void setNext(ChainingEntry entry) {
        this.next = (AbstractCacheEntry<K, V>) entry;
    }

    /**
     * Creates a new AbstractCacheEntry.
     *
     * @param key
     *            the key of the cache entry
     * @param value
     *            the value of the cache entry
     * @param cost
     *            the cost of the cache entry
     * @param creationTime
     *            the creation time of the cache entry
     * @param lastUpdateTime
     *            the last update time of this cache entry
     * @param size
     *            the size of the cache entry
     */
    AbstractCacheEntry(K key, V value, double cost, long creationTime, long lastUpdateTime,
            long size, AttributeMap attributes) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        this.hash = hash(key.hashCode());
        this.key = key;
        this.value = value;
        this.cost = cost;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.size = size;
        this.attributes = attributes;
    }
    static int hash(int h) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= h >>> 20 ^ h >>> 12;
        return h ^ h >>> 7 ^ h >>> 4;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        Map.Entry e = (Map.Entry) o;
        Object k1 = getKey();
        Object k2 = e.getKey();
        // we keep null checks, might later want to use Map.Entry instead of
        // Entry to compare with
        if (k1 == k2 || k1 != null && k1.equals(k2)) {
            // new Exception().printStackTrace();
            Object v1 = value;
            Object v2 = e.getValue();
            if (v1 == v2 || v1.equals(v2)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public AttributeMap getAttributes() {
        AttributeMap map = new DefaultAttributeMap(attributes);
        CostAttribute.setCost(map, getCost());
        SizeAttribute.set(map, getSize());
        return Attributes.unmodifiableAttributeMap(map);
    }

    /** {@inheritDoc} */
    public double getCost() {
        return cost;
    }

    /** {@inheritDoc} */
    public long getCreationTime() {
        return creationTime;
    }

    /** {@inheritDoc} */
    public K getKey() {
        return key;
    }

    public boolean isExpired() {
        return isExpired;
    }

    /**
     * @return the lastUpdateTime
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public int getPolicyIndex() {
        return policyIndex;
    }

    /** {@inheritDoc} */
    public long getSize() {
        return size;
    }

    /** {@inheritDoc} */
    public V getValue() {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }

    public boolean isDead() {
        return getPolicyIndex() > 0;
    }

    boolean isExpired(Predicate<CacheEntry<K, V>> filter, long timestamp) {
        if (filter != null && filter.evaluate(this)) {
            return true;
        }
        long expTime = getExpirationTime();
        return expTime == TimeToLiveAttribute.FOREVER ? false : Clock.isPassed(timestamp, expTime);
    }

    boolean needsRefresh(Predicate<CacheEntry<K, V>> filter, long timestamp) {
        if (filter != null && filter.evaluate(this)) {
            return true;
        }
        long refreshTime = getRefreshTime();
        return refreshTime == TimeToRefreshAttribute.FOREVER ? false : Clock.isPassed(timestamp,
                refreshTime);
    }

    abstract void setHits(long hits);

    abstract void setLastAccessTime(long lastAccessTime);

    public V setValue(V v) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }

    public int getHash() {
        return hash;
    }

    abstract long getRefreshTime();

    void setPolicyIndex(int index) {
        this.policyIndex = index;
    }
}

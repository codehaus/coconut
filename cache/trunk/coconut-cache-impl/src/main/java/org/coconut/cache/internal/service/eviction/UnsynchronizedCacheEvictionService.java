/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.ArrayList;
import java.util.List;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.InternalCacheSupport;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class UnsynchronizedCacheEvictionService<K, V, T extends CacheEntry<K, V>> extends
        AbstractEvictionService<K, V, T> {
    private final ReplacementPolicy<T> cp;

    private int maxSize;

    private int preferableSize;

    private long maxCapacity;

    private long preferableCapacity;

    // @SuppressWarnings("unchecked")
    public UnsynchronizedCacheEvictionService(CacheEvictionConfiguration<K, V> conf,
            InternalCacheSupport<K, V> helper) {
        super(helper);
        cp = conf.getPolicy() == null ? Policies.newLRU() : (ReplacementPolicy) conf
                .getPolicy();
        maxSize = EvictionUtils.getMaximumSizeFromConfiguration(conf);
        maxCapacity = EvictionUtils.getMaximumVolumeFromConfiguration(conf);
        preferableCapacity = EvictionUtils.getPreferableVolumeFromConfiguration(conf);
        preferableSize = EvictionUtils.getPreferableSizeFromConfiguration(conf);
    }

    /** {@inheritDoc} */
    public T evictNext() {
        return cp.evictNext();
    }

    /** {@inheritDoc} */
    public void remove(int index) {
        if (cp != null) {
            cp.remove(index);
        }
    }

    /** {@inheritDoc} */
    public void touch(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "index must be a non negative number, was " + index);
        }
        if (cp != null) {
            cp.touch(index);
        }
    }

    /** {@inheritDoc} */
    public List<T> evict(int count) {
        List<T> list = new ArrayList<T>();
        while (count-- > 0) {
            T e = evictNext();
            list.add(e);
        }
        return list;
    }

    /** {@inheritDoc} */
    public List<T> evict(int size, long capacity) {
        ArrayList<T> list = new ArrayList<T>();
        int diffSize = size - maxSize;
        long diffCapacity = capacity - maxCapacity;
        while (diffSize-- > 0 || diffCapacity > 0) {
            T e = evictNext();
            list.add(e);
            diffCapacity -= e.getSize();
        }
        return list;
    }

    /** {@inheritDoc} */
    public boolean isCapacityBreached(long capacity) {
        return capacity > maxCapacity;
    }

    /** {@inheritDoc} */
    public boolean isSizeBreached(int size) {
        return size > maxSize;
    }

    /** {@inheritDoc} */
    public int add(T t) {
        if (maxCapacity == 0) {
            return -1;
        }
        return cp.add(t);
    }

    /** {@inheritDoc} */
    public int getMaximumSize() {
        return maxSize;
    }

    /** {@inheritDoc} */
    public int getPreferableSize() {
        return preferableSize;
    }

    /** {@inheritDoc} */
    public void setPreferableSize(int size) {
        this.preferableSize = new CacheEvictionConfiguration<K, V>().setPreferableSize(
                size).getPreferableSize();
    }

    /** {@inheritDoc} */
    public void setMaximumSize(int size) {
        this.maxSize = new CacheEvictionConfiguration<K, V>().setMaximumSize(size)
                .getMaximumSize();
    }

    /** {@inheritDoc} */
    public long getMaximumVolume() {
        return maxCapacity;
    }

    /** {@inheritDoc} */
    public void setMaximumVolume(long size) {
        this.maxCapacity = new CacheEvictionConfiguration<K, V>()
                .setMaximumVolume(size).getMaximumVolume();
    }

    /** {@inheritDoc} */
    public long getPreferableCapacity() {
        return preferableCapacity;
    }

    /** {@inheritDoc} */
    public void setPreferableCapacity(long size) {
        this.preferableCapacity = new CacheEvictionConfiguration<K, V>()
                .setPreferableVolume(size).getPreferableVolume();
    }

    /** {@inheritDoc} */
    public void clear() {
        cp.clear();
    }

    /** {@inheritDoc} */
    public boolean replace(int index, T t) {
        return cp.update(index, t);
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.spi.ReplacementPolicy;
import org.coconut.core.AttributeMaps;

/**
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
public class UnsynchronizedCacheEvictionService<K, V, T extends CacheEntry<K, V>> extends
        AbstractEvictionService<K, V, T> {
    private final ReplacementPolicy<T> cp;

    private long maxVolume;

    private int maxSize;

    // @SuppressWarnings("unchecked")
    public UnsynchronizedCacheEvictionService(CacheEvictionConfiguration<K, V> conf,
            InternalCacheSupport<K, V> helper) {
        super(helper);
        cp = conf.getPolicy() == null ? Policies.newLRU() : (ReplacementPolicy) conf
                .getPolicy();
        maxSize = EvictionUtils.getMaximumSizeFromConfiguration(conf);
        maxVolume = EvictionUtils.getMaximumVolumeFromConfiguration(conf);
    }

    /** {@inheritDoc} */
    public int add(T t) {
        //TODO Test maxSize, maxVolume
        if (maxVolume == 0) {
            return -1;
        }
        return cp.add(t, AttributeMaps.EMPTY_MAP);
    }

    /** {@inheritDoc} */
    public void clear() {
        cp.clear();
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
        long diffCapacity = capacity - maxVolume;
        while (diffSize-- > 0 || diffCapacity > 0) {
            T e = evictNext();
            list.add(e);
            diffCapacity -= e.getSize();
        }
        return list;
    }

    /** {@inheritDoc} */
    public T evictNext() {
        return cp.evictNext();
    }

    /** {@inheritDoc} */
    public Collection<?> getChildServices() {
        return Arrays.asList(cp);
    }

    /** {@inheritDoc} */
    public int getMaximumSize() {
        return maxSize;
    }

    /** {@inheritDoc} */
    public long getMaximumVolume() {
        return maxVolume;
    }

    /** {@inheritDoc} */
    public boolean isSizeBreached(int size) {
        return size > maxSize;
    }

    /** {@inheritDoc} */
    public boolean isVolumeBreached(long capacity) {
        return capacity > maxVolume;
    }

    public boolean isSizeOrVolumeBreached(int size, long volume) {
        return isSizeBreached(size) || isVolumeBreached(volume);
    }

    /** {@inheritDoc} */
    public void remove(int index) {
        if (cp != null) {
            cp.remove(index);
        }
    }

    /** {@inheritDoc} */
    public boolean replace(int index, T t) {
        return cp.update(index, t,AttributeMaps.EMPTY_MAP);
    }

    /** {@inheritDoc} */
    public void setMaximumSize(int size) {
        this.maxSize = new CacheEvictionConfiguration<K, V>().setMaximumSize(size)
                .getMaximumSize();
    }

    /** {@inheritDoc} */
    public void setMaximumVolume(long size) {
        this.maxVolume = new CacheEvictionConfiguration<K, V>().setMaximumVolume(size)
                .getMaximumVolume();
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

}

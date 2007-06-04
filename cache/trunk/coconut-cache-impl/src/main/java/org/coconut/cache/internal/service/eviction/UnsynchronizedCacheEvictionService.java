/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedCacheEvictionService<K, V, T extends CacheEntry<K, V>> extends
        AbstractEvictionService<K, V, T> {
    private final ReplacementPolicy<T> cp;

    private int maxSize;

    private int preferableSize;

    private long maxCapacity;

    private long preferableCapacity;

    private Filter<? super CacheEntry<K, V>> idleFilter;

    private long defaultIdleTimeNS;

    // private long
    // @SuppressWarnings("unchecked")
    public UnsynchronizedCacheEvictionService(CacheEvictionConfiguration<K, V> conf,
            CacheHelper<K, V> helper) {
        super(helper);
        cp = conf.getPolicy() == null ? Policies.newLRU() : (ReplacementPolicy) conf
                .getPolicy();
        maxSize = conf.getMaximumSize();
        maxCapacity = conf.getMaximumCapacity();
        preferableCapacity = conf.getPreferableCapacity();
        preferableSize = conf.getPreferableSize();
        defaultIdleTimeNS = conf.getDefaultIdleTime(TimeUnit.NANOSECONDS);
        idleFilter = conf.getIdleFilter();
    }

    public T evictNext() {
        return cp.evictNext();
    }

    public void remove(int index) {
        if (cp != null) {
            cp.remove(index);
        }
    }

    public void touch(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "index must be a non negative number, was " + index);
        }
        if (cp != null) {
            cp.touch(index);
        }
    }

    public boolean isEnabled() {
        return cp != null;
    }

    public List<T> evict(int count) {
        ArrayList<T> list = new ArrayList<T>();
        while (count-- > 0) {
            T e = evictNext();
            list.add(e);
        }
        return list;
    }

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

    public boolean isCapacityBreached(long capacity) {
        return capacity > maxCapacity;
    }

    public boolean isSizeBreached(int size) {
        return size > maxSize;
    }

    public int add(T t) {
        if (maxCapacity == 0) {
            return -1;
        }
        return cp.add(t);
    }

    public int getMaximumSize() {
        return maxSize;
    }

    public int getPreferableSize() {
        return preferableSize;
    }

    public void setPreferableSize(int size) {
        this.preferableSize = new CacheEvictionConfiguration().setPreferableSize(size)
                .getPreferableSize();
    }

    public void setMaximumSize(int size) {
        this.maxSize = new CacheEvictionConfiguration().setMaximumSize(size)
                .getMaximumSize();
    }

    public long getMaximumCapacity() {
        return maxCapacity;
    }

    public void setMaximumCapacity(long size) {
        this.maxCapacity = new CacheEvictionConfiguration().setMaximumCapacity(size)
                .getMaximumCapacity();
    }

    public long getPreferableCapacity() {
        return preferableCapacity;
    }

    public void setPreferableCapacity(long size) {
        this.preferableCapacity = new CacheEvictionConfiguration().setPreferableCapacity(
                size).getPreferableCapacity();
    }

    /**
     * 
     */
    public void clear() {
        cp.clear();
    }


    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#getIdleFilter()
     */
    public Filter<? super CacheEntry<K, V>> getIdleFilter() {
        return idleFilter;
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#setIdleFilter(org.coconut.filter.Filter)
     */
    public void setIdleFilter(Filter<? super CacheEntry<K, V>> filter) {
        idleFilter = filter;
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionMXBean#getEvictionFilterAsString()
     */
    public String getIdleFilterAsString() {
        Filter<? super CacheEntry<K, V>> f = idleFilter;
        if (f == null) {
            return "null";
        } else {
            return f.toString();
        }
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#getDefaultIdleTime(java.util.concurrent.TimeUnit)
     */
    public long getDefaultIdleTime(TimeUnit unit) {
        return new CacheEvictionConfiguration().setDefaultIdleTime(defaultIdleTimeNS,
                TimeUnit.NANOSECONDS).getDefaultIdleTime(unit);
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#setDefaultIdleTime(long,
     *      java.util.concurrent.TimeUnit)
     */
    public void setDefaultIdleTime(long idleTime, TimeUnit unit) {
        defaultIdleTimeNS = new CacheEvictionConfiguration().setDefaultIdleTime(idleTime,
                unit).getDefaultIdleTime(TimeUnit.NANOSECONDS);
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.InternalCacheEvictionService#replace(int,
     *      org.coconut.cache.CacheEntry)
     */
    public boolean replace(int index, T t) {
        return cp.update(index, t);
    }

    public void evict(Object key) {}

    public void evictAll() {}

}

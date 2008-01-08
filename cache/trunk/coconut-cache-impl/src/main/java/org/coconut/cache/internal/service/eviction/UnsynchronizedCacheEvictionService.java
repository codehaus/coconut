/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.coconut.attribute.Attributes;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.servicemanager.AbstractCacheServiceManager;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;

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
    /** The ReplacementPolicy used for deciding which elements to evict. */
    private final ReplacementPolicy<T> cp;

    /** The maximum volume of this cache. */
    private long maxVolume;

    /** The maximum size of this cache. */
    private int maxSize;

    final Cache c;

    final InternalCacheListener listener;

    final AbstractCacheServiceManager manager;

    // @SuppressWarnings("unchecked")
    public UnsynchronizedCacheEvictionService(AbstractCacheServiceManager manager, Cache c,
            InternalCacheListener listener, AbstractCacheEntryFactoryService<K, V> factory,
            CacheEvictionConfiguration<K, V> conf, InternalCache<K, V> helper) {
        super(factory);
        cp = conf.getPolicy() == null ? new LRUPolicy<T>(1) : (ReplacementPolicy) conf.getPolicy();
        maxSize = EvictionUtils.getMaximumSizeFromConfiguration(conf);
        // System.out.println("maxSize " + maxSize);
        maxVolume = EvictionUtils.getMaximumVolumeFromConfiguration(conf);
        this.c = c;
        this.listener = listener;
        this.manager = manager;
    }

    /** {@inheritDoc} */
    public int add(T t) {
// // TODO Test maxSize, maxVolume
// if (maxVolume == 0 || maxSize == 0) {
// return -1;
// }
        return cp.add(t, Attributes.EMPTY_ATTRIBUTE_MAP);
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

// /** {@inheritDoc} */
// public List<T> evict(int size, long capacity) {
// ArrayList<T> list = new ArrayList<T>();
// int diffSize = size - maxSize;
// long diffCapacity = capacity - maxVolume;
// while (diffSize-- > 0 || diffCapacity > 0) {
// T e = evictNext();
// list.add(e);
// diffCapacity -= e.getSize();
// }
// return list;
// }

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
        cp.remove(index);
    }

    /** {@inheritDoc} */
    public boolean replace(int index, T t) {
        return cp.update(index, t, Attributes.EMPTY_ATTRIBUTE_MAP);
    }

    /** {@inheritDoc} */
    public void setMaximumSize(int size) {
        this.maxSize = new CacheEvictionConfiguration<K, V>().setMaximumSize(size).getMaximumSize();
    }

    /** {@inheritDoc} */
    public void setMaximumVolume(long size) {
        this.maxVolume = new CacheEvictionConfiguration<K, V>().setMaximumVolume(size)
                .getMaximumVolume();
    }

    /** {@inheritDoc} */
    public void touch(int index) {
// if (index < 0) {
// throw new IllegalArgumentException("index must be a non negative number, was " +
// index);
// }
        cp.touch(index);
    }

    @Override
    void trimCache(int toSize, long toVolume) {
        long started = listener.beforeTrim(toSize, toVolume);

        manager.lazyStart(true);
      //  int size = map.size();
      //  long volume = map.volume();
      //  List<CacheEntry<K, V>> l = map.trimCache(toSize, toVolume);

      //  listener.afterTrimCache(started, l, size, map.size(), volume, map.volume());
    }

}

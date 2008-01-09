/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.Collections;
import java.util.List;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.CacheMutex;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;

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
public class SynchronizedCacheEvictionService<K, V> extends UnsynchronizedCacheEvictionService
        implements ManagedLifecycle {
    private final Object mutex;

    public SynchronizedCacheEvictionService(MemoryStore<K, V> ms, CacheMutex mutex,
            InternalCacheListener listener, AbstractCacheEntryFactoryService<K, V> factory) {
        super(ms, listener, factory);
        this.mutex = mutex.getMutex();
    }

    /** {@inheritDoc} */
    public void manage(ManagedGroup parent) {
        ManagedGroup g = parent.addChild(CacheEvictionConfiguration.SERVICE_NAME,
                "Cache Eviction attributes and operations");
        g.add(EvictionUtils.wrapMXBean(this));
    }

    /** {@inheritDoc} */
    @Override
    public long getMaximumVolume() {
        synchronized (mutex) {
            return super.getMaximumVolume();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getMaximumSize() {
        synchronized (mutex) {
            return super.getMaximumSize();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setMaximumVolume(long size) {
        synchronized (mutex) {
            super.setMaximumVolume(size);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setMaximumSize(int size) {
        synchronized (mutex) {
            super.setMaximumSize(size);
        }
    }

    /** {@inheritDoc} */
    @Override
    void trimCache(int toSize, long toVolume) {
        long started = listener.beforeTrim(toSize, toVolume);
        int size = 0;
        int newSize = 0;
        long volume = 0;
        long newVolume = 0;
        List<CacheEntry<K, V>> l = Collections.EMPTY_LIST;

        synchronized (this) {
            // manager.lazyStart(true);
// size = map.size();
// volume = map.volume();
// l = map.trimCache(toSize, toVolume);
// newSize = map.size();
// newVolume = map.volume();
        }
        listener.afterTrimCache(started, l, size, newSize, volume, newVolume);
    }
}

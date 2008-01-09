/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionMXBean;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
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
 *            the type of keys maintained by this service
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractEvictionService<K, V, T extends CacheEntry<K, V>> extends
        AbstractCacheLifecycle implements CacheEvictionService<K, V>,
        CacheEvictionMXBean {

    private final AbstractCacheEntryFactoryService<K, V> entryFactory;

    private final MemoryStore<K, V> ms;

    /**
     * Creates a new AbstractEvictionService.
     * 
     * @param evictionSupport
     *            the InternalCacheSupport for the cache
     */
    public AbstractEvictionService(MemoryStore<K, V> ms,
            AbstractCacheEntryFactoryService<K, V> factory) {
        this.entryFactory = factory;
        this.ms = ms;
    }

    public int getMaximumSize() {
        return ms.getMaximumSize();
    }

    public long getMaximumVolume() {
        return ms.getMaximumVolume();
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheEvictionService.class, EvictionUtils.wrapService(this));
    }

    public boolean isDisabled() {
        return entryFactory.isDisabled();
    }

    public void setDisabled(boolean isDisabled) {
        entryFactory.setDisabled(isDisabled);
    }

    public void setMaximumSize(int size) {
        ms.setMaximumSize(size);
    }

    public void setMaximumVolume(long volume) {
        ms.setMaximumVolume(volume);
    }

    @Override
    public String toString() {
        return "Eviction Service";
    }

    /** {@inheritDoc} */
    public void trimToSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be a negative number, was " + size);
        }
        trimCache(size, Long.MAX_VALUE);
    }

    /** {@inheritDoc} */
    public void trimToVolume(long volume) {
        if (volume < 0) {
            throw new IllegalArgumentException("volume cannot be a negative number, was " + volume);
        }
        trimCache(Integer.MAX_VALUE, volume);
    }

    abstract void trimCache(int size, long capacity);
}

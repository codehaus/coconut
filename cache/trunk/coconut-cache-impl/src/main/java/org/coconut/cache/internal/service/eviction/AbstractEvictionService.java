/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
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
        AbstractCacheLifecycle implements InternalCacheEvictionService<K, V, T>,
        CacheEvictionMXBean, ManagedLifecycle, CompositeService {

    private final AbstractCacheEntryFactoryService<K, V> entryFactory;

    /** An EvictionSupport instance used for trimming the cache. */
    private final EvictionSupport helper;

    /**
     * Creates a new AbstractEvictionService.
     *
     * @param evictionSupport
     *            the InternalCacheSupport for the cache
     */
    public AbstractEvictionService(AbstractCacheEntryFactoryService<K, V> factory,
            EvictionSupport evictionSupport) {
        this.helper = evictionSupport;
        this.entryFactory = factory;
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheEvictionService.class, EvictionUtils.wrapService(this));
    }

    public boolean isDisabled() {
        return entryFactory.isDisabled();
    }

    /** {@inheritDoc} */
    public void manage(ManagedGroup parent) {
        ManagedGroup g = parent.addChild(CacheEvictionConfiguration.SERVICE_NAME,
                "Cache Eviction attributes and operations");
        g.add(EvictionUtils.wrapMXBean(this));
    }

    public void setDisabled(boolean isDisabled) {
        entryFactory.setDisabled(isDisabled);
    }

    @Override
    public String toString() {
        return "Eviction Service";
    }

    /** {@inheritDoc} */
    public void trimToSize(int size) {
        helper.trimCache(size, Long.MAX_VALUE);
    }

    /** {@inheritDoc} */
    public void trimToVolume(long capacity) {
        helper.trimCache(Integer.MAX_VALUE, capacity);
    }
}

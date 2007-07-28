/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheHelper;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionMXBean;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by this service
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractEvictionService<K, V, T extends CacheEntry<K, V>> extends
        AbstractInternalCacheService implements InternalCacheEvictionService<K, V, T>,
        CacheEvictionService<K, V>, CacheEvictionMXBean, ManagedObject {

    private final CacheHelper<K, V> helper;

    /**
     * 
     */
    public AbstractEvictionService(CacheHelper<K, V> helper) {
        super(CacheEvictionConfiguration.SERVICE_NAME);
        this.helper = helper;
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        serviceMap.put(CacheEvictionService.class, EvictionUtils.wrapService(this));
    }

    /** {@inheritDoc} */
    public void manage(ManagedGroup parent) {
        ManagedGroup g = parent.addChild(CacheEvictionConfiguration.SERVICE_NAME,
                "Cache Eviction attributes and operations");
        g.add(EvictionUtils.wrapMXBean(this));
    }

    /** {@inheritDoc} */
    public void trimToVolume(long capacity) {
        helper.trimToCapacity(capacity);
    }

    /** {@inheritDoc} */
    public void trimToSize(int size) {
        helper.trimToSize(size);
    }
}

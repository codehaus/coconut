/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheHelper;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionMXBean;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractEvictionService<K, V, T extends CacheEntry<K, V>> extends
        AbstractInternalCacheService implements InternalCacheEvictionService<K, V, T>,
        CacheEvictionService<K, V>, CacheEvictionMXBean {
    private final CacheHelper<K, V> helper;

    /**
     * 
     */
    public AbstractEvictionService(CacheHelper<K, V> helper) {
        super(CacheEvictionConfiguration.SERVICE_NAME);
        this.helper = helper;
    }

    /**
     * @see org.coconut.cache.service.servicemanager.AbstractCacheService#initialize(org.coconut.cache.CacheConfiguration,
     *      java.util.Map)
     */
    @Override
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        serviceMap.put(CacheEvictionService.class, EvictionUtils.wrapService(this));
    }

    @Override
    protected void registerMXBeans(ManagedGroup root) {
        ManagedGroup g = root.addChild(CacheEvictionConfiguration.SERVICE_NAME,
                "Cache Eviction attributes and operations");
        g.add(EvictionUtils.wrapMXBean(this));
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionMXBean#trimToCapacity(long)
     */
    public void trimToCapacity(long capacity) {
        helper.trimToCapacity(capacity);
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionMXBean#trimToSize(int)
     */
    public void trimToSize(int size) {
        helper.trimToSize(size);
    }

    public void evict(Object key) {
        helper.evict(key);
    }

    public void evictIdleElements() {
        helper.evictIdleElements();
    }

    public void evictAll(Collection<? extends K> keys) {
        helper.evictAll(keys);
    }

}

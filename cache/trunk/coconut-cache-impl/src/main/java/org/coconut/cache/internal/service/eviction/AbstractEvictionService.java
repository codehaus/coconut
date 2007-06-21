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
import org.coconut.cache.service.management.CacheManagementService;
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

    static int getInitialMaximumSize(CacheEvictionConfiguration<?, ?> conf) {
        int tmp = conf.getMaximumSize();
        return tmp == 0 ? Integer.MAX_VALUE : tmp;
    }

    static long getInitialMaximumCapacity(CacheEvictionConfiguration<?, ?> conf) {
        long tmp = conf.getMaximumCapacity();
        return tmp == 0 ? Long.MAX_VALUE : tmp;
    }

    static int getPreferableSize(CacheEvictionConfiguration<?, ?> conf) {
        int tmp = conf.getPreferableSize();
        return tmp == 0 ? Integer.MAX_VALUE : tmp;
    }

    static long getPreferableCapacity(CacheEvictionConfiguration<?, ?> conf) {
        long tmp = conf.getPreferableCapacity();
        return tmp == 0 ? Long.MAX_VALUE : tmp;
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
    public void start(Map<Class<?>, Object> allServices) {
        CacheManagementService cms = (CacheManagementService) allServices
                .get(CacheManagementService.class);
        if (cms != null) {
            ManagedGroup group = cms.getRoot();
            ManagedGroup g = group.addChild(CacheEvictionConfiguration.SERVICE_NAME,
                    "Cache Eviction attributes and operations");
            g.add(EvictionUtils.wrapMXBean(this));
        }
        super.start(allServices);
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

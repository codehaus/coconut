/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionMXBean;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.management.ManagedGroup;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

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
        serviceMap.put(CacheEvictionService.class,
                new DelegatedCacheEvictionService<K, V>(this));
    }

    @Override
    public void start(Map<Class<?>, Object> allServices) {
        CacheManagementService cms = (CacheManagementService) allServices
                .get(CacheManagementService.class);
        if (cms != null) {
            ManagedGroup group = cms.getRoot();
            ManagedGroup g = group.addChild(CacheEvictionConfiguration.SERVICE_NAME,
                    "Cache Eviction attributes and operations");
            g.add(new DelegatedCacheEvictionMXBean(this));
        }
        super.start(allServices);
    }

// /**
// * @see org.coconut.cache.service.eviction.CacheEvictionMXBean#getDefaultIdleTimeMs()
// */
// public long getDefaultIdleTimeMs() {
// return getDefaultIdleTime(TimeUnit.MILLISECONDS);
// }
//
// /**
// * @see
// org.coconut.cache.service.eviction.CacheEvictionMXBean#setDefaultIdleTimeMs(long)
// */
// public void setDefaultIdleTimeMs(long idleTimeMs) {
// setDefaultIdleTime(idleTimeMs, TimeUnit.MILLISECONDS);
// }

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

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#evict(java.lang.Object)
     */
    public void evict(Object key) {
        helper.evict(key);
    }

    public void evictIdleElements() {
        helper.evictIdleElements();
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#evictAll(java.util.Collection)
     */
    public void evictAll(Collection<? extends K> keys) {
        helper.evictAll(keys);
    }

    public static class DelegatedCacheEvictionService<K, V> implements
            CacheEvictionService<K, V> {
        private final CacheEvictionService<K, V> service;

        DelegatedCacheEvictionService(CacheEvictionService<K, V> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

// public void evict(K key) {
// service.evict(key);
// }
//
// public void evictAll(Collection<? extends K> keys) {
// service.evictAll(keys);
// }
//
// public long getDefaultIdleTime(TimeUnit unit) {
// return service.getDefaultIdleTime(unit);
// }

        public long getMaximumCapacity() {
            return service.getMaximumCapacity();
        }

        public int getMaximumSize() {
            return service.getMaximumSize();
        }

//
// public void setDefaultIdleTime(long idleTime, TimeUnit unit) {
// service.setDefaultIdleTime(idleTime, unit);
// }

        public void setMaximumCapacity(long maximumCapacity) {
            service.setMaximumCapacity(maximumCapacity);
        }

        public void setMaximumSize(int maximumSize) {
            service.setMaximumSize(maximumSize);
        }

        public void trimToCapacity(long capacity) {
            service.trimToCapacity(capacity);
        }

        public void trimToSize(int size) {
            service.trimToSize(size);
        }
//
// public void evictIdleElements() {
// service.evictIdleElements();
// }
    }

    /**
     * <p>
     * Must be a public class to allow reflection.
     */
    public static class DelegatedCacheEvictionMXBean implements CacheEvictionMXBean {
        private final CacheEvictionService<?, ?> service;

        DelegatedCacheEvictionMXBean(CacheEvictionService<?, ?> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

// @ManagedAttribute(description = "The default time to idle for cache entries in
// milliseconds")
// public long getDefaultIdleTimeMs() {
// return service.getDefaultIdleTime(TimeUnit.MILLISECONDS);
// }

        @ManagedAttribute(description = "The maximum capacity of the cache")
        public long getMaximumCapacity() {
            return service.getMaximumCapacity();
        }

        @ManagedAttribute(description = "The maximum size of the cache")
        public int getMaximumSize() {
            return service.getMaximumSize();
        }

// public void setDefaultIdleTimeMs(long idleTimeMs) {
// service.setDefaultIdleTime(idleTimeMs, TimeUnit.MILLISECONDS);
// }

        public void setMaximumCapacity(long maximumCapacity) {
            service.setMaximumCapacity(maximumCapacity);
        }

        public void setMaximumSize(int maximumSize) {
            service.setMaximumSize(maximumSize);
        }

        @ManagedOperation(description = "Trims the cache to the specified capacity")
        public void trimToCapacity(long capacity) {
            service.trimToCapacity(capacity);
        }

        @ManagedOperation(description = "Trims the cache to the specified size")
        public void trimToSize(int size) {
            service.trimToSize(size);
        }

// @ManagedOperation(description = "Evict all elements that idle")
// public void evictIdleElements() {
// service.evictIdleElements();
// }
    }
}

package org.coconut.cache.internal.service.eviction;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionMXBean;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

public class EvictionUtils {
    
    public static <K, V> CacheEvictionService<K, V> wrapService(
            CacheEvictionService<K, V> service) {
        return new DelegatedCacheEvictionService<K, V>(service);
    }

    public static CacheEvictionMXBean wrapMXBean(CacheEvictionService<?, ?> service) {
        return new DelegatedCacheEvictionMXBean(service);
    }

    /**
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

        @ManagedAttribute(description = "The maximum capacity of the cache")
        public long getMaximumCapacity() {
            return service.getMaximumCapacity();
        }

        @ManagedAttribute(description = "The maximum size of the cache")
        public int getMaximumSize() {
            return service.getMaximumSize();
        }

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

//        @ManagedAttribute(defaultValue = "Preferable Size", description = "The preferable size of the cache")
//        int getPreferableSize();

//        @ManagedAttribute(defaultValue = "Preferable Capacity", description = "The preferable capacity of the cache")
        //long getPreferableCapacity();

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

        public long getMaximumCapacity() {
            return service.getMaximumCapacity();
        }

        public int getMaximumSize() {
            return service.getMaximumSize();
        }

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
    
    // @ManagedAttribute(description = "The default time to idle for cache entries in
    // milliseconds")
    // public long getDefaultIdleTimeMs() {
    // return service.getDefaultIdleTime(TimeUnit.MILLISECONDS);
    // }

    // public void setDefaultIdleTimeMs(long idleTimeMs) {
    // service.setDefaultIdleTime(idleTimeMs, TimeUnit.MILLISECONDS);
    // }

    // @ManagedOperation(description = "Evict all elements that idle")
    // public void evictIdleElements() {
    // service.evictIdleElements();
    // }

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
    //
    // public void setDefaultIdleTime(long idleTime, TimeUnit unit) {
    // service.setDefaultIdleTime(idleTime, unit);
    // }
    //
    // public void evictIdleElements() {
    // service.evictIdleElements();
    // }
}

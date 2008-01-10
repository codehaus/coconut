/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.memorystore;

import org.coconut.cache.ParallelCache;
import org.coconut.cache.service.memorystore.MemoryStoreConfiguration;
import org.coconut.cache.service.memorystore.MemoryStoreMXBean;
import org.coconut.cache.service.memorystore.MemoryStoreService;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * Various utilities used for the eviction service.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: EvictionUtils.java 559 2008-01-09 16:28:27Z kasper $
 */
final class MemoryStoreUtils {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private MemoryStoreUtils() {}

    // /CLOVER:ON

    /**
     * This class wraps CacheEvictionService as a CacheEvictionMXBean.
     * <p>
     * Must be a public class to allow for reflection.
     */
    public static class DelegatedCacheEvictionMXBean implements MemoryStoreMXBean {
        /** The service we are wrapping. */
        private final MemoryStoreService<?, ?> service;

        /**
         * Creates a new CacheEvictionMXBean by wrapping a CacheEvictionService.
         *
         * @param service
         *            the service to wrap.
         */
        DelegatedCacheEvictionMXBean(MemoryStoreService<?, ?> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        /** {@inheritDoc} */
        public int getMaximumSize() {
            return service.getMaximumSize();
        }

        /** {@inheritDoc} */
        public long getMaximumVolume() {
            return service.getMaximumVolume();
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The maximum size of the cache")
        public void setMaximumSize(int maximumSize) {
            service.setMaximumSize(maximumSize);
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The maximum capacity of the cache")
        public void setMaximumVolume(long maximumCapacity) {
            service.setMaximumVolume(maximumCapacity);
        }

        /** {@inheritDoc} */
        @ManagedOperation(description = "Trims the cache to the specified size")
        public void trimToSize(int size) {
            service.trimToSize(size);
        }

        /** {@inheritDoc} */
        @ManagedOperation(description = "Trims the cache to the specified volume")
        public void trimToVolume(long capacity) {
            service.trimToVolume(capacity);
        }
    }

    /**
     * This class wraps a CacheEvictionService implementation, only exposing the public
     * methods in CacheEvictionService.
     */
    static class DelegatedCacheEvictionService<K, V> implements MemoryStoreService<K, V> {
        /** The CacheEvictionService we are wrapping. */
        private final MemoryStoreService<K, V> service;

        /**
         * Creates a new DelegatedCacheEvictionService from the specified
         * CacheEvictionService instance.
         *
         * @param service
         *            the CacheEvictionService to wrap.
         */
        DelegatedCacheEvictionService(MemoryStoreService<K, V> service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        /** {@inheritDoc} */
        public int getMaximumSize() {
            return service.getMaximumSize();
        }

        /** {@inheritDoc} */
        public long getMaximumVolume() {
            return service.getMaximumVolume();
        }

        /** {@inheritDoc} */
        public void setMaximumSize(int maximumSize) {
            service.setMaximumSize(maximumSize);
        }

        /** {@inheritDoc} */
        public void setMaximumVolume(long maximumCapacity) {
            service.setMaximumVolume(maximumCapacity);
        }

        /** {@inheritDoc} */
        public void trimToSize(int size) {
            service.trimToSize(size);
        }

        /** {@inheritDoc} */
        public void trimToVolume(long volume) {
            service.trimToVolume(volume);
        }

        /** {@inheritDoc} */
        public boolean isDisabled() {
            return service.isDisabled();
        }

        /** {@inheritDoc} */
        public void setDisabled(boolean isDisabled) {
            service.setDisabled(isDisabled);
        }

        public ParallelCache<K, V> getParallelCache() {
            return service.getParallelCache();
        }
    }

    /**
     * Wraps a CacheEvictionService as a CacheEvictionMXBean.
     *
     * @param service
     *            the CacheEvictionService to wrap
     * @return the wrapped CacheEvictionMXBean
     */
    public static MemoryStoreMXBean wrapMXBean(MemoryStoreService<?, ?> service) {
        return new DelegatedCacheEvictionMXBean(service);
    }

    /**
     * Wraps a CacheEvictionService implementation such that only methods from the
     * CacheEvictionService interface is exposed.
     *
     * @param service
     *            the CacheEvictionService to wrap
     * @return a wrapped service that only exposes CacheEvictionService methods
     * @param <K>
     *            the type of keys maintained by the specified service
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> MemoryStoreService<K, V> wrapService(MemoryStoreService<K, V> service) {
        return new DelegatedCacheEvictionService<K, V>(service);
    }


// /**
// * Returns the preferable size configured in the specified configuration.
// *
// * @param conf
// * the configuration to read the preferable size from
// * @return the preferable size configured in the specified configuration
// */
// static int getPreferableSizeFromConfiguration(CacheEvictionConfiguration<?, ?> conf) {
// int tmp = conf.getPreferableSize();
// return tmp == 0 ? Integer.MAX_VALUE : tmp;
// }
//
// /**
// * Returns the preferable volume configured in the specified configuration.
// *
// * @param conf
// * the configuration to read the preferable volume from
// * @return the preferable volume configured in the specified configuration
// */
// static long getPreferableVolumeFromConfiguration(CacheEvictionConfiguration<?, ?> conf)
// {
// long tmp = conf.getPreferableVolume();
// return tmp == 0 ? Long.MAX_VALUE : tmp;
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

    //
    // public void evictIdleElements() {
    // service.evictIdleElements();
    // }
}

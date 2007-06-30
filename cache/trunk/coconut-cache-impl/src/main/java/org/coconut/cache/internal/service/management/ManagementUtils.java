package org.coconut.cache.internal.service.management;

import org.coconut.cache.Cache;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.management.ManagedGroup;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * Various utilities class and methods relating to the implementation of the
 * {@link CacheManagementService} service.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ManagementUtils {

    /**
     * Wraps a CacheManagementService implementation such that only methods from the
     * CacheManagementService interface is exposed.
     * 
     * @param servicec
     *            the CacheManagementService to wrap
     * @return a wrapped service that only exposes CacheManagementService methods
     */
    public static CacheManagementService wrapService(CacheManagementService service) {
        return new DelegatedCacheManagementService(service);
    }

    /**
     * Wraps a Cache in a CacheMXBean.
     * 
     * @param service
     *            the Cache to wrap
     * @return the wrapped CacheMXBean
     */
    public static CacheMXBean wrapMXBean(Cache<?, ?> service) {
        return new DelegatedCacheMXBean(service);
    }

    /**
     * A wrapper class that exposes only the CacheManagementService methods of a
     * CacheManagementService implementation.
     */
    public static final class DelegatedCacheManagementService implements
            CacheManagementService {
        /** The CacheManagementService that is wrapped. */
        private final CacheManagementService delegate;

        /**
         * Creates a wrapped DelegatedCacheManagementService from the specified
         * implementation
         * 
         * @param service
         *            the DelegatedCacheManagementService to wrap
         */
        public DelegatedCacheManagementService(CacheManagementService service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.delegate = service;
        }

        /**
         * @see org.coconut.cache.service.management.CacheManagementService#getRoot()
         */
        public ManagedGroup getRoot() {
            return delegate.getRoot();
        }
    }

    /**
     * A class that exposes a {@link Cache} as a {@link CacheMXBean}.
     */
    public static final class DelegatedCacheMXBean implements CacheMXBean {
        /** The cache that is wrapped. */
        private final Cache<?, ?> cache;

        /**
         * Creates a new
         * 
         * @param cache
         *            the cache to wrap
         */
        public DelegatedCacheMXBean(Cache<?, ?> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /**
         * @see org.coconut.cache.service.management.CacheMXBean#clear()
         */
        @ManagedOperation(description = "Clears the cache")
        public void clear() {
            cache.clear();
        }

        /**
         * @see org.coconut.cache.service.management.CacheMXBean#evict()
         */
        @ManagedOperation(description = "Evicts expired entries and performs housekeeping on the cache")
        public void evict() {
            cache.evict();
        }

        /**
         * @see org.coconut.cache.service.management.CacheMXBean#getCapacity()
         */
        @ManagedAttribute(description = "The total size of all elements contained in the cache")
        public long getCapacity() {
            return cache.getCapacity();
        }

        /**
         * @see org.coconut.cache.service.management.CacheMXBean#getName()
         */
        @ManagedAttribute(description = "The name of the cache")
        public String getName() {
            return cache.getName();
        }

        /**
         * @see org.coconut.cache.service.management.CacheMXBean#getSize()
         */
        @ManagedAttribute(description = "The number of elements contained in the cache")
        public int getSize() {
            return cache.size();
        }
    }
}

package org.coconut.cache.internal.service.management;

import org.coconut.cache.Cache;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.management.ManagedGroup;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ManagementUtils {

    public static CacheManagementService wrapService(CacheManagementService service) {
        return new DelegatedCacheManagementService(service);
    }

    public static CacheMXBean wrapMXBean(Cache<?, ?> service) {
        return new DelegatedCacheMXBean(service);
    }

    public static final class DelegatedCacheManagementService implements CacheManagementService {
        private final CacheManagementService delegate;

        /**
         * @param name
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
     * <p>
     * Must be a public class to allow reflection.
     */
    public static final class DelegatedCacheMXBean implements CacheMXBean {
        private final Cache<?, ?> cache;

        public DelegatedCacheMXBean(Cache<?, ?> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        @ManagedOperation(description = "Clears the cache")
        public void clear() {
            cache.clear();
        }

        @ManagedOperation(description = "Evicts expired entries and performs housekeeping on the cache")
        public void evict() {
            cache.evict();
        }

        @ManagedAttribute(description = "The total size of all elements contained in the cache")
        public long getCapacity() {
            return cache.getCapacity();
        }

        @ManagedAttribute(description = "The name of the cache")
        public String getName() {
            return cache.getName();
        }

        @ManagedAttribute(description = "The number of elements contained in the cache")
        public int getSize() {
            return cache.size();
        }
    }
}

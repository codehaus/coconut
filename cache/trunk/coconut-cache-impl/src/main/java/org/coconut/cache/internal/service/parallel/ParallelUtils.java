package org.coconut.cache.internal.service.parallel;

import org.coconut.cache.CacheParallelService;
import org.coconut.cache.service.parallel.ParallelCache;

final class ParallelUtils {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private ParallelUtils() {}
    // /CLOVER:ON

    /**
     * Wraps a CacheManagementService implementation such that only methods from the
     * CacheManagementService interface is exposed.
     *
     * @param service
     *            the CacheManagementService to wrap
     * @return a wrapped service that only exposes CacheManagementService methods
     */
    public static CacheParallelService wrapService(CacheParallelService service) {
        return new DelegatedParallelCacheService(service);
    }
    /**
     * A wrapper class that exposes only the CacheManagementService methods of a
     * CacheManagementService implementation.
     */
    public static final class DelegatedParallelCacheService implements CacheParallelService {
        /** The CacheManagementService that is wrapped. */
        private final CacheParallelService delegate;

        /**
         * Creates a wrapped CacheManagementService from the specified implementation.
         *
         * @param service
         *            the CacheManagementService to wrap
         */
        public DelegatedParallelCacheService(CacheParallelService service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.delegate = service;
        }

        public ParallelCache get() {
            return delegate.get();
        }
    }
}

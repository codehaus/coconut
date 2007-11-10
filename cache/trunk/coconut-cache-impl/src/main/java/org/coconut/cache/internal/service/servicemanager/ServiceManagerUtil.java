/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.Map;

import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

public class ServiceManagerUtil {

    /**
     * Wraps a CacheServiceManagerService implementation such that only methods from the
     * CacheServiceManagerService interface is exposed.
     * 
     * @param service
     *            the CacheServiceManagerService to wrap
     * @return a wrapped service that only exposes CacheServiceManagerService methods
     */
    public static <K, V> CacheServiceManagerService wrapService(
            CacheServiceManagerService service) {
        return new DelegatedCacheServiceManagerService(service);
    }
    
    /**
     * A wrapper class that exposes only the CacheServiceManagerService methods of a
     * CacheServiceManagerService implementation.
     */
    public static final class DelegatedCacheServiceManagerService implements
            CacheServiceManagerService {

        /** The CacheServiceManagerService that is wrapped. */
        private final CacheServiceManagerService delegate;

        /**
         * Creates a wrapped CacheServiceManagerService from the specified implementation.
         * 
         * @param service
         *            the CacheServiceManagerService to wrap
         */
        public DelegatedCacheServiceManagerService(CacheServiceManagerService service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.delegate = service;
        }
        /** {@inheritDoc} */
        public Map<Class<?>, Object> getAllServices() {
            return delegate.getAllServices();
        }
        
        /** {@inheritDoc} */
        public boolean hasService(Class<?> serviceType) {
            return delegate.hasService(serviceType);
        }
        /** {@inheritDoc} */
        public void shutdownServiceAsynchronously(AsynchronousShutdownObject service) {
            delegate.shutdownServiceAsynchronously(service);
        }
    }

}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.Map;

import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

/**
 * Various utility classes for {@link CacheServiceManagerService} implementations.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class ServiceManagerUtil {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private ServiceManagerUtil() {}

    // /CLOVER:ON

    /**
     * Wraps a CacheServiceManagerService implementation such that only methods from the
     * CacheServiceManagerService interface is exposed.
     *
     * @param service
     *            the CacheServiceManagerService to wrap
     * @return a wrapped service that only exposes CacheServiceManagerService methods
     */
    public static CacheServiceManagerService wrapService(InternalCacheServiceManager service) {
        return new DelegatedCacheServiceManagerService(service);
    }

    /**
     * A wrapper class that exposes only the CacheServiceManagerService methods of a
     * CacheServiceManagerService implementation.
     */
    public static final class DelegatedCacheServiceManagerService implements
            CacheServiceManagerService {

        /** The CacheServiceManagerService that is wrapped. */
        private final InternalCacheServiceManager delegate;

        /**
         * Creates a wrapped CacheServiceManagerService from the specified implementation.
         *
         * @param service
         *            the CacheServiceManagerService to wrap
         */
        public DelegatedCacheServiceManagerService(InternalCacheServiceManager service) {
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
        public <T> T getService(Class<T> serviceType) {
            return delegate.getServiceFromCache(serviceType);
        }
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import java.util.ArrayList;
import java.util.Collection;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import net.jcip.annotations.ThreadSafe;

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
 * @version $Id$
 */
final class ManagementUtils {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private ManagementUtils() {}
    // /CLOVER:ON

    /**
     * Wraps a Cache in a CacheMXBean.
     * <p>
     * The returned CacheMXBean will be thread-safe if the specified cache is thread-safe.
     * 
     * @param service
     *            the Cache to wrap
     * @return the wrapped CacheMXBean
     */
    public static CacheMXBean wrapMXBean(Cache<?, ?> service) {
        return new DelegatedCacheMXBean(service);
    }

    /**
     * Wraps a CacheManagementService implementation such that only methods from the
     * CacheManagementService interface is exposed.
     * 
     * @param service
     *            the CacheManagementService to wrap
     * @return a wrapped service that only exposes CacheManagementService methods
     */
    public static CacheManagementService wrapService(CacheManagementService service) {
        return new DelegatedCacheManagementService(service);
    }

    /**
     * A wrapper class that exposes only the CacheManagementService methods of a
     * CacheManagementService implementation.
     */
    public static final class DelegatedCacheManagementService implements CacheManagementService {
        /** The CacheManagementService that is wrapped. */
        private final CacheManagementService delegate;

        /**
         * Creates a wrapped CacheManagementService from the specified implementation.
         * 
         * @param managementService
         *            the CacheManagementService to wrap
         */
        public DelegatedCacheManagementService(CacheManagementService managementService) {
            if (managementService == null) {
                throw new NullPointerException("managementService is null");
            }
            this.delegate = managementService;
        }

        /** {@inheritDoc} */
        public ManagedGroup add(Object o) {
            return delegate.add(o);
        }

        /** {@inheritDoc} */
        public ManagedGroup addChild(String name, String description) {
            return delegate.addChild(name, description);
        }

        /** {@inheritDoc} */
        public Collection<ManagedGroup> getChildren() {
            return delegate.getChildren();
        }

        /** {@inheritDoc} */
        public String getDescription() {
            return delegate.getDescription();
        }

        /** {@inheritDoc} */
        public String getName() {
            return delegate.getName();
        }

        /** {@inheritDoc} */
        public ObjectName getObjectName() {
            return delegate.getObjectName();
        }

        /** {@inheritDoc} */
        public Collection<?> getObjects() {
            return delegate.getObjects();
        }

        /** {@inheritDoc} */
        public ManagedGroup getParent() {
            return delegate.getParent();
        }

        /** {@inheritDoc} */
        public MBeanServer getServer() {
            return delegate.getServer();
        }

        /** {@inheritDoc} */
        public boolean isRegistered() {
            return delegate.isRegistered();
        }

        /** {@inheritDoc} */
        public void register(MBeanServer server, ObjectName objectName) throws JMException {
            delegate.register(server, objectName);
        }

        /** {@inheritDoc} */
        public void remove() {
            delegate.remove();
        }

        /** {@inheritDoc} */
        public void unregister() throws JMException {
            delegate.unregister();
        }
    }

    /**
     * A class that exposes a {@link Cache} as a {@link CacheMXBean}.
     */
    public static final class DelegatedCacheMXBean implements CacheMXBean {
        /** The cache that is wrapped. */
        private final Cache<?, ?> cache;

        /**
         * Creates a new DelegatedCacheMXBean.
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

        /** {@inheritDoc} */
        @ManagedOperation(description = "Clears the cache")
        public void clear() {
            cache.clear();
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The name of the cache")
        public String getName() {
            return cache.getName();
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The number of elements contained in the cache")
        public int getSize() {
            return cache.size();
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The total size of all elements contained in the cache")
        public long getVolume() {
            return cache.getVolume();
        }
    }

}

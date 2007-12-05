/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * Provides information about the configuration of the cache. Used when initializing
 * services.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheLifecycleInitializer {
    /**
     * Returns the configuration used for creating this cache.
     * 
     * @return the configuration used for creating this cache
     */
    CacheConfiguration<?, ?> getCacheConfiguration();

    /**
     * Registers the specified service in the cache. The service can later be retrieved by
     * calls to {@link Cache#getService(Class)} with the specified class as parameter.
     * 
     * @param <T>
     *            the type of the service
     * @param clazz
     *            the type of the service
     * @param service
     *            the service to register
     */
    <T> void registerService(Class<T> clazz, T service);

    /**
     * Returns the type of cache that is being initialized.
     * 
     * @return the type of cache that is being initialized
     */
    Class<? extends Cache> getCacheType();
}

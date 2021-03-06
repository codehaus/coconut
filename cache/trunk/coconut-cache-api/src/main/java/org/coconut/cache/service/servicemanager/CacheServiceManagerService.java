/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;

/**
 * This is the main interface for controlling the services of a cache at runtime.
 * <p>
 * An instance of this interface can be retrieved by using {@link Cache#getService(Class)}
 * to look it up.
 *
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheServiceManagerService&lt;?, ?&gt; ces = c.getService(CacheServiceManagerService.class);
 * ces.getAllServices();
 * </pre>
 *
 * Or by using {@link CacheServices}
 *
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheServiceManagerService&lt;?, ?&gt; ces = CacheServices.services.serviceManager();
 * ces.hasService(CacheLoadingService.class);
 * </pre>
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheServiceManagerService {

    /**
     * Returns a service of the specified type or throws a
     * {@link IllegalArgumentException} if no such service exists.
     *
     * @param <T>
     *            the type of service to retrieve
     * @param serviceType
     *            the type of service to retrieve
     * @return a service of the specified type
     * @throws IllegalArgumentException
     *             if no service of the specified type exist
     * @throws NullPointerException
     *             if the specified service is null
     * @see org.coconut.cache.CacheServices
     * @see CacheServiceManagerService#hasService(Class)
     * @see CacheServiceManagerService#getAllServices()
     */
    <T> T getService(Class<T> serviceType);

    /**
     * Returns whether or not this cache contains a service of the specified type.
     *
     * @param serviceType
     *            the type of service
     * @return true if this cache has a service of the specified type registered,
     *         otherwise false
     * @see CacheServiceManagerService#getAllServices()
     */
    boolean hasService(Class<?> serviceType);

    /**
     * Returns all registered services within the cache.
     *
     * @return a map of all registered services
     */
    Map<Class<?>, Object> getAllServices();
}

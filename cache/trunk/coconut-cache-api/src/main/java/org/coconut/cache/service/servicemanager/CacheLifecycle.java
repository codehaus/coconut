/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheLifecycle {

    /**
     * Initializes the object. This is the first method in the cache lifecycle that is
     * called.
     * <p>
     * This method will be called from within the constructor of the cache. Any runtime
     * exception thrown by this method will not be handled.
     * <p>
     * TODO: do we call terminated, for components whose initialize method has already
     * been run, but where another components initialize method fails.
     * 
     * @param configuration
     *            the CacheConfiguration for the cache that this object belongs to
     */
    void initialize(CacheConfiguration<?, ?> configuration);

    /**
     * Initializes the service.
     * 
     * @param serviceMap
     *            a map that can be used to add public services
     */
    void registerServices(Map<Class<?>, Object> serviceMap);

    /**
     * Start the service. The specified cache can be used to retrieve other cache
     * services.
     * 
     * @param allServiceMap
     *            a map of registered service
     */
    void start(Map<Class<?>, Object> allServiceMap);

    /**
     * All services have been intialized correctly, and the cache is ready for use.
     * 
     * @param cache
     *            the cache that was started
     */
    void started(Cache<?, ?> cache);

    /**
     * The cache has been shutdown.
     */
    void shutdown();

    /**
     * Method invoked when the cache has terminated. This method is invoked as the last
     * method in this lifecycle interface and is called when the cache and all of it
     * services has been succesfully shutdown.
     */
    void terminated();
}

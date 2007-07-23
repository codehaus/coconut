/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.Map;
import java.util.concurrent.Executor;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheService {
    /**
     * Returns the readable name of the service.
     * 
     * @return the readable name of the service
     */
    String getName();

    /**
     * Initializes the service.
     * 
     * @param configuration
     *            the CacheConfiguration for the cache
     * @param serviceMap
     *            a map that can be used to add public services
     */
    void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap);

    /**
     * Start the service. The specified cache can be used to retrieve other cache
     * services.
     * 
     * @param allServiceMap
     *            a map of registered service
     */
    void start(Map<Class<?>, Object> allServiceMap);

    /**
     * All services has started succesfully, and the cache is ready for use.
     * 
     * @param cache the cache that was started
     */
    void started(Cache<?, ?> cache);

    void shutdown(Executor e);

    // void shutdownNow(Cache<?, ?> c);

    /**
     * Method invoked when the Cache has terminated. Note: To properly nest multiple
     * overridings, subclasses should generally invoke <tt>super.terminated</tt> within
     * this method.
     */
    void terminated();
}

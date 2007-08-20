/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.Map;
import java.util.concurrent.Executor;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheLifecycle;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheService extends CacheLifecycle {
    /**
     * Returns the readable name of the service.
     * 
     * @return the readable name of the service
     */
    String getName();

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

    void shutdown(Executor e);

    // void shutdownNow(Cache<?, ?> c);
}

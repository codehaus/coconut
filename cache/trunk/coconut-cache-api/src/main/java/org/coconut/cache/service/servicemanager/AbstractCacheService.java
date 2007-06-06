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
public abstract class AbstractCacheService implements CacheService {

    private final String name;

    public AbstractCacheService() {
        name = getClass().getSimpleName();
    }

    public AbstractCacheService(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.name = name;
    }

    /**
     * @see org.coconut.cache.service.servicemanager.CacheService#getName()
     */
    public final String getName() {
        return name;
    }

// /**
// * Registers all the public services this service exposes. For example, an expiration
// * service would use:
// *
// * <pre>
// * Map&lt;Class, Object&gt; serviceMap = null;
// * serviceMap.put(CacheExpirationMXBean.class, null);
// * serviceMap.put(CacheExpirationService.class, null);
// * </pre>
// *
// * @param serviceMap
// */
// public void registerServices(Map<Class<?>, Object> serviceMap) {
// // ignore
// }

    /**
     * @see org.coconut.cache.service.servicemanager.CacheLifecycle#start(org.coconut.cache.CacheConfiguration)
     */
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
    // do nothing
    }

    /**
     * @see org.coconut.cache.service.servicemanager.CacheService#start(org.coconut.cache.Cache)
     */
    public void start(Cache<?, ?> cache) {
    // do nothing
    }

    /**
     * @see org.coconut.cache.service.servicemanager.CacheService#shutdown(java.util.concurrent.Executor)
     */
    public void shutdown(Executor e) {
    // ignore
    }

    /**
     * @see org.coconut.cache.service.servicemanager.CacheLifecycle#terminated()
     */
    public void terminated() {
    // do nothing
    }

}

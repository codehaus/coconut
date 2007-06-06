/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

/**
 * A utility class to get hold of cache services in an easy and typesafe manner.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public final class CacheServices {

    public static CacheManagementService management(Cache<?, ?> cache) {
        return cache.getService(CacheManagementService.class);
    }
    public static CacheServiceManagerService serviceManager(Cache<?, ?> cache) {
        return cache.getService(CacheServiceManagerService.class);
    }

    
    public static CacheEventService event(Cache<?, ?> cache) {
        return cache.getService(CacheEventService.class);
    }

    public static CacheEvictionService eviction(Cache<?, ?> cache) {
        return cache.getService(CacheEvictionService.class);
    }

    /**
     * @param cache
     *            the cache for which to return an expiration service
     * @return a CacheExpirationService
     */
    public static <K, V> CacheExpirationService<K, V> expiration(Cache<K, V> cache) {
        return cache.getService(CacheExpirationService.class);
    }

    public static boolean hasExpirationService(Cache<?, ?> cache) {
        return cache.getService(CacheExpirationService.class) == null;
    }

}

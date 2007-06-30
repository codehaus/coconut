/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

/**
 * A utility class to get hold of different cache services in an easy and typesafe manner.
 * 
 * <pre>
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public final class CacheServices {

    public static <K, V> CacheEventService<K, V> event(Cache<K, V> cache) {
        return cache.getService(CacheEventService.class);
    }

    public static <K, V> CacheEvictionService<K, V> eviction(Cache<K, V> cache) {
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

    public static <K, V> CacheLoadingService<K, V> loading(Cache<K, V> cache) {
        return cache.getService(CacheLoadingService.class);
    }

    public static CacheManagementService management(Cache<?, ?> cache) {
        return cache.getService(CacheManagementService.class);
    }

    public static CacheServiceManagerService servicemanager(Cache<?, ?> cache) {
        return cache.getService(CacheServiceManagerService.class);
    }

}

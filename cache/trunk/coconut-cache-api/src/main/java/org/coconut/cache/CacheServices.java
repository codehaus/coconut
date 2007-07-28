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
 * For example, the following will return the {@link CacheEvictionService} for a given
 * cache.
 * 
 * <pre>
 * Cache&lt;Integer, String&gt; cache = somecache;
 * CacheEvictionService&lt;Integer, String&gt; service = CacheServices.eviction(cache);
 * service.trimToSize(10);
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public final class CacheServices {

    /** Cannot instantiate. */
    private CacheServices() {}

    /**
     * Return the event service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the event service
     * @return the event service for the cache *
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheEventService<K, V> event(Cache<K, V> cache) {
        return cache.getService(CacheEventService.class);
    }

    /**
     * Return the eviction service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the eviction service
     * @return the eviction service for the cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheEvictionService<K, V> eviction(Cache<K, V> cache) {
        return cache.getService(CacheEvictionService.class);
    }

    /**
     * Return the expiration service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the expiration service
     * @return the expiration service for the cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheExpirationService<K, V> expiration(Cache<K, V> cache) {
        return cache.getService(CacheExpirationService.class);
    }

    /**
     * Return the loading service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the loading service
     * @return the loading service for the cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheLoadingService<K, V> loading(Cache<K, V> cache) {
        return cache.getService(CacheLoadingService.class);
    }

    /**
     * Return the management service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the management service
     * @return the management service for the cache
     */
    public static CacheManagementService management(Cache<?, ?> cache) {
        return cache.getService(CacheManagementService.class);
    }

    /**
     * Return the servicemanager service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the servicemanager service
     * @return the servicemanager service for the cache
     */
    public static CacheServiceManagerService servicemanager(Cache<?, ?> cache) {
        return cache.getService(CacheServiceManagerService.class);
    }
}

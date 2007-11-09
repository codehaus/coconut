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
import org.coconut.cache.service.statistics.CacheStatisticsService;

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
 * @version $Id$
 * @see $HeadURL:
 *      https://svn.codehaus.org/coconut/cache/trunk/coconut-cache-api/src/main/java/org/coconut/cache/CacheServices.java $
 */
@SuppressWarnings("unchecked")
public final class CacheServices {

    /** Cannot instantiate. */
    ///CLOVER:OFF
    private CacheServices() {}
    ///CLOVER:ON

    /**
     * Returns the event service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the event service
     * @return the event service for the cache
     * @throws IllegalArgumentException
     *             if no event service is available for the specified cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheEventService<K, V> event(Cache<K, V> cache) {
        return cache.getService(CacheEventService.class);
    }

    /**
     * Returns the eviction service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the eviction service
     * @return the eviction service for the cache
     * @throws IllegalArgumentException
     *             if no eviction service is available for the specified cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheEvictionService<K, V> eviction(Cache<K, V> cache) {
        return cache.getService(CacheEvictionService.class);
    }

    /**
     * Returns the expiration service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the expiration service
     * @return the expiration service for the cache
     * @throws IllegalArgumentException
     *             if no expiration service is available for the specified cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheExpirationService<K, V> expiration(Cache<K, V> cache) {
        return cache.getService(CacheExpirationService.class);
    }

    /**
     * Returns the loading service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the loading service
     * @return the loading service for the cache
     * @throws IllegalArgumentException
     *             if no loading service is available for the specified cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheLoadingService<K, V> loading(Cache<K, V> cache) {
        return cache.getService(CacheLoadingService.class);
    }

    /**
     * Returns the management service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the management service
     * @return the management service for the cache
     * @throws IllegalArgumentException
     *             if no management service is available for the specified cache
     */
    public static CacheManagementService management(Cache<?, ?> cache) {
        return cache.getService(CacheManagementService.class);
    }

    /**
     * Returns the servicemanager service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the servicemanager service
     * @return the servicemanager service for the cache
     * @throws IllegalArgumentException
     *             if no servicemanager service is available for the specified cache
     */
    public static CacheServiceManagerService servicemanager(Cache<?, ?> cache) {
        return cache.getService(CacheServiceManagerService.class);
    }

    /**
     * Returns the statistics service for the specified cache.
     * 
     * @param cache
     *            the cache for which to return the statistics service
     * @return the statistics service for the cache
     * @throws IllegalArgumentException
     *             if no statistics service is available for the specified cache
     */
    public static CacheStatisticsService statistics(Cache<?, ?> cache) {
        return cache.getService(CacheStatisticsService.class);
    }
}

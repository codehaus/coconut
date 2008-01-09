package org.coconut.cache;

import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.memorystore.MemoryStoreService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.service.worker.CacheWorkerService;

/**
 * A utility class to get hold of different cache services in an easy and typesafe manner.
 * For example, the following will return the {@link MemoryStoreService} for a given
 * cache.
 *
 * <pre>
 * Cache&lt;Integer, String&gt; cache = somecache;
 * CacheEvictionService&lt;Integer, String&gt; service = cache.services().eviction();
 * service.trimToSize(10);
 * </pre>
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheServices.java 469 2007-11-17 14:32:25Z kasper $
 */
public class CacheServices<K, V> {
    private final Cache<K, V> cache;

    public CacheServices(Cache<K, V> cache) {
        this.cache = cache;
    }

    /**
     * Returns the event service for the specified cache.
     *
     * @return the event service for the cache
     * @throws IllegalArgumentException
     *             if no event service is available for the specified cache
     */
    public CacheEventService<K, V> event() {
        return getService(CacheEventService.class);
    }

    /**
     * Returns the memory store service for the specified
     *
     * @return the memory store service for the cache
     * @throws IllegalArgumentException
     *             if no eviction service is available for the specified cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public MemoryStoreService<K, V> memoryStore() {
        return getService(MemoryStoreService.class);
    }

    /**
     * Returns the expiration service for the specified
     *
     * @return the expiration service for the cache
     * @throws IllegalArgumentException
     *             if no expiration service is available for the specified cache
     * @param <K>
     *            the type of keys maintained by the specified cache
     * @param <V>
     *            the type of mapped values
     */
    public CacheExpirationService<K, V> expiration() {
        return getService(CacheExpirationService.class);
    }

    /**
     * Returns the loading service for the specified
     *
     * @return the loading service for the cache
     * @throws IllegalArgumentException
     *             if no loading service is available for the specified cache
     */
    public CacheLoadingService<K, V> loading() {
        return getService(CacheLoadingService.class);
    }

    /**
     * Returns the management service for the specified
     *
     * @return the management service for the cache
     * @throws IllegalArgumentException
     *             if no management service is available for the specified cache
     */
    public CacheManagementService management() {
        return getService(CacheManagementService.class);
    }

    /**
     * Returns the servicemanager service for the specified
     *
     * @return the servicemanager service for the cache
     * @throws IllegalArgumentException
     *             if no servicemanager service is available for the specified cache
     */
    public CacheServiceManagerService servicemanager() {
        return getService(CacheServiceManagerService.class);
    }

    /**
     * Returns the statistics service for the specified
     *
     * @return the statistics service for the cache
     * @throws IllegalArgumentException
     *             if no statistics service is available for the specified cache
     */
    public CacheStatisticsService statistics() {
        return getService(CacheStatisticsService.class);
    }

    /**
     * Returns the worker service for the specified
     *
     * @return the worker service for the cache
     * @throws IllegalArgumentException
     *             if no worker service is available for the specified cache
     */
    public CacheWorkerService worker() {
        return getService(CacheWorkerService.class);
    }

    protected <T> T getService(Class<T> serviceType) {
        return cache.getService(serviceType);
    }
}

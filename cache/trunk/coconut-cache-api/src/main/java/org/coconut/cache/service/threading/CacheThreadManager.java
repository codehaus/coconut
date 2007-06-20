package org.coconut.cache.service.threading;

public interface CacheThreadManager {
    /**
     * Returns a CacheServiceThreadManager for the specified service.
     * 
     * @param service
     * @return
     */
    CacheServiceThreadManager createCacheExecutor(Class<?> service);
}

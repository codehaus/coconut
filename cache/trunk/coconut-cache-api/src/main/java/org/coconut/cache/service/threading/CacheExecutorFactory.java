package org.coconut.cache.service.threading;

public interface CacheExecutorFactory {
    CacheServiceExecutor createCacheExecutor(Class<?> service);
}

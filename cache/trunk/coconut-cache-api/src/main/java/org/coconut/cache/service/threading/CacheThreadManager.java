package org.coconut.cache.service.threading;

public interface CacheThreadManager {
    CacheServiceThreadManage createCacheExecutor(Class<?> service);
}

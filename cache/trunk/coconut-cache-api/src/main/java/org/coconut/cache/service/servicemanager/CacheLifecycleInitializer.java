package org.coconut.cache.service.servicemanager;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

public interface CacheLifecycleInitializer {
    CacheConfiguration<?, ?> getCacheConfiguration();

    <T> void registerService(Class<T> clazz, T service);

    Class<? extends Cache> getCacheType();
}

package org.coconut.cache.internal.service.servicemanager;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

public abstract class AbstractCacheServiceManager implements CacheServiceManager {

    abstract RunState getRunState();

    private final Cache<?, ?> cache;

    private volatile CacheConfiguration conf;

    AbstractCacheServiceManager(Cache<?, ?> cache, CacheConfiguration conf) {
        if (cache == null) {
            throw new NullPointerException("cache is null");
        }
        this.cache = cache;
    }

    Cache<?, ?> getCache() {
        return cache;
    }

    public boolean isShutdown() {
        return getRunState().isShutdown();
    }

    public boolean isStarted() {
        return getRunState().isStarted();
    }

    public boolean isTerminated() {
        return getRunState().isTerminated();
    }

    CacheConfiguration getConf() {
        return conf;
    }

    void setConf(CacheConfiguration conf) {
        this.conf = conf;
    }

}

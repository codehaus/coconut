package org.coconut.cache.internal.service.servicemanager;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

public abstract class AbstractCacheServiceManager implements InternalCacheServiceManager {

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

    static enum RunState {
        NOTRUNNING, RUNNING, SHUTDOWN, STOP, TIDYING, TERMINATED, COULD_NOT_START;

        public boolean isStarted() {
            return this != NOTRUNNING && this != COULD_NOT_START;
        }

        public boolean isShutdown() {
            return this != RUNNING && this != NOTRUNNING;
        }

        public boolean isTerminating() {
            return this == SHUTDOWN || this == STOP;
        }

        public boolean isTerminated() {
            return this == TERMINATED || this == COULD_NOT_START;
        }
    }
}

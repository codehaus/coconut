package org.coconut.cache.internal.service.servicemanager;

public abstract class AbstractCacheServiceManager implements CacheServiceManager {

    abstract RunState getRunState();
    
    public boolean isShutdown() {
        return getRunState().isShutdown();
    }

    public boolean isStarted() {
        return getRunState().isStarted();
    }

    public boolean isTerminated() {
        return getRunState().isTerminated();
    }

}

package org.coconut.cache.internal.service.servicemanager;

import java.util.concurrent.Future;

import org.coconut.cache.Cache;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

class ServiceHolder {
    private final boolean isInternal;

    private final CacheLifecycle service;

    private int state;

    volatile Future future;

    ServiceHolder(CacheLifecycle service, boolean isInternal) {
        this.isInternal = isInternal;
        this.service = service;
    }

    CacheLifecycle getService() {
        return service;
    }

    void initialize(CacheLifecycleInitializer cli) {
        state = 1;
        service.initialize(cli);
        state = 2;
    }

    boolean isInitialized() {
        return state >= 2;
    }

    boolean isInternal() {
        return isInternal;
    }

    boolean isStarted() {
        return state >= 4;
    }

    void shutdown() {
        state = 7;
        service.shutdown();
        state = 8;
    }

    void shutdownNow() {
        if (future != null) {
            state = 9;
            service.shutdownNow();
            state = 10;
        }
    }

    void start(CacheServiceManagerService serviceManager) {
        state = 3;
        service.start(serviceManager);
        state = 4;
    }

    void started(Cache<?, ?> c) {
        state = 5;
        service.started(c);
        state = 6;
    }

    void terminated() {
        state = 11;
        service.terminated();
        state = 12;
    }
}

package org.coconut.cache.internal.service.servicemanager;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;

public class SynchronizedCacheServiceManager implements CacheServiceManager {
    private final Inner delegate;

    private final Object mutex;

    SynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf) {
        delegate = new Inner(cache, helper, conf);
        this.mutex = cache;
    }

    class Inner extends UnsynchronizedCacheServiceManager {
        
        public Inner(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
                CacheConfiguration<?, ?> conf) {
            super(cache, helper, conf);
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return super.awaitTermination(timeout, unit);
        }

        @Override
        public void shutdown() {
            super.shutdown();
        }

        @Override
        public void shutdownNow() {
            super.shutdownNow();
        }

        @Override
        public void shutdownServiceAsynchronously(AsynchronousShutdownObject service2) {
            super.shutdownServiceAsynchronously(service2);
        }
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        // TODO impl
        return false;
    }

    public Map<Class<?>, Object> getAllServices() {
        synchronized (mutex) {
            return delegate.getAllServices();
        }
    }

    public <T> T getInternalService(Class<T> type) {
        synchronized (mutex) {
            return delegate.getInternalService(type);
        }
    }

    public <T> T getPublicService(Class<T> type) {
        synchronized (mutex) {
            return delegate.getPublicService(type);
        }
    }

    public <T> T getService(Class<T> type) {
        synchronized (mutex) {
            return delegate.getService(type);
        }
    }

    public boolean hasService(Class<?> type) {
        synchronized (mutex) {
            return delegate.hasService(type);
        }
    }

    public boolean isShutdown() {
        synchronized (mutex) {
            return delegate.isShutdown();
        }
    }

    public boolean isStarted() {
        synchronized (mutex) {
            return delegate.isStarted();
        }
    }

    public boolean isTerminated() {
        synchronized (mutex) {
            return delegate.isTerminated();
        }
    }

    public void lazyStart(boolean failIfShutdown) {
        delegate.lazyStart(failIfShutdown);
    }

    public void prestart() {
        delegate.prestart();
    }

    public void registerService(Class type, Class<? extends AbstractCacheLifecycle> service) {
        synchronized (mutex) {
            delegate.registerService(type, service);
        }
    }

    public void registerServices(Class<? extends AbstractCacheLifecycle>... services) {
        synchronized (mutex) {
            delegate.registerServices(services);
        }
    }

    public void shutdown() {
        synchronized (mutex) {
            delegate.shutdown();
        }
    }

    public void shutdownNow() {
        delegate.shutdownNow();

    }

    public void shutdownServiceAsynchronously(AsynchronousShutdownObject service2) {
        delegate.shutdownServiceAsynchronously(service2);
    }

    public String toString() {
        synchronized (mutex) {
            return delegate.toString();
        }
    }
}

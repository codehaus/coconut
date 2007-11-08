package org.coconut.cache.internal.service.servicemanager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.omg.PortableServer.ThreadPolicy;

public class SynchronizedCacheServiceManager implements CacheServiceManager {
    private final Inner delegate;

    private final Object mutex;

    public SynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        delegate = new Inner(cache, helper, conf,classes);
        this.mutex = cache;
    }

    class Inner extends UnsynchronizedCacheServiceManager {
        private final Object mutex;

        public Inner(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
                CacheConfiguration<?, ?> conf,
                Collection<Class<? extends AbstractCacheLifecycle>> classes) {
            super(cache, helper, conf, classes);
            this.mutex = cache;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
//            ThreadPoolExecutor
            return super.awaitTermination(timeout, unit);
        }

        @Override
        public synchronized void shutdown() {
            super.shutdown();
        }

        @Override
        public synchronized void shutdownNow() {
            shutdown();
            for (ServiceHolder sh : super.internalServices) {
                if (sh.aso != null) {
                    sh.aso.shutdownNow();
                }
            }
            for (ServiceHolder sh : super.externalServices) {
                if (sh.aso != null) {
                    sh.aso.shutdownNow();
                }
            }
        }

        @Override
        public void shutdownServiceAsynchronously(AsynchronousShutdownObject service2) {
            ServiceHolder sh = super.serviceBeingShutdown;
            if (sh == null) {
                throw new IllegalStateException();
            }
            sh.aso = service2;
        }
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
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

    public void shutdown() {
        delegate.shutdown();
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

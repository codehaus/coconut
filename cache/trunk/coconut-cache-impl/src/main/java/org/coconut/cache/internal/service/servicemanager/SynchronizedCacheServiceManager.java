/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;

public class SynchronizedCacheServiceManager implements InternalCacheServiceManager {
    private final Inner delegate;

    private final Object mutex;

    public SynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        delegate = new Inner(cache, helper, conf, classes);
        this.mutex = cache;
    }

    class Inner extends UnsynchronizedCacheServiceManager {
        private final Object mutex;

        private final ReentrantLock mainLock = new ReentrantLock();

        private final List<ServiceHolder> missing = new ArrayList<ServiceHolder>();

        /**
         * Wait condition to support awaitTermination
         */
        private final Condition termination = mainLock.newCondition();

        public Inner(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
                CacheConfiguration<?, ?> conf,
                Collection<Class<? extends AbstractCacheLifecycle>> classes) {
            super(cache, helper, conf, classes);
            this.mutex = cache;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            long nanos = unit.toNanos(timeout);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                for (;;) {
                    if (isTerminated())
                        return true;
                    if (nanos <= 0)
                        return false;
                    nanos = termination.awaitNanos(nanos);
                }
            } finally {
                mainLock.unlock();
            }
        }

        protected void tryTerminate() {
            //TODO fix missing, not threadSafe
            for (Iterator<ServiceHolder> iterator = missing.iterator(); iterator.hasNext();) {
                if (iterator.next().aso.isTerminated()) {
                    iterator.remove();
                }
            }
            if (missing.size() == 0) {
                super.doTerminate();
            } else {
                if (shutdownThread == null) {
                    shutdownThread = new Thread(new Runnable() {
                        public void run() {
                            for (;;) {
                                if (missing.size() == 0) {
                                    doTerminate();
                                    return;
                                }
                                for (ServiceHolder sh : missing) {
                                    try {
                                        sh.aso.awaitTermination(60, TimeUnit.SECONDS);
                                    } catch (InterruptedException e) {
                                        // ignore???
                                    }
                                }
                                tryTerminate();
                            }
                        }
                    }, "cache shutdown thread");
                    shutdownThread.start();
                }
            }
        }

        protected void doTerminate() {
            synchronized (mutex) {
                super.doTerminate();
                shutdownThread = null;
            }
        }

        private Thread shutdownThread;

        @Override
        public void shutdown() {
            synchronized (mutex) {
                super.shutdown();
            }
        }

        @Override
        public synchronized void shutdownNow() {
            synchronized (mutex) {
                shutdown();
                for (ServiceHolder sh : super.services) {
                    if (sh.aso != null && !sh.aso.isTerminated()) {
                        sh.aso.shutdownNow();
                    }
                }
                tryTerminate();
            }
        }

        @Override
        public void shutdownServiceAsynchronously(AsynchronousShutdownObject service2) {
            synchronized (mutex) {
                ServiceHolder sh = super.serviceBeingShutdown;
                if (sh == null) {
                    throw new IllegalStateException();
                }
                sh.aso = service2;
                missing.add(sh);
            }
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

    public boolean lazyStart(boolean failIfShutdown) {
        return delegate.lazyStart(failIfShutdown);
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

    public void shutdown(Throwable cause) {
       
        delegate.shutdown(cause);
    }
}

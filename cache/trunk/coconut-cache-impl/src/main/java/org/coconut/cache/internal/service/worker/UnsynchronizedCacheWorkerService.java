package org.coconut.cache.internal.service.worker;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.internal.service.servicemanager.CacheServiceManager;
import org.coconut.cache.service.servicemanager.AbstractCacheService;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.core.AttributeMap;

public class UnsynchronizedCacheWorkerService extends AbstractCacheService implements
        CacheWorkerService {

    private final SameThreadCacheWorker worker = new SameThreadCacheWorker();

    private final CacheServiceManager csm;

    public UnsynchronizedCacheWorkerService(CacheServiceManager csm) {
        this.csm = csm;
    }

    public CacheWorkerManager getManager() {
        return worker;
    }

     class SameThreadCacheWorker extends CacheWorkerManager {

        private final ExecutorService es = new SameThreadExecutorService();

        @Override
        public ExecutorService getExecutorService(Class<?> service,
                AttributeMap attributes) {
            return es;
        }

        @Override
        public ScheduledExecutorService getScheduledExecutorService(Class<?> service,
                AttributeMap attributes) {
            throw new UnsupportedOperationException();
        }
    }

     class SameThreadExecutorService extends AbstractExecutorService {
        public boolean awaitTermination(long timeout, TimeUnit unit)
                throws InterruptedException {
            return csm.awaitTermination(timeout, unit);
        }

        public boolean isShutdown() {
            return csm.isShutdown();
        }

        public boolean isTerminated() {
            return csm.isTerminated();
        }

        public void shutdown() {
            throw new UnsupportedOperationException("ExecutorService can only be shutdown by the cache");
        }

        public List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException("ExecutorService can only be shutdown by the cache");
        }

        public void execute(Runnable command) {
            command.run();
        }
    }
}

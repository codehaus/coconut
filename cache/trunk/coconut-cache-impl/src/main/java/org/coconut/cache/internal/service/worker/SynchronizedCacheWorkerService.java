package org.coconut.cache.internal.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.cache.internal.service.servicemanager.CacheServiceManager;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.core.AttributeMap;

public class SynchronizedCacheWorkerService extends AbstractCacheWorkerService {

    private final SameThreadCacheWorker worker = new SameThreadCacheWorker();

    public CacheWorkerManager getManager() {
        return worker;
    }

    class SameThreadCacheWorker extends CacheWorkerManager {

        private final ExecutorService es;

        SameThreadCacheWorker() {
            es = Executors.newCachedThreadPool(new WorkerUtils.DefaultThreadFactory(
                    "cache"));
        }

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

}

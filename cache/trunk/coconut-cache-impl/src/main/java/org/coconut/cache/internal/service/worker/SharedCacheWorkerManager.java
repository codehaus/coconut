package org.coconut.cache.internal.service.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.worker.CacheWorkerManager;

public class SharedCacheWorkerManager extends CacheWorkerManager {

    private final ExecutorService es;

    private final ScheduledExecutorService ses;

    SharedCacheWorkerManager(String cacheName) {
        es = Executors.newCachedThreadPool(new WorkerUtils.DefaultThreadFactory("cache-"
                + cacheName));
        ses = Executors.newScheduledThreadPool(5, new WorkerUtils.DefaultThreadFactory("cache-"
                + cacheName));
    }

    /** {@inheritDoc} */
    @Override
    public ExecutorService getExecutorService(Object service, AttributeMap attributes) {
        return es;
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService(Object service,
            AttributeMap attributes) {
        return ses;
    }

    @Override
    public void shutdown(Shutdown shutdown) {
        es.shutdown();
        ses.shutdown();
        shutdown.shutdownAsynchronously(new Callable() {
            public Object call() {
                for (;;) {
                    try {
                        es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                        ses.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                        return Void.TYPE;
                    } catch (InterruptedException ie) {
                        es.shutdownNow();
                        ses.shutdownNow();
                    }
                }
            }
        });
    }
}

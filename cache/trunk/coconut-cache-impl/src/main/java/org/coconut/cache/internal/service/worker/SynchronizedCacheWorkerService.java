/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.worker;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.internal.service.servicemanager.InternalCacheServiceManager;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.worker.CacheWorkerConfiguration;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.cache.service.worker.CacheWorkerService;

public class SynchronizedCacheWorkerService extends AbstractCacheWorkerService implements
        CompositeService, CacheWorkerService {

    private final String cacheName;

    private final CacheWorkerManager worker;

    public SynchronizedCacheWorkerService(String cacheName, CacheWorkerConfiguration conf,
            InternalCacheServiceManager csm) {
        this.cacheName = cacheName;
        if (conf.getWorkerManager() == null) {
            worker = new SameThreadCacheWorker();
        } else {
            worker = conf.getWorkerManager();
        }
    }

    /** {@inheritDoc} */
    public Collection<?> getChildServices() {
        return Arrays.asList(worker);
    }

    /** {@inheritDoc} */
    /** {@inheritDoc} */
    public CacheWorkerManager getManager() {
        return worker;
    }

    /** {@inheritDoc} */
    public ScheduledExecutorService getScheduledExecutorService(Object service) {
        return getScheduledExecutorService(service, Attributes.EMPTY_MAP);
    }

    /** {@inheritDoc} */
    public ScheduledExecutorService getScheduledExecutorService(Object service,
            AttributeMap attributes) {
        return worker.getScheduledExecutorService(service, attributes);
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheWorkerService.class, this);
    }

    class SameThreadCacheWorker extends CacheWorkerManager {

        final ExecutorService es;

        final ScheduledExecutorService ses;

        SameThreadCacheWorker() {
            es = Executors.newCachedThreadPool(new WorkerUtils.DefaultThreadFactory("cache-"
                    + cacheName));
            ses = Executors.newScheduledThreadPool(5, new WorkerUtils.DefaultThreadFactory("cache-"
                    + cacheName));
        }

        /** {@inheritDoc} */
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
                public Object call() throws InterruptedException {
                    try {
                        es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                        ses.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                    } catch (InterruptedException ie) {
                        es.shutdownNow();
                        ses.shutdownNow();
                        throw ie;
                    }
                    return Void.TYPE;
                }
            });
        }
// @Override
// public void shutdownNow() {
// es.shutdownNow();
// ses.shutdownNow();
// }
    }
}

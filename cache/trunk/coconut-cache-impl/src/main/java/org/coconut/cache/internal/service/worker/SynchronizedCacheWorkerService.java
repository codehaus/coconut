/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheServices;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.internal.service.servicemanager.InternalCacheServiceManager;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.service.worker.CacheWorkerConfiguration;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.cache.service.worker.CacheWorkerService;
import org.coconut.core.AttributeMap;

public class SynchronizedCacheWorkerService extends AbstractCacheWorkerService implements
        CompositeService, CacheWorkerService {

    private final String cacheName;

    private final CacheWorkerManager worker;

    private final InternalCacheServiceManager csm;

    public SynchronizedCacheWorkerService(String cacheName, CacheWorkerConfiguration conf,
            InternalCacheServiceManager csm) {
        this.csm = csm;
        this.cacheName = cacheName;
        if (conf.getWorkerManager() == null) {
            worker = new SameThreadCacheWorker();
        } else {
            worker = conf.getWorkerManager();
        }
    }

    public CacheWorkerManager getManager() {
        return worker;
    }
    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycleInitializer cli) {
        cli.registerService(CacheWorkerService.class, this);
    }
    class SameThreadCacheWorker extends CacheWorkerManager {

        final ExecutorService es;

        final ScheduledExecutorService ses;

        SameThreadCacheWorker() {
            es = Executors.newCachedThreadPool(new WorkerUtils.DefaultThreadFactory("cache-"
                    + cacheName));
            ses = Executors.newScheduledThreadPool(5);
        }

        public ExecutorService getExecutorService(Object service, AttributeMap attributes) {
            return es;
        }

        @Override
        public ScheduledExecutorService getScheduledExecutorService(Object service,
                AttributeMap attributes) {
            return ses;
        }

        @Override
        public void shutdown() {
            es.shutdown();
            ses.shutdown();
            csm.getService(CacheServiceManagerService.class).shutdownServiceAsynchronously(
                    new AsynchronousShutdownObject() {
                        public boolean awaitTermination(long timeout, TimeUnit unit)
                                throws InterruptedException {
                            es.awaitTermination(timeout, unit);
                            return ses.awaitTermination(timeout, unit);
                        }

                        public boolean isTerminated() {
                            return es.isTerminated() && ses.isTerminated();
                        }

                        public void shutdownNow() {
                            es.shutdownNow();
                            ses.shutdownNow();
                        }
                    });
        }
    }

    public Collection<?> getChildServices() {
        return Arrays.asList(worker);
    }

    public ScheduledExecutorService getScheduledExecutorService(Object service) {
        return worker.getScheduledExecutorService(service, null);
    }

}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.cache.internal.service.servicemanager.InternalCacheServiceManager;
import org.coconut.cache.service.worker.CacheWorkerConfiguration;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.core.AttributeMap;

public class SynchronizedCacheWorkerService extends AbstractCacheWorkerService {

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

    class SameThreadCacheWorker extends CacheWorkerManager {

        private final ExecutorService es;

        SameThreadCacheWorker() {
            es = Executors.newCachedThreadPool(new WorkerUtils.DefaultThreadFactory("cache-"
                    + cacheName));
        }

        @Override
        public ExecutorService getExecutorService(Class<?> service, AttributeMap attributes) {
            return es;
        }

        @Override
        public ScheduledExecutorService getScheduledExecutorService(Class<?> service,
                AttributeMap attributes) {
            throw new UnsupportedOperationException();
        }
    }

}

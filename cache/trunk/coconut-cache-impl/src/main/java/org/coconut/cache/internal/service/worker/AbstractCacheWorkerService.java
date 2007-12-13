/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.attribute.Attributes;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.worker.CacheWorkerManager;

public abstract class AbstractCacheWorkerService extends AbstractCacheLifecycle implements
        InternalCacheWorkerService {

    /**
     * Returns a ExecutorService that can be used to asynchronously execute tasks for the
     * specified service.
     * 
     * @param service
     *            the service to return an ExecutorService for
     * @return a ExecutorService that can be used to asynchronously execute tasks for the
     *         specified service
     */
    public final ExecutorService getExecutorService(Class<?> service) {
        return getManager().getExecutorService(service, Attributes.EMPTY_MAP);
    }

    public final ScheduledExecutorService getScheduledExecutorService(Class<?> service) {
        return getManager().getScheduledExecutorService(service, Attributes.EMPTY_MAP);
    }
    
    abstract CacheWorkerManager getManager();
    
    public String toString() {
        return "Worker Service";
    }
}

package org.coconut.cache.internal.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.core.AttributeMaps;

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
        return getManager().getExecutorService(service, AttributeMaps.EMPTY_MAP);
    }

    public ScheduledExecutorService getScheduledExecutorService(Class<?> service) {
        return getManager().getScheduledExecutorService(service, AttributeMaps.EMPTY_MAP);
    }

}

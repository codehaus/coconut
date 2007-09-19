package org.coconut.cache.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;

/**
 * This class is reponsible for creating {@link ExecutorService}'s that are used to
 * asynchronously execute tasks within the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class CacheWorkerManager {

    /**
     * Returns a ExecutorService that can be used to asynchronously execute tasks for the
     * specified service.
     * 
     * @param service
     *            the service to return an ExecutorService for
     * @return a ExecutorService that can be used to asynchronously execute tasks for the
     *         specified service
     */
    public ExecutorService getExecutorService(Class<?> service) {
        return getExecutorService(service, AttributeMaps.EMPTY_MAP);
    }

    /**
     * Returns a ExecutorService that can be used to asynchronously execute tasks for the
     * specified service.
     * 
     * @param service
     *            the service for which an ExecutorService should be returned
     * @param properties
     *            a list of properties that is passed to the concrete implementation of
     *            the cache worker manager
     * @return a ExecutorService that can be used to asynchronously execute tasks for the
     *         specified service
     */
    public abstract ExecutorService getExecutorService(Class<?> service,
            AttributeMap attributes);

    public ScheduledExecutorService getScheduledExecutorService(Class<?> service) {
        return getScheduledExecutorService(service, AttributeMaps.EMPTY_MAP);
    }

    public abstract ScheduledExecutorService getScheduledExecutorService(
            Class<?> service, AttributeMap attributes);

}

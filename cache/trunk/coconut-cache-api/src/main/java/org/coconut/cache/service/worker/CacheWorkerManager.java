package org.coconut.cache.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.core.AttributeMap;

/**
 * This class is reponsible for creating {@link ExecutorService}'s that are used to
 * asynchronously execute tasks within the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class CacheWorkerManager {

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

    public abstract ScheduledExecutorService getScheduledExecutorService(
            Class<?> service, AttributeMap attributes);

}

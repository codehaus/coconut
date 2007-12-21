/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;

/**
 * This is the main interface for controlling the worker service of a cache at runtime.
 * <p>
 * An instance of this interface can be retrieved by using {@link Cache#getService(Class)}
 * to look it up.
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheWorkerService worker = c.getService(CacheWorkerService.class);
 * worker....
 * </pre>
 * 
 * Or by using {@link CacheServicesOld}
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheWorkerService worker = CacheServices.worker(c);
 * worker...
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheStatisticsService.java 430 2007-11-11 14:50:09Z kasper $
 */
public interface CacheWorkerService {

    /**
     * Returns a ExecutorService for the specified service.
     * 
     * @param service
     *            the service that needs a ExecutorService
     * @return a ExecutorService for the specified service
     */
    ExecutorService getExecutorService(Object service);

    /**
     * Returns a ExecutorService for the specified service.
     * 
     * @param service
     *            the service that needs a ExecutorService
     * @param attributes
     *            a map of attributes that can be used by the work service to determind
     *            what types of executor services that should be created
     * @return a ExecutorService for the specified service
     */
    ExecutorService getExecutorService(Object service, AttributeMap attributes);

    /**
     * Returns a ScheduledExecutorService for the specified service.
     * 
     * @param service
     *            the service that needs a ScheduledExecutorService
     * @return a ScheduledExecutorService for the specified service
     */
    ScheduledExecutorService getScheduledExecutorService(Object service);

    /**
     * Returns a ScheduledExecutorService for the specified service.
     * 
     * @param service
     *            the service that needs a ScheduledExecutorService
     * @param attributes
     *            a map of attributes that can be used by the work service to determind
     *            what types of executor services that should be created
     * @return a ScheduledExecutorService for the specified service
     */
    ScheduledExecutorService getScheduledExecutorService(Object service, AttributeMap attributes);
}

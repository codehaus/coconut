/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.core.AttributeMap;

/**
 * This class is reponsible for creating {@link ExecutorService}'s that are used to
 * asynchronously execute tasks within the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class CacheWorkerManager extends AbstractCacheLifecycle {

    /**
     * Returns a ExecutorService that can be used to asynchronously execute tasks for the
     * specified service.
     * 
     * @param service
     *            the service for which an ExecutorService should be returned
     * @param attributes
     *            a map of attributes that is passed to the concrete implementation of the
     *            cache worker manager
     * @return a ExecutorService that can be used to asynchronously execute tasks for the
     *         specified service
     */
    public abstract ExecutorService getExecutorService(Object service, AttributeMap attributes);

    /**
     * Returns a ScheduledExecutorService that can be used to asynchronously schedule
     * tasks for the specified service.
     * 
     * @param service
     *            the service for which an ScheduledExecutorService should be returned
     * @param attributes
     *            a map of attributes that is passed to the concrete implementation of the
     *            cache worker manager
     * @return a ScheduledExecutorService that can be used to asynchronously schedule
     *         tasks for the specified service
     */
    public abstract ScheduledExecutorService getScheduledExecutorService(Object service,
            AttributeMap attributes);

}

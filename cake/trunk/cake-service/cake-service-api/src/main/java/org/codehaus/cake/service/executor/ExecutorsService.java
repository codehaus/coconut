/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.service.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import jsr166y.forkjoin.ForkJoinExecutor;

import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.service.ServiceManager;

/**
 * This is the main interface for scheduling and executing tasks at runtime.
 * <p>
 * An instance of this interface can be retrieved by using
 * {@link ServiceManager#getService(Class)} to look it up.
 * 
 * <pre>
 * ServiceManager&lt;?, ?&gt; sm = someContainer;
 * ExecutorManagerService executorManager = sm.getService(ExecutorManagerService.class);
 * executorManager.getExecutor(someService)
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheStatisticsService.java 430 2007-11-11 14:50:09Z kasper $
 */
public interface ExecutorsService {

    /**
     * Returns a {@link ExecutorService} for the specified service.
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
     *            a map of attributes that can be used to determind whuch type of executor
     *            service should be returned
     * @return a scheduled for the specified service
     */
    ExecutorService getExecutorService(Object service, AttributeMap attributes);

    /**
     * Returns a {@link ForkJoinExecutor} for the specified service.
     * 
     * @param service
     *            the service that needs a ForkJoinExecutor
     * @return a ForkJoinExecutor for the specified service
     */
    ForkJoinExecutor getForkJoinExecutor(Object service);

    /**
     * Returns a {@link ForkJoinExecutor} for the specified service.
     * 
     * @param service
     *            the service that needs a ForkJoinExecutor
     * @param attributes
     *            a map of attributes that can be used to determind whuch type of forkjoin
     *            executor should be returned
     * @return a ForkJoinExecutor for the specified service
     */
    ForkJoinExecutor getForkJoinExecutor(Object service, AttributeMap attributes);

    /**
     * Returns a ScheduledExecutorService for the specified service.
     * 
     * @param service
     *            the service that needs a ScheduledExecutorService
     * @return a ScheduledExecutorService for the specified service
     */
    ScheduledExecutorService getScheduledExecutorService(Object service);

    /**
     * Returns a {@link ScheduledExecutorService} for the specified service.
     * 
     * @param service
     *            the service that needs a ScheduledExecutorService
     * @param attributes
     *            a map of attributes that can be used to determind whuch type of
     *            scheduled executor service should be returned
     * @return a ScheduledExecutorService for the specified service
     */
    ScheduledExecutorService getScheduledExecutorService(Object service, AttributeMap attributes);
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

/**
 * Defines additional methods to {@link CacheServiceManagerService}. This service is only
 * available for internal cache services.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface InternalCacheServiceManager extends CacheServiceManagerService {
    
    /**
     * Blocks until all tasks have completed execution after a shutdown request, or the
     * timeout occurs, or the current thread is interrupted, whichever happens first.
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return <tt>true</tt> if this cache terminated and <tt>false</tt> if the
     *         timeout elapsed before termination
     * @throws InterruptedException
     *             if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Returns <tt>true</tt> if the cache has been shut down.
     * 
     * @return <tt>true</tt> if the cache has been shut down
     */
    boolean isShutdown();

    /**
     * Returns <tt>true</tt> if the cache has been started.
     * 
     * @return <tt>true</tt> if the cache has been started
     */
    boolean isStarted();

    /**
     * Returns <tt>true</tt> if all tasks have completed following shut down. Note that
     * <tt>isTerminated</tt> is never <tt>true</tt> unless either <tt>shutdown</tt>
     * or <tt>shutdownNow</tt> was called first.
     * 
     * @return <tt>true</tt> if all tasks have completed following shut down
     */
    boolean isTerminated();

    <T> T getInternalService(Class<T> type);

    <T> T getServiceFromCache(Class<T> type);

    boolean lazyStart(boolean failIfShutdown);

    /**
     * Initiates an orderly shutdown of the cache. In which previously submitted tasks are
     * executed, but no new tasks will be accepted. Invocation has no additional effect if
     * already shut down.
     * 
     * @throws SecurityException
     *             if a security manager exists and shutting down this Cache may
     *             manipulate threads that the caller is not permitted to modify because
     *             it does not hold {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>,
     *             or the security manager's <tt>checkAccess</tt> method denies access.
     */
    void shutdown();

    //void shutdown(Throwable cause);

    void shutdownNow();
}

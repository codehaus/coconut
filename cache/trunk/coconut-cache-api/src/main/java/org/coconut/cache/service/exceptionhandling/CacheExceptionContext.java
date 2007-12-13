/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.cache.Cache;
import org.coconut.core.Logger;

/**
 * A CacheExceptionContext is provided by the cache to all of the exception-handle methods
 * defined in {@link CacheExceptionHandler} whenever an exceptional state is raised.
 * <p>
 * Users will most likely never need to create instances of this class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class CacheExceptionContext<K, V> {

    /**
     * Returns the cache in which the failure occured.
     * 
     * @return the cache in which the failure occured
     */
    public abstract Cache<K, V> getCache();

    /**
     * Returns the default configured logger for handling exceptions for the cache in
     * which this failure occured.
     * 
     * @return the default configured logger for handling exceptions for the cache in
     *         which this failure occured
     */
    public abstract Logger defaultLogger();

    /**
     * Returns the cause of the failure.
     * 
     * @return the cause of the failure
     */
    public abstract Throwable getCause();
// /**
// * Shutdowns the cache. Either we should only take a runtime exception, or else the
// * cache needs to wrap the cause in a runtime exception. Well the later is more
// * flexible, and not that tedios to implement
// *
// * @param cause the cause of the failure
// */
// public abstract void shutdownCache(Throwable cause);
}

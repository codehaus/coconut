/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.cache.Cache;
import org.coconut.core.Logger;

/**
 * A CacheExceptionContext is provided by the cache to all of the exception-handle methods
 * defined in {@link CacheExceptionHandler} whenever any exception occures.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
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
}

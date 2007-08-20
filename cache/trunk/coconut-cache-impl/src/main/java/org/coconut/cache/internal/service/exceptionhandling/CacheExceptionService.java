/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.exceptionhandling;

import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;

/**
 * An exception service available on runtime.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public interface CacheExceptionService<K, V> {
    /**
     * Returns the CacheExceptionHandler configured for this cache.
     * 
     * @return the CacheExceptionHandler configured for this cache
     */
    CacheExceptionHandler<K, V> getExceptionHandler();

    /**
     * Creates and returns a new CacheExceptionContext.
     * 
     * @return a new CacheExceptionContext.
     */
    CacheExceptionContext<K, V> createContext();
}

package org.coconut.cache.internal.service.exceptionhandling;

import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;

/**
 * An exception service available on runtime.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheExceptionService<K, V> {
    CacheExceptionHandler<K, V> getExceptionHandler();

    /**
     * Create a new CacheExceptionContext.
     */
    CacheExceptionContext<K, V> createContext();
}

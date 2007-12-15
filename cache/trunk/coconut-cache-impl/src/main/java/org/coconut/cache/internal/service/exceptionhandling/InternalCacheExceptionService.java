/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.exceptionhandling;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.core.Logger;

/**
 * An exception service available as an internal service at runtime.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public interface InternalCacheExceptionService<K, V> {

    /**
     * Creates a new CacheExceptionContext with no
     * {@link CacheExceptionContext#getCause() cause}.
     * 
     * @return the newly created CacheExceptionContext
     */
    CacheExceptionContext<K, V> createContext(String message);

    CacheExceptionContext<K, V> createContext(Throwable cause, String message);

    void fatalRuntimeException(String msg);

    void fatalRuntimeException(String msg, RuntimeException cause);

    void initializationFailed(CacheConfiguration<K, V> configuration, String cacheName,
            Class<? extends Cache> cacheType, CacheLifecycle service, RuntimeException cause);

    /**
     * Returns the CacheExceptionHandler configured for this cache.
     * 
     * @return the CacheExceptionHandler configured for this cache
     */
    CacheExceptionHandler<K, V> getHandler();
}

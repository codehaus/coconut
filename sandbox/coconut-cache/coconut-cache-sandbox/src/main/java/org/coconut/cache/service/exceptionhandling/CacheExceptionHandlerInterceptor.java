/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.cache.Cache;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExceptionHandlerInterceptor<K, V> extends
        AbstractCacheExceptionHandler<K, V> {
    private final AbstractCacheExceptionHandler<K, V> next;

    public CacheExceptionHandlerInterceptor(AbstractCacheExceptionHandler<K, V> handler) {
        if (handler == null) {
            throw new NullPointerException("handler is null");
        }
        this.next = handler;
    }

    /**
     * @see org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler#loadAllFailed(org.coconut.cache.service.loading.CacheLoader,
     *      java.util.Map, boolean, java.lang.Throwable)
     */
    @Override
    public V loadFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader, K key,
            AttributeMap map, boolean isAsync, Throwable cause) {
        preloadFailed(cache, loader, key, map, isAsync, cause);
        V v = next.loadFailed(cache, loader, key, map, isAsync, cause);
        return postLoadFailed(cache, loader, key, map, isAsync, cause, v);
    }

    protected void preloadFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader,
            K key, AttributeMap map, boolean isAsync, Throwable cause) {
    // ignore
    }

    protected V postLoadFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader,
            K key, AttributeMap map, boolean isAsync, Throwable cause, V result) {
        return result;
    }

    /**
     * @see org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler#warning(java.lang.String)
     */
    @Override
    public void warning(String warning) {
        preWarning(warning);
        next.warning(warning);
        postWarning(warning);
    }

    /**
     * @see org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler#warning(java.lang.String)
     */
    protected void preWarning(String warning) {
    // ignore
    }

    /**
     * @see org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler#warning(java.lang.String)
     */
    protected void postWarning(String warning) {
    // ignore
    }

}

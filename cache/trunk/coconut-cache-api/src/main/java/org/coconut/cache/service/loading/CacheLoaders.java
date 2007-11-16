/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;

/**
 * Various {@link CacheLoader} utility classes and functions.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class CacheLoaders {

    /**
     * Returns a Runnable that when invoked will call the
     * {@link CacheLoadingService#load(Object)} method on the specified cache with the
     * specified key as parameter.
     * 
     * @param fromCache
     *            the cache from which to retrieve the CacheLoadingService and invoke the
     *            load method on
     * @param key
     *            the key to load
     * @return a Runnable that when invoked will call the
     *         {@link CacheLoadingService#forceLoadAll()} method on the specified cache
     */
    public static <K> Runnable runLoad(Cache<K, ?> fromCache, final K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        final CacheLoadingService<K, ?> service = CacheServices.loading(fromCache);
        return new Runnable() {
            public void run() {
                service.load(key);
            }
        };
    }

    /**
     * Returns a Runnable that when invoked will call the
     * {@link CacheLoadingService#forceLoadAll()} method on the specified cache.
     * 
     * @param fromCache
     *            the cache from which to retrieve the CacheLoadingService and invoke the
     *            forceLoadAll method on
     * @return a Runnable that when invoked will call the
     *         {@link CacheLoadingService#forceLoadAll()} method on the specified cache
     */
    public static Runnable runForceLoadAll(Cache<?, ?> fromCache) {
        final CacheLoadingService<?, ?> service = CacheServices.loading(fromCache);
        return new Runnable() {
            public void run() {
                service.forceLoadAll();
            }
        };
    }

    /**
     * Returns a Runnable that when invoked will call the
     * {@link CacheLoadingService#loadAll()} method on the specified cache.
     * 
     * @param fromCache
     *            the cache from which to retrieve the CacheLoadingService and invoke the
     *            loadAll method on
     * @return a Runnable that when invoked will call the
     *         {@link CacheLoadingService#loadAll()} method on the specified cache
     */
    public static Runnable runLoadAll(Cache<?, ?> fromCache) {
        final CacheLoadingService<?, ?> service = CacheServices.loading(fromCache);
        return new Runnable() {
            public void run() {
                service.loadAll();
            }
        };
    }
}

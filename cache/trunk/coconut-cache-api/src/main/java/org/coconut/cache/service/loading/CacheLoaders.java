/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;

/**
 * Various {@link CacheLoadingService}} and {@link CacheLoader} utility classes and
 * functions.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class CacheLoaders {
    /** Cannot instantiate. */
    // /CLOVER:OFF
    private CacheLoaders() {}

    // /CLOVER:ON
    /**
     * This method converts the specified cache to a cache loader. Calls to
     * {@link CacheLoader#load(Object, AttributeMap)} will be converted to calls to
     * {@link Cache#get(Object)}. Calls to
     * {@link CacheLoader#loadAll(java.util.Collection)} will be converted to calls to
     * {@link Cache#getAll(java.util.Collection)}.
     * 
     * @param cache
     *            the cache to load entries from
     * @return a cache loader that can load values from another cache
     * @throws NullPointerException
     *             if the specified cache is <code>null</code>
     * @param <K>
     *            the type of keys used for loading values
     * @param <V>
     *            the type of values that are loaded
     */
    public static <K, V> CacheLoader<K, V> cacheAsCacheLoader(Cache<K, V> cache) {
        return new CacheAsCacheLoader<K, V>(cache);
    }

    /**
     * Returns a CacheLoader that will return <tt>null</tt> as a result for any key.
     * 
     * @return a CacheLoader that returns <tt>null</tt> for any key.
     * @param <K>
     *            the type of keys used for loading values
     * @param <V>
     *            the type of values that are loaded
     */
    public static <K, V> CacheLoader<K, V> nullLoader() {
        return new NullLoader<K, V>();
    }

    /**
     * Returns a Runnable that when invoked will call the
     * {@link CacheLoadingService#forceLoad(Object)} method on the specified cache with
     * the specified key as parameter. This can, for example, be used to schedule the
     * reload of a specific element.
     * 
     * @param cache
     *            the cache from which to retrieve the CacheLoadingService and invoke the
     *            forceLoad method on
     * @param key
     *            the key to load
     * @return a Runnable that when invoked will call the
     *         {@link CacheLoadingService#forceLoad(Object)} method on the specified cache
     *         with the specified key as parameter
     * @param <K>
     *            the type of keys used for loading values
     */
    public static <K> Runnable runForceLoad(Cache<K, ?> cache, final K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        final CacheLoadingService<K, ?> service = CacheServices.loading(cache);
        return new Runnable() {
            public void run() {
                service.forceLoad(key);
            }
        };
    }

    /**
     * Returns a Runnable that when invoked will call the
     * {@link CacheLoadingService#forceLoadAll()} method on the specified cache.
     * 
     * @param cache
     *            the cache from which to retrieve the CacheLoadingService and invoke the
     *            forceLoadAll method on
     * @return a Runnable that when invoked will call the
     *         {@link CacheLoadingService#forceLoadAll()} method on the specified cache
     */
    public static Runnable runForceLoadAll(Cache<?, ?> cache) {
        final CacheLoadingService<?, ?> service = CacheServices.loading(cache);
        return new Runnable() {
            public void run() {
                service.forceLoadAll();
            }
        };
    }

    /**
     * Returns a Runnable that when invoked will call the
     * {@link CacheLoadingService#load(Object)} method on the specified cache with the
     * specified key as parameter.
     * 
     * @param cache
     *            the cache from which to retrieve the CacheLoadingService and invoke the
     *            load method on
     * @param key
     *            the key to load
     * @return a Runnable that when invoked will call the
     *         {@link CacheLoadingService#load(Object)} method on the specified cache with
     *         the specified key as parameter
     * @param <K>
     *            the type of keys used for loading values
     */
    public static <K> Runnable runLoad(Cache<K, ?> cache, final K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        final CacheLoadingService<K, ?> service = CacheServices.loading(cache);
        return new Runnable() {
            public void run() {
                service.load(key);
            }
        };
    }

    /**
     * Returns a Runnable that when invoked will call the
     * {@link CacheLoadingService#loadAll()} method on the specified cache.
     * 
     * @param cache
     *            the cache from which to retrieve the CacheLoadingService and invoke the
     *            loadAll method on
     * @return a Runnable that when invoked will call the
     *         {@link CacheLoadingService#loadAll()} method on the specified cache
     */
    public static Runnable runLoadAll(Cache<?, ?> cache) {
        final CacheLoadingService<?, ?> service = CacheServices.loading(cache);
        return new Runnable() {
            public void run() {
                service.loadAll();
            }
        };
    }

    /**
     * This class wraps a cache in such a way that it can be used as a cache loader for
     * another cache.
     */
    final static class CacheAsCacheLoader<K, V> extends AbstractCacheLoader<K, V> {

        /** The cache used as a cache loader. */
        private final Cache<K, V> cache;

        /**
         * Creates a new CacheAsCacheLoader.
         * 
         * @param cache
         *            the cache to load values from
         * @throws NullPointerException
         *             if the specified cache is <code>null</code>
         */
        public CacheAsCacheLoader(Cache<K, V> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /** {@inheritDoc} */
        public V load(K key, AttributeMap attributes) {
            return cache.get(key);
        }
    }

    /**
     * A loader that always return <code>null</code> for any key.
     * 
     * @param <K>
     *            the type of keys used for loading values
     * @param <V>
     *            the type of values that are loaded
     */
    final static class NullLoader<K, V> extends AbstractCacheLoader<K, V> {
        /** {@inheritDoc} */
        public V load(K key, AttributeMap attributes) {
            return null;
        }
    }
}

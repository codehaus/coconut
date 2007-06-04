/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AsyncCacheLoader;
import org.coconut.core.Callback;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface LoaderEntryService<K, V> {

    /**
     * Asynchronously loads a CacheEntry for the given and calls AbstractCache.  
     * @param key
     *            the key for which to load an entry
     * @return a Future representing pending completion of the loading, and
     *         whose <tt>get()</tt> method will return <tt>null</tt> upon
     *         completion.
     */
    Future<?> asyncLoad(K key);

    Future<?> asyncLoad(K key, Callback<CacheEntry<K, V>> eh);

    Future<?> asyncLoadAll(Collection<? extends K> keys);

    Future<?> asyncLoadAll(Collection<? extends K> keys,
            Callback<Map<K, CacheEntry<K, V>>> p);

    CacheEntry<K, V> load(K key);

    Map<K, CacheEntry<K, V>> loadAll(Collection<? extends K> keys);
    
    
    

//    public static <K, V> AsyncCacheLoader<K, V> noAsyncLoad(CacheLoader<K, V> loader) {
//        return new NoAsyncLoad<K, V>(loader);
//    }
//
//    static class NoAsyncLoad<K, V> implements AsyncCacheLoader<K, V> {
//
//        private final CacheLoader<K, V> loader;
//
//        /**
//         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object,
//         *      org.coconut.core.Callback)
//         */
//        public Future<?> asyncLoad(K key, Callback<V> c) {
//            throw new UnsupportedOperationException(
//                    "operation not supported, A AsyncCacheLoader has not been specfied");
//        }
//
//        /**
//         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoadAll(java.util.Collection,
//         *      org.coconut.core.Callback)
//         */
//        public Future<?> asyncLoadAll(Collection<? extends K> keys, Callback<Map<K, V>> c) {
//            throw new UnsupportedOperationException(
//                    "operation not supported, A AsyncCacheLoader has not been specfied");
//        }
//
//        /**
//         * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
//         */
//        public V load(K key) throws Exception {
//            return loader.load(key);
//        }
//
//        /**
//         * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
//         */
//        public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
//            return loader.loadAll(keys);
//        }
//
//        /**
//         * @param loader
//         */
//        public NoAsyncLoad(final CacheLoader<K, V> loader) {
//            super();
//            this.loader = loader;
//        }
//
//    }

}


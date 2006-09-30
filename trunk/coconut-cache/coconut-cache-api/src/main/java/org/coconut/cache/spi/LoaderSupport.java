/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.Caches;
import org.coconut.core.Callback;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoaderSupport {

    /**
     * This class can use in for asynchronously loading a value into the cache.
     */
    @SuppressWarnings("hiding")
    static class LoadValueCallable<K, V> implements Callable<V> {
        /* The cache to load the value from */
        private final Callback<V> c;

        private CacheErrorHandler<K, V> errorHandler;

        /* The key for which a value should be loaded */
        private final K key;

        private final CacheLoader<? super K, ? extends V> loader;

        /**
         * Constructs a new LoadValueCallable.
         * 
         * @param c
         *            The cache to load the value from
         * @param key
         *            The key for which a value should be loaded
         */
        LoadValueCallable(CacheLoader<? super K, ? extends V> loader, K key,
                Callback<V> c, CacheErrorHandler<K, V> errorHandler) {
            this.loader = loader;
            this.c = c;
            this.key = key;
            this.errorHandler = errorHandler;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() throws Exception {
            V v;
            try {
                v = loader.load(key);
            } catch (Exception e) {
                v = errorHandler.asyncLoadingFailed(key, e);
                if (v == null) {
                    c.failed(e);
                    throw e;
                }
            }
            c.completed(v);
            return null; // current contract is to only return null
        }
    }

    // static <K, V> CacheLoader<K, CacheEntry<K, V>>
    // getLoader(CacheConfiguration<K, V> cc) {
    // CacheLoader<K, V> loader = (CacheLoader<K, V>) cc.backend().getLoader();
    // }

    /**
     * This class can use in for asynchronously loading a value into the cache.
     */
    @SuppressWarnings("hiding")
    static class LoadValuesCallable<K, V> implements Callable<V> {
        /* The cache to load the value from */
        private final Callback<Map<K, V>> c;

        private CacheErrorHandler<K, V> errorHandler;

        /* The key for which a value should be loaded */
        private final Collection<? extends K> keys;

        private final CacheLoader<? super K, ? extends V> loader;

        /**
         * Constructs a new LoadValueCallable.
         * 
         * @param c
         *            The cache to load the value from
         * @param key
         *            The key for which a value should be loaded
         */
        LoadValuesCallable(CacheLoader<? super K, ? extends V> loader,
                Collection<? extends K> keys, Callback<Map<K, V>> c,
                CacheErrorHandler<K, V> errorHandler) {
            this.loader = loader;
            this.c = c;
            this.keys = keys;
            this.errorHandler = errorHandler;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() throws Exception {
            Map<K, V> values;

            try {
                values = (Map<K, V>) loader.loadAll(keys);
            } catch (Exception e) {
                values = errorHandler.asyncLoadingAllFailed(keys, e);
                if (values == null) {
                    c.failed(e);
                    throw e;
                }
            }
            c.completed(values);
            return null; // current contract is to only return null
        }
    }

    static class SameThreadExecutor implements Executor, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -6365439666830575122L;

        /**
         * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
         */
        public void execute(Runnable command) {
            command.run();
        }

    }

    public static final Executor SAME_THREAD_EXECUTOR = new SameThreadExecutor();

    public static <K, V> CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> getLoader(
            CacheConfiguration<K, V> conf) {
        if (conf.backend().getLoader() != null) {
            return Caches.asCacheLoader(conf.backend().getLoader());
        } else if (conf.backend().getExtendedLoader() != null) {
            return conf.backend().getExtendedLoader();
        } else {
            return Caches.nullLoader();
        }
    }

    public static <K, V> Future<?> load(final AbstractCache<K, V> map,
            final CacheLoader<? super K, ? extends V> loader, final K key) {
        return load(map, loader, key, SAME_THREAD_EXECUTOR);
    }

    public static <K, V> Future<?> loadEntry(
            final AbstractCache<K, V> map,
            final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            final K key) {
        return null;
    }

    public static <K, V> Future<?> loadAllEntries(
            final AbstractCache<K, V> map,
            final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            final Collection<? extends K> key) {
        return null;
    }

    public static <K, V> Future<?> load(final AbstractCache<K, V> map,
            final CacheLoader<? super K, ? extends V> loader, final K key, Executor e) {
        return load(loader, key, e, new Callback<V>() {
            public void completed(V result) {
                if (result != null) {
                    map.put(key, result);
                }
            }

            public void failed(Throwable cause) {
                // allready handled, nothing to cleanup
            }
        }, map.getErrorHandler());
    }

    public static <K, V> Future<?> loadAll(final AbstractCache<K, V> map,
            final CacheLoader<? super K, ? extends V> loader,
            final Collection<? extends K> key) {
        return loadAll(map, loader, key, SAME_THREAD_EXECUTOR);
    }

    public static <K, V> Future<?> loadAll(final AbstractCache<K, V> map,
            final CacheLoader<? super K, ? extends V> loader,
            final Collection<? extends K> key, Executor e) {
        return loadAll(loader, key, e, new Callback<Map<K, V>>() {
            public void completed(Map<K, V> result) {
                if (result != null) {
                    Map<K, V> noNullsMap = new HashMap<K, V>(map.size());
                    for (Map.Entry<K, V> e : result.entrySet()) {
                        V value = e.getValue();
                        if (value != null) {
                            noNullsMap.put(e.getKey(), value);
                        }
                    }
                    map.putAll(noNullsMap);
                }
            }

            public void failed(Throwable cause) {
                // allready handled, nothing to cleanup
            }
        }, map.getErrorHandler());
    }

    public static <K, V> V loadNow(final AbstractCache<K, V> map,
            final CacheLoader<? super K, ? extends V> loader, final K key) {
        V v;
        try {
            v = loader.load(key);
        } catch (Exception e) {
            v = map.getErrorHandler().asyncLoadingFailed(key, e);
        }
        return v;
    }

    public static <K, V> CacheEntry<K, V> loadEntryNow(
            final AbstractCache<K, V> map,
            final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            final K key) {
        CacheEntry<K, V> v;
        try {
            v = (CacheEntry) loader.load(key);
        } catch (Exception e) {
            v = map.getErrorHandler().asyncLoadingEntryFailed(key, e);
        }
        return v;
    }

    // public static final Executor SameThreadExecutor=
    /** {@inheritDoc} */
    protected static <K, V> Future<?> load(
            final CacheLoader<? super K, ? extends V> loader, final K key,
            final Executor e, Callback<V> callback, CacheErrorHandler<K, V> errorHandler) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        FutureTask<V> ft = new FutureTask<V>(new LoadValueCallable<K, V>(loader, key,
                callback, errorHandler));
        e.execute(ft);
        return ft;
    }

    protected static <K, V> Future<?> load(
            final CacheLoader<? super K, ? extends V> loader, final K key,
            final Map<K, V> map, CacheErrorHandler<K, V> errorHandler) {
        return load(loader, key, SAME_THREAD_EXECUTOR, new Callback<V>() {
            public void completed(V result) {
                if (result != null) {
                    map.put(key, result);
                }
            }

            public void failed(Throwable cause) {
                // allready handled, nothing to cleanup
            }
        }, errorHandler);
    }

    /** {@inheritDoc} */
    protected static <K, V> Future<?> loadAll(
            final CacheLoader<? super K, ? extends V> loader,
            final Collection<? extends K> keys, final Executor e,
            Callback<Map<K, V>> callback, CacheErrorHandler<K, V> errorHandler) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        AbstractCache.checkCollectionForNulls(keys);
        FutureTask<V> ft = new FutureTask<V>(new LoadValuesCallable<K, V>(loader, keys,
                callback, errorHandler));
        e.execute(ft);
        return ft;
    }
    
//    
//    /*
//     * cache.load -> load value -> put value into cache cache.get -> load value ->
//     * put value into cache cache.load -> load failed -> log exception , put
//     * dummy value in (opt) cache.get -> load failed -> log/throw exception ,
//     * put dummy value in (opt)
//     */
//    protected CacheEntry<K, V> loadValueFromCacheLoader(K key, boolean isAsyncLoad) {
//        try {
//            CacheEntry<K, V> entry = extendedLoader.load(key);
//            return entry;
//        } catch (Exception e) {
//            return getErrorHandler().loadingEntryOfValueFailed(key, e);
//        }
//    }
//
//    protected LoadableCacheEntryCache(final CacheConfiguration<K, V> conf) {
//        super(conf);
//        if (conf.backend().hasLoader()) {
//            if (conf.backend().getLoader() != null) {
//                // this.loader = conf.backend().getLoader();
//                this.extendedLoader = null;
//            } else {
//                this.extendedLoader = conf.backend().getExtendedLoader();
//                // this.loader = null;
//            }
//        } else {
//            // this.loader = Caches.nullLoader();
//            this.extendedLoader = null;
//        }
//    }
//
//
//
//    protected abstract CacheEntry<K, V> backendEntryLoaded(CacheEntry<K, V> ce,
//            boolean isSimpleLoader);
//
//    /**
//     * This class can use in for asynchronously loading a value into the cache.
//     */
//    @SuppressWarnings("hiding")
//    static class LoadValueCallable<K, V> implements Callable<V> {
//        /* The cache to load the value from */
//        private final LoadableCacheEntryCache<K, V> c;
//
//        /* The key for which a value should be loaded */
//        private final K key;
//
//        /**
//         * Constructs a new LoadValueCallable.
//         * 
//         * @param c
//         *            The cache to load the value from
//         * @param key
//         *            The key for which a value should be loaded
//         */
//        LoadValueCallable(LoadableCacheEntryCache<K, V> c, K key) {
//            this.c = c;
//            this.key = key;
//        }
//
//        /**
//         * @see java.util.concurrent.Callable#call()
//         */
//        public V call() {
//            CacheEntry<K, V> entry = c.loadValueFromCacheLoader(key, true);
//            c.backendEntryLoaded(entry, c.isSimpleLoader);
//            return null; // currently contract is to only return null
//        }
//    }
}

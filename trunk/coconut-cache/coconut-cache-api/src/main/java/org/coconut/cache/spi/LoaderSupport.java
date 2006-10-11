/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheException;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.Caches;
import org.coconut.core.Callback;
import org.coconut.core.EventHandler;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoaderSupport {

    public final static Executor SAME_THREAD_EXECUTOR = new SameThreadExecutor();

    public static <K, V> AsyncCacheLoader<K, V> noAsyncLoad(CacheLoader<K, V> loader) {
        return new NoAsyncLoad<K, V>(loader);
    }

    static class NoAsyncLoad<K, V> implements AsyncCacheLoader<K, V> {

        private final CacheLoader<K, V> loader;

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoad(K key, Callback<V> c) {
            throw new UnsupportedOperationException(
                    "operation not supported, A AsyncCacheLoader has not been specfied");
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoadAll(java.util.Collection,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoadAll(Collection<? extends K> keys, Callback<Map<K, V>> c) {
            throw new UnsupportedOperationException(
                    "operation not supported, A AsyncCacheLoader has not been specfied");
        }

        /**
         * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
         */
        public V load(K key) throws Exception {
            return loader.load(key);
        }

        /**
         * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
         */
        public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
            return loader.loadAll(keys);
        }

        /**
         * @param loader
         */
        public NoAsyncLoad(final CacheLoader<K, V> loader) {
            super();
            this.loader = loader;
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

    static class AsyncCacheLoaderAdaptor<K, V> implements AsyncCacheLoader<K, V> {
        final Executor es;

        final CacheLoader<K, V> loader;

        /**
         * @param es
         * @param loader
         * @param cache
         */
        public AsyncCacheLoaderAdaptor(final Executor es, final CacheLoader<K, V> loader) {
            this.es = es;
            this.loader = loader;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoad(K key, Callback<V> c) {
            LoadValueRunnable lvr = new LoadValueRunnable<K, V>(loader, key, c);
            FutureTask<V> ft = new FutureTask<V>(lvr, null);
            es.execute(ft);
            return ft;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoadAll(java.util.Collection,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoadAll(Collection<? extends K> keys, Callback<Map<K, V>> c) {
            AbstractCache.checkCollectionForNulls(keys);
            LoadValuesRunnable lvr = new LoadValuesRunnable<K, V>(loader, keys, c);
            FutureTask<V> ft = new FutureTask<V>(lvr, null);
            es.execute(ft);
            return ft;
        }

        /**
         * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
         */
        public V load(K key) throws Exception {
            return loader.load(key);
        }

        /**
         * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
         */
        public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
            return loader.loadAll(keys);
        }
    }

    static class LoadValueRunnable<K, V> implements Runnable {
        private final Callback<V> callback;

        private final K key;

        private final CacheLoader<K, V> loader;

        /**
         * @param loader
         * @param key
         * @param callback
         */
        public LoadValueRunnable(final CacheLoader<K, V> loader, final K key,
                final Callback<V> callback) {
            this.loader = loader;
            this.key = key;
            this.callback = callback;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                V v = loader.load(key);
                callback.completed(v);
            } catch (Exception e) {
                callback.failed(e);
            }
        }
    }

    static class LoadValuesRunnable<K, V> implements Runnable {
        private final Callback<Map<K, V>> callback;

        private final Collection<? extends K> keys;

        private final CacheLoader<K, V> loader;

        /**
         * @param loader
         * @param key
         * @param callback
         */
        public LoadValuesRunnable(final CacheLoader<K, V> loader,
                final Collection<? extends K> keys, final Callback<Map<K, V>> callback) {
            this.loader = loader;
            this.keys = keys;
            this.callback = callback;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                Map<K, V> map = loader.loadAll(keys);
                callback.completed(map);
            } catch (Throwable e) {
                callback.failed(e);
            }
        }
    }

    static class NonNullEntriesIntoCache<K, V> implements
            EventHandler<Map<K, CacheEntry<K, V>>> {
        private final AbstractCache<K, V> cache;

        /**
         * @param cache
         */
        public NonNullEntriesIntoCache(final AbstractCache<K, V> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
         */
        public void handle(Map<K, CacheEntry<K, V>> map) {
            ArrayList<CacheEntry<K, V>> col = new ArrayList<CacheEntry<K, V>>(map.size());
            for (Map.Entry<K, CacheEntry<K, V>> e : map.entrySet()) {
                CacheEntry<K, V> value = e.getValue();
                if (value != null) {
                    col.add(value);
                }
            }
            if (col.size() > 0) {
                cache.putEntries(col);
            }
        }
    }

    static class NonNullEntryIntoCache<K, V> implements EventHandler<CacheEntry<K, V>> {
        private final AbstractCache<K, V> cache;

        /**
         * @param cache
         */
        public NonNullEntryIntoCache(final AbstractCache<K, V> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
         */
        public void handle(CacheEntry<K, V> entry) {
            if (entry != null) {
                cache.putEntry(entry);
            }
        }
    }

    static class NonNullValueIntoMap<K, V> implements EventHandler<V> {
        private final Map<K, V> cache;

        private final K key;

        /**
         * @param cache
         */
        public NonNullValueIntoMap(final Map<K, V> map, final K key) {
            if (map == null) {
                throw new NullPointerException("map is null");
            } else if (key == null) {
                throw new NullPointerException("key is null");
            }
            this.key = key;
            this.cache = map;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
         */
        public void handle(V value) {
            if (value != null) {
                cache.put(key, value);
            }
        }
    }

    static class NonNullValuesIntoMap<K, V> implements EventHandler<Map<K, V>> {
        private final Map<K, V> map;

        /**
         * @param cache
         */
        public NonNullValuesIntoMap(final Map<K, V> map) {
            if (map == null) {
                throw new NullPointerException("map is null");
            }
            this.map = map;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
         */
        public void handle(Map<K, V> map) {
            Map<K, V> noNullsMap = new HashMap<K, V>(map.size());
            for (Map.Entry<K, V> e : map.entrySet()) {
                V value = e.getValue();
                if (value != null) {
                    noNullsMap.put(e.getKey(), value);
                }
            }
            if (noNullsMap.size() > 0) {
                this.map.putAll(noNullsMap);
            }
        }
    }

    static class SingleEntryCallback<K, V> implements Callback<CacheEntry<K, V>> {
        private final EventHandler<CacheEntry<K, V>> ac;

        private final CacheErrorHandler<K, V> errorHandler;

        private final K key;

        private final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader;

        /**
         * @param ac
         * @param key
         */
        public SingleEntryCallback(
                final K key,
                EventHandler<CacheEntry<K, V>> eh,
                final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
                CacheErrorHandler<K, V> errorHandler) {
            this.key = key;
            this.ac = eh;
            this.errorHandler = errorHandler;
            this.loader = loader;
        }

        /**
         * @see org.coconut.core.Callback#completed(java.lang.Object)
         */
        public void completed(CacheEntry<K, V> result) {
            if (result != null) {
                try {
                    ac.handle(result);
                } catch (Throwable e) {
                    errorHandler
                            .unhandledError(new CacheException("unknown exception", e));
                }
            }
        }

        /**
         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
         */
        public void failed(Throwable cause) {
            CacheEntry<K, V> v = errorHandler.loadEntryFailed(loader, key, true, cause);
            completed(v);
        }
    }

    static class SingleValueCallback<K, V> implements Callback<V> {
        private final CacheErrorHandler<K, V> errorHandler;

        private final EventHandler<V> eventHandler;

        private final K key;

        private final CacheLoader<? super K, ? extends V> loader;

        /**
         * @param ac
         * @param key
         */
        public SingleValueCallback(final K key, EventHandler<V> eh,
                CacheLoader<? super K, ? extends V> loader,
                CacheErrorHandler<K, V> errorHandler) {
            this.key = key;
            this.eventHandler = eh;
            this.errorHandler = errorHandler;
            this.loader = loader;
        }

        /**
         * @see org.coconut.core.Callback#completed(java.lang.Object)
         */
        public void completed(V result) {
            if (result != null) {
                try {
                    eventHandler.handle(result);
                } catch (Throwable e) {
                    errorHandler
                            .unhandledError(new CacheException("unknown exception", e));
                }
            }
        }

        /**
         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
         */
        public void failed(Throwable cause) {
            V v = errorHandler.loadFailed(loader, key, false, cause);
            completed(v);
        }
    }

    static class SingleValuesCallback<K, V> implements Callback<Map<K, V>> {
        private final CacheErrorHandler<K, V> errorHandler;

        private final EventHandler<Map<K, V>> eventHandler;

        private final Collection<? extends K> keys;

        private final CacheLoader<? super K, ? extends V> loader;

        /**
         * @param ac
         * @param key
         */
        public SingleValuesCallback(final Collection<? extends K> keys,
                EventHandler<Map<K, V>> eh, CacheLoader<? super K, ? extends V> loader,
                CacheErrorHandler<K, V> errorHandler) {
            this.keys = keys;
            this.eventHandler = eh;
            this.errorHandler = errorHandler;
            this.loader = loader;
        }

        /**
         * @see org.coconut.core.Callback#completed(java.lang.Object)
         */
        public void completed(Map<K, V> result) {
            if (result != null) {
                try {
                    eventHandler.handle(result);
                } catch (Throwable e) {
                    errorHandler
                            .unhandledError(new CacheException("unknown exception", e));
                }
            }
        }

        /**
         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
         */
        public void failed(Throwable cause) {
            Map<K, V> v = errorHandler.loadAllFailed(loader, keys, true, cause);
            completed(v);
        }
    }

    static class SingleEntriesCallback<K, V> implements
            Callback<Map<K, CacheEntry<K, V>>> {
        private final Collection<? extends K> keys;

        private final EventHandler<Map<K, CacheEntry<K, V>>> eventHandler;

        private final CacheErrorHandler<K, V> errorHandler;

        private final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader;

        /**
         * @param ac
         * @param key
         */
        public SingleEntriesCallback(
                final Collection<? extends K> keys,
                EventHandler<Map<K, CacheEntry<K, V>>> eh,
                CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
                CacheErrorHandler<K, V> errorHandler) {
            this.keys = keys;
            this.eventHandler = eh;
            this.errorHandler = errorHandler;
            this.loader = loader;
        }

        /**
         * @see org.coconut.core.Callback#completed(java.lang.Object)
         */
        public void completed(Map<K, CacheEntry<K, V>> result) {
            if (result != null) {
                try {
                    eventHandler.handle(result);
                } catch (Throwable e) {
                    errorHandler
                            .unhandledError(new CacheException("unknown exception", e));
                }
            }
        }

        /**
         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
         */
        public void failed(Throwable cause) {
            Map<K, CacheEntry<K, V>> v = errorHandler.loadAllEntrisFailed(loader, keys,
                    true, cause);
            completed(v);
        }
    }

    public static <K, V> AsyncCacheLoader<K, V> wrapAsAsync(CacheLoader<K, V> loader,
            Executor e) {
        return loader instanceof AsyncCacheLoader ? (AsyncCacheLoader) loader
                : new AsyncCacheLoaderAdaptor<K, V>(e, loader);
    }

    public static <K, V> Future<?> asyncLoad(
            AsyncCacheLoader<? super K, ? extends V> loader, final K key,
            CacheErrorHandler<K, V> errorHandler, EventHandler<V> eh) {
        Callback<V> c = new SingleValueCallback<K, V>(key, eh, loader, errorHandler);
        return loader.asyncLoad(key, (Callback) c);
    }

    public static <K, V> Future<?> asyncLoad(
            AsyncCacheLoader<? super K, ? extends V> loader, final K key,
            CacheErrorHandler<K, V> errorHandler, Map<K, V> sink) {
        return asyncLoad(loader, key, errorHandler, new NonNullValueIntoMap<K, V>(sink,
                key));
    }

    public static <K, V> Future<?> asyncLoadAll(
            AsyncCacheLoader<? super K, ? extends V> loader,
            final Collection<? extends K> keys, CacheErrorHandler<K, V> errorHandler,
            EventHandler<Map<K, V>> eh) {
        Callback<Map<K, V>> c = new SingleValuesCallback<K, V>(keys, eh, loader,
                errorHandler);
        return loader.asyncLoadAll(keys, (Callback) c);
    }

    public static <K, V> Future<?> asyncLoadAll(
            AsyncCacheLoader<? super K, ? extends V> loader,
            final Collection<? extends K> keys, CacheErrorHandler<K, V> errorHandler,
            Map<K, V> sink) {
        return asyncLoadAll(loader, keys, errorHandler, new NonNullValuesIntoMap<K, V>(
                sink));
    }

    public static <K, V> Future<?> asyncLoadEntry(
            AsyncCacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            final K key, AbstractCache<K, V> sink) {
        return asyncLoadEntry(loader, key, sink.getErrorHandler(),
                new NonNullEntryIntoCache<K, V>(sink));
    }

    public static <K, V> Future<?> asyncLoadEntry(
            AsyncCacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            final K key, CacheErrorHandler<K, V> errorHandler,
            EventHandler<CacheEntry<K, V>> eh) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        Callback<CacheEntry<K, V>> c = new SingleEntryCallback<K, V>(key, eh, loader,
                errorHandler);
        return loader.asyncLoad(key, (Callback) c);
    }

    public static <K, V> Future<?> asyncLoadAllEntries(
            AsyncCacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            final Collection<? extends K> keys, CacheErrorHandler<K, V> errorHandler,
            EventHandler<Map<K, CacheEntry<K, V>>> eh) {
        SingleEntriesCallback<K, V> c = new SingleEntriesCallback<K, V>(keys, eh, loader,
                errorHandler);
        return loader.asyncLoadAll(keys, (Callback) c);
    }

    public static <K, V> Future<?> asyncLoadAllEntries(
            AsyncCacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            final Collection<? extends K> keys, AbstractCache<K, V> cache) {
        return asyncLoadAllEntries(loader, keys, cache.getErrorHandler(),
                new NonNullEntriesIntoCache<K, V>(cache));
    }

    /**
     * Attempts to load and return a value from the specified cache loader. If
     * the load fails by throwing an exception. The specified CacheErrorHandler
     * will be used to handle the error.
     * 
     * @param loader
     *            the cache loader that the value should be loaded from
     * @param key
     *            the key for which value to load
     * @param errorHandler
     *            the error handler used for handling errors
     * @return the value for the given key or <tt>null</tt> if no value could
     *         be found for the specified key
     */
    public static <K, V> V load(final CacheLoader<? super K, ? extends V> loader,
            final K key, final CacheErrorHandler<K, V> errorHandler) {
        V v;
        try {
            v = loader.load(key);
        } catch (Exception e) {
            v = errorHandler.loadFailed(loader, key, false, e);
        }
        return v;
    }

    public static <K, V> Map<K, V> loadAll(
            final CacheLoader<? super K, ? extends V> loader,
            Collection<? extends K> keys, final CacheErrorHandler<K, V> errorHandler) {
        Map<K, V> map;
        try {
            map = (Map<K, V>) loader.loadAll(keys);
        } catch (Exception e) {
            map = errorHandler.loadAllFailed(loader, keys, false, e);
        }
        return map;
    }

    public static <K, V> Map<K, CacheEntry<K, V>> loadAllEntries(
            final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            Collection<? extends K> keys, final CacheErrorHandler<K, V> errorHandler) {
        Map<K, CacheEntry<K, V>> map;
        try {
            map = (Map<K, CacheEntry<K, V>>) loader.loadAll(keys);
        } catch (Exception e) {
            map = errorHandler.loadAllEntrisFailed(loader, keys, false, e);
        }
        return map;
    }

    public static <K, V> CacheEntry<K, V> loadEntry(
            final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            final K key, final CacheErrorHandler<K, V> errorHandler) {
        CacheEntry<K, V> entry;
        try {
            entry = (CacheEntry<K, V>) loader.load(key);
        } catch (Exception e) {
            entry = errorHandler.loadEntryFailed(loader, key, false, e);
        }
        return entry;
    }

    public static <K, V> CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> getLoader(
            CacheConfiguration<K, V> conf) {
        if (conf.backend().getStore() != null) {
            return (CacheLoader) conf.backend().getStore();
        } else if (conf.backend().getLoader() != null) {
            return Caches.asCacheLoader(conf.backend().getLoader());
        } else if (conf.backend().getExtendedLoader() != null) {
            return conf.backend().getExtendedLoader();
        } else {
            return Caches.nullLoader();
        }
    }

}

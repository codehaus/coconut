/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.services.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheException;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.AsyncCacheLoader;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.cache.spi.CacheUtil;
import org.coconut.cache.spi.CacheExecutorRunnable;
import org.coconut.cache.util.AbstractCacheLoader;
import org.coconut.cache.util.DefaultCacheEntry;
import org.coconut.core.Callback;
import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadUtil {

    public static <K, V> AsyncCacheLoader<K, V> wrapAsAsync(CacheLoader<K, V> loader,
            Executor e) {
        return loader instanceof AsyncCacheLoader ? (AsyncCacheLoader<K, V>) loader
                : new AsyncCacheLoaderAdaptor<K, V>(e, loader);
    }

    public static <K, V> Callback<Map<K, CacheEntry<K, V>>> entriesLoadedCallback(
            final Collection<? extends K> keys,
            EventProcessor<Map<K, CacheEntry<K, V>>> eh,
            final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            CacheErrorHandler<K, V> errorHandler) {
        return new EntriesLoadedCallback<K, V>(keys, eh, loader, errorHandler);
    }

    public static <K, V> Callback<CacheEntry<K, V>> entryLoadedCallback(
            final K key,
            EventProcessor<CacheEntry<K, V>> eh,
            final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            CacheErrorHandler<K, V> errorHandler) {
        return new EntryLoadedCallback<K, V>(key, eh, loader, errorHandler);
    }

    static class AsyncCacheLoaderAdaptor<K, V> implements AsyncCacheLoader<K, V> {
        private final Executor executor;

        private final CacheLoader<K, V> loader;

        /**
         * @param es
         * @param loader
         * @param cache
         */
        public AsyncCacheLoaderAdaptor(final Executor executor,
                final CacheLoader<K, V> loader) {
            if (executor == null) {
                throw new NullPointerException("es is null");
            } else if (loader == null) {
                throw new NullPointerException("loader is null");
            }
            this.executor = executor;
            this.loader = loader;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoad(K key, Callback<V> c) {
            LoadValueRunnable lvr = new LoadValueRunnable<K, V>(loader, key, c);
            FutureTask<V> ft = new FutureTask<V>(lvr, null);
            executor.execute(ft);
            return ft;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoadAll(java.util.Collection,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoadAll(Collection<? extends K> keys, Callback<Map<K, V>> c) {
            CacheUtil.checkCollectionForNulls(keys);
            LoadValuesRunnable lvr = new LoadValuesRunnable<K, V>(loader, keys, c);
            FutureTask<V> ft = new FutureTask<V>(lvr, null);
            executor.execute(ft);
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

    public static <K, V> EventProcessor<Map<K, CacheEntry<K, V>>> entriesNonNullIntoAbstractCache(
            AbstractCache<K, V> cache) {
        return new EntriesNonNullIntoCache<K, V>(cache);
    }

    public static <K, V> EventProcessor<CacheEntry<K, V>> entryNonNullIntoAbstractCache(
            AbstractCache<K, V> cache) {
        return new EntryNonNullIntoCache<K, V>(cache);
    }

    static abstract class AbstractLoadedCallback<K, V, E> implements Callback<E> {
        final CacheErrorHandler<K, V> errorHandler;

        final EventProcessor<E> eventHandler;

        public AbstractLoadedCallback(EventProcessor<E> eh,
                CacheErrorHandler<K, V> errorHandler) {
            if (eh == null) {
                throw new NullPointerException("eh, loader, errorHandler is null");
            } else if (errorHandler == null) {
                throw new NullPointerException("errorHandler is null");
            }
            this.eventHandler = eh;
            this.errorHandler = errorHandler;
        }

        /**
         * @see org.coconut.core.Callback#completed(java.lang.Object)
         */
        public void completed(E result) {
            try {
                eventHandler.process(result);
            } catch (RuntimeException e) {
                errorHandler.unhandledRuntimeException(e);
            }
        }
    }

    static class EntriesLoadedCallback<K, V> extends
            AbstractLoadedCallback<K, V, Map<K, CacheEntry<K, V>>> {
        private final Collection<? extends K> keys;

        private final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader;

        /**
         * @param key
         * @param eh
         * @param loader
         * @param errorHandler
         */
        public EntriesLoadedCallback(
                final Collection<? extends K> keys,
                EventProcessor<Map<K, CacheEntry<K, V>>> eh,
                final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
                CacheErrorHandler<K, V> errorHandler) {
            super(eh, errorHandler);
            if (keys == null) {
                throw new NullPointerException("key is null");
            } else if (loader == null) {
                throw new NullPointerException("loader is null");
            }
            this.keys = keys;
            this.loader = loader;
        }

        /**
         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
         */
        public void failed(Throwable cause) {
            Map<K, CacheEntry<K, V>> map = errorHandler.loadAllFailed(loader, keys, true,
                    cause);
            completed(map);
        }
    }

    static class EntriesNonNullIntoCache<K, V> implements
            EventProcessor<Map<K, CacheEntry<K, V>>> {
        private final AbstractCache<K, V> cache;

        /**
         * @param cache
         */
        public EntriesNonNullIntoCache(final AbstractCache<K, V> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
         */
        public void process(Map<K, CacheEntry<K, V>> map) {
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

    static class EntryLoadedCallback<K, V> extends
            AbstractLoadedCallback<K, V, CacheEntry<K, V>> {
        private final K key;

        private final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader;

        /**
         * @param key
         * @param eh
         * @param loader
         * @param errorHandler
         */
        public EntryLoadedCallback(
                final K key,
                EventProcessor<CacheEntry<K, V>> eh,
                final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
                CacheErrorHandler<K, V> errorHandler) {
            super(eh, errorHandler);
            if (key == null) {
                throw new NullPointerException("key is null");
            } else if (loader == null) {
                throw new NullPointerException("loader is null");
            }
            this.key = key;
            this.loader = loader;
        }

        /**
         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
         */
        public void failed(Throwable cause) {
            CacheEntry<K, V> v = errorHandler.loadFailed(loader, key, true, cause);
            completed(v);
        }
    }

    static class EntryNonNullIntoCache<K, V> implements EventProcessor<CacheEntry<K, V>> {
        private final AbstractCache<K, V> cache;

        /**
         * @param cache
         */
        public EntryNonNullIntoCache(final AbstractCache<K, V> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
         */
        public void process(CacheEntry<K, V> entry) {
            if (entry != null) {
                cache.putEntry(entry);
            }
        }
    }

    static class LoadValueRunnable<K, V> implements CacheExecutorRunnable.LoadKey<K> {
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
            if (loader == null) {
                throw new NullPointerException("loader is null");
            } else if (key == null) {
                throw new NullPointerException("key is null");
            } else if (callback == null) {
                throw new NullPointerException("callback is null");
            }
            this.loader = loader;
            this.key = key;
            this.callback = callback;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader.LoadKeyRunnable#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                callback.completed(loader.load(key));
            } catch (Exception e) {
                callback.failed(e);
            }
        }
    }

    static class LoadValuesRunnable<K, V> implements CacheExecutorRunnable.LoadKeys<K> {
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
            if (loader == null) {
                throw new NullPointerException("loader is null");
            } else if (keys == null) {
                throw new NullPointerException("key is null");
            } else if (callback == null) {
                throw new NullPointerException("callback is null");
            }
            this.loader = loader;
            this.keys = keys;
            this.callback = callback;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader.LoadKeysRunnable#getKeys()
         */
        public Collection<? extends K> getKeys() {
            return keys;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                callback.completed(loader.loadAll(keys));
            } catch (Throwable e) {
                callback.failed(e);
            }
        }
    }

    /* Should be refactored */

    public static <K, V> CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> toExtendedCacheLoader(
            CacheLoader<K, V> loader) {
        if (loader instanceof AsyncCacheLoader) {
            return new AbstractAsyncLoaderToExtendedLoader<K, V>(
                    (AsyncCacheLoader) loader);
        } else {
            return loader instanceof AbstractCacheLoader ? new AbstractLoaderToExtendedLoader(
                    loader)
                    : new LoaderToExtendedLoader(loader);
        }

    }

    final static class AbstractAsyncLoaderToExtendedLoader<K, V> extends
            AbstractLoaderToExtendedLoader<K, V> implements
            AsyncCacheLoader<K, CacheEntry<K, V>> {

        private final AsyncCacheLoader<K, V> loader;

        AbstractAsyncLoaderToExtendedLoader(AsyncCacheLoader<K, V> loader) {
            super(loader);
            this.loader = loader;
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoad(final K key, final Callback<CacheEntry<K, V>> c) {
            return loader.asyncLoad(key, new Callback<V>() {

                public void completed(V result) {
                    CacheEntry<K, V> e = result == null ? null
                            : new DefaultCacheEntry<K, V>(key, result);
                    c.completed(e);
                }

                public void failed(Throwable cause) {
                    c.failed(cause);
                }
            });
        }

        /**
         * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoadAll(java.util.Collection,
         *      org.coconut.core.Callback)
         */
        public Future<?> asyncLoadAll(Collection<? extends K> keys,
                Callback<Map<K, CacheEntry<K, V>>> c) {
            throw new UnsupportedOperationException();
        }
    }

    static class AbstractLoaderToExtendedLoader<K, V> extends
            AbstractCacheLoader<K, CacheEntry<K, V>> {

        final CacheLoader<K, V> loader;

        AbstractLoaderToExtendedLoader(CacheLoader<K, V> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> load(K key) throws Exception {
            V v = loader.load(key);
            return v == null ? null : new DefaultCacheEntry<K, V>(key, v);
        }
    }

    static class LoaderToExtendedLoader<K, V> implements CacheLoader<K, CacheEntry<K, V>> {

        final CacheLoader<K, V> loader;

        LoaderToExtendedLoader(CacheLoader<K, V> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> load(K key) throws Exception {
            V v = loader.load(key);
            return v == null ? null : new DefaultCacheEntry<K, V>(key, v);
        }

        /**
         * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
         */
        public Map<K, CacheEntry<K, V>> loadAll(Collection<? extends K> keys)
                throws Exception {
            Map<K, V> h = loader.loadAll(keys);
            Map<K, CacheEntry<K, V>> result = new HashMap<K, CacheEntry<K, V>>();
            for (Map.Entry<K, V> e : h.entrySet()) {
                K k = e.getKey();
                V v = e.getValue();
                result.put(k, v == null ? null : new DefaultCacheEntry<K, V>(k, v));
            }
            return result;
        }
    }

}

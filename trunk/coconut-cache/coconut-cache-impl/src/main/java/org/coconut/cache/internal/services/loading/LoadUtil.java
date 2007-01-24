/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
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

    public static <K, V> Callback<V> valueLoadedCallback(final K key,
            EventProcessor<V> eh, final CacheLoader<? super K, ? extends V> loader,
            CacheErrorHandler<K, V> errorHandler) {
        return new ValueLoadedCallback<K, V>(key, eh, loader, errorHandler);
    }

    public static <K, V> Callback<Map<K, V>> valuesLoadedCallback(
            final Collection<? extends K> keys, EventProcessor<Map<K, V>> eh,
            final CacheLoader<? super K, ? extends V> loader,
            CacheErrorHandler<K, V> errorHandler) {
        return new ValuesLoadedCallback<K, V>(keys, eh, loader, errorHandler);
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
            AbstractCache.checkCollectionForNulls(keys);
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

    public static <K, V> EventProcessor<V> valueNonNullIntoMap(K key, Map<K, V> map) {
        return new ValueNonNullIntoMap<K, V>(map, key);
    }

    public static <K, V> EventProcessor<Map<K, V>> valuesNonNullIntoMap(Map<K, V> map) {
        return new ValuesNonNullIntoMap<K, V>(map);
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
            } catch (Throwable e) {
                errorHandler.unhandledError(new CacheException("unknown exception", e));
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
            Map<K, CacheEntry<K, V>> map = errorHandler.loadAllEntrisFailed(loader, keys,
                    true, cause);
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
            CacheEntry<K, V> v = errorHandler.loadEntryFailed(loader, key, true, cause);
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

    static class LoadValueRunnable<K, V> implements AsyncCacheLoader.LoadKeyRunnable<K> {
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

    static class LoadValuesRunnable<K, V> implements AsyncCacheLoader.LoadKeysRunnable<K> {
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

    static class ValueLoadedCallback<K, V> extends AbstractLoadedCallback<K, V, V> {
        private final K key;

        private final CacheLoader<? super K, ? extends V> loader;

        /**
         * @param ac
         * @param key
         */
        public ValueLoadedCallback(final K key, EventProcessor<V> eh,
                CacheLoader<? super K, ? extends V> loader,
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
            V v = errorHandler.loadFailed(loader, key, false, cause);
            completed(v);
        }
    }

    static class ValueNonNullIntoMap<K, V> implements EventProcessor<V> {
        private final Map<K, V> cache;

        private final K key;

        /**
         * @param cache
         */
        public ValueNonNullIntoMap(final Map<K, V> map, final K key) {
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
        public void process(V value) {
            if (value != null) {
                cache.put(key, value);
            }
        }
    }

    static class ValuesLoadedCallback<K, V> extends
            AbstractLoadedCallback<K, V, Map<K, V>> {
        private final Collection<? extends K> keys;

        private final CacheLoader<? super K, ? extends V> loader;

        /**
         * @param ac
         * @param key
         */
        public ValuesLoadedCallback(final Collection<? extends K> keys,
                EventProcessor<Map<K, V>> eh, CacheLoader<? super K, ? extends V> loader,
                CacheErrorHandler<K, V> errorHandler) {
            super(eh, errorHandler);
            this.keys = keys;
            this.loader = loader;
        }

        /**
         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
         */
        public void failed(Throwable cause) {
            Map<K, V> v = errorHandler.loadAllFailed(loader, keys, true, cause);
            completed(v);
        }
    }

    static class ValuesNonNullIntoMap<K, V> implements EventProcessor<Map<K, V>> {
        private final Map<K, V> map;

        /**
         * @param cache
         */
        public ValuesNonNullIntoMap(final Map<K, V> map) {
            if (map == null) {
                throw new NullPointerException("map is null");
            }
            this.map = map;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
         */
        public void process(Map<K, V> map) {
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
}

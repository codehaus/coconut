/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheException;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.Caches;
import org.coconut.cache.spi.LoadableCache.LoadValueCallable;
import org.coconut.cache.spi.LoadableCache.LoadValuesRunnable;
import org.coconut.core.Log;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class LoadableCacheEntryCache<K, V> extends AbstractCache<K, V> {

    private final Log log;

    private final CacheLoader<K, ? extends CacheEntry<K, V>> extendedLoader;

    private final boolean isSimpleLoader = false;

    /** {@inheritDoc} */
    @Override
    public Future<?> load(final K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        FutureTask<V> ft = new FutureTask<V>(new LoadValueCallable<K, V>(this, key));
        loadAsynchronous(ft);
        return ft;
    }

    /** {@inheritDoc} */
    @Override
    public Future<?> loadAll(final Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        checkCollectionForNulls(keys);
        FutureTask<V> ft = new FutureTask<V>(new LoadValuesRunnable<K, V>(this, keys));
        loadAsynchronous(ft);
        return ft;
    }

    /**
     * This method is used for handling the requests submitted to {link
     * #load(key)} and {link #loadAll(key)}. This method wraps the actual
     * loading logic into a Runnable that can be run in any way.
     * <p>
     * The default implementation just runs the request in the current thread
     * ie. not asynchronously.
     * 
     * @param r
     */
    protected void loadAsynchronous(Runnable r) {
        r.run();
    }

    /*
     * cache.load -> load value -> put value into cache cache.get -> load value ->
     * put value into cache cache.load -> load failed -> log exception , put
     * dummy value in (opt) cache.get -> load failed -> log/throw exception ,
     * put dummy value in (opt)
     */
    protected CacheEntry<K, V> loadValueFromCacheLoader(K key, boolean isAsyncLoad) {
        try {
            CacheEntry<K, V> entry = extendedLoader.load(key);
            return entry;
        } catch (Exception e) {
            return loadingOfValueFailed(key, e);
        }
    }

    protected LoadableCacheEntryCache(final CacheConfiguration<K, V> conf) {
        super(conf);
        if (conf.backend().hasLoader()) {
            if (conf.backend().getLoader() != null) {
                // this.loader = conf.backend().getLoader();
                this.extendedLoader = null;
            } else {
                this.extendedLoader = conf.backend().getExtendedLoader();
                // this.loader = null;
            }
        } else {
            // this.loader = Caches.nullLoader();
            this.extendedLoader = null;
        }
        log = conf.getLog();
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    protected CacheEntry<K, V> loadingOfValueFailed(K key, Throwable cause) {
        throw new CacheException("Failed to load value [key = " + key.toString() + "]", cause);
    }

    protected abstract CacheEntry<K, V> backendEntryLoaded(CacheEntry<K, V> ce,
            boolean isSimpleLoader);

    /**
     * This class can use in for asynchronously loading a value into the cache.
     */
    @SuppressWarnings("hiding")
    static class LoadValueCallable<K, V> implements Callable<V> {
        /* The cache to load the value from */
        private final LoadableCacheEntryCache<K, V> c;

        /* The key for which a value should be loaded */
        private final K key;

        /**
         * Constructs a new LoadValueCallable.
         * 
         * @param c
         *            The cache to load the value from
         * @param key
         *            The key for which a value should be loaded
         */
        LoadValueCallable(LoadableCacheEntryCache<K, V> c, K key) {
            this.c = c;
            this.key = key;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() {
            CacheEntry<K, V> entry = c.loadValueFromCacheLoader(key, true);
            c.backendEntryLoaded(entry, c.isSimpleLoader);
            return null; // currently contract is to only return null
        }
    }

    /**
     * This class can be use for asynchronously loading values into the cache.
     */
    @SuppressWarnings("hiding")
    static class LoadValuesRunnable<K, V> implements Callable<V> {

        /* The cache to load the value from */
        private final LoadableCacheEntryCache<K, V> c;

        /* The keys for which a value should be loaded */
        private final Collection<? extends K> keys;

        /**
         * Constructs a new LoadValuesCallable.
         * 
         * @param c
         *            The cache to load the value from
         * @param key
         *            The keys for which the values should be loaded
         */
        LoadValuesRunnable(LoadableCacheEntryCache<K, V> c, Collection<? extends K> keys) {
            this.c = c;
            this.keys = keys;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() {
            // c.loadValuesIntoCache(keys);
            return null; // currently contract is to return null
        }
    }
}

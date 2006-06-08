/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheException;
import org.coconut.cache.CacheItemEvent;
import org.coconut.cache.CacheLoader;
import org.coconut.cache.Caches;
import org.coconut.cache.util.DefaultCacheEntry;
import org.coconut.core.Log;
import org.coconut.core.Logs;

/**
 * This class provides a skeletal implementation of a <tt>Cache</tt> that is
 * able to use a {@link org.coconut.cache.CacheLoader} to load values into the
 * cache. interface, to minimize the effort required to implement this
 * interface.
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class LoadableCache<K, V> extends AbstractCache<K, V> {

    /** The loaded we are fetching new values from. */
    private final CacheLoader<K, ? extends V> loader;

    private final CacheLoader<K, ? extends CacheEntry<K, V>> extendedLoader;

    /**
     * Constructs a new LoadableCache.
     */
    protected LoadableCache() {
        this.loader = Caches.nullLoader();
        this.extendedLoader = null;
    }

    protected LoadableCache(final CacheConfiguration<K, V> conf) {
        super(conf);
        if (conf.backend().hasLoader()) {
            if (conf.backend().getLoader() != null) {
                this.loader = conf.backend().getLoader();
                this.extendedLoader = null;
            } else {
                this.extendedLoader = conf.backend().getExtendedLoader();
                this.loader = null;
            }
        } else {
            this.loader = Caches.nullLoader();
            this.extendedLoader = null;
        }
    }

    /**
     * Returns the <tt>CacheLoader</tt> defined for this Cache or null if no
     * ordinary loaded has been defined.
     */
    public final CacheLoader<K, ? extends V> getLoader() {
        return loader;
    }

    /**
     * Returns the extended <tt>CacheLoader</tt> defined for this Cache.
     */
    public final CacheLoader<K, ? extends CacheEntry<K, V>> getExtendedLoader() {
        return extendedLoader;
    }

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

    protected void putCacheItem(CacheEntry<K, V> item) {
        throw new UnsupportedOperationException("putCacheItem not supported");
    }

    protected boolean usesExtendedCacheLoader() {
        return extendedLoader != null;
    }

    protected V loadValueFromCacheLoader(final K key, boolean doPut) {
        V value = null;
        try {
            value = loader.load(key);
        } catch (Exception e) {
            e.printStackTrace();
            value = loadingOfValueFailed(key, e);
        }
        if (value != null && doPut) {
            put(key, value);
        }
        return value;
    }

    protected void loadValuesIntoCache(final Collection<? extends K> keys) {
        if (loader != null) {
            Map<K, V> values = loadValuesFromCacheLoader(keys);
            if (values != null && !values.isEmpty()) {
                HashMap<K, V> newMap = new HashMap<K, V>();
                for (Map.Entry<K, V> entry : values.entrySet()) {
                    V value = entry.getValue();
                    if (value != null) {
                        newMap.put(entry.getKey(), entry.getValue());
                    }
                }
                putAll(newMap);
            }
        } else {
            Map<K, CacheEntry<K, V>> values = loadExtendedValuesFromCacheLoader(keys);
            if (values != null && !values.isEmpty()) {
                // TODO n
                Collection<CacheEntry<K, V>> newMap = new ArrayList<CacheEntry<K, V>>(values.size());
                for (CacheEntry<K, V> entry : values.values()) {
                    if (entry != null) {
                        newMap.add(entry);
                    }
                }
                putAllEntries(newMap);
            }
        }
    }

    protected CacheEntry<K, V> loadExtendedValueFromCacheLoader(final K key, boolean doPut) {
        CacheEntry<K, V> value = null;
        try {
            value = extendedLoader.load(key);
            if (value != null) {
                if (value.getKey() == null) {
                    throw new NullPointerException("loaded key was null");
                } else if (value.getValue() == null) {
                    throw new NullPointerException("loaded value was null");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            value = loadingOfCacheEntryFailed(key, e);
        }
        if (value != null && doPut) {
            putEntry(value);
        }
        return value;
    }

    protected Map<K, CacheEntry<K, V>> loadExtendedValuesFromCacheLoader(
            final Collection<? extends K> keys) {
        try {
            Map<K, ? extends CacheEntry<K, V>> map = extendedLoader.loadAll(keys);
            // remove all nulls
            Map<K, CacheEntry<K, V>> noNullsMap = new HashMap<K, CacheEntry<K, V>>(map.size());
            for (CacheEntry<K, V> e : map.values()) {
                if (e != null) {
                    noNullsMap.put(e.getKey(), e);
                }
            }
            return noNullsMap;
        } catch (Exception e) {
            return loadingOfCacheEntryFailed(keys, e);
        }
    }

    protected Map<K, V> loadValuesFromCacheLoader(final Collection<? extends K> keys) {
        try {
            Map<K, ? extends V> map = loader.loadAll(keys);
            // remove all nulls
            Map<K, V> noNullsMap = new HashMap<K, V>(map.size());
            for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = map.entrySet()
                    .iterator(); i.hasNext();) {
                Map.Entry<? extends K, ? extends V> e = i.next();
                noNullsMap.put(e.getKey(), e.getValue());
            }
            return noNullsMap;
        } catch (Exception e) {
            return loadingOfValuesFailed(keys, e);
        }
    }

    protected CacheEntry<K, V> putEntry(CacheEntry<K, V> entry) {
        throw new UnsupportedOperationException("Cache does not support inserting cache entries");
    }

    protected void putAllEntries(Collection<CacheEntry<K, V>> entries) {
        throw new UnsupportedOperationException("Cache does not support inserting cache entries");
    }

    protected void noValueLoadedFor(K key) {
        // ignore
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    protected V loadingOfValueFailed(K key, Throwable cause) {
        throw new CacheException("Failed to load value [key = " + key.toString() + "]", cause);
    }

    protected CacheEntry<K, V> loadingOfCacheEntryFailed(K key, Throwable cause) {
        return new DefaultCacheEntry<K, V>(key, loadingOfValueFailed(key, cause));
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    protected Map<K, V> loadingOfValuesFailed(Collection<? extends K> keys, Throwable cause) {
        throw new CacheException("Failed to load values for collection of keys [keys.size = "
                + keys.size() + "]", cause);
    }

    protected Map<K, CacheEntry<K, V>> loadingOfCacheEntryFailed(Collection<? extends K> keys,
            Throwable cause) {
        throw new CacheException("Failed to load values for collection of keys [keys.size = "
                + keys.size() + "]", cause);
    }

    /**
     * This class can use in for asynchronously loading a value into the cache.
     */
    @SuppressWarnings("hiding")
    static class LoadValueCallable<K, V> implements Callable<V> {
        /* The cache to load the value from */
        private final LoadableCache<K, V> c;

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
        LoadValueCallable(LoadableCache<K, V> c, K key) {
            this.c = c;
            this.key = key;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() {
            if (c.loader != null) {
                c.loadValueFromCacheLoader(key, true);
            } else {
                c.loadExtendedValueFromCacheLoader(key, true);
            }

            return null; // currently contract is to only return null
        }
    }

    /**
     * This class can be use for asynchronously loading values into the cache.
     */
    @SuppressWarnings("hiding")
    static class LoadValuesRunnable<K, V> implements Callable<V> {

        /* The cache to load the value from */
        private final LoadableCache<K, V> c;

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
        LoadValuesRunnable(LoadableCache<K, V> c, Collection<? extends K> keys) {
            this.c = c;
            this.keys = keys;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() {
            c.loadValuesIntoCache(keys);
            return null; // currently contract is to return null
        }
    }

}
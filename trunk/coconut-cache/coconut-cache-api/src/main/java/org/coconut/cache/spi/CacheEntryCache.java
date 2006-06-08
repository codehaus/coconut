/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.CacheConfiguration;


public abstract class CacheEntryCache<K, V> extends LoadableCache<K, V> {

    public CacheEntryCache() {
        super();
    }

    protected CacheEntryCache(CacheConfiguration<K, V> conf) {
        super(conf);
    }

    protected abstract MutableCacheEntry<K, V> lazyCreate(K key,
            boolean isLoading);

    @Override
    public Future<?> load(K key) {
        MutableCacheEntry<K, V> entry = lazyCreate(key, true);
        return loadForEntry(entry);
    }

    protected Future<?> loadForEntry(MutableCacheEntry<K, V> entry) {
        if (entry.tryPrepareLoad(getExpirationStrategy()== CacheConfiguration.ExpirationStrategy.STRICT)) {
            FutureTask<?> loaderTask = new FutureTask<V>(new LoadValueCallable(
                    entry));
            loadAsynchronous(loaderTask);
            return loaderTask;
        } else {
            return new FutureTask<V>(new WaitOnLoadCallable(entry));
        }
    }

    protected void innerLoad(MutableCacheEntry<K, V> entry) {
        FutureTask<?> loaderTask = new FutureTask<V>(new LoadValueCallable(entry));
        loadAsynchronous(loaderTask);
        // return loaderTask;

    }

    @Override
    public Future<?> loadAll(Collection<? extends K> keys) {
        // if (keys.size()==1) {
        // K key=keys.iterator().next();
        // return load(key);
        // }
        Collection<MutableCacheEntry<K, V>> waitOn = null;
        Map<K, MutableCacheEntry<K, V>> map = new HashMap<K, MutableCacheEntry<K, V>>(
                keys.size());
        for (K key : keys) {
            if (key == null) {
                throw new NullPointerException("keys contain null");
            }
            MutableCacheEntry<K, V> entry = lazyCreate(key, true);
            if (entry
                    .tryPrepareLoad(getExpirationStrategy() == CacheConfiguration.ExpirationStrategy.STRICT)) {
                map.put(key, entry);
            } else {
                if (waitOn == null) {
                    waitOn = new LinkedList<MutableCacheEntry<K, V>>();
                }
                waitOn.add(entry);
            }
        }
        FutureTask ft = new FutureTask<V>(new AggregateLoader(map, waitOn));
        loadAsynchronous(ft);
        return ft;
    }

    class WaitOnLoadCallable implements Callable<V> {

        private final MutableCacheEntry<?, V> entry;

        WaitOnLoadCallable(MutableCacheEntry<K, V> entry) {
            this.entry = entry;
        }

        public V call() throws Exception {
            entry.getValue();
            return null;
        }

    }

    /**
     * This class can use in for asynchronously loading a value into the cache.
     */
    class LoadValueCallable implements Callable<V> {
        /* The cache to load the value from */
        private final MutableCacheEntry<K, V> entry;

        /**
         * Constructs a new LoadValueCallable.
         * 
         * @param c
         *            The cache to load the value from
         * @param key
         *            The key for which a value should be loaded
         */
        LoadValueCallable(MutableCacheEntry<K, V> entry) {
            this.entry = entry;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() {
            V value = null;
            try {
                value = null; // loadValueFromCacheLoader(entry.getKey());
            } finally {
                if (value == null) {
                    // we need write lock
                    noValueLoadedFor(entry.getKey());
                }
                entry.loadFinished(value);
            }

            return null; // currently contract is to only return null
        }
    }

    class AggregateLoader implements Callable {
        private final Collection<MutableCacheEntry<K, V>> waitOn;

        private final Map<K, MutableCacheEntry<K, V>> map;

        AggregateLoader(Map<K, MutableCacheEntry<K, V>> map,
                Collection<MutableCacheEntry<K, V>> waitOn) {
            this.waitOn = waitOn;
            this.map = map;
        }

        public Object call() throws Exception {
            Map<K, V> loaded = loadValuesFromCacheLoader(map.keySet());
            // loaded is guranteed null free
            for (Map.Entry<K, V> entry : loaded.entrySet()) {
                V value = entry.getValue();
                map.get(entry.getKey()).loadFinished(value);
                if (value == null) {
                    noValueLoadedFor(entry.getKey());
                }
            }
            if (waitOn != null) {
                for (MutableCacheEntry<K, V> entry : waitOn) {
                    entry.getValue();
                }
            }
            return null;
        }

    }
}

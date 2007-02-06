/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.util.FilteredPolicyDecorator;
import org.coconut.cache.spi.CacheExecutorRunnable;
import org.coconut.cache.util.AbstractCacheLoader;
import org.coconut.cache.util.CacheDecorator;
import org.coconut.event.EventBus;
import org.coconut.filter.Filter;

/**
 * Factory and utility methods for for creating different types of
 * {@link Cache Caches} and {@link CacheLoader CacheLoaders}. Furthermore there
 * are a number of small utility functions concerning general cache usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public final class Caches {

    /**
     * Returns a Runnable that when executed will call the clear method on the
     * specified cache.
     * <p>
     * The following example shows how this can be used to clear the cache every
     * hour.
     * 
     * <pre>
     * Cache c;
     * ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
     * ses.scheduleAtFixedRate(Caches.clearAsRunnable(c), 0, 60 * 60, TimeUnit.SECONDS);
     * </pre>
     * 
     * @param cache
     *            the cache on which to call evict
     * @return a runnable where invocation of the run method will clear the
     *         specified cache
     * @throws NullPointerException
     *             if the specified cache is <tt>null</tt>.
     */
    public static Runnable clearAsRunnable(Cache<?, ?> cache) {
        return new ClearRunnable(cache);
    }

    
    /**
     * This method converts the specified cache to a cache loader. Calls to
     * {@link CacheLoader#load(Object)} will be converted to calls on
     * {@link Cache#get(Object)}. Calls to
     * {@link CacheLoader#loadAll(Collection)} will be converted to calls on
     * {@link Cache#getAll(Collection)}
     * 
     * @param c
     *            the cache to load entries from
     * @return a cache loader that can load values from another cache
     */
    public static <K, V> CacheLoader<K, V> cacheAsCacheLoader(Cache<K, V> c) {
        return new CacheAsCacheLoader<K, V>(c);
    }

    public static <K, V> ReplacementPolicy<? extends Map.Entry<K, V>> entryKeyAcceptor(
            ReplacementPolicy policy, Filter<? extends K> filter) {
        return new FilteredPolicyDecorator(policy, CacheFilters.keyFilter(filter));
    }

    public static <K, V> ReplacementPolicy<? extends Map.Entry<K, V>> entryValueAcceptor(
            ReplacementPolicy policy, Filter<? extends V> filter) {
        return new FilteredPolicyDecorator(policy, CacheFilters.valueFilter(filter));
    }

    /**
     * This class wraps a cache in such a way that it can be used as a cache
     * loader for another cache.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
     * @version $Id$
     */
    static class CacheAsCacheLoader<K, V> implements CacheLoader<K, V>, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -1907266938637317312L;

        /** The cache used as a cache loader. */
        private final Cache<K, V> cache;

        public CacheAsCacheLoader(Cache<K, V> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /** {@inheritDoc} */
        public V load(K key) {
            return cache.get(key);
        }

        /** {@inheritDoc} */
        public Map<K, V> loadAll(Collection<? extends K> keys) {
            return cache.getAll(keys);
        }
    }

    /**
     * Returns a Runnable that when executed will call the evict method on the
     * supplied cache.
     * 
     * <pre>
     * Cache c;
     * ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
     * ses.scheduleAtFixedRate(Caches.evictAsRunnable(c), 0, 60, TimeUnit.SECONDS);
     * </pre>
     * 
     * @param cache
     *            the cache on which to call evict
     * @return a evict runnable
     * @throws NullPointerException
     *             if the cache is <tt>null</tt>.
     */
    public static Runnable evictAsRunnable(Cache<?, ?> cache) {
        return new EvictRunnable(cache);
    }


    /**
     * Returns a cache loader that returns <tt>null</tt> for any key. The
     * loadAll() method will return a map with a mapping for each key to
     * <tt>null</tt>.
     * 
     * @return a cache loader that returns <tt>null</tt> for any key.
     */
    public static <K, V> CacheLoader<K, V> nullLoader() {
        return new NullLoader<K, V>();
    }

    /**
     * Returns a synchronized (thread-safe) cache loader backed by the specified
     * cache loader. In order to guarantee serial access, it is critical that
     * <strong>all</strong> access to the backing cache loader is accomplished
     * through the returned cache loader.
     * <p>
     * If the specified cache loader is an instance of an
     * {@link org.coconut.cache.util.AbstractCacheLoader} the returned cache
     * loader will also be an instance of an AbstractCacheLoader.
     * <p>
     * The returned cache loader will be serializable if the specified cache
     * loader is serializable.
     * 
     * @param loader
     *            the cache loader to be "wrapped" in a synchronized cache
     *            loader.
     * @return a synchronized cache loader using the specified cache loader.
     */
    public static <K, V> CacheLoader<K, V> synchronizedCacheLoader(
            CacheLoader<K, V> loader) {
        if (loader instanceof AbstractCacheLoader) {
            //loader cannot be null
            return new SynchronizedAbstractCacheLoader<K, V>(loader);
        } else {
            return new SynchronizedCacheLoader<K, V>(loader);
        }
    }

    /**
     * Returns an unmodifiable view of the specified cache. This method allows
     * modules to provide users with "read-only" access to internal caches.
     * Query operations on the returned cache "read through" to the specified
     * cache, and attempts to modify the returned cache, whether direct or via
     * its collection views, result in an <tt>UnsupportedOperationException</tt>.
     * <p>
     * The returned cache will be serializable if the specified cache is
     * serializable.
     * <p>
     * The returned cache cannot guard against values entering the cache due to
     * calls to get() which in turn invokes a cache loader to fetch the
     * requested item.
     * 
     * @param c
     *            the cache for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of the specified cache.
     */
    public static <K, V> Cache<K, V> unmodifiableCache(Cache<? extends K, ? extends V> c) {
        return new UnmodifiableCache<K, V>(c);
    }

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Caches() {
    }

    // /CLOVER:ON

    /**
     * A runnable used for calling clear on a cache.
     */
    static class ClearRunnable implements CacheExecutorRunnable.CacheClear, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -9150488448517115905L;

        /** The cache to call clear on. */
        private final Cache<?, ?> cache;

        /**
         * Creates a new ClearRunnable.
         * 
         * @param cache
         *            the cache to call clear on
         */
        ClearRunnable(Cache<?, ?> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /** {@inheritDoc} */
        public void run() {
            cache.clear();
        }

        /**
         * @see org.coconut.cache.spi.ExecutorEvent.Clear#getCache()
         */
        public Cache getCache() {
            return cache;
        }
    }

    static class EvictRunnable implements CacheExecutorRunnable.CacheEvict, Serializable {

        /** serial version UID */
        private static final long serialVersionUID = 5989561008827627705L;

        private final Cache<?, ?> cache;

        EvictRunnable(Cache<?, ?> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        public void run() {
            cache.evict();
        }

        /**
         * @see org.coconut.cache.spi.ExecutorEvent.Clear#getCache()
         */
        public Cache getCache() {
            return cache;
        }
    }

    final static class NullLoader<K, V> extends AbstractCacheLoader<K, V> implements
            Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -4411446068656772121L;

        /** {@inheritDoc} */
        public V load(K key) {
            return null;
        }
    }

    final static class SynchronizedAbstractCacheLoader<K, V> extends
            AbstractCacheLoader<K, V> implements CacheLoader<K, V>, Serializable {
        /** serial version UID */
        private static final long serialVersionUID = 8225025114128657456L;

        /** the loader to delegate to */
        private final CacheLoader<K, V> loader;

        SynchronizedAbstractCacheLoader(CacheLoader<K, V> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public synchronized V load(K key) throws Exception {
            return loader.load(key);
        }
    }

    final static class SynchronizedCacheLoader<K, V> implements CacheLoader<K, V>,
            Serializable {

        /** serial version UID */
        private static final long serialVersionUID = -1525938064071224475L;

        /** the loader to delegate to */
        private final CacheLoader<K, V> loader;

        SynchronizedCacheLoader(CacheLoader<K, V> loader) {
            if (loader == null) {
                throw new NullPointerException("loader is null");
            }
            this.loader = loader;

        }

        /** {@inheritDoc} */
        public synchronized V load(K key) throws Exception {
            return loader.load(key);
        }

        /** {@inheritDoc} */
        public synchronized Map<K, V> loadAll(Collection<? extends K> keys)
                throws Exception {
            return loader.loadAll(keys);
        }
    }

    final static class UnmodifiableCache<K, V> extends CacheDecorator<K, V> implements
            Serializable {
        /** serial version UID */
        private static final long serialVersionUID = 2573709165408359708L;

        /**
         * @param c
         *            the cache to provide an unmodifiable view of
         */
        UnmodifiableCache(Cache<? extends K, ? extends V> c) {
            super((Cache) c); // super checks for null
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public Set<Entry<K, V>> entrySet() {
            return Collections.unmodifiableSet(super.entrySet());
        }

        public void evict() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.util.CacheDecorator#get(java.lang.Object)
         */
        @Override
        public V get(Object key) {
            return peek((K) key);
        }

        /**
         * @see org.coconut.cache.util.CacheDecorator#getAll(java.util.Collection)
         */
        @Override
        public Map<K, V> getAll(Collection<? extends K> keys) {
            if (keys == null) {
                throw new NullPointerException("keys is null");
            }
            Map<K, V> result = new HashMap<K, V>();
            for (K k : keys) {
                result.put(k, peek(k));
            }
            return result;
        }

        /**
         * @see org.coconut.cache.util.CacheDecorator#getEntry(java.lang.Object)
         */
        @Override
        public CacheEntry<K, V> getEntry(K key) {
            return peekEntry(key);
        }

        public EventBus<CacheEvent<K, V>> getEventBus() {
            throw new UnsupportedOperationException();
        }

        public Set<K> keySet() {
            return Collections.unmodifiableSet(super.keySet());
        }

        public Future<?> loadAll(Collection<? extends K> keys) {
            throw new UnsupportedOperationException();
        }

        public Future<?> load(K key) {
            throw new UnsupportedOperationException();
        }

        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        public V put(K key, V value, long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        public void putAll(Map<? extends K, ? extends V> t) {
            throw new UnsupportedOperationException();
        }

        public void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        public V putIfAbsent(K key, V value) {
            throw new UnsupportedOperationException();
        }

        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        public V replace(K key, V value) {
            throw new UnsupportedOperationException();
        }

        public boolean replace(K key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        public void resetStatistics() {
            throw new UnsupportedOperationException();
        }

        public Collection<V> values() {
            return Collections.unmodifiableCollection(super.values());
        }
    }
}
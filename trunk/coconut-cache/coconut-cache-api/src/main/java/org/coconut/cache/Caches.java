/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.coconut.cache.spi.CacheSupport;
import org.coconut.cache.spi.Ressources;
import org.coconut.cache.util.AbstractCacheLoader;
import org.coconut.cache.util.CacheDecorator;
import org.coconut.cache.util.DefaultCacheEntry;
import org.coconut.event.bus.EventBus;
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
     * A runnable used for calling clear on a cache.
     */
    static class ClearRunnable implements Runnable, Serializable {

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
    }

    static class EvictRunnable implements Runnable, Serializable {

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
    }

    /**
     * The default implementation of a <code>HitStat<code>.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     */
    static final class ImmutableHitStat implements Cache.HitStat, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 2775783950714414347L;

        /** The number of cache hits */
        private final long hits;

        /** The number of cache misses */
        private final long misses;

        /**
         * Constructs a new HitStat.
         * 
         * @param hits
         *            the number of cache hits
         * @param misses
         *            the number of cache misses
         */
        ImmutableHitStat(Cache.HitStat hitstat) {
            this(hitstat.getNumberOfHits(), hitstat.getNumberOfMisses());
        }

        /**
         * Constructs a new HitStat.
         * 
         * @param hits
         *            the number of cache hits
         * @param misses
         *            the number of cache misses
         */
        ImmutableHitStat(long hits, long misses) {
            if (hits < 0) {
                throw new IllegalArgumentException("hits must be 0 or greater");
            } else if (misses < 0) {
                throw new IllegalArgumentException("misses must be 0 or greater");
            }
            this.misses = misses;
            this.hits = hits;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Cache.HitStat)) {
                return false;
            }
            Cache.HitStat hs = (Cache.HitStat) obj;
            return hs.getNumberOfHits() == hits && hs.getNumberOfMisses() == misses;
        }

        /** {@inheritDoc} */
        public float getHitRatio() {
            final long sum = hits + misses;
            if (sum == 0) {
                return Float.NaN;
            }
            return ((float) hits) / sum;
        }

        /** {@inheritDoc} */
        public long getNumberOfHits() {
            return hits;
        }

        /**
         * {@inheritDoc}
         */
        public long getNumberOfMisses() {
            return misses;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            long value = hits ^ misses;
            return (int) (value ^ (value >>> 32));
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return Ressources.getMessageFormatter("org.coconut.cache.hitstat").format(
                    new Object[] { getHitRatio(), hits, misses });
        }
    }

    final static class MapAdapter<K, V> implements Cache<K, V>, Serializable {

        /** serial version UID */
        private static final long serialVersionUID = -5535423001040946603L;

        /** the map to delegate to */
        private final Map<K, V> map;

        MapAdapter(Map<K, V> map) {
            if (map == null)
                throw new NullPointerException("map is null");
            this.map = map;
        }

        /** {@inheritDoc} */
        public void clear() {
            map.clear();
        }

        /** {@inheritDoc} */
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        /** {@inheritDoc} */
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        /** {@inheritDoc} */
        public Set<Entry<K, V>> entrySet() {
            return map.entrySet();
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            return map.equals(o);
        }

        /** {@inheritDoc} */
        public void evict() {
            // ignore
        }

        /** {@inheritDoc} */
        public V get(Object key) {
            return map.get(key);
        }

        /** {@inheritDoc} */
        public Map<K, V> getAll(Collection<? extends K> keys) {
            Map<K, V> result = new HashMap<K, V>(keys.size());
            for (K key : keys) {
                if (key == null) {
                    throw new NullPointerException("collection contains a null element");
                }
                result.put(key, map.get(key));
            }
            return result;
        }

        /** {@inheritDoc} */
        public EventBus<CacheEvent<K, V>> getEventBus() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public Cache.HitStat getHitStat() {
            return STAT00;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return map.hashCode();
        }

        /** {@inheritDoc} */
        public boolean isEmpty() {
            return map.isEmpty();
        }

        /** {@inheritDoc} */
        public Set<K> keySet() {
            return map.keySet();
        }

        /** {@inheritDoc} */
        public Future<?> load(K key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public Future<?> loadAll(Collection<? extends K> keys) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public V peek(Object key) {
            return map.get(key);
        }

        /** {@inheritDoc} */
        public V put(K key, V value) {
            return map.put(key, value);
        }

        /** {@inheritDoc} */
        public V put(K key, V value, long timeout, TimeUnit unit) {
            return put(key, value);
        }

        /** {@inheritDoc} */
        public void putAll(Map<? extends K, ? extends V> t) {
            map.putAll(t);

        }

        /** {@inheritDoc} */
        public void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
            putAll(t);
        }

        /** {@inheritDoc} */
        public V putIfAbsent(K key, V value) {
            if (!map.containsKey(key)) {
                return map.put(key, value);
            } else {
                return map.get(key);
            }
        }

        /** {@inheritDoc} */
        public V remove(Object key) {
            return map.remove(key);
        }

        /** {@inheritDoc} */
        public boolean remove(Object key, Object value) {
            if (map.containsKey(key) && map.get(key).equals(value)) {
                map.remove(key);
                return true;
            } else {
                return false;
            }

        }

        /** {@inheritDoc} */
        public V replace(K key, V value) {
            if (map.containsKey(key)) {
                return map.put(key, value);
            } else {
                return null;
            }
        }

        /** {@inheritDoc} */
        public boolean replace(K key, V oldValue, V newValue) {
            if (map.containsKey(key) && map.get(key).equals(oldValue)) {
                map.put(key, newValue);
                return true;
            } else {
                return false;
            }
        }

        /** {@inheritDoc} */
        public void resetStatistics() {
            // ignore
        }

        /** {@inheritDoc} */
        public int size() {
            return map.size();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return map.toString();
        }

        /** {@inheritDoc} */
        public Collection<V> values() {
            return map.values();
        }

        /** {@inheritDoc} */
        public ReadWriteLock getLock(K... keys) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public CacheQuery<K, V> query(Filter<? super CacheEntry<K, V>> filter) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> getEntry(K key) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link #call}.
     */
    final static class NullCallable<V> implements Callable<V>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 4869209484084557763L;

        /** {@inheritDoc} */
        public V call() {
            return null;
        }
    }

    final static class ExtendedLoaderToLoader<K, V> implements CacheLoader<K, V> {

        private final CacheLoader<K, ? extends CacheEntry<K, V>> loader;

        ExtendedLoaderToLoader(CacheLoader<K, ? extends CacheEntry<K, V>> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public V load(K key) throws Exception {
            CacheEntry<K, V> i = loader.load(key);
            if (i == null) {
                return null;
            } else {
                return i.getValue();
            }
        }

        /** {@inheritDoc} */
        public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
            HashMap<K, V> map = new HashMap<K, V>(keys.size());
            Map<K, ? extends CacheEntry<K, V>> loaded = loader.loadAll(keys);
            for (Map.Entry<K, ? extends CacheEntry<K, V>> e : loaded.entrySet()) {
                if (e.getValue() == null) {
                    map.put(e.getKey(), null);
                } else {
                    map.put(e.getKey(), e.getValue().getValue());
                }
            }
            return map;
        }
    }

    final static class AbstractExtendedLoaderToLoader<K, V> extends
            AbstractCacheLoader<K, V> {

        private final CacheLoader<K, ? extends CacheEntry<K, V>> loader;

        AbstractExtendedLoaderToLoader(CacheLoader<K, ? extends CacheEntry<K, V>> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public V load(K key) throws Exception {
            return loader.load(key).getValue();
        }
    }

    final static class AbstractLoaderToExtendedLoader<K, V> extends
            AbstractCacheLoader<K, CacheEntry<K, V>> {

        private final CacheLoader<K, V> loader;

        AbstractLoaderToExtendedLoader(CacheLoader<K, V> loader) {
            this.loader = loader;
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> load(K key) throws Exception {
            return new DefaultCacheEntry<K, V>(key, loader.load(key));
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
            if (loader == null) {
                throw new NullPointerException("loader is null");
            }
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
        // TODO Get should be peek()??????
        /** serial version UID */
        private static final long serialVersionUID = -8041219332852403222L;

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

        public EventBus<CacheEvent<K, V>> getEventBus() {
            // TODO this is okay, just need to return an unmodifiable event bus
            // we should probably define it in the event bus project
            throw new UnsupportedOperationException();
        }

        public Set<K> keySet() {
            return Collections.unmodifiableSet(super.keySet());
        }

        public Future<?> load(K key) {
            throw new UnsupportedOperationException();
        }

        public Future<?> loadAll(Collection<? extends K> keys) {
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

        @Override
        public ReadWriteLock getLock(K... keys) {
            // TODO perhaps we should return the lock just make
            // The thing is though that Lock does not have any non
            // mutable operations.
            throw new UnsupportedOperationException();
        }
    }

    // static class UnmodifiableReadWriteLock {
    // private final Lock read;
    // private final Lock write;
    // }
    //
    // static class UnmodifiableLock implements Lock{
    // private final Lock lock;
    //
    // /**
    // * @see java.util.concurrent.locks.Lock#lock()
    // */
    // public void lock() {
    // throw new UnsupportedOperationException("lock not supported by
    // unmodifiable cache");
    // }
    //
    // /**
    // * @see java.util.concurrent.locks.Lock#lockInterruptibly()
    // */
    // public void lockInterruptibly() throws InterruptedException {
    // throw new UnsupportedOperationException("lock not supported by
    // unmodifiable cache");
    // }
    //
    // /**
    // * @see java.util.concurrent.locks.Lock#tryLock()
    // */
    // public boolean tryLock() {
    // throw new UnsupportedOperationException("lock not supported by
    // unmodifiable cache");
    // }
    //
    // /**
    // * @see java.util.concurrent.locks.Lock#tryLock(long,
    // java.util.concurrent.TimeUnit)
    // */
    // public boolean tryLock(long time, TimeUnit unit) throws
    // InterruptedException {
    // throw new UnsupportedOperationException("lock not supported by
    // unmodifiable cache");
    // }
    //
    // /**
    // * @see java.util.concurrent.locks.Lock#unlock()
    // */
    // public void unlock() {
    // // TODO Auto-generated method stub
    //            
    // }
    //
    // /**
    // * @see java.util.concurrent.locks.Lock#newCondition()
    // */
    // public Condition newCondition() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    // }
    // /**
    // * Creates a thread-safe Cache that is held entirely in memory. The cache
    // * returned is <tt>not</tt> optimized for heavy concurrency.
    // *
    // * @param policy
    // * the replacement policy that should be used for determining
    // * which elements to evict when the Cache is full.
    // * @param maxSize
    // * the maximum number of elements that the Cache can contain
    // * @return the newly created Cache
    // */
    // public static <K, V> Cache<K, V> newFastMemoryCache(
    // final CachePolicy policy, final int maxSize) {
    // return null;
    // // return new DefaultSynchronousCache<K, V>(policy, maxSize);
    // }

    // /**
    // * Creates a thread-safe Cache that is held entirely in memory. If a
    // request
    // * is made for an element that is not in the cache the cache will try to
    // get
    // * the loader to create or fetch one for it. The cache returned is
    // * <tt>not</tt> optimized for heavy concurrency.
    // *
    // * @param policy
    // * the replacement policy that should be used for determining
    // * which elements to evict when the Cache is full.
    // * @param maxSize
    // * the maximum number of elements that the cache can contain
    // * @param loader
    // * the loader that is used for creating or fetching elements that
    // * are not present in the Cache
    // * @return the newly created Cache
    // */
    // public static <K, V> Cache<K, V> newFastMemoryCache(
    // final CachePolicy policy, final int maxSize,
    // CacheLoader<K, V> loader) {
    // return new CacheEntrySynchronousCache<K, V>(loader);
    // }

    // /**
    // * Creates a new <code>CacheLoader</code> by wrapping multiple loaders.
    // * Any Cache that access the loader will first ask the loader that is
    // first
    // * in the list of loaders. If the object cannot be found there it will ask
    // * the loader that is second in the list and so on. If the value cannot be
    // * found in any of the loaders null is returned. The <code>loadAll</code>
    // * method has similiar semantics. Any map that is returned will return
    // keys
    // * that could be resolved to <code>null</code>.
    // *
    // * @param loaders the list of loaders that we should wrap
    // * @return A CacheLoader that ask each loader by turn.
    // */
    // public static <K, V> ArrayCacheLoader<K, V>
    // newCacfheLoader(CacheLoader<K, V>... loaders) {
    // // CacheLoader[] array = loaders.toArray(new
    // // CacheLoader[loaders.size()]);
    // return new ArrayCacheLoader<K, V>(loaders);
    // }

    /**
     * The empty cache (immutable). This cache is serializable.
     * 
     * @see #emptyCache()
     */
    public static final Cache EMPTY_CACHE;

    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link #call}.
     */
    private static Callable NULL_CALLABLE = new NullCallable();

    public static final Cache.HitStat STAT00 = new ImmutableHitStat(0, 0);

    static {
        EMPTY_CACHE = mapToCache(Collections.emptyMap());
    }

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
     * ses.scheduleAtFixedRate(Caches.clearAsRunnable(c), 0, 3600, TimeUnit.SECONDS);
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
     * Returns the empty cache (immutable). This cache is serializable.
     * <p>
     * This example illustrates the type-safe way to obtain an empty cache:
     * 
     * <pre>
     * Cache&lt;String, Date&gt; s = Caches.emptyCache();
     * </pre>
     * 
     * Implementation note: Implementations of this method need not create a
     * separate <tt>Cache</tt> object for each call. Using this method is
     * likely to have comparable cost to using the like-named field. (Unlike
     * this method, the field does not provide type safety.)
     * 
     * @see #EMPTY_CACHE
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Cache<K, V> emptyCache() {
        return (Cache<K, V>) EMPTY_CACHE;
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
    public static <K, V> CacheLoader<K, V> cacheAsLoader(Cache<K, V> c) {
        return new CacheAsCacheLoader<K, V>(c);
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
     * Wraps a Map inside a cache.
     * 
     * @param map
     *            the map to wrap
     * @return a Cache wrapping a map
     */
    public static <K, V> Cache<K, V> mapToCache(Map<K, V> map) {
        return new MapAdapter<K, V>(map);
    }

    /**
     * Creates new a new HitStat object with the same number of hits and misses
     * as the specified HitStat.
     * 
     * @param copyFrom
     *            the HitStat to copy from
     * @return
     */
    public static Cache.HitStat newImmutableHitStat(Cache.HitStat copyFrom) {
        return new ImmutableHitStat(copyFrom);
    }

    public static Cache.HitStat newImmutableHitStat(long hits, long misses) {
        return new ImmutableHitStat(hits, misses);
    }

    /**
     * Constructs a new Cache which is held entirely in memory and which
     * capacity is only limited by the the amount of physical memory. This Cache
     * is optimized for a high amount of Retrieval operations (get) and
     * generally do not block these operations, so may overlap with update
     * operations (including put and remove). For aggregate operations such as
     * <tt>putAll</tt> and <tt>clear</tt>, concurrent retrievals may
     * reflect insertion or removal of only some entries. Similarly, Iterators
     * and Enumerations return elements reflecting the state of the cache at
     * some point at or since the creation of the iterator/enumeration. They do
     * <em>not</em> throw {@link java.util.ConcurrentModificationException}.
     * However, iterators are designed to be used by only one thread at a time.
     * <p>
     * The following special conditions apply:
     * <ul>
     * <li> {@link Cache#getEventBus()} is not supported and throws an
     * {@link java.lang.UnsupportedOperationException}.
     * <li> {@link Cache#getHitStat()} returns a instance of a
     * {@link Cache.HitStat} with no records of hits or misses and a ratio of
     * -1.
     * <li> Calls to {@link Cache#evict()}, {@link Cache#load(Object)},
     * {@link Cache#loadAll(Collection)} and {@link Cache#resetStatistics()} has
     * no effect and returns immediatly.
     * </ul>
     * <p>
     * 
     * @return a new unlimited cache
     */
    public static <K, V> Cache<K, V> newUnlimitedCache() {
        // return new CacheEntrySynchronousCache<K, V>();
        return null;
    }

    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link java.util.concurrent.Callable#call}.
     */
    @SuppressWarnings("unchecked")
    public static <V> Callable<V> nullCallable() {
        return NULL_CALLABLE;
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
            return new SynchronizedAbstractCacheLoader<K, V>(loader);
        } else {
            return new SynchronizedCacheLoader<K, V>(loader);
        }
    }

    public static <K, V> CacheLoader<K, V> asOrdinaryCacheLoader(
            CacheLoader<K, ? extends CacheEntry<K, V>> loader) {
        if (loader instanceof AbstractCacheLoader) {
            return new AbstractExtendedLoaderToLoader<K, V>(loader);
        } else {
            return new ExtendedLoaderToLoader<K, V>(loader);
        }
    }

    public static <K, V> CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> asCacheLoader(
            CacheLoader<K, V> loader) {
        return new AbstractLoaderToExtendedLoader(loader);
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

    public static boolean supportsJMX(Cache<?, ?> cache) {
        return cache.getClass().isAnnotationPresent(CacheSupport.class)
                && cache.getClass().getAnnotation(CacheSupport.class).JMXSupport();
    }

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Caches() {
    }
    // /CLOVER:ON
}
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
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.spi.CacheSupport;
import org.coconut.cache.spi.CacheUtil;
import org.coconut.event.EventBus;
import org.coconut.filter.Filter;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Caches2 {

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
        public CacheEntry<K, V> getEntry(Object key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public EventBus<CacheEvent<K, V>> getEventBus() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public Cache.HitStat getHitStat() {
            return CacheUtil.STAT00;
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
        public Future<?> loadAll(Collection<? extends K> keys) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public Future<?> load(K key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public V peek(Object key) {
            return map.get(key);
        }

        /**
         * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
         */
        public CacheEntry<K, V> peekEntry(Object key) {
            throw new UnsupportedOperationException();
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
    }

    /**
     * The empty cache (immutable). This cache is serializable.
     * 
     * @see #emptyCache()
     */
    public static final Cache EMPTY_CACHE;

    static {
        EMPTY_CACHE = mapToCache(Collections.emptyMap());
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
    public static <K, V> Cache<K, V> newUnsynchronizedCache() {
        // return new CacheEntrySynchronousCache<K, V>();
        return null;
    }
    public static boolean supportsJMX(Cache<?, ?> cache) {
        return cache.getClass().isAnnotationPresent(CacheSupport.class)
                && cache.getClass().getAnnotation(CacheSupport.class).JMXSupport();
    }
    public static Filter<CacheEntry> checkUpdateTime(String cronExpression) {
        return null;
    }



    static class CronUpdateTimeChecker implements Filter<CacheEntry> {

        /**
         * @see org.coconut.filter.Filter#accept(java.lang.Object)
         */
        public boolean accept(CacheEntry element) {
            long updateTime = element.getLastUpdateTime();
            // TODO Auto-generated method stub
            return false;
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
}

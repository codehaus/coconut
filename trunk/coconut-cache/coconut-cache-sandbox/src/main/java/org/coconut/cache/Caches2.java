/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.coconut.cache.spi.CacheSupport;
import org.coconut.filter.Filter;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Caches2 {

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
     * This class wraps a cache in such a way that it can be used as a cache
     * loader for another cache.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
     * @version $Id: Caches.java 182 2007-01-14 22:25:59Z kasper $
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

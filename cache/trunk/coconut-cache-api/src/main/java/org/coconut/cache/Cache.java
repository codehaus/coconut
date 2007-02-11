/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A <tt>cache</tt> is a collection of data duplicating original values stored
 * elsewhere or computed earlier, where the original data are expensive (usually
 * in terms of access time) to fetch or compute relative to reading from the
 * cache. Once the data are stored in the cache, future use can be made by
 * accessing the cached copy rather than refetching or recomputing the original
 * data, so that the average access time is lowered.
 * <p>
 * Currently Coconut cache only supports two types of caches that are entirely
 * held in memory: {@link org.coconut.cache.defaults.UnsynchronizedCache} and
 * {@link org.coconut.cache.defaults.SynchronizedCache}.
 * <p>
 * Coconut Cache is made up of a number of core classes. This <a href="{@docRoot}/index.html">page</a>
 * tries to explain how they relate.
 * <p>
 * The three collection views, which allow a cache's contents to be viewed as a
 * set of keys, collection of values, or set of key-value mappings only shows
 * values contained in the actual cache not any values that is stored in any
 * CacheLoader. Furthermore, the cache will <tt>not</tt> attempt to fetch
 * updated values for entries that has expired when calling methods on any of
 * the collection views.
 * <p>
 * All general-purpose <tt>Cache</tt> implementation classes should provide
 * three "standard" constructors: a void (no arguments) constructor, which
 * creates an empty cache with default settings, a constructor with a single
 * argument of type Map, which creates a new cache with the same key-value
 * mappings as its argument, and finally a constructor with a single argument of
 * type {@link CacheConfiguration}. There is no way to enforce this
 * recommendation (as interfaces cannot contain constructors) but all of the
 * general-purpose cache implementations in Coconut Cache comply.
 * <p>
 * Cache implementations generally do not define element-based versions of the
 * <tt>equals</tt> and <tt>hashCode</tt> methods, but instead inherit the
 * identity-based versions from class <tt>Object</tt>. Nore, are they
 * generally serializable.
 * <p>
 * Unlike {@link java.util.HashMap}, a <tt>cache</tt> does NOT allow
 * <tt>null</tt> to be used as a key or value. It is the authors belief that
 * allowing null values (or keys) does more harm then good, by masking what are
 * almost always usage errors. If nulls are absolutely needed the <a
 * href="http://today.java.net/today/2004/12/10/refactor.pdf">Null Object
 * Pattern</a> can be used as an alternative.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java 130 2006-10-11 13:00:46Z kasper $
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
public interface Cache<K, V> extends ConcurrentMap<K, V> {

    /**
     * Used in {@link #put(Object, Object, long, TimeUnit)} and
     * {@link #putAll(Map, long, TimeUnit)} to specify that an element should
     * use the default expiration time configured for the cache or never expire
     * if no such default value has been configured for the cache.
     */
    static final long DEFAULT_EXPIRATION = 0;

    /**
     * Used in {@link #put(Object, Object, long, TimeUnit)} and
     * {@link #putAll(Map, long, TimeUnit)} to specify that an element should
     * never expire.
     */
    static final long NEVER_EXPIRE = Long.MAX_VALUE;

    /**
     * Performs cleanup of the cache. This might be everything from persisting
     * stale data to disk to adapting the cache with a better eviction policy
     * given the current access pattern. This is done to avoid paying the cost
     * upfront by application threads when accessing entries in the cache
     * through {@link #get(Object)} or {@link #getAll(Collection)}.
     * <p>
     * Unless otherwise specified calling this method is the responsibility of
     * the user. The typical usage is to create a single thread that
     * periodically runs this method.
     * <p>
     * Implementations that block (stop-the-world) all other concurrent access
     * to the cache by calling this method should clearly specified it.
     * 
     * @see Caches#evictAsRunnable(Cache)
     */
    void evict();

    /**
     * Attempts to retrieve all of the mappings for the specified collection of
     * keys. The effect of this call is equivalent to that of calling
     * {@link #get(Object)} on this cache once for each key in the specified
     * collection. However, in some cases it can be much faster to load several
     * cache items at once, for example, if the cache must fetch the values from
     * a remote host.
     * <p>
     * If a value is not contained in the cache and the value cannot be loaded
     * by any of the configured cache backends. The returned map will contain a
     * mapping from the key to <tt>null</tt>.
     * <p>
     * The behavior of this operation is unspecified if the specified collection
     * is modified while the operation is in progress.
     * 
     * @param keys
     *            the keys to get.
     * @return a map with mappings from each key to the corresponding value, or
     *         to <tt>null</tt> if no mapping for this key exists.
     * @throws ClassCastException
     *             if any of the keys in the specified collection are of an
     *             inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified collection of keys is <tt>null</tt> or the
     *             specified collection contains <tt>null</tt>
     */
    Map<K, V> getAll(Collection<? extends K> keys);

    /**
     * Returns the current <tt>hit statistics</tt> for the cache (optional
     * operation). The returned object is a immutable snapshot that only
     * reflects the current state of the cache at the calling time.
     * 
     * @return the current hit statistics
     * @throws UnsupportedOperationException
     *             if gathering of statistics is not supported by this cache.
     */
    Cache.HitStat getHitStat();

    // determine how exceptions are thrown from the future
    /**
     * Attempts to load the value for the specified key from a configured cache
     * backend. This method is usefull for preloading the cache with entries
     * that might be used at later time. Preloading attempts to place data in
     * the cache far enough in advance to hide the latency of a cache miss.
     * <p>
     * A {@link java.util.concurrent.Future} is returned that can be used to
     * check if the loading is complete and to wait for its completion.
     * Cancellation can be performed by the using the
     * {@link java.util.concurrent.Future#cancel(boolean)} method.
     * <p>
     * This method does not guarantee that the specified value is ever loaded
     * into the cache. Implementations are free to ignore the hint, however,
     * most implementations won't. If the implementation chooses to ignore some
     * or all calls to this method. The returned futures
     * {@link java.util.concurrent.Future#isCancelled()} method will return
     * <tt>true</tt> and {@link java.util.concurrent.Future#get()} returns
     * <tt>null</tt>.
     * <p>
     * If no backend, cache loader or cache store, is configured for the cache a
     * call to this method is silently ignored. And the returned futures
     * {@link java.util.concurrent.Future#isCancelled()} method will return
     * <tt>true</tt>
     * <p>
     * Unless otherwise specified the loading is done asynchronously. Any cache
     * implementation that is not thread-safe (ie supposed to be accessed by a
     * single thread only) will need to load the value before returning from
     * this method. Because it cannot allow a background thread to set the value
     * once loaded.
     * 
     * @param key
     *            whose associated value is to be loaded.
     * @return a Future representing pending completion of the load, and whose
     *         <tt>get()</tt> method will return <tt>null</tt> upon
     *         completion.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache
     *             (optional).
     * @throws UnsupportedOperationException
     *             if the implementation does not support asynchronously load of
     *             elements
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    Future<?> load(K key);

    /**
     * Attempts to asynchronously load the values to which this cache maps the
     * specified keys. The effect of this call is equivalent to that of calling
     * {@link #load(Object)}once for each mapping from key k to value v in the
     * specified map. However certain implementation might take advantage of
     * bulk loading.
     * <p>
     * The behavior of this operation is unspecified if the specified collection
     * is modified while the operation is in progress.
     * <p>
     * The methods on the returned {@link java.util.concurrent.Future} are all
     * <tt>bulk</tt> operations.
     * {@link java.util.concurrent.Future#cancel(Boolean)} will attempt to
     * cancel the loading of all entries.{@link
     * java.util.concurrent.Future#get()} will not return until all entries has
     * been loaded.
     * <p>
     * 
     * @param keys
     *            whose associated values is to be loaded.
     * @throws ClassCastException
     *             if any of the keys in the specified collection are of an
     *             inappropriate type for this cache (optional).
     * @throws UnsupportedOperationException
     *             if the implementation does not support asynchronously load of
     *             elements
     * @throws NullPointerException
     *             if the specified collection of keys is <tt>null</tt> or the
     *             specified collection contains <tt>null</tt> values
     */
    Future<?> loadAll(Collection<? extends K> keys);

    /**
     * This method works analogoes to the {@link java.util.Map#get(Object)}
     * method. However, it will not try to fetch missing items in any configured
     * backend, it will only return a value if it actually exists in the cache.
     * Furthermore, it will not effect the statistics gathered by the cache and
     * no {@link CacheItemEvent.ItemAccessed} event will be raised. Finally,
     * even if the item has expired it will still be returned by this method.
     * <p>
     * All implementations of this method should take care to assure that a call
     * to peek does not have any side effects on the cache or any retrived
     * value.
     * 
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this cache maps the specified key, or
     *         <tt>null</tt> if the cache contains no mapping for this key.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache
     *             (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    V peek(K key);

    /**
     * Works as {@link java.util.Map#get(Object)} with the following
     * modification.
     * <p>
     * If no mapping exists for the specified key and a backend has been
     * configured for the cache. The cache will attempt to load a value for the
     * specified key through the backend.
     * 
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this map
     *             (optional).
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     * @throws CacheException
     *             if the any configured backend store failed while trying to
     *             load a value (optional).
     */
    V get(Object key);

    /**
     * Associates the specified value with the specified key in this cache
     * (optional operation). If the cache previously contained a mapping for
     * this key, the old value is replaced by the specified value. (A cache
     * <tt>c</tt> is said to contain a mapping for a key <tt>k</tt> if and
     * only if {@link #containsKey(Object) c.containsKey(k)} would return
     * <tt>true</tt>.))
     * <p>
     * It is often more effective to specify a {@link CacheLoader} that
     * implicitly loads values then to explicitly add them to cache using the
     * various <tt>put</tt> and <tt>putAll</tt> methods.
     * <p>
     * If a backend store is configured for the cache. The value might be stored
     * in this store.
     * 
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @param timeout
     *            the time from now to when the element can be expired
     * @param unit
     *            the time unit of the timeout parameter.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this
     *             cache.
     * @throws ClassCastException
     *             if the class of the specified key or value prevents it from
     *             being stored in this cache.
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being
     *             stored in this cache.
     * @throws NullPointerException
     *             if the specified key, value or timeunit is <tt>null</tt>.
     */
    V put(K key, V value, long timeout, TimeUnit unit);

    /**
     * Copies all of the mappings from the specified map to this cache (optional
     * operation). The effect of this call is equivalent to that of calling
     * {@link #put(Object,Object,long,TimeUnit) put(k, v,time,unit)} on this map
     * once for each mapping from key <tt>k</tt> to value <tt>v</tt> in the
     * specified map.
     * 
     * @param t
     *            Mappings to be stored in this cache.
     * @param timeout
     *            the time from now to when the elements can be expired
     * @param unit
     *            the time unit of the timeout parameter.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this
     *             cache.
     * @throws ClassCastException
     *             if the class of a key or value in the specified map prevents
     *             it from being stored in this cache.
     * @throws IllegalArgumentException
     *             some aspect of a key or value in the specified map prevents
     *             it from being stored in this cache.
     * @throws NullPointerException
     *             if the specified map is <tt>null</tt>, contains
     *             <tt>null</tt> keys or values or the specified timeunit is
     *             <tt>null</tt>.
     */
    void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit);

    /**
     * Resets the hit ratio.
     * <p>
     * The number of hits returned by individual items
     * {@link CacheEntry#getHits()} are not affected by calls to this method.
     * 
     * @throws UnsupportedOperationException
     *             if the cache does not allow resetting the cache statistics
     *             (read-only cache)
     */
    void resetStatistics();

    /**
     * Retrieves a {@link CacheEntry} for the specified key (optional). If no
     * entry exists for the specified key any configured cache backend is asked
     * to try and fetch an entry for the key.
     * <p>
     * 
     * @param key
     *            whose associated cache entry is to be returned.
     * @return the cache entry to which this cache maps the specified key, or
     *         <tt>null</tt> if the cache contains no mapping for this key.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache
     *             (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     * @throws UnsupportedOperationException
     *             if the cache does not cache entries
     */
    CacheEntry<K, V> getEntry(K key);

    /**
     * This method works analogoes to the {@link #peek(Object)} method. However,
     * it will return a cache entry instead of just the value.
     * <p>
     * Just like {@link #peek(Object)} any implementation of this method should
     * take care to assure that the call does not have any side effects on the
     * cache or any retrived entry.
     * 
     * @param key
     *            key whose associated value is to be returned.
     * @return the cache entry to which this cache maps the specified key, or
     *         <tt>null</tt> if the cache contains no mapping for this key.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache
     *             (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    CacheEntry<K, V> peekEntry(K key);


    <T> T getService(Class<T> serviceType);
    
    /**
     * The class holds the hit statistics for a cache. Unless otherwise
     * specified implementations of this interface is immutable.
     */
    interface HitStat {

        /**
         * Returns the ratio between cache hits and misses or
         * {@link java.lang.Double#NaN} if no hits or misses has been recorded.
         * 
         * @return the ratio between cache hits and misses or NaN if no hits or
         *         misses has been recorded
         */
        float getHitRatio();

        /**
         * Returns the number of succesfull hits for a cache. A request to a
         * cache is a hit if the value is already contained within the cache and
         * no external cache backends must be used to fetch the value.
         * 
         * @return the number of hits
         */
        long getNumberOfHits();

        /**
         * Returns the number of cache misses. A request is a miss if the value
         * is not already contained within the cache when it is requested and a
         * cache backend must fetch the value.
         * 
         * @return the number of cache misses.
         */
        long getNumberOfMisses();
    }
}

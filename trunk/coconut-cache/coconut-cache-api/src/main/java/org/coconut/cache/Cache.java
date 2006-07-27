/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for
 details.
 */
package org.coconut.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.coconut.event.bus.EventBus;
import org.coconut.filter.Filter;

/**
 * A <tt>cache</tt> is a collection of data duplicating original values stored
 * elsewhere or computed earlier, where the original data are expensive (usually
 * in terms of access time) to fetch or compute relative to reading from the
 * cache. Once the data are stored in the cache, future use can be made by
 * accessing the cached copy rather than refetching or recomputing the original
 * data, so that the average access time is lowered.
 * <p>
 * Coconut cache supports a wide range of different cache implementations.
 * Ranging from simple unsynchronized caches to highly concurrent distributed
 * caches. See a list of all the various cache implementations <a
 * href="http://org.coconut.codehaus.org/cache/cache-implementations.html">here</a>.
 * <p>
 * This <a href="{@docRoot}/index.html">page</a> details how the various
 * cache classes relate.
 * <p>
 * The first level is thread safety/performance unsynchronized cache, for
 * example, {@link org.coconut.cache.impl.memory.UnlimitedCache} synchronized
 * caches which offers thread safety. concurrent caches with offers thread
 * safery and concurrent retrievels on the expense of features Coconut comes
 * with build-in support for the following types of cache. TODO What is a cache
 * interface Distributed.. serializable CachePolicies.
 * <p>
 * The three collection views, which allow a cache's contents to be viewed as a
 * set of keys, collection of values, or set of key-value mappings only shows
 * values contained in the actual cache not in any backend storages.
 * Furthermore, it will <tt>not</tt> attempt to fetch updated values for
 * entries that has expired.
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
 * Unlike {@link java.util.HashMap}, a <code>cache</code> does NOT allow
 * <tt>null</tt> to be used as a key or value. It is the authors belief that
 * allowing null values (or keys) does more harm then good, by masking what are
 * almost always usage errors. If nulls are absolutely needed the <a
 * href="http://today.java.net/today/2004/12/10/refactor.pdf">Null Object
 * Pattern</a> can be used as an alternative.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
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
     * through {@link #get(Object)}.
     * <p>
     * Unless otherwise specified calling this method is the responsibility of
     * the programmer. The typical usage is to create a single thread that
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
     * mapping from the key to <code>null</code>.
     * <p>
     * The behavior of this operation is unspecified if the specified collection
     * is modified while the operation is in progress.
     * 
     * @param keys
     *            the keys to get.
     * @return a map with mappings from each key to the corresponding value, or
     *         to <code>null</code> if no mapping for this key exists.
     * @throws ClassCastException
     *             if any of the keys in the specified collection are of an
     *             inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified collection of keys is <tt>null</tt> or the
     *             specified collection contains <tt>null</tt>
     */
    Map<K, V> getAll(Collection<? extends K> keys);

    /**
     * Returns the {@link org.coconut.event.bus.EventBus} attached to this cache
     * (optional operation). The event bus can be used for getting notications
     * about various {@link CacheEvent events} that is being raised internally
     * in the cache.
     * 
     * @throws UnsupportedOperationException
     *             if the cache does not support notifications of events in the
     *             cache.
     * @see CacheEvent
     * @see CacheItemEvent
     */
    EventBus<CacheEvent<K, V>> getEventBus();

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
     * This method does not guarantee that the specified value is ever loaded
     * into the cache. Implementations are free to ignore the hint, however,
     * most implementations won't. If the implementation chooses to ignore some
     * or all calls to this method. The returned futures
     * {@link java.util.concurrent.Future#isCancelled()} method will return
     * <tt>true</tt>
     * <p>
     * A {@link java.util.concurrent.Future} is returned that can be used to
     * check if the loading is complete and to wait for its completion.
     * Cancellation can be performed by the using the
     * {@link java.util.concurrent.Future#cancel(boolean)} method.
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
     * @return a Future representing pending completion of the task, and whose
     *         <tt>get()</tt> method will return <code>null</code> upon
     *         completion.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache
     *             (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    Future<?> load(K key);

    /**
     * Attempts to asynchronously load the values to which this cache maps the
     * specified keys. The effect of this call is equivalent to that of calling
     * {@link #load(Object)} on this cache once for each mapping from key k to
     * value v in the specified map. The behavior of this operation is
     * unspecified if the specified collection is modified while the operation
     * is in progress.
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
     * @throws NullPointerException
     *             if the specified collection of keys is <tt>null</tt> or the
     *             specified collection contains <tt>null</tt> values
     */
    Future<?> loadAll(Collection<? extends K> keys);

    /**
     * This method works analogoes to the {@link java.util.Map#get(Object)}
     * method. However, it will not try to fetch missing items in any configured
     * backend, it will only return a value if it actually exists in the cache.
     * Furthermore it will not effect the statistics gathered by the cache and
     * no {@link CacheItemEvent.ItemAccessed} event will be raised.
     * <p>
     * Some implementations might use this as a hint to prefetch the specified
     * element if it is not already in the cache.
     * <p>
     * TODO does this check for expired items????
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
    V peek(Object key);

    /**
     * Works as {@link java.util.Map#get(Object)} with the following
     * modifications.
     * <p>
     * If no mapping exists for the given any configured cache backend is asked
     * to try and fetch a value for the specified key.
     * <p>
     * TODO What happens when a get fails on a backend store. throw exception
     * right? Perhaps point to the method in spi.loadableCache that can be
     * overridden to alter the behaviour
     * <p>
     * Unlike {@link java.util.Map#get(Object)} throwing a
     * {@link NullPointerException} is not optional when <tt>null</tt> is
     * specified as the key.
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
     *             being stored in this cacje.
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
     * Tries to acquire a lock on either the cache instance, a single item in
     * the cache, or a set of items in the cache depending on the specified
     * arguments. This operation is optional.
     * <p>
     * getLock() : A call to getLock() with no arguments will return a lock that
     * can be used to acquire a lock on the whole cache. Read and write lock
     * should have semantics as the getLock(all items). Furthermore there are
     * the additional constraints. Locks cannot be acquired on non existing
     * items
     * <p>
     * getLock(k) : a call to getLock() with a single key argument will return a
     * lock that can be used to acquire a lock on the cache entry with the
     * specified key. trying to acquire a lock on an item that does not exist
     * will return a d
     * <p>
     * getLock(k1, k2, k3) = a call to getLock() with multiple key arguments
     * will return a lock that can be used to acquire a lock on all the cache
     * entries with the specified keys. //use lock comperator or natural
     * ordering What about locks on non existing keys??? we might for example
     * want to insert 2,3,4,5 and then unlock when all have been inserted. We
     * could use finalization/weak queue. Using this method for locking multiple
     * elements is prefereble to locking one element at a time because it avoids
     * potential deadlock.
     * <p>
     * TODO Sematics of read lock versus write lock
     * <p>
     * Unless otherwise specified the lock is held by the thread that locks the
     * lock.
     * <p>
     * Might want to set useOnlyWriteLock=true to save the overhead of a full
     * read-writelock implementation.
     * 
     * @param keys
     * @return a read write lock that can be used to lock a specific element,
     *         elements or the whole cache depending on the usage
     * @throws IllegalArgumentException
     *             if multiple key arguments are no natural order exists among
     *             the keys and no lock key comparator has been specified for
     *             the cache
     * @throws NullPointerException
     *             if any of the specified keys are <tt>null</tt>.
     * @throws UnsupportedOperationException
     *             if the <tt>getLock</tt> method is not supported by this
     *             cache
     */
    ReadWriteLock getLock(K... keys);

    /**
     * Resets the hit ratio.
     * <p>
     * The number of hits returned by {@link CacheEntry#getHits()} is not
     * affected by calls to this method.
     * 
     * @throws UnsupportedOperationException
     *             if the cache does not allow resetting the cache statistics
     *             (read-only cache)
     */
    void resetStatistics();

    /**
     * Retrieves a {@link CacheEntry} for the specified key (optional). If no
     * entry exists for the specified key <code>null</code> is returned. This
     * method will not attempt to fetch a missing value from any configured
     * cache backends.
     * <p>
     * But actually why not why would anyone call this method if they don't want
     * the value. It can always be guarded by an containsKey() call // * as get,
     * will load value on miss, // functions as a cache miss if value is not
     * there? // maybe it should just work as peek(); // * If it loads values it
     * should function as a cache -> don't fetch values on getEntry() method But
     * if we load entries, we should probably also supply a get all entries
     * (Collection) as load+get.
     * <p>
     * Come up with example usage
     * <p>
     * TODO: Determine whether or not this entry loads
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
     */
    CacheEntry<K, V> getEntry(K key);

    /**
     * This method is used to make queries into the cache locating cache entries
     * that match a particular criteria. The returned {@link CacheQuery} can be
     * used for returning all the matching cache entries at once or just a small
     * subset (paging functionality) at a time.
     * <p>
     * NOTICE: querying a cache can be a very time consuming affair especially
     * if no usefull indexes are available at query time.
     * <p>
     * If a backend stored is configured <tt>and</tt> it supports querying it
     * will be used for querying otherwise only the local cache will be queried.
     * 
     * @param filter
     *            the filter used to identify which entries should be retrieved
     * @return a cache query that can be used to retrieve the matching entries
     */
    CacheQuery<K, V> query(Filter<? super CacheEntry<K, V>> filter);

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

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.pocket;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface PocketCache<K, V> extends ConcurrentMap<K, V> {

    /**
     * Performs cleanup of the cache. This might be everything from persisting
     * removing stale entries to adapting the cache with a better eviction
     * policy given the current access pattern. This is done to avoid paying the
     * cost upfront by application threads when accessing entries in the cache
     * through {@link #get(Object)}.
     * <p>
     * Unless otherwise specified calling this method is the responsibility of
     * the user. The typical usage is to create a single thread that
     * periodically runs this method.
     * <p>
     * Implementations that block (stop-the-world) all other concurrent access
     * to the cache by calling this method should clearly specified it.
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
     * by the configured {@link ValueLoader}. The returned map will contain a
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
     * Works as {@link java.util.Map#get(Object)} with the following
     * modifications.
     * <p>
     * If no mapping exists for the specified key in the cache. The cache will
     * attempt to retrieve the value from the configured {@link ValueLoader}.
     * If the value can be succesfully retrieved from the loader it will
     * automatically be put into the cache.
     */
    V get(Object key);

    /**
     * This method works analogoes to the {@link java.util.Map#get(Object)}
     * method. However, it will not try to fetch missing items from the
     * configured {@link ValueLoader}, it will only return a value if it
     * actually exists in the cache. Furthermore, calling this method does not
     * effect the statistics gathered by the cache.
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
     * Resets the hit ratio (optional).
     * 
     * @throws UnsupportedOperationException
     *             if the cache does not allow resetting the cache statistics
     *             (read-only cache)
     */
    void resetStatistics();

    /**
     * Returns the ratio between cache hits and misses or
     * {@link java.lang.Double#NaN} if no hits or misses has been recorded.
     * 
     * @return the ratio between cache hits and misses or NaN if no hits or
     *         misses has been recorded
     */
    double getHitRatio();

    /**
     * Returns the number of succesfull hits for a cache. A request to a cache
     * is a hit if the value is already contained within the cache and no
     * external cache backends must be used to fetch the value.
     * 
     * @return the number of hits
     */
    long getNumberOfHits();

    /**
     * Returns the number of cache misses. A request is a miss if the value is
     * not already contained within the cache when it is requested and a cache
     * backend must fetch the value.
     * 
     * @return the number of cache misses.
     */
    long getNumberOfMisses();

    /**
     * Evicts elements until the size of the cache is the specified new size. If
     * the specified newSize is greater then the current size nothing will
     * happen
     * 
     * @param newSize
     *            the size of the cache
     * @throws IllegalArgumentException
     *             if the specified newSize is <0
     */
    void trimToSize(int newSize);

    // TODO: Does remove trim down to default Trim size?
    void setEvictWatermark(int trimSize);

    int getEvictWatermark();

    /**
     * Returns the maximum number of elements this cache can hold or
     * {@link java.lang.Integer#MAX_VALUE} if no hard limit exist or the cache
     * does not support a hard limit.
     * 
     * @see #setCapacity(int)
     */
    int getCapacity();

    /**
     * Sets the hard limit of the the cache (optional operation).
     * <p>
     * If the specified new limit is less then the current size of the cache. An
     * implementation can choose to evict entries until the size of the cache.
     * However, this is an implementation specific detail.
     * <p>
     * If defaultTrimSize is greater or equal to the new hard limit then set
     * trimSize to hardLimit-1
     * 
     * @throws IllegalArgumentException
     *             if the specified limit is not a positive number (>0)
     * @throws UnsupportedOperationException
     *             if the cache does not support a hard limit of the number of
     *             cache entries.
     */
    void setCapacity(int limit);
}

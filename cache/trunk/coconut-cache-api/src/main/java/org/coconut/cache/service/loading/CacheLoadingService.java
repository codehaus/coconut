/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.core.AttributeMap;
import org.coconut.core.Transformer;
import org.coconut.filter.Filter;

/**
 * This is the main interface for controlling the remote management of a cache at runtime.
 * <p>
 * Most of the methods for this service is usefull for preloading the cache with entries
 * that might be used at later time. Preloading attempts to place data in the cache far
 * enough in advance to hide the latency of a cache miss.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheLoadingService<K, V> {

    /**
     * Returns the default expiration time for entries. If entries never expire,
     * {@link Long#MAX_VALUE} is returned.
     * 
     * @param unit
     *            the time unit that should be used for returning the default expiration
     * @return the default expiration time for entries, or {value Long#MAX_VALUE} if
     *         entries never expire
     */
    long getDefaultTimeToRefresh(TimeUnit unit);

    /**
     * Sets the default expiration time for new objetcs that are added to the cache. If no
     * default expiration time has been set, entries will never expire.
     * 
     * @param timeToLive
     *            the time from insertion to the point where the entry should expire
     * @param unit
     *            the time unit of the timeToLive argument
     * @throws IllegalArgumentException
     *             if the specified time to live is negative (<0)
     * @throws NullPointerException
     *             if the specified time unit is <tt>null</tt>
     * @see #getDefaultTimeToRefresh(TimeUnit)
     */
    void setDefaultTimeToRefresh(long timeToLive, TimeUnit unit);

    /**
     * Attempts to reload all the cache entries that is accepted by the specified filter.
     * 
     * @param filter
     *            the filter to test cache entries against
     * @return A future indicating pending load of all the entries
     */
    void filteredLoad(Filter<? super CacheEntry<K, V>> filter);

    void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            AttributeMap defaultAttributes);

    void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer);

    /**
     * Attempts to load a value for the specified key even if a valid mapping for the
     * specified key is already in the cache. Otherwise it would like
     * {@link #load(Object)}
     * 
     * @param key
     *            whose associated value is to be loaded.
     * @return a Future representing pending completion of the load, and whose
     *         <tt>get()</tt> method will return <tt>null</tt> upon completion.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    void forceLoad(K key);

    void forceLoad(K key, AttributeMap attributes);

    void forceLoadAll(AttributeMap attributes);

    void forceLoadAll(Collection<? extends K> keys);

    void forceLoadAll(Map<K, AttributeMap> mapsWithAttributes);

    /**
     * If a mapping for the specified key is not already in the cache. This method will
     * attempt to load the value for the specified key from a configured cache loader.
     * <p>
     * A {@link java.util.concurrent.Future} is returned that can be used to check if the
     * loading is complete and to wait for its completion. Cancellation can be performed
     * by the using the {@link java.util.concurrent.Future#cancel(boolean)} method.
     * <p>
     * This method does not guarantee that the specified value is ever loaded into the
     * cache. Implementations are free to ignore the hint, however, most implementations
     * won't. If the implementation chooses to ignore some or all calls to this method.
     * The returned futures {@link java.util.concurrent.Future#isCancelled()} method will
     * return <tt>true</tt> and {@link java.util.concurrent.Future#get()} returns
     * <tt>null</tt>.
     * <p>
     * Unless otherwise specified the loading is done asynchronously. Any cache
     * implementation that is not thread-safe (ie supposed to be accessed by a single
     * thread only) will need to load the value before returning from this method. Because
     * it cannot allow a background thread to set the value once loaded.
     * 
     * @param key
     *            whose associated value is to be loaded.
     * @return a Future representing pending completion of the load, and whose
     *         <tt>get()</tt> method will return <tt>null</tt> upon completion.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    void load(K key);

    void load(K key, AttributeMap attributes);

    /**
     * Attempts to asynchronously load the values to which this cache maps the specified
     * keys. The effect of this call is equivalent to that of calling
     * {@link #load(Object)}once for each mapping from key k to value v in the specified
     * map. However, it is possible for implementations to take advantage of bulk loading.
     * <p>
     * The behavior of this operation is unspecified if the specified collection is
     * modified while the operation is in progress.
     * <p>
     * The methods on the returned {@link java.util.concurrent.Future} are all
     * <tt>bulk</tt> operations that operate on the entire keyset.
     * {@link java.util.concurrent.Future#cancel(boolean)} will attempt to cancel the
     * loading of all entries.{@link java.util.concurrent.Future#get()} will not return
     * until all entries has been loaded.
     * <p>
     * 
     * @param keys
     *            whose associated values is to be loaded.
     * @throws ClassCastException
     *             if any of the keys in the specified collection are of an inappropriate
     *             type for this cache (optional).
     * @throws UnsupportedOperationException
     *             if the implementation does not support asynchronously load of elements
     * @throws NullPointerException
     *             if the specified collection of keys is <tt>null</tt> or the specified
     *             collection contains <tt>null</tt> values
     */
    void loadAll(Collection<? extends K> keys);

    void loadAll(Map<K, AttributeMap> mapsWithAttributes);

    /**
     * Attempts to reload all entries that are currently held in the cache.
     * 
     * @return a Future representing pending completion of all the loads, and whose
     *         <tt>get()</tt> method will return <tt>null</tt> upon completion.
     */
    void reloadAll();
}

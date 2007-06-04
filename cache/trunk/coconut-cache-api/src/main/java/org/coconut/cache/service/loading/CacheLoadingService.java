/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

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
     * Attempts to reload all the cache entries that is accepted by the specified filter.
     * 
     * @param filter
     *            the filter to test cache entries against
     * @return A future indicating pending load of all the entries
     */
    Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter);

    Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            AttributeMap defaultAttributes);

    Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer);

    /**
     * Attempts to load a value for the specified key even if a valid mapping for the
     * specified key is already in the cache.
     * 
     * @param key
     * @return
     */
    Future<?> forceLoad(K key);

    Future<?> forceLoad(K key, AttributeMap attributes);

    Future<?> forceLoadAll(AttributeMap attributes);

    Future<?> forceLoadAll(Collection<? extends K> keys);

    Future<?> forceLoadAll(Map<K, AttributeMap> mapsWithAttributes);

    // determine how exceptions are thrown from the future
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
     * If no backend, cache loader or cache store, is configured for the cache a call to
     * this method is silently ignored. And the returned futures
     * {@link java.util.concurrent.Future#isCancelled()} method will return <tt>true</tt>
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
     * @throws UnsupportedOperationException
     *             if the implementation does not support asynchronously load of elements
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    Future<?> load(K key);

    Future<?> load(K key, AttributeMap attributes);

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
     * {@link java.util.concurrent.Future#cancel(Boolean)} will attempt to cancel the
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
    Future<?> loadAll(Collection<? extends K> keys);

    Future<?> loadAll(Map<K, AttributeMap> mapsWithAttributes);

    /**
     * Attempts to reload all entries that are currently held in the cache.
     * 
     * @return a Future representing pending completion of all the loads, and whose
     *         <tt>get()</tt> method will return <tt>null</tt> upon completion.
     */
    Future<?> reloadAll();
}

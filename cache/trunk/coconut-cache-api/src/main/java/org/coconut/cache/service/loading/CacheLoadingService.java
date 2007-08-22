/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.core.AttributeMap;
import org.coconut.core.Transformer;
import org.coconut.filter.Filter;

/**
 * This is the main interface for controlling the cache loader of a cache at runtime.
 * <p>
 * Most of the methods for this service is usefull for preloading the cache with entries
 * that might be used at later time. Preloading attempts to place data in the cache far
 * enough in advance to hide the latency of a cache miss.
 * <p>
 * This service is only available if a cache loader has been set using
 * {@link CacheLoadingConfiguration#setLoader(CacheLoader)}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache containing this service
 * @param <V>
 *            the type of mapped values
 */
public interface CacheLoadingService<K, V> {

    /**
     * Attempts to reload all the cache entries where the {@link Filter#accept(Object)}
     * method of the specified filter returns true .
     * 
     * @param filter
     *            the filter to test cache entries against
     */
    void filteredLoad(Filter<? super CacheEntry<K, V>> filter);

    void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            AttributeMap defaultAttributes);

    void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer);

    /**
     * This method works analogous to the {@link #load(Object)} method. Except, that it
     * will attempt to load a new value for the specified key even if a valid mapping for
     * the specified key is already in the cache.
     * 
     * @param key
     *            whose associated value is to be loaded.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    void forceLoad(K key);

    /**
     * This method works analogous to the {@link #load(Object, AttributeMap)} method.
     * Except, that it will attempt to load a new value for the specified key even if a
     * valid mapping for the specified key is already in the cache.
     * 
     * @param key
     *            whose associated value is to be loaded.
     * @param attributes
     *            a map of attributes that will be available in the attribute map parsed
     *            to {@link CacheLoader#load(Object, AttributeMap)} method of the
     *            configured cache loader
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key or attribute map is <tt>null</tt>
     */
    void forceLoad(K key, AttributeMap attributes);

    /**
     * Attempts to reload all entries that are currently held in the cache from the
     * configured cache loader.
     */
    void forceLoadAll();

    /**
     * Attempts to reload all entries that are currently held in the cache from the
     * configured cache loader.
     * 
     * @param attributes
     *            a map of attributes that will be available in the attribute map parsed
     *            to {@link CacheLoader#load(Object, AttributeMap)} method of the
     *            configured cache loader
     * @throws NullPointerException
     *             if the specified attribute map is <tt>null</tt>
     */
    void forceLoadAll(AttributeMap attributes);

    /**
     * For all keys in the specified collection . This method will attempt to load the
     * value for the key from the configured cache loader. The effect of this call is
     * equivalent to that of calling {@link #forceLoad(Object)} once for each key in the
     * specified collection. However, This operation may be more efficient than repeatedly
     * calling {@link #forceLoad(Object)} for each key.
     * <p>
     * The behavior of this operation is unspecified if the specified collection is
     * modified while the operation is in progress.
     * 
     * @param keys
     *            whose associated values is to be loaded.
     * @throws ClassCastException
     *             if any of the keys in the specified collection are of an inappropriate
     *             type for this cache (optional).
     * @throws NullPointerException
     *             if the specified collection of keys is <tt>null</tt> or the specified
     *             collection contains a <tt>null</tt> value
     */
    void forceLoadAll(Collection<? extends K> keys);

    /**
     * For all keys and corresponding AttributeMap in the specified map. This method will
     * attempt to load the value for the key and AttributeMap from the configured cache
     * loader. The effect of this call is equivalent to that of calling
     * {@link #forceLoad(Object, AttributeMap)} once for each entry in the specified map.
     * However, This operation may be more efficient than repeatedly calling
     * {@link #forceLoad(Object, AttributeMap)}.
     * <p>
     * The behavior of this operation is unspecified if the specified collection is
     * modified while the operation is in progress.
     * 
     * @param mapsWithAttributes
     *            a map of keys that should be loaded with an associated AttributeMap
     * @throws ClassCastException
     *             if any of the keys in the specified map are of an inappropriate type
     *             for this cache (optional).
     * @throws NullPointerException
     *             if the specified map is <tt>null</tt> or any of the keys in the map
     *             is <tt>null</tt>
     */
    void forceLoadAll(Map<K, AttributeMap> mapsWithAttributes);

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
     * If a mapping for the specified key is not already in the cache. This method will
     * attempt to load the value for the specified key from the configured cache loader.
     * <p>
     * This method does not guarantee that the specified value is ever loaded into the
     * cache. Implementations are free to ignore the hint, however, most implementations
     * won't.
     * <p>
     * Unless otherwise specified the loading is done asynchronously. Any cache
     * implementation that is not thread-safe (ie supposed to be accessed by a single
     * thread only) will need to load the value before returning from this method. Because
     * it cannot allow a background thread to set the value once loaded.
     * 
     * @param key
     *            whose associated value is to be loaded.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    void load(K key);

    /**
     * This method works analogous to the {@link #load(Object)} method. Except that all
     * the attributes available in the the specified attribute map. Will be parsed along
     * to the {@link CacheLoader#load(Object, AttributeMap)} method of the configured
     * cache loader.
     * 
     * @param key
     *            whose associated value is to be loaded.
     * @param attributes
     *            a map of attributes that will be available in the attribute map parsed
     *            to the {@link CacheLoader#load(Object, AttributeMap)} method of the
     *            configured cache loader
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key or attribute map is <tt>null</tt>
     */
    void load(K key, AttributeMap attributes);

    /**
     * For all keys in the specified collection and where a valid mapping from the key is
     * not already in the cache. This method will attempt to load the value for the key
     * from the configured cache loader. The effect of this call is equivalent to that of
     * calling {@link #load(Object)} once for each key in the specified collection.
     * However, This operation may be more efficient than repeatedly calling
     * {@link #load(Object)} for each key.
     * <p>
     * The behavior of this operation is unspecified if the specified collection is
     * modified while the operation is in progress.
     * 
     * @param keys
     *            whose associated values is to be loaded.
     * @throws ClassCastException
     *             if any of the keys in the specified collection are of an inappropriate
     *             type for this cache (optional).
     * @throws NullPointerException
     *             if the specified collection of keys is <tt>null</tt> or the specified
     *             collection contains a <tt>null</tt> value
     */
    void loadAll(Collection<? extends K> keys);

    /**
     * @param mapsWithAttributes
     *            a map with keys that should be loaded and a corresponding attribute map
     */
    void loadAll(Map<K, AttributeMap> mapsWithAttributes);

    // void loadAll() -> load all expired or needs refresh

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
}

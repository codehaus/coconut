/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;

/**
 * This is the main interface for controlling the cache loading service at runtime.
 * <p>
 * Most of the methods for this service is usefull for reloading cache entries or
 * prefetching the cache with entries that might be used at later time.
 * <p>
 * This service is only available at runtime if a cache loader has been set using
 * {@link CacheLoadingConfiguration#setLoader(CacheLoader)}.
 * <p>
 * An instance of this interface can be retrieved by using {@link Cache#getService(Class)}
 * to look it up.
 *
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheLoadingService&lt;?, ?&gt; ces = c.getService(CacheLoadingService.class);
 * ces.load("somekey");
 * </pre>
 *
 * Or by using {@link CacheServices}
 *
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheLoadingService&lt;?, ?&gt; ces = CacheServices.services.loading();
 * ces.forceLoad("somekey");
 * </pre>
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache containing this service
 * @param <V>
 *            the type of mapped values
 */
public interface CacheLoadingService<K, V> {

    /**
     * This method will attempt to load the value, for the specified key, from the
     * configured cache loader even if a valid mapping for the specified key is already in
     * the cache.
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
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
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
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
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
     */
    void forceLoadAll();

    /**
     * Attempts to reload all entries that are currently held in the cache from the
     * configured cache loader.
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
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
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
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
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
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
    void forceLoadAll(Map<? extends K, ? extends AttributeMap> mapsWithAttributes);

    /**
     * Returns the default refresh time for entries. If entries are never refreshed,
     * {@link Long#MAX_VALUE} is returned.
     *
     * @param unit
     *            the time unit that should be used for returning the default refresh time
     * @return the default refresh time for entries, or {value Long#MAX_VALUE} if entries
     *         never needs refreshing
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
     * it cannot allow a background thread to add the value to cache once loaded.
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
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
     * the attributes in the the specified attribute map will be parsed along to the
     * {@link CacheLoader#load(Object, AttributeMap)} method of the configured cache
     * loader.
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
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
     * Attempts to reload all entries that are either expired or which needs refreshing.
     */
    void loadAll();

    /**
     * Attempts to reload all entries that are either expired or which needs refreshing.
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
     *
     * @param attributes
     *            a map of attributes that will be available in the attribute map parsed
     *            to {@link CacheLoader#load(Object, AttributeMap)} method of the
     *            configured cache loader
     * @throws NullPointerException
     *             if the specified attribute map is <tt>null</tt>
     */
    void loadAll(AttributeMap attributes);

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
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
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
     * <p>
     * If this cache has been shutdown calls to this method is ignored.
     *
     * @param mapsWithAttributes
     *            a map with keys that should be loaded and a corresponding attribute map
     */
    void loadAll(Map<? extends K, ? extends AttributeMap> mapsWithAttributes);

    /**
     * Sets the default refresh time for new objects that are added to the cache. If no
     * default refresh time has been set, entries will never be refreshed.
     *
     * @param timeToRefresh
     *            the time from insertion to the point where the entry should be refreshed
     * @param unit
     *            the time unit of the timeToRefresh argument
     * @throws IllegalArgumentException
     *             if the specified time to live is negative (<0)
     * @throws NullPointerException
     *             if the specified time unit is <tt>null</tt>
     * @see #getDefaultTimeToRefresh(TimeUnit)
     */
    void setDefaultTimeToRefresh(long timeToRefresh, TimeUnit unit);
}

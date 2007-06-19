/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.core.AttributeMap;

/**
 * A <tt>cache</tt> is a collection of data duplicating original values stored elsewhere
 * or computed earlier, where the original data are expensive (usually in terms of access
 * time) to fetch or compute relative to reading from the cache. Once the data are stored
 * in the cache, future use can be made by accessing the cached copy rather than
 * refetching or recomputing the original data, so that the average access time is
 * lowered.
 * <p>
 * Currently only two implementations exist. Both of them are entirely held in memory:
 * {@link org.coconut.cache.defaults.UnsynchronizedCache} and
 * {@link org.coconut.cache.defaults.SynchronizedCache}. However, both disk-based and
 * distributed implementations are on the drawing board. As a consequence the javadoc
 * makes frequent references to external caches even though no implementations exist yet.
 * <p>
 * The three collection views, which allow a cache's contents to be viewed as a set of
 * keys, collection of values, or set of key-value mappings only shows values contained in
 * the actual cache not any values that is stored in any backend store. Furthermore, the
 * cache will <tt>not</tt> check whether or not an entry has expired when calling
 * methods on any of the collection views. As a result the cache might return values that
 * have expired.
 * <p>
 * All general-purpose <tt>Cache</tt> implementation classes should provide two
 * "standard" constructors: a void (no arguments) constructor, which creates an empty
 * cache with default settings, and a constructor with a single argument of type
 * {@link CacheConfiguration}. There is no way to enforce this recommendation (as
 * interfaces cannot contain constructors) but all of the general-purpose cache
 * implementations in Coconut Cache comply. Unlike its super class {@link java.util.Map},
 * a constructor taking a single map is not required.
 * <p>
 * Generally, Cache implementations do not define element-based versions of the
 * <tt>equals</tt> and <tt>hashCode</tt> methods, but instead inherit the
 * identity-based versions from class <tt>Object</tt>. Nore, are they generally
 * serializable.
 * <p>
 * Unlike {@link java.util.HashMap}, a instance of <tt>Cache</tt> does NOT allow
 * <tt>null</tt> to be used as a key or value. It is the authors belief that allowing
 * null values (or keys) does more harm then good, by masking what are almost always usage
 * errors. If nulls are absolutely needed the <a
 * href="http://today.java.net/today/2004/12/10/refactor.pdf">Null Object Pattern</a> can
 * be used as an alternative.
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
     * Removes all of the entries from this cache. This method will not attempt to remove
     * entries that are stored externally, for example, on disk. The cache will be empty
     * after this call returns.
     * <p>
     * When all entries have been removed a single {@link CacheEvent.CacheCleared} will be
     * raised.
     */
    void clear();

    /**
     * Performs cleanup of the cache. This might be everything from persisting stale data
     * to disk to adapting the cache with a better eviction policy given the current
     * access pattern. This is done to avoid paying the cost upfront by application
     * threads when accessing entries in the cache through {@link #get(Object)} or
     * {@link #getAll(Collection)}.
     * <p>
     * Regular eviction is typically scheduled through
     * {@link CacheEvictionConfiguration#setScheduledEvictionAtFixedRate(long, java.util.concurrent.TimeUnit)}
     * If this is not set it is the responsibility of the user to regular call this
     * method.
     */
    void evict();

    /**
     * Works as {@link java.util.Map#get(Object)} with the following modifications.
     * <p>
     * If the cache has a configured {@link CacheLoader}. And no mapping exists for the
     * specified key or the specific mapping has expired. The cache will transparently
     * attempt to load a value for the specified key through the cache loader.
     * <p>
     * If cache statistics is enabled the hit/miss. Hits on the actual entry.
     * <p>
     * 
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this cache maps the specified key
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     * @throws CacheException
     *             if the backend cache loader failed while trying to load a value
     *             (optional). A cache might be configured to return null instead
     * @see Map#get(Object)
     * @see CacheLoadingConfiguration#setLoader(CacheLoader)
     * @see CacheExceptionHandler#loadFailed(Cache,
     *      org.coconut.cache.service.loading.CacheLoader, Object, AttributeMap, boolean,
     *      Throwable)
     */
    V get(Object key);

    /**
     * Attempts to retrieve all of the mappings for the specified collection of keys. The
     * effect of this call is equivalent to that of calling {@link #get(Object)} on this
     * cache once for each key in the specified collection. However, in some cases it can
     * be much faster to load several cache items at once, for example, if the cache must
     * fetch the values from a remote host.
     * <p>
     * If a value is not contained in the cache and the value cannot be loaded by any of
     * the configured cache backends. The returned map will contain a mapping from the key
     * to <tt>null</tt>.
     * <p>
     * The behavior of this operation is unspecified if the specified collection is
     * modified while the operation is in progress.
     * 
     * @param keys
     *            a collection of keys whose associated values are to be returned.
     * @return a map with mappings from each key to the corresponding value, or to
     *         <tt>null</tt> if no mapping for this key exists.
     * @throws ClassCastException
     *             if any of the keys in the specified collection are of an inappropriate
     *             type for this cache (optional).
     * @throws NullPointerException
     *             if the specified collection of keys is <tt>null</tt> or the specified
     *             collection contains a <tt>null</tt>
     * @throws CacheException
     *             if the backend cache loader failed while trying to load a value
     *             (optional). A cache might be configured to return null instead
     */
    Map<K, V> getAll(Collection<? extends K> keys);

    /**
     * Returns all registered services within the cache.
     * 
     * @return
     */
    Map<Class<?>, Object> getAllServices();

    /**
     * Returns the current capacity of this cache. If the current capacity of is greater
     * then Long.MAX_VALUE, this method returns Long.MAX_VALUE.
     * 
     * @return the current capacity of this cache
     */
    long getCapacity();

    /**
     * Retrieves a {@link CacheEntry} for the specified key (optional). If no entry exists
     * for the specified key any configured cache backend is asked to try and fetch an
     * entry for the key.
     * 
     * @param key
     *            whose associated cache entry is to be returned.
     * @return the cache entry to which this cache maps the specified key, or
     *         <tt>null</tt> if the cache contains no mapping for this key.
     * @throws CacheException
     *             if the backend cache loader failed while trying to load a value
     *             (optional). A cache might be configured to return null instead
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     * @throws UnsupportedOperationException
     *             if the cache does not cache entries
     */
    CacheEntry<K, V> getEntry(K key);

    /**
     * Returns the name of the cache. If no name has been specified while constructing the
     * cache a random name should be generated.
     * 
     * @return the name of the cache
     */
    String getName();

    /**
     * Returns a service of the specified type.
     * 
     * @param <T>
     *            the type of service to retrieve
     * @param serviceType
     *            the type of service to retrieve
     * @return a service of the specified type
     * @throws CacheException
     *             if no service of the specified type is registered
     * @see org.coconut.cache.service.CacheServices
     */
    <T> T getService(Class<T> serviceType);

    /**
     * Returns whether or not this cache contains a service of the specified type.
     * 
     * @param serviceType
     *            the type of service
     * @return true if this has a service of the specified type registered, otherwise
     *         false
     */
    boolean hasService(Class<?> serviceType);

    /**
     * This method works analogoes to the {@link java.util.Map#get(Object)} method.
     * However, it will not try to fetch missing items, it will only return a value if it
     * actually exists in the cache. Furthermore, it will not effect the statistics
     * gathered by the cache and no {@link CacheItemEvent.ItemAccessed} event will be
     * raised. Finally, even if the item is expired it will still be returned.
     * <p>
     * All implementations of this method should take care to assure that a call to peek
     * does not have any side effects on the cache or any retrived value.
     * 
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this cache maps the specified key, or <tt>null</tt> if
     *         the cache contains no mapping for this key.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    V peek(K key);

    /**
     * This method works analogoes to the {@link #peek(Object)} method. However, it will
     * return a cache entry instead of just the value.
     * <p>
     * Just like {@link #peek(Object)} any implementation of this method should take care
     * to assure that the call does not have any side effects on the cache or any retrived
     * entry.
     * 
     * @param key
     *            key whose associated cache entry is to be returned.
     * @return the cache entry to which this cache maps the specified key, or
     *         <tt>null</tt> if the cache contains no mapping for this key.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    CacheEntry<K, V> peekEntry(K key);

    /**
     * Associates the specified value with the specified key in this cache (optional
     * operation). If the cache previously contained a mapping for this key, the old value
     * is replaced by the specified value. (A cache <tt>m</tt> is said to contain a
     * mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) m.containsKey(k)} would return <tt>true</tt>.))
     * 
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @param attributes
     *            a map of additional attributes
     * @return previous value associated with specified key, or <tt>null</tt> if there
     *         was no mapping for key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this cache.
     * @throws ClassCastException
     *             if the class of the specified key or value prevents it from being
     *             stored in this cache.
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this cache.
     * @throws NullPointerException
     *             if either the specified key, value or attributes is <tt>null</tt>.
     * @see Map#put(Object, Object)
     */
    V put(K key, V value, AttributeMap attributes);
}

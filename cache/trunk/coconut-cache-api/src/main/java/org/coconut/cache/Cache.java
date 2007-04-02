/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.coconut.core.AttributeMap;

/**
 * A <tt>cache</tt> is a collection of data duplicating original values stored
 * elsewhere or computed earlier, where the original data are expensive (usually
 * in terms of access time) to fetch or compute relative to reading from the
 * cache. Once the data are stored in the cache, future use can be made by
 * accessing the cached copy rather than refetching or recomputing the original
 * data, so that the average access time is lowered.
 * <p>
 * Currently only two implementations exist. Both of them are entirely held in
 * memory: {@link org.coconut.cache.defaults.UnsynchronizedCache} and
 * {@link org.coconut.cache.defaults.SynchronizedCache}.
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

    <T> T getService(Class<T> serviceType);

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
    
    V put(K key, V value, AttributeMap attributes);

    //void putAll(Map<K,V> keys, AttributeMap defaultAttributes);
    //void putAll(Map<K,V> keys, AttributeMap defaultAttributes, Map<K,AttributeMap> specialAttributes);
}

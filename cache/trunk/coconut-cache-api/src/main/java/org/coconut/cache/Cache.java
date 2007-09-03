/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
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
 * distributed implementations are on the drawing board. As a consequence the
 * documentation might make references to external caches even though no implementations
 * exist yet.
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
     * Blocks until all tasks have completed execution after a shutdown request, or the
     * timeout occurs, or the current thread is interrupted, whichever happens first.
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return <tt>true</tt> if this cache terminated and <tt>false</tt> if the
     *         timeout elapsed before termination
     * @throws InterruptedException
     *             if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Removes all of the entries from this cache. This method will not attempt to remove
     * entries that are stored externally, for example, on disk. The cache will be empty
     * after this call returns.
     * <p>
     * When all entries have been removed a single
     * {@link org.coconut.cache.service.event.CacheEvent.CacheCleared} will be raised.
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
     * {@link org.coconut.cache.service.eviction.CacheEvictionConfiguration#setScheduledEvictionAtFixedRate(long, java.util.concurrent.TimeUnit)}
     * If this is not set it is the responsibility of the user to regular call this
     * method.
     */
    void evict();

    /**
     * Works as {@link java.util.Map#get(Object)} with the following modifications.
     * <p>
     * If the cache has a configured {@link org.coconut.cache.service.loading.CacheLoader}.
     * And no mapping exists for the specified key or the specific mapping has expired.
     * The cache will transparently attempt to load a value for the specified key through
     * the cache loader.
     * <p>
     * The number of cache hits will increase by 1 if the cache loader is not consulted
     * when using this method (mapping is already present and not expired). Otherwise the
     * number of misses will be increased by 1.
     * <p>
     * If the <tt><A HREF="service/event/package-summary.html"><CODE>event</CODE></A></tt>
     * service is enabled the following events may be raised.
     * 
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this cache maps the specified key
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the cache has already been shutdown, see
     *             {@link org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#cacheWasShutdown(org.coconut.cache.service.exceptionhandling.CacheExceptionContext, String)}
     * @throws CacheException
     *             if the backend cache loader failed while trying to load a value
     *             (optional). See
     *             {@link org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#loadFailed(org.coconut.cache.service.exceptionhandling.CacheExceptionContext, org.coconut.cache.service.loading.CacheLoader, Object, AttributeMap, boolean, Exception)}
     * @see Map#get(Object)
     * @see org.coconut.cache.service.loading.CacheLoadingConfiguration#setLoader(org.coconut.cache.service.loading.CacheLoader)
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
     * <p>
     * Works as {@link #get(Object)} with the following modification.
     * <p>
     * An immutable cache entry is returned.
     * 
     * @param key
     *            whose associated cache entry is to be returned.
     * @return the cache entry to which this cache maps the specified key, or
     *         <tt>null</tt> if the cache contains no mapping for this key.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the cache has already been shutdown, see
     *             {@link org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#cacheWasShutdown(org.coconut.cache.service.exceptionhandling.CacheExceptionContext, String)}
     * @throws CacheException
     *             if the backend cache loader failed while trying to load a value
     *             (optional). See
     *             {@link org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#loadFailed(org.coconut.cache.service.exceptionhandling.CacheExceptionContext, org.coconut.cache.service.loading.CacheLoader, Object, AttributeMap, boolean, Exception)}
     */
    CacheEntry<K, V> getEntry(K key);

    /**
     * Returns the name of the cache. If no name has been specified while configuring the
     * cache. The cache must choose a may return any valid name.
     * 
     * @return the name of the cache
     */
    String getName();

    /**
     * Returns a service of the specified type or throws a CacheException if no such
     * service exists.
     * 
     * @param <T>
     *            the type of service to retrieve
     * @param serviceType
     *            the type of service to retrieve
     * @return a service of the specified type
     * @throws CacheException
     *             if no service of the specified type exist
     * @see org.coconut.cache.CacheServices
     * @see #hasService(Class)
     * @see CacheServiceManagerService#getAllServices()
     */
    <T> T getService(Class<T> serviceType);

    /**
     * Returns the current volume of this cache. If the current volume of this cache is
     * greater then Long.MAX_VALUE, this method returns Long.MAX_VALUE.
     * 
     * @return the current volume of this cache
     */
    long getVolume();

    /**
     * Returns <tt>true</tt> if this cache has been shut down.
     * 
     * @return <tt>true</tt> if this cache has been shut down
     */
    boolean isShutdown();

    /**
     * Returns <tt>true</tt> if this cache has been started.
     * 
     * @return <tt>true</tt> if this cache has been started
     */
    boolean isStarted();

    /**
     * Returns <tt>true</tt> if all tasks have completed following shut down. Note that
     * <tt>isTerminated</tt> is never <tt>true</tt> unless either <tt>shutdown</tt>
     * or <tt>shutdownNow</tt> was called first.
     * 
     * @return <tt>true</tt> if all tasks have completed following shut down
     */
    boolean isTerminated();

    /**
     * This method works analogoes to the {@link get(Object)} method with the following
     * modifications.
     * <p>
     * However, it will not try to fetch missing items, it will only return a value if it
     * actually exists in the cache. Furthermore, it will not effect the statistics
     * gathered by the cache and no
     * {@link org.coconut.cache.service.event.CacheEntryEvent.CacheEntryEvent.ItemAccessed}
     * event will be raised. Finally, even if the item is expired it will still be
     * returned.
     * <p>
     * All implementations of this method should take care to assure that a call to peek
     * does not have any unforseen side effects. For example, it should not modify some
     * state in addition to returning a value or not returning a value.
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
     * is replaced by the specified value. (A cache <tt>c</tt> is said to contain a
     * mapping for a key <tt>k</tt> if and only if
     * {@link org.coconut.cache.Cache#containsKey(Object) c.containsKey(k)} would return
     * <tt>true</tt>.))
     * <p>
     * It is often more effective to specify a
     * {@link org.coconut.cache.service.loading.CacheLoader} that implicitly loads values
     * then to explicitly add them to cache using the various <tt>put</tt> and
     * <tt>putAll</tt> methods.
     * 
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
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
     *             if the specified key or value is <tt>null</tt>.
     */
    V put(K key, V value);

//    /**
//     * Associates the specified value with the specified key in this cache (optional
//     * operation). If the cache previously contained a mapping for this key, the old value
//     * is replaced by the specified value. (A cache <tt>m</tt> is said to contain a
//     * mapping for a key <tt>k</tt> if and only if
//     * {@link #containsKey(Object) m.containsKey(k)} would return <tt>true</tt>.))
//     * 
//     * @param key
//     *            key with which the specified value is to be associated.
//     * @param value
//     *            value to be associated with the specified key.
//     * @param attributes
//     *            a map of additional attributes
//     * @return previous value associated with specified key, or <tt>null</tt> if there
//     *         was no mapping for key.
//     * @throws UnsupportedOperationException
//     *             if the <tt>put</tt> operation is not supported by this cache.
//     * @throws ClassCastException
//     *             if the class of the specified key or value prevents it from being
//     *             stored in this cache.
//     * @throws IllegalArgumentException
//     *             if some aspect of this key or value prevents it from being stored in
//     *             this cache.
//     * @throws NullPointerException
//     *             if either the specified key, value or attributes is <tt>null</tt>.
//     * @see Map#put(Object, Object)
//     */
//    V put(K key, V value, AttributeMap attributes);

    /**
     * Initiates an orderly shutdown of the cache. In which currently running tasks, such
     * as cache loading will be executed, but no new tasks will be started and no values
     * will be added to the cache. Invocation has no additional effect if already shut
     * down.
     * 
     * @throws SecurityException
     *             if a security manager exists and shutting down this Cache may
     *             manipulate threads that the caller is not permitted to modify because
     *             it does not hold {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>,
     *             or the security manager's <tt>checkAccess</tt> method denies access.
     */
    void shutdown();

    /**
     * Attempts to stop all actively executing tasks within the cache and halts the
     * processing of waiting tasks. Invocation has no additional effect if already shut
     * down.
     * <p>
     * There are no guarantees beyond best-effort attempts to stop processing actively
     * executing tasks in the cache. For example, typical implementations will cancel via
     * {@link Thread#interrupt}, so any task that fails to respond to interrupts may
     * never terminate.
     * 
     * @throws SecurityException
     *             if a security manager exists and shutting down this Cache may
     *             manipulate threads that the caller is not permitted to modify because
     *             it does not hold {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>,
     *             or the security manager's <tt>checkAccess</tt> method denies access.
     */
    void shutdownNow();
}

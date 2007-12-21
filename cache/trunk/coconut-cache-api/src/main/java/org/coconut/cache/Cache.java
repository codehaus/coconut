/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;

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
 * documentation might make references to caches with external storage even though no
 * implementations exist yet.
 * <p>
 * The three collection views, which allow a cache's contents to be viewed as a set of
 * keys, collection of values, or set of key-value mappings only shows values contained in
 * the actual cache. Furthermore, the cache will <tt>not</tt> check whether or not an
 * entry has expired when calling methods on any of the collection views. As a result the
 * cache might return values that have expired.
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
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
public interface Cache<K, V> extends ConcurrentMap<K, V> {

    /**
     * Blocks until all tasks within this cache have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is interrupted, whichever
     * happens first.
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
     * Removes all entries from this cache. This method will not attempt to remove entries
     * that are stored externally, for example, on disk. The cache will be empty after
     * this call returns.
     * <p>
     * A {@link org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved} event will be
     * raised for each mapping that is removed when the cache is cleared. When all entries
     * have been removed from the cache a single
     * {@link org.coconut.cache.service.event.CacheEvent.CacheCleared} will be raised.
     * This event is only raised if the state of the cache changed (cache was non-empty).
     * <p>
     * If the reason for clearing the cache is to get rid of stale data another
     * alternative, if the cache has a CacheLoader defined, might be to use
     * {@link org.coconut.cache.service.loading.CacheLoadingService#forceLoadAll()} which will reload all elements that
     * current in the cache.
     * <p>
     * If the cache has been shutdown calls to this method is ignored
     * 
     * @throws UnsupportedOperationException
     *             if the <tt>clear</tt> operation is not supported by this cache
     */
    void clear();

    /**
     * Returns <tt>true</tt> if this cache contains a mapping for the specified key.
     * More formally, returns <tt>true</tt> if and only if this cache contains a mapping
     * for a key <tt>k</tt> such that <tt>(key==null ? k==null : key.equals(k))</tt>.
     * (There can be at most one such mapping.)
     * <p>
     * This method does not check the expiration status of an element and will return if a
     * expired element is present in the cache for the specified key.
     * 
     * @param key
     *            key whose presence in this cache is to be tested
     * @return <tt>true</tt> if this cache contains a mapping for the specified key
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional)
     * @throws NullPointerException
     *             if the specified key is null
     */
    boolean containsKey(Object key);

    /**
     * Returns <tt>true</tt> if this cache maps one or more keys to the specified value.
     * More formally, returns <tt>true</tt> if and only if this cache contains at least
     * one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>. This operation will probably
     * require time linear in the cache size for most implementations of the
     * <tt>Cache</tt> interface.
     * <p>
     * This method does not check the expiration status of an element and will return if a
     * expired element is present in the cache for the specified value.
     * 
     * @param value
     *            value whose presence in this cache is to be tested
     * @return <tt>true</tt> if this cache maps one or more keys to the specified value
     * @throws ClassCastException
     *             if the value is of an inappropriate type for this cache (optional)
     * @throws NullPointerException
     *             if the specified value is null
     */
    boolean containsValue(Object value);

    /**
     * Returns a {@link Set} view of the mappings contained in this cache. The set is
     * backed by the cache, so changes to the cache are reflected in the set, and
     * vice-versa. If the cache is modified while an iteration over the set is in progress
     * (except through the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the iterator) the results
     * of the iteration are undefined. The set supports element removal, which removes the
     * corresponding mapping from the cache, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations. It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     * <p>
     * Unlike {@link Cache#get(Object)} no methods on the view checks if an element has
     * expired. For example, iterating though values the view might return an expired
     * element.
     * <p>
     * If the cache has been shutdown calls to <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operation will throw an IllegalStateException.
     * 
     * @return a set view of the mappings contained in this map
     */
    Set<Map.Entry<K, V>> entrySet();

    /**
     * Works as {@link #get(Object)} with the following modifications.
     * <p>
     * If the cache has a configured {@link org.coconut.cache.service.loading.CacheLoader}.
     * And no mapping exists for the specified key or the specific mapping has expired.
     * The cache will transparently attempt to load a value for the specified key through
     * the cache loader.
     * <p>
     * The number of cache hits will increase by 1 if a valid mapping is present.
     * Otherwise the number of cache misses will be increased by 1.
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
     *             if the cache has already been shutdown
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws CacheException
     *             if the cache loader failed while trying to load a value (optional). See
     *             {@link org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#loadingLoadValueFailed(org.coconut.cache.service.exceptionhandling.CacheExceptionContext, org.coconut.cache.service.loading.CacheLoader, Object, AttributeMap)}
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
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws CacheException
     *             if the backend cache loader failed while trying to load a value
     *             (optional). A cache might be configured to return <code>null</code>
     *             instead
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
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws IllegalArgumentException
     *             if the cache has already been shutdown
     * @throws CacheException
     *             if the backend cache loader failed while trying to load a value
     *             (optional). See
     *             {@link org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#loadingLoadValueFailed(org.coconut.cache.service.exceptionhandling.CacheExceptionContext, org.coconut.cache.service.loading.CacheLoader, Object, AttributeMap)}
     */
    CacheEntry<K, V> getEntry(K key);

    /**
     * Returns the name of the cache. If no name has been specified while configuring the
     * cache. The cache must choose a valid name. A valid name contains no other
     * characters then alphanumeric characters and '_' or '-'.
     * 
     * @return the name of the cache
     * @see CacheConfiguration#setName(String)
     */
    String getName();

    /**
     * Returns a service of the specified type or throws a
     * {@link IllegalArgumentException} if no such service exists.
     * 
     * @param <T>
     *            the type of service to retrieve
     * @param serviceType
     *            the type of service to retrieve
     * @return a service of the specified type
     * @throws IllegalArgumentException
     *             if no service of the specified type exist
     * @throws NullPointerException
     *             if the specified service type is null
     * @see org.coconut.cache.CacheServicesOld
     * @see org.coconut.cache.service.servicemanager.CacheServiceManagerService#hasService(Class)
     * @see org.coconut.cache.service.servicemanager.CacheServiceManagerService#getAllServices()
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
     * Returns <tt>true</tt> if this cache contains no elements.
     * <p>
     * If this cache has not been started a call to this method will automatically start
     * it. If this cache has been shutdown this method returns <tt>true</tt>.
     * 
     * @return <tt>true</tt> if this cache contains no elements
     */
    boolean isEmpty();

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
     * Returns <tt>true</tt> if all service tasks have completed following shut down.
     * Note that <tt>isTerminated</tt> is never <tt>true</tt> unless either
     * <tt>shutdown</tt> or <tt>shutdownNow</tt> was called first.
     * 
     * @return <tt>true</tt> if all tasks have completed following shut down
     */
    boolean isTerminated();

    /**
     * Returns a {@link Set} view of the keys contained in this cache. The set is backed
     * by the cache, so changes to the cache are reflected in the set, and vice-versa. If
     * the cache is modified while an iteration over the set is in progress (except
     * through the iterator's own <tt>remove</tt> operation), the results of the
     * iteration are undefined. The set supports element removal, which removes the
     * corresponding mapping from the cache, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
     * <tt>clear</tt> operations. It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     * <p>
     * Unlike {@link Cache#get(Object)} no methods on the view checks if an element has
     * expired. For example, iterating though key set the view might return a key for an
     * expired element.
     * <p>
     * If the cache has been shutdown calls to <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operation will throw an IllegalStateException.
     * 
     * @return a set view of the keys contained in this cache
     */
    Set<K> keySet();

    /**
     * This method works analogoes to the {@link #get(Object)} method with the following
     * modifications.
     * <p>
     * However, it will not try to fetch missing items, it will only return a value if it
     * actually exists in the cache. Furthermore, it will not effect the statistics
     * gathered by the cache. Finally, even if the item is expired it will still be
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
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws NullPointerException
     *             if the specified key or value is <tt>null</tt>.
     */
    V put(K key, V value);

    CacheServices<K,V> services();
    /**
     * Copies all of the mappings from the specified map to this cache (optional
     * operation). The effect of this call is equivalent to that of calling
     * {@link #put(Object,Object) put(k, v)} on this cache once for each mapping from key
     * <tt>k</tt> to value <tt>v</tt> in the specified map. The behavior of this
     * operation is undefined if the specified map is modified while the operation is in
     * progress.
     * 
     * @param m
     *            mappings to be stored in this cache
     * @throws UnsupportedOperationException
     *             if the <tt>putAll</tt> operation is not supported by this cache
     * @throws ClassCastException
     *             if the class of a key or value in the specified map prevents it from
     *             being stored in this cache
     * @throws NullPointerException
     *             if the specified map is null or if specified map contains null keys or
     *             values
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws IllegalArgumentException
     *             if some property of a key or value in the specified map prevents it
     *             from being stored in this cache
     */
    void putAll(Map<? extends K, ? extends V> m);

    /**
     * If the specified key is not already associated with a value, associate it with the
     * given value. This is equivalent to
     * 
     * <pre>
     * if (!cache.containsKey(key))
     *     return cache.put(key, value);
     * else
     *     return cache.get(key);
     * </pre>
     * 
     * except that the action is performed atomically.
     * 
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with the specified key, or <tt>null</tt> if
     *         there was no mapping for the key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this cache
     * @throws ClassCastException
     *             if the class of the specified key or value prevents it from being
     *             stored in this cache
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws NullPointerException
     *             if the specified key or value is null
     * @throws IllegalArgumentException
     *             if some property of the specified key or value prevents it from being
     *             stored in this cache
     */
    V putIfAbsent(K key, V value);

    /**
     * Removes the mapping for a key from this cache if it is present (optional
     * operation). More formally, if this cache contains a mapping from key <tt>k</tt>
     * to value <tt>v</tt> such that <code>(key==null ?  k==null : key.equals(k))</code>,
     * that mapping is removed. (The cache can contain at most one such mapping.)
     * <p>
     * Returns the value to which this cache previously associated the key, or
     * <tt>null</tt> if the cache contained no mapping for the key.
     * <p>
     * The cache will not contain a mapping for the specified key once the call returns.
     * 
     * @param key
     *            key whose mapping is to be removed from the cache
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if
     *         there was no mapping for <tt>key</tt>.
     * @throws UnsupportedOperationException
     *             if the <tt>remove</tt> operation is not supported by this cache
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional)
     * @throws NullPointerException
     *             if the specified key is null
     */
    V remove(Object key);

    /**
     * Removes the entry for a key only if currently mapped to a given value. This is
     * equivalent to
     * 
     * <pre>
     * if (cache.containsKey(key) &amp;&amp; cache.get(key).equals(value)) {
     *     cache.remove(key);
     *     return true;
     * } else
     *     return false;
     * </pre>
     * 
     * except that the action is performed atomically.
     * 
     * @param key
     *            key with which the specified value is associated
     * @param value
     *            value expected to be associated with the specified key
     * @return <tt>true</tt> if the value was removed
     * @throws UnsupportedOperationException
     *             if the <tt>remove</tt> operation is not supported by this cache
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws ClassCastException
     *             if the key or value is of an inappropriate type for this cache
     *             (optional)
     * @throws NullPointerException
     *             if the specified key or value is null
     */
    boolean remove(Object key, Object value);

    /**
     * Attempts to remove all of the mappings for the specified collection of keys. The
     * effect of this call is equivalent to that of calling
     * {@link org.coconut.cache.Cache#remove(Object)} on the cache once for each key in
     * the specified collection. However, in some cases it can be much faster to remove
     * several cache items at once, for example, if some of the values must also be
     * removed on a remote host.
     * 
     * @param keys
     *            a collection of keys whose associated mappings are to be removed.
     * @throws UnsupportedOperationException
     *             if the <tt>remove</tt> operation is not supported by this cache.
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws NullPointerException
     *             if the specified collection or any of its containing keys are
     *             <tt>null</tt>.
     */
    void removeAll(Collection<? extends K> keys);

    /**
     * Replaces the entry for a key only if currently mapped to some value. This is
     * equivalent to
     * 
     * <pre>
     * if (cache.containsKey(key)) {
     *     return cache.put(key, value);
     * } else
     *     return null;
     * </pre>
     * 
     * except that the action is performed atomically.
     * 
     * @param key
     *            key with which the specified value is associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with the specified key, or <tt>null</tt> if
     *         there was no mapping for the key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this cache
     * @throws ClassCastException
     *             if the class of the specified key or value prevents it from being
     *             stored in this cache
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws NullPointerException
     *             if the specified key or value is null
     * @throws IllegalArgumentException
     *             if some property of the specified key or value prevents it from being
     *             stored in this cache
     */
    V replace(K key, V value);

    /**
     * Replaces the entry for a key only if currently mapped to a given value. This is
     * equivalent to
     * 
     * <pre>
     * if (cache.containsKey(key) &amp;&amp; cache.get(key).equals(oldValue)) {
     *     cache.put(key, newValue);
     *     return true;
     * } else
     *     return false;
     * </pre>
     * 
     * except that the action is performed atomically.
     * 
     * @param key
     *            key with which the specified value is associated
     * @param oldValue
     *            value expected to be associated with the specified key
     * @param newValue
     *            value to be associated with the specified key
     * @return <tt>true</tt> if the value was replaced
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this cache
     * @throws ClassCastException
     *             if the class of a specified key or value prevents it from being stored
     *             in this cache
     * @throws NullPointerException
     *             if a specified key or value is null
     * @throws IllegalStateException
     *             if the cache has been shutdown
     * @throws IllegalArgumentException
     *             if some property of a specified key or value prevents it from being
     *             stored in this cache
     */
    boolean replace(K key, V oldValue, V newValue);

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

    /**
     * Returns the number of elements in this cache. If the cache contains more than
     * <tt>Integer.MAX_VALUE</tt> elements, returns <tt>Integer.MAX_VALUE</tt>.
     * <p>
     * If this cache has not been started a call to this method will automatically start
     * it. If this cache has been shutdown this method returns <tt>0</tt>.
     * 
     * @return the number of elements in this cache
     */
    int size();

    /**
     * Returns a {@link Collection} view of the values contained in this cache. The
     * collection is backed by the cache, so changes to the cache are reflected in the
     * collection, and vice-versa. If the cache is modified while an iteration over the
     * collection is in progress (except through the iterator's own <tt>remove</tt>
     * operation), the results of the iteration are undefined. The collection supports
     * element removal, which removes the corresponding mapping from the cache, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     * <p>
     * Unlike {@link Cache#get(Object)} no methods on the view checks if an element has
     * expired. For example, iterating though values the view might return an expired
     * element.
     * <p>
     * If the cache has been shutdown calls to <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operation will throw an IllegalStateException.
     * 
     * @return a collection view of the values contained in this cache
     */
    Collection<V> values();
}

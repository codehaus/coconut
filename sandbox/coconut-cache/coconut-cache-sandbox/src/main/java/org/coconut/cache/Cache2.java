/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface Cache2<K, V> extends Cache<K, V> {

    //put all on abstract cache
    Map<K, CacheEntry<K, V>> getAllEntries(Collection<? extends K> keys);

    CacheEntry<K, V> putEntry(CacheEntry<K, V> entry);

    void putAllEntries(Collection<? extends CacheEntry<K, V>> entries);

    /* think about these */
    CacheEntry<K, V> putIfAbsentEntry(CacheEntry<K, V> entry);

    boolean removeEntry(CacheEntry<K, V> entry);

    CacheEntry<K, V> replaceEntry(CacheEntry<K, V> entry);

    boolean replaceEntry(CacheEntry<K, V> newEntry, CacheEntry<K, V> oldEntry);
    
    /*
     * All o The first level is thread safety/performance unsynchronized cache,
     * for example, org. {@link org.coconut.cache.defaults.UnsynchronizedCache}
     * synchronized caches which offers thread safety. concurrent caches with
     * offers thread safery and concurrent retrievels on the expense of
     * features. Coconut comes with build-in support for the following types of
     * cache. TODO What is a cache interface Distributed.. serializable
     * CachePolicies. <p> small range of different cache implementations.
     * Ranging from simple unsynchronized caches to highly concurrent
     * distributed caches. See a list of all the various cache implementations
     * <a
     * href="http://org.coconut.codehaus.org/cache/cache-implementations.html">here</a>.
     */

    


//  /**
//   * Performs cleanup of the cache. This might be everything from persisting stale data
//   * to disk to adapting the cache with a better eviction policy given the current
//   * access pattern. This is done to avoid paying the cost upfront by application
//   * threads when accessing entries in the cache through {@link #get(Object)} or
//   * {@link #getAll(Collection)}.
//   * <p>
//   * Regular eviction is typically scheduled through
//   * {@link org.coconut.cache.service.eviction.CacheEvictionConfiguration#setScheduledEvictionAtFixedRate(long, java.util.concurrent.TimeUnit)}
//   * If this is not set it is the responsibility of the user to regular call this
//   * method.
//   */
//  void evict();

    
// /**
// * Associates the specified value with the specified key in this cache (optional
// * operation). If the cache previously contained a mapping for this key, the old value
// * is replaced by the specified value. (A cache <tt>m</tt> is said to contain a
// * mapping for a key <tt>k</tt> if and only if
// * {@link #containsKey(Object) m.containsKey(k)} would return <tt>true</tt>.))
// *
// * @param key
// * key with which the specified value is to be associated.
// * @param value
// * value to be associated with the specified key.
// * @param attributes
// * a map of additional attributes
// * @return previous value associated with specified key, or <tt>null</tt> if there
// * was no mapping for key.
// * @throws UnsupportedOperationException
// * if the <tt>put</tt> operation is not supported by this cache.
// * @throws ClassCastException
// * if the class of the specified key or value prevents it from being
// * stored in this cache.
// * @throws IllegalArgumentException
// * if some aspect of this key or value prevents it from being stored in
// * this cache.
// * @throws NullPointerException
// * if either the specified key, value or attributes is <tt>null</tt>.
// * @see Map#put(Object, Object)
// */
// V put(K key, V value, AttributeMap attributes);

}

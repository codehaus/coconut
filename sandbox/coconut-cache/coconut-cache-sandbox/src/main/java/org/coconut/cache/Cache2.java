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

    List<?> getAllServices();
}

/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.sandbox.store;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheStore<K, V> extends CacheLoader<K, CacheEntry<K, V>> {

    // most likely we do not need to fetch the value, as it should probably
    // allready be in the cache, just make sure we check it.

    //Callback future instead of Future???
    Future<CacheEntry<K, V>> delete(K key, boolean retrivePreviousValue);

    Future<Map<K, CacheEntry<K, V>>> deleteAll(
            Filter<? extends CacheEntry<K, V>> filter,
            boolean retrivePreviousValues);

    // previous values?

    Future<Map<K, CacheEntry<K, V>>> loadAll(
            Filter<? extends CacheEntry<K, V>> filter);

    // what about failures?? all or nothing, or just as many as possible???
    /**
     * In case of failure, Depending on the implementation either all or no of
     * the items are deleted, or as many as possible and an aggregated exception
     * is thrown listing all the values that could not be deleted.
     * 
     * @param colKeys
     * @param retrivePreviousValues
     * @return
     */
    Future<Map<K, CacheEntry<K, V>>> deleteAll(Collection<? extends K> colKeys,
            boolean retrivePreviousValues);

    Future<CacheEntry<K, V>> loadAsync(K key);

    Future<Map<K, CacheEntry<K, V>>> loadAllAsync(Collection<? extends K> keys);

    Future<Map<K, CacheEntry<K, V>>> loadAllAsync(
            Filter<? extends CacheEntry<K, V>> filter);

    Future<V> store(CacheEntry<K, V> entry);

    Map<K, V> storeAll(Collection<CacheEntry<? extends K, ? extends V>> entries);

    // iterator??? optional
}

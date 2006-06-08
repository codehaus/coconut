/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.sandbox.store;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.core.Callback;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface Store<K, V> {
    Future<V> delete(K key, boolean retrivePreviousValue);

    /**
     * In case of failure, Depending on the implementation either all or no of
     * the items are deleted, or as many as possible and an aggregated exception
     * is thrown listing all the values that could not be deleted.
     * 
     * @param colKeys
     * @param retrivePreviousValues
     * @return
     */
    Future<Map<K, V>> deleteAll(Collection<? extends K> colKeys,
            boolean retrivePreviousValues);

    // previous values?

    Future<Map<K, V>> deleteAll(Filter<? extends V> filter,
            boolean retrivePreviousValues);

    Future<Map<K, V>> loadAll(Filter<? extends V> filter);

    Future<Map<K, V>> loadAllAsync(Collection<? extends K> keys);

    Future<Map<K, V>> loadAllAsync(Filter<? extends V> filter);

    Future<V> loadAsync(K key, Callback<V> callback);

    Future<V> store(K key, V value, boolean retrivePreviousValues);

    Future<Map<K, V>> storeAll(Map<K, V> entries, boolean retrivePreviousValues);

}

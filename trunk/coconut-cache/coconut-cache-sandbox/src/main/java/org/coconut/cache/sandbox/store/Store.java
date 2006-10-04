/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.sandbox.store;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.core.Callback;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Store.java 38 2006-08-22 10:09:08Z kasper $
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

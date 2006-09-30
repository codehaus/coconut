/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheLoader;

/**
 * To be documented. This will change for sure!!
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface CacheStore2<K, V> extends CacheLoader<K, V> {
    void delete(K key) throws Exception; /* IO Exception? */
    void deleteAll(Collection< ? extends K> colKeys) throws Exception;
    void store(K Key, V value) throws Exception;
    void storeAll(Map< ? extends K, ? extends V> mapEntries) throws Exception;
    
    void sync();

//    void eraseAll() throws Exception;
//    Iterable<K> keys() throws Exception;
//    Iterable<Map.Entry< ? extends K, ? extends V>> entries() throws Exception;
//    Iterable<V> values() throws Exception;
//    int size() throws Exception;

    
    //events -deleted, added, accessed
    
}

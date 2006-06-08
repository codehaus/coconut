package org.coconut.cache.store;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheLoader;

/**
 * To be documented. This will change for sure!!
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface OldCacheStore<K, V> extends CacheLoader<K, V> {
    void erase(K key) throws IOException;
    void eraseAll(Collection< ? extends K> colKeys) throws Exception;
    void store(K oKey, V oValue) throws Exception;
    //void store(K oKey, V oValue, long timeout, TimeUnit unit) throws Exception;
    void storeAll(Map< ? extends K, ? extends V> mapEntries) throws Exception;
    
    //void sync();

    void eraseAll() throws Exception;
    Iterable<K> keys() throws Exception;
    Iterable<Map.Entry< ? extends K, ? extends V>> entries() throws Exception;
    Iterable<V> values() throws Exception;
    int size() throws Exception;

}

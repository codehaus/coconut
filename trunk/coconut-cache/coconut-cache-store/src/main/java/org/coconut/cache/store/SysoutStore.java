/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.store;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheLoader;
import org.coconut.cache.spi.CacheStore;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SysoutStore<K, V> implements CacheStore<K, V> {

    private final CacheLoader<K, V> loader;

    /**
     * @param loader
     */
    public SysoutStore(final CacheLoader<K, V> loader) {
        this.loader = loader;
    }

    /**
     * @see org.coconut.cache.store.CacheStore#store(java.lang.Object,
     *      java.lang.Object, boolean)
     */
    public void store(K key, V value) throws Exception {
        System.out.println("Stored " + key + "," + value);
    }

    /**
     * @see org.coconut.cache.store.CacheStore#storeAll(java.util.Map, boolean)
     */
    public void storeAll(Map<K, V> entries) throws Exception {
        for (Map.Entry<K, V> e : entries.entrySet()) {
            store(e.getKey(), e.getValue());
        }
    }

    /**
     * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
     */
    public V load(K key) throws Exception {
        return loader.load(key);
    }

    /**
     * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
     */
    public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
        return loader.loadAll(keys);
    }

    /**
     * @see org.coconut.cache.store.CacheStore#delete(java.lang.Object)
     */
    public void delete(K key) throws Exception {
        
    }

    /**
     * @see org.coconut.cache.store.CacheStore#deleteAll(java.util.Collection)
     */
    public void deleteAll(Collection<? extends K> keys) throws Exception {
        // TODO Auto-generated method stub
        
    }

}

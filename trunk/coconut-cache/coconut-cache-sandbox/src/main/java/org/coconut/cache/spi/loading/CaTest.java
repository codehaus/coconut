/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi.loading;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;
import org.coconut.core.Callback;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CaTest<K, V> implements CacheLoaderCallback<K, V> {

    public static void main(String[] args) {
        
    }
    /**
     * @see org.coconut.cache.spi.loading.CacheLoaderCallback#load(org.coconut.cache.CacheLoader,
     *      java.lang.Object, org.coconut.core.Callback)
     */
    public void load(CacheLoader<K, V> loader, K key, Callback<V> callback) {
        try {
            V v = loader.load(key);
            callback.completed(v);
        } catch (Exception e) {
            callback.failed(e);
        }
    }

    /**
     * @see org.coconut.cache.spi.loading.CacheLoaderCallback#loadAll(org.coconut.cache.CacheLoader,
     *      java.util.Collection, org.coconut.core.Callback)
     */
    public void loadAll(CacheLoader<K, V> loader, Collection<? extends K> keys,
            Callback<Map<K, V>> callback) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.coconut.cache.spi.loading.CacheLoaderCallback#loadAllEntries(org.coconut.cache.CacheLoader,
     *      java.util.Collection, org.coconut.core.Callback)
     */
    public void loadAllEntries(CacheLoader<K, CacheEntry<K, V>> loader,
            Collection<? extends K> keys, Callback<Map<K, CacheEntry<K, V>>> callback) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.coconut.cache.spi.loading.CacheLoaderCallback#loadEntry(org.coconut.cache.CacheLoader,
     *      java.lang.Object, org.coconut.core.Callback)
     */
    public void loadEntry(CacheLoader<K, CacheEntry<K, V>> loader, K key,
            Callback<V> callback) {
        // TODO Auto-generated method stub

    }

}

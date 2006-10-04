/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.sandbox.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.util.AbstractCacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractStore<K, V> extends AbstractCacheLoader<K, CacheEntry<K, V>>
        implements SynchronousCacheStore<K, V> {

    /**
     * @see org.coconut.cache.sandbox.store.SynchronousCacheStore#delete(java.lang.Object,
     *      boolean)
     */
    public CacheEntry<K, V> delete(K key, boolean retrivePreviousValue) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.sandbox.store.SynchronousCacheStore#deleteAll(java.util.Collection,
     *      boolean)
     */
    public Map<K, CacheEntry<K, V>> deleteAll(Collection<? extends K> colKeys,
            boolean retrivePreviousValues) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.sandbox.store.SynchronousCacheStore#store(org.coconut.cache.CacheEntry,
     *      boolean)
     */
    public CacheEntry<K, V> store(CacheEntry<? extends K, ? extends V> entry,
            boolean retrivePreviousValue) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.sandbox.store.SynchronousCacheStore#storeAll(java.util.Collection,
     *      boolean)
     */
    public Map<K, CacheEntry<K, V>> storeAll(
            Collection<CacheEntry<? extends K, ? extends V>> entries,
            boolean retrievePrevioursValues) throws Exception {
        if (retrievePrevioursValues) {
            for (CacheEntry<? extends K, ? extends V> entry : entries) {
                store(entry, retrievePrevioursValues);
            }
            return new HashMap<K, CacheEntry<K, V>>();
        } else {
            HashMap<K, CacheEntry<K, V>> h = new HashMap<K, CacheEntry<K, V>>();
            for (CacheEntry<? extends K, ? extends V> entry : entries) {
                h.put(entry.getKey(), store(entry, retrievePrevioursValues));
            }
            return h;
        }
    }

}

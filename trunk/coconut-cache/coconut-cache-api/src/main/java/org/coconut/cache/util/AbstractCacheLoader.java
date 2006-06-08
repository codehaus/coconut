/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.cache.CacheLoader;

/**
 * An abstract implementation of a {@link org.coconut.cache.CacheLoader}. Use
 * this class if you only need to override the <code>load</code> method.
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public abstract class AbstractCacheLoader<K, V> implements CacheLoader<K, V> {

    /**
     * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
     */
    public final Map<K, V> loadAll(Collection<? extends K> keys)
            throws Exception {
        HashMap<K, V> h = new HashMap<K, V>();
        for (K key : keys) {
            h.put(key, load(key)); // we keep nulls
        }
        return h;
    }
}

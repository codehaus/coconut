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
public interface CacheLoaderCallback<K, V> {
    void load(CacheLoader<K, V> loader, K key, Callback<V> callback);
    void loadAll(CacheLoader<K, V> loader, Collection<? extends K> keys, Callback<Map<K, V>> callback);
    void loadEntry(CacheLoader<K, CacheEntry<K,V>> loader, K key, Callback<V> callback);
    void loadAllEntries(CacheLoader<K, CacheEntry<K,V>> loader, Collection<? extends K> keys, Callback<Map<K, CacheEntry<K,V>>> callback);
}

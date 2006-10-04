/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.sandbox.store;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface SynchronousCacheStore<K, V> extends CacheLoader<K, CacheEntry<K, V>> {

    CacheEntry<K, V> delete(K key, boolean retrivePreviousValue) throws Exception;

    Map<K, CacheEntry<K, V>> deleteAll(Collection<? extends K> colKeys,
            boolean retrivePreviousValues) throws Exception;

    CacheEntry<K, V> store(CacheEntry<? extends K, ? extends V> entry,
            boolean retrivePreviousValue) throws Exception;

    Map<K, CacheEntry<K, V>> storeAll(
            Collection<CacheEntry<? extends K, ? extends V>> entries,
            boolean retrievePrevioursValues) throws Exception;

    // query?
}

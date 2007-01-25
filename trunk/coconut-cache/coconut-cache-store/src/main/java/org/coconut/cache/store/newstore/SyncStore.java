/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.newstore;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheLoader;

/**
 * We need to able to retrieve the previous value... Shitty thing but is needed
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface SyncStore<K, V> extends CacheLoader<K, V> {
    /**
     * TODO: what if key doesn't exist return null or throw exception?????
     * 
     * @param key
     * @param retrievePreviousValue
     * @return the value attached to the key if retrievePreviousValue is
     *         <code>true</code> otherwise returns <code>null</code>
     * @throws Exception
     */
    V delete(K key, boolean retrievePreviousValue) throws Exception;

    Map<K, V> deleteAll(Collection<? extends K> colKeys,
            boolean retrievePreviousValues) throws Exception;

    /* Return previous value??? is needed */
    V store(K Key, V value, boolean retrievePreviousValue) throws Exception;

    Map<K, V> storeAll(Map<? extends K, ? extends V> mapEntries,
            boolean retrievePreviousValues) throws Exception;

}

/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheLoader;

/**
 * Interface responsible for storing entries.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheStore<K, V> extends CacheLoader<K, V> {

    /**
     * Deletes the key for the underlying store.
     * 
     * @param key
     *            the key to delete
     * @throws Exception
     */
    void delete(K key) throws Exception;

    void deleteAll(Collection<? extends K> keys) throws Exception;

    void store(K key, V value) throws Exception;

    void storeAll(Map<K, V> entries) throws Exception;
}

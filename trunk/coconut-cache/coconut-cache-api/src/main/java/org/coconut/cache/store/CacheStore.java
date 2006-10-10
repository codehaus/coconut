/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.store;

import java.util.Map;

import org.coconut.cache.CacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheStore<K, V> extends CacheLoader<K, V> {

    V store(K key, V value, boolean retrievePrevious) throws Exception;

    Map<K, V> storeAll(Map<K, V> entries, boolean retrievePrevious) throws Exception;
}

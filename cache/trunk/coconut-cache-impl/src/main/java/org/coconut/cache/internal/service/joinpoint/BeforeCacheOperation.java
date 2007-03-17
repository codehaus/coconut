/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.joinpoint;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface BeforeCacheOperation<K, V> {

    long beforeCacheClear(Cache<K, V> cache);

    long beforeCacheEvict(Cache<K, V> cache);

    long beforeGet(Cache<K, V> cache, K key);

    long beforePut(Cache<K, V> cache, CacheEntry<K, V> entry);

    long beforePut(Cache<K, V> cache, Object key, Object value);

    long beforePutAll(Cache<K, V> cache, Collection<? extends CacheEntry<K, V>> added);

    long beforePutAll(Cache<K, V> cache, Map<? extends K, ? extends V> map);

    long beforeRemove(Cache<K, V> cache, Object key);

    long beforeReplace(Cache<K, V> cache, K key, V oldValue, V newValue);

    long beforeTrimToSize(Cache<K, V> cache);

}

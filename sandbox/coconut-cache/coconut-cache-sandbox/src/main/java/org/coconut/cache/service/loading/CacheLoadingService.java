/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.CacheEntry;
import org.coconut.core.Callback;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheLoadingService<K, V> {

    Future<?> load(K key, CacheLoader<K, V> loader);

    Future<?> loadAll(Collection<? extends K> keys, CacheLoader<K, V> loader);

    V loadAndGet(K key, CacheLoader<K, V> loader);

    Map<K, V> loadAndGetAll(Collection<? extends K> keys, CacheLoader<K, V> loader);

    Future<?> load(K key, Callback<CacheEntry<K, V>> eh);
}

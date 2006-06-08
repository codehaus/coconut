package org.coconut.cache.spi;

import java.util.concurrent.Future;

/**
 * This should go away and be replaced with an Event based stuff.
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface AsyncCacheLoader<K, V> {
    Future<V> asyncLoad(K key);
    //Map<K, Future<V>> load(Collection< ? extends K> keys);
}

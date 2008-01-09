package org.coconut.cache.internal;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

public interface InternalCacheFactory<K, V> {
    Cache<K, V> create(Cache<K, V> c, CacheConfiguration<K, V> conf);
}

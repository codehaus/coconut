package org.coconut.cache.internal;

import org.coconut.cache.CacheEntry;

public interface InternalCacheEntry<K, V> extends CacheEntry<K, V> {
    CacheEntry<K, V> safe();
    boolean isDead();
}

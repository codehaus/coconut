package org.coconut.cache.internal.service.entry;

import org.coconut.cache.CacheEntry;

public interface InternalCacheEntry<K, V> extends CacheEntry<K, V> {

    boolean isCachable();

    boolean isExpired();
}

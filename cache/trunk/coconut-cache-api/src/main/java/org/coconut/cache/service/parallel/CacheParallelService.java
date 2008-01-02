package org.coconut.cache.service.parallel;

public interface CacheParallelService<K, V> {
    ParallelCache<K, V> get();
}

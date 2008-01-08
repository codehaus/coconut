package org.coconut.cache.internal.memory;

import org.coconut.cache.CacheEntry;
import org.coconut.internal.forkjoin.ParallelArray;

public interface EvictableMemoryStore<K, V> extends MemoryStore<K, V> {
    int getMaximumSize();

    long getMaximumVolume();

    ParallelArray<CacheEntry<K, V>> trimSmall();
    
    ParallelArray<CacheEntry<K, V>> trim();

    ParallelArray<CacheEntry<K, V>> trimTo(int size, long volume);

    void setMaximumSize(int size);

    void setMaximumVolume(long volume);

    long volume();
}

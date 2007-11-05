package org.coconut.cache.internal.service.eviction;

public interface EvictionSupport {

    void trimCache(int size, long capacity);
}

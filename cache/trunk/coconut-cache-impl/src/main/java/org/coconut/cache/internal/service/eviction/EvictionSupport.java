package org.coconut.cache.internal.service.eviction;

public interface EvictionSupport {

    void trimToVolume(long capacity);

    void trimToSize(int size);
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.List;

import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheEvictionService<K, V, T extends CacheEntry<K, V>> {

    int getPreferableSize();

    long getPreferableCapacity();

    /**
     * Clears the expiration policy.
     */
    void clear();

    T evictNext();

    void remove(int index);

    void touch(int index);

    boolean replace(int index, T t);

    List<T> evict(int count);

    List<T> evict(int size, long capacity);

    boolean isCapacityBreached(long capacity);

    boolean isSizeBreached(int size);

    int add(T t);
    
//methods when we have a some storage
    //public void evict(Object key);

    //public void evictAll(Collection keys) {}

    //public void evictIdleElements() {}
}

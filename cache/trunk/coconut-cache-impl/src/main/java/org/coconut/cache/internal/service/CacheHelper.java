/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.core.AttributeMap;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheHelper<K, V> {
    
    Cache<K,V> getCache();
//    
//    Collection<K> filterEntries(Collection<? super K> col,
//            Filter<? super CacheEntry> filter);

    void valueLoaded(K key, V value, AttributeMap attributes);

    void valuesLoaded(Map<? super K, ? extends V> values,
            Map<? extends K, AttributeMap> keys);

    boolean expire(K key);

    int expireAll(Collection<? extends K> collection);

    int expireAll(Filter<? extends CacheEntry<K, V>> filter);

    Object getMutex();

    V put(K key, V value, AttributeMap attributes);
    void putAll(Map<? extends K, ? extends V> keyValues);
    void putAll(Map<? extends K, ? extends V> keyValues,
            Map<? extends K, AttributeMap> attributes);

    boolean isValid(K key);

    Collection<? extends K> filterKeys(Filter<? super CacheEntry<K, V>> filter);

    Collection<? extends CacheEntry<K, V>> filter(Filter<? super CacheEntry<K, V>> filter);
    
    public void trimToCapacity(long capacity);

    public void trimToSize(int size);

    public void evict(Object key);
    public void evictAll(Collection keys);

    void evictIdleElements();
}

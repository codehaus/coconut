/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SynchronizedPocketCache<K, V> implements PocketCache<K, V>, Serializable {

    private final UnsafePocketCache<K, V> cache;

    private final Map<K, V> syncMap;

    public SynchronizedPocketCache(ValueLoader<K, V> loader) {
        this.cache = new UnsafePocketCache<K, V>(loader);
        syncMap = Collections.synchronizedMap(cache);
    }

    public void clear() {
        synchronized (cache) {
            cache.clear();
        }
    }

    public boolean containsKey(Object key) {
        synchronized (cache) {
            return cache.containsKey(key);
        }
    }

    public boolean containsValue(Object value) {
        synchronized (cache) {
            return cache.containsValue(value);
        }
    }

    public Set<Entry<K, V>> entrySet() {
        synchronized (cache) {
            return syncMap.entrySet();
        }
    }

    public boolean equals(Object o) {
        synchronized (cache) {
            return cache.equals(o);
        }
    }

    public void evict() {
        synchronized (cache) {
            cache.evict();
        }
    }

    public V get(Object key) {
        synchronized (cache) {
            return cache.get(key);
        }
    }

    public Map<K, V> getAll(Collection<? extends K> keys) {
        synchronized (cache) {
            return cache.getAll(keys);
        }
    }

    public double getHitRatio() {
        synchronized (cache) {
            return cache.getHitRatio();
        }
    }

    public long getNumberOfHits() {
        synchronized (cache) {
            return cache.getNumberOfHits();
        }
    }

    public long getNumberOfMisses() {
        synchronized (cache) {
            return cache.getNumberOfMisses();
        }
    }

    public int hashCode() {
        synchronized (cache) {
            return cache.hashCode();
        }
    }

    public boolean isEmpty() {
        synchronized (cache) {
            return cache.isEmpty();
        }
    }

    public Set<K> keySet() {
        synchronized (cache) {
            return syncMap.keySet();
        }
    }

    public V peek(Object key) {
        synchronized (cache) {
            return cache.peek(key);
        }
    }

    public V put(K key, V value) {
        synchronized (cache) {
            return cache.put(key, value);
        }
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        synchronized (cache) {
            cache.putAll(t);
        }
    }

    public V remove(Object key) {
        synchronized (cache) {
            return cache.remove(key);
        }
    }

    public void resetStatistics() {
        synchronized (cache) {
            cache.resetStatistics();
        }
    }

    public int size() {
        synchronized (cache) {
            return cache.size();
        }
    }

    public String toString() {
        synchronized (cache) {
            return cache.toString();
        }
    }

    public void trimToSize(int newSize) {
        synchronized (cache) {
            cache.trimToSize(newSize);
        }
    }

    public Collection<V> values() {
        synchronized (cache) {
            return syncMap.values();
        }
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    public V putIfAbsent(K key, V value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object, java.lang.Object)
     */
    public boolean remove(Object key, Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object)
     */
    public V replace(K key, V value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public boolean replace(K key, V oldValue, V newValue) {
        // TODO Auto-generated method stub
        return false;
    }
}

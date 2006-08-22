/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsafePocketCache<K, V> implements Map<K, V> {

    private final Map<K, V> map=new HashMap<K,V>();

    private long hits;

    private long misses;

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    public V peek(Object key) {
        return null;
    }

    public void trimToSize(int newSize) {

    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(K key, V value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> t) {
        // TODO Auto-generated method stub

    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return map.remove(key);
    }

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        return map.size();
    }

    public void resetStatistics() {
        hits = 0;
        misses = 0;
    }

    public long getHits() {
        return hits;
    }

    public long getMisses() {
        return misses;
    }

    public double getHitRatio() {
        return hits == 0 && misses == 0 ? Double.NaN : (hits / (misses + hits));
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        map.clear();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }
}

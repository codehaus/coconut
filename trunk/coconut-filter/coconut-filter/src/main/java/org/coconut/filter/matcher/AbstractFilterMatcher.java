/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractFilterMatcher<K, E> implements FilterMatcher<K, E> {
    private final Map<K, Filter<? super E>> map;

    public AbstractFilterMatcher(Map<K, Filter<? super E>> map) {
        this.map = map;
    }

    protected Map<K,Filter<? super E>> getMap() {
        return map;
    }
    /**
     * @see java.util.concurrent.ConcurrentHashMap#clear()
     */
    public void clear() {
        map.clear();
    }
   
    /**
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<K, Filter<? super E>>> entrySet() {
        return map.entrySet();
    }

    /**
     * @param key
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * @param value
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }


    /**
     * @param o
     * @return
     * @see java.util.AbstractMap#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return map.equals(o);
    }

    /**
     * @param key
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#get(java.lang.Object)
     */
    public Filter<? super E> get(Object key) {
        return map.get(key);
    }

    /**
     * @return
     * @see java.util.AbstractMap#hashCode()
     */
    public int hashCode() {
        return map.hashCode();
    }

    /**
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#isEmpty()
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }


    /**
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#keySet()
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * @param key
     * @param value
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#put(java.lang.Object,
     *      java.lang.Object)
     */
    public Filter<? super E> put(K key, Filter<? super E> value) {
        return map.put(key, value);
    }

    /**
     * @param t
     * @see java.util.concurrent.ConcurrentHashMap#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends Filter<? super E>> t) {
        map.putAll(t);
    }

    /**
     * @param key
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#remove(java.lang.Object)
     */
    public Filter<? super E> remove(Object key) {
        return map.remove(key);
    }


    /**
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#size()
     */
    public int size() {
        return map.size();
    }

    /**
     * @return
     * @see java.util.AbstractMap#toString()
     */
    public String toString() {
        return map.toString();
    }

    /**
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#values()
     */
    public Collection<Filter<? super E>> values() {
        return map.values();
    }

}

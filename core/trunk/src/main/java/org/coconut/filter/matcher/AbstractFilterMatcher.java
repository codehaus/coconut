/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
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
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }


    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return map.equals(o);
    }


    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Filter<? super E> get(Object key) {
        return map.get(key);
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return map.hashCode();
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
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
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
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Filter<? super E> remove(Object key) {
        return map.remove(key);
    }



    /**
     * @see java.util.Map#size()
     */
    public int size() {
        return map.size();
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return map.toString();
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection<Filter<? super E>> values() {
        return map.values();
    }

}

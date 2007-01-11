/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.core.EventProcessor;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultFilterMatcher<K, E> implements FilterMatcher<K, E> {

    private final ConcurrentHashMap<K, Filter<? super E>> map = new ConcurrentHashMap<K, Filter<? super E>>();

    /**
     * @see org.coconut.filter.spi.FilterIndexer#match(java.lang.Object)
     */
    public Collection<K> match(E event) {
        ArrayList<K> al = new ArrayList<K>();
        for (Map.Entry<K, Filter<? super E>> f : map.entrySet()) {
            if (f.getValue().accept(event)) {
                al.add(f.getKey());
            }
        }
        return al;
    }

    /**
     * @see org.coconut.filter.matcher.FilterMatcher#apply(java.lang.Object, org.coconut.core.EventHandler)
     */
    public void match(E object, EventProcessor<E> eh) {
        for (Map.Entry<K, Filter<? super E>> f : map.entrySet()) {
            if (f.getValue().accept(object)) {
               eh.process(object);
            }
        }
    }
    
    /**
     * 
     * @see java.util.concurrent.ConcurrentHashMap#clear()
     */
    public void clear() {
        map.clear();
    }

    /**
     * @param value
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#contains(java.lang.Object)
     */
    public boolean contains(Object value) {
        return map.contains(value);
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
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#elements()
     */
    public Enumeration<Filter<? super E>> elements() {
        return map.elements();
    }

    /**
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#entrySet()
     */
    public Set<Entry<K, Filter<? super E>>> entrySet() {
        return map.entrySet();
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
     * @see java.util.concurrent.ConcurrentHashMap#keys()
     */
    public Enumeration<K> keys() {
        return map.keys();
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
     * @see java.util.concurrent.ConcurrentHashMap#put(java.lang.Object, java.lang.Object)
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
     * @param value
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    public Filter<? super E> putIfAbsent(K key, Filter<? super E> value) {
        return map.putIfAbsent(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#remove(java.lang.Object, java.lang.Object)
     */
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
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
     * @param key
     * @param oldValue
     * @param newValue
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public boolean replace(K key, Filter<? super E> oldValue, Filter<? super E> newValue) {
        return map.replace(key, oldValue, newValue);
    }

    /**
     * @param key
     * @param value
     * @return
     * @see java.util.concurrent.ConcurrentHashMap#replace(java.lang.Object, java.lang.Object)
     */
    public Filter<? super E> replace(K key, Filter<? super E> value) {
        return map.replace(key, value);
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

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DummyCache<K, V> implements Cache<K, V> {
	private String name;

	/**
     * @see org.coconut.cache.Cache#hasService(java.lang.Class)
     */
	public boolean hasService(Class serviceType) {
		// TODO Auto-generated method stub
		return false;
	}

	public volatile boolean isStarted;

	/**
     * @param configuration
     */
	public DummyCache(CacheConfiguration configuration) {
		this.name = configuration.getName();
	}

	/**
     * @see org.coconut.cache.Cache#evict()
     */
	public void evict() {
	// TODO Auto-generated method stub

	}

	/**
     * @see org.coconut.cache.Cache#get(java.lang.Object)
     */
	public V get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see org.coconut.cache.Cache#getAll(java.util.Collection)
     */
	public Map<K, V> getAll(Collection<? extends K> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see org.coconut.cache.Cache#getCapacity()
     */
	public long getCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
     * @see org.coconut.cache.Cache#getEntry(java.lang.Object)
     */
	public CacheEntry<K, V> getEntry(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see org.coconut.cache.Cache#getName()
     */
	public String getName() {
		return name;
	}

	/**
     * @see org.coconut.cache.Cache#getService(java.lang.Class)
     */
	public <T> T getService(Class<T> serviceType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see org.coconut.cache.Cache#peek(java.lang.Object)
     */
	public V peek(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
	public CacheEntry<K, V> peekEntry(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see org.coconut.cache.Cache#put(java.lang.Object, java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
	public V put(K key, V value, AttributeMap attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
	public V putIfAbsent(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object,
     *      java.lang.Object)
     */
	public boolean remove(Object key, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
     *      java.lang.Object)
     */
	public V replace(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
	public boolean replace(K key, V oldValue, V newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
     * @see java.util.Map#clear()
     */
	public void clear() {
	// TODO Auto-generated method stub

	}

	/**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

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
     * @see java.util.Map#isEmpty()
     */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
     * @see java.util.Map#keySet()
     */
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
	public V put(K key, V value) {
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
     * @see java.util.Map#remove(java.lang.Object)
     */
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @see java.util.Map#size()
     */
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
     * @see java.util.Map#values()
     */
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

}

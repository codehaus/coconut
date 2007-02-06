/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AbstractCache;

/**
 * TODO fix loading. fix event bus make cache services mutable
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SynchronizedCache<K, V> extends AbstractCache<K, V> {

    private UnsynchronizedCache<K, V> wrapped;

    /**
     * @see org.coconut.cache.spi.AbstractCache#trimToSize(int)
     */
    @Override
    public synchronized void trimToSize(int newSize) {
        wrapped.trimToSize(newSize);
    }

    /**
     * @see java.util.AbstractMap#entrySet()
     */
    @Override
    public synchronized Set<java.util.Map.Entry<K, V>> entrySet() {
        return wrapped.entrySet();
    }

    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    protected synchronized CacheEntry<K, V> peekEntry(K key, boolean doCopy) {
        return wrapped.peekEntry(key, doCopy);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#containsValue(java.lang.Object)
     */
    @Override
    public synchronized boolean containsValue(Object value) {
        return wrapped.containsValue(value);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#evict()
     */
    @Override
    public synchronized void evict() {
        wrapped.evict();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getAll0(java.util.Collection)
     */
    @Override
    public synchronized Map<K, V> getAll(Collection<? extends K> keys) {
        return wrapped.getAll(keys);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#getHitStat()
     */
    @Override
    public synchronized org.coconut.cache.Cache.HitStat getHitStat() {
        return wrapped.getHitStat();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#loadAllAsync(java.util.Collection)
     */
    @Override
    public synchronized Future<?> loadAll(Collection<? extends K> keys) {
        return wrapped.loadAll(keys);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#loadAsync(java.lang.Object)
     */
    @Override
    public synchronized Future<?> load(K key) {
        return wrapped.loadAsync(this, key);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public synchronized V putIfAbsent(K key, V value) {
        return wrapped.putIfAbsent(key, value);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#remove(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public synchronized boolean remove(Object key, Object value) {
        return wrapped.remove(key, value);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        return wrapped.replace(key, oldValue, newValue);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#replace(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public synchronized V replace(K key, V value) {
        return wrapped.replace(key, value);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#resetStatistics()
     */
    @Override
    public synchronized void resetStatistics() {
        wrapped.resetStatistics();
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putEntries(java.util.Collection)
     */
    @Override
    public synchronized void putEntries(Collection<CacheEntry<K, V>> entries) {
        wrapped.putEntries(entries);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#putEntry(org.coconut.cache.CacheEntry)
     */
    @Override
    public synchronized void putEntry(CacheEntry<K, V> entry) {
        wrapped.putEntry(entry);
    }

    /**
     * @see org.coconut.cache.Cache#getEntry(java.lang.Object)
     */
    public CacheEntry<K, V> getEntry(K key) {
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
     *      long, java.util.concurrent.TimeUnit)
     */
    public V put(K key, V value, long timeout, TimeUnit unit) {
        return put(key, value, timeout, unit);
    }

}

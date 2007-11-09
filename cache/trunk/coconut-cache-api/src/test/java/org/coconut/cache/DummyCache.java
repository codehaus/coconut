/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A dummy implementation of a {@link Cache}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DummyCache<K, V> implements Cache<K, V> {

    private final String name;

    public volatile boolean isStarted;

    public DummyCache(CacheConfiguration<?, ?> configuration) {
        this.name = configuration.getName();
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    /** {@inheritDoc} */
    public void clear() {}

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return false;
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return false;
    }

    /** {@inheritDoc} */
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return null;
    }

    /** {@inheritDoc} */
    public V get(Object key) {
        return null;
    }

    /** {@inheritDoc} */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        return null;
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> getEntry(K key) {
        return null;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public <T> T getService(Class<T> serviceType) {
        return null;
    }

    /** {@inheritDoc} */
    public long getVolume() {
        return 0;
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isShutdown() {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isStarted() {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isTerminated() {
        return false;
    }

    /** {@inheritDoc} */
    public Set<K> keySet() {
        return null;
    }

    /** {@inheritDoc} */
    public V peek(K key) {
        return null;
    }

    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    public CacheEntry<K, V> peekEntry(K key) {
        return null;
    }

    /** {@inheritDoc} */
    public V put(K key, V value) {
        return null;
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends K, ? extends V> t) {}

    /** {@inheritDoc} */
    public V putIfAbsent(K key, V value) {
        return null;
    }

    /** {@inheritDoc} */
    public V remove(Object key) {
        return null;
    }

    /** {@inheritDoc} */
    public boolean remove(Object key, Object value) {
        return false;
    }

    /** {@inheritDoc} */
    public void removeAll(Collection<? extends K> keys) {

    }

    /** {@inheritDoc} */
    public V replace(K key, V value) {
        return null;
    }

    /** {@inheritDoc} */
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    /** {@inheritDoc} */
    public void shutdown() {}

    /** {@inheritDoc} */
    public void shutdownNow() {}

    /** {@inheritDoc} */
    public int size() {
        return 0;
    }

    /** {@inheritDoc} */
    public Collection<V> values() {
        return null;
    }
}

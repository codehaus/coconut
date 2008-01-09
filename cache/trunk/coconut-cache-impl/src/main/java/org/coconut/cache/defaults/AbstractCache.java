/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheServices;
import org.coconut.cache.internal.InternalCacheFactory;
import org.coconut.cache.spi.ConfigurationValidator;

/**
 * An abstract implementation of {@link Cache}. Currently not general usable, hence some
 * methods and constructors have package private access.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCache<K, V> extends AbstractMap<K, V> implements Cache<K, V> {
    final Cache<K, V> cache;

    AbstractCache(InternalCacheFactory factory) {
        this(factory, CacheConfiguration.create());
    }

    AbstractCache(InternalCacheFactory factory, CacheConfiguration conf) {
        ConfigurationValidator.getInstance().verify(conf, getClass());
        this.cache = factory.create(this, conf);
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return cache.awaitTermination(timeout, unit);
    }

    /** {@inheritDoc} */
    public void clear() {
        cache.clear();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    /** {@inheritDoc} */
    public Set<Entry<K, V>> entrySet() {
        return cache.entrySet();
    }

    /** {@inheritDoc} */
    public V get(Object key) {
        return cache.get(key);
    }

    /** {@inheritDoc} */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        return cache.getAll(keys);
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> getEntry(K key) {
        return cache.getEntry(key);
    }

    /** {@inheritDoc} */
    public String getName() {
        return cache.getName();
    }

    /** {@inheritDoc} */
    public <T> T getService(Class<T> serviceType) {
        return cache.getService(serviceType);
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /** {@inheritDoc} */
    public boolean isShutdown() {
        return cache.isShutdown();
    }

    /** {@inheritDoc} */
    public boolean isStarted() {
        return cache.isStarted();
    }

    /** {@inheritDoc} */
    public boolean isTerminated() {
        return cache.isTerminated();
    }

    /** {@inheritDoc} */
    public Set<K> keySet() {
        return cache.keySet();
    }

    /** {@inheritDoc} */
    public V peek(K key) {
        return cache.peek(key);
    }

    /** {@inheritDoc} */
    public CacheEntry<K, V> peekEntry(K key) {
        return cache.peekEntry(key);
    }

    public abstract void prestart();

    /** {@inheritDoc} */
    public V put(K key, V value) {
        return cache.put(key, value);
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends K, ? extends V> m) {
        cache.putAll(m);
    }

    /** {@inheritDoc} */
    public V putIfAbsent(K key, V value) {
        return cache.putIfAbsent(key, value);
    }

    /** {@inheritDoc} */
    public V remove(Object key) {
        return cache.remove(key);
    }

    /** {@inheritDoc} */
    public boolean remove(Object key, Object value) {
        return cache.remove(key, value);
    }

    /** {@inheritDoc} */
    public void removeAll(Collection<? extends K> keys) {
        cache.removeAll(keys);
    }

    /** {@inheritDoc} */
    public V replace(K key, V value) {
        return cache.replace(key, value);
    }

    /** {@inheritDoc} */
    public boolean replace(K key, V oldValue, V newValue) {
        return cache.replace(key, oldValue, newValue);
    }

    public CacheServices<K, V> services() {
        return cache.services();
    }

    /** {@inheritDoc} */
    public void shutdown() {
        cache.shutdown();
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        cache.shutdownNow();
    }

    /** {@inheritDoc} */
    public int size() {
        return cache.size();
    }

    /** {@inheritDoc} */
    public Collection<V> values() {
        return cache.values();
    }

    /** {@inheritDoc} */
    public long volume() {
        return cache.volume();
    }
}

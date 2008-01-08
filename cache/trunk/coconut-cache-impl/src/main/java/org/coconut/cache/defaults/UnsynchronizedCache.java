/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.UnsynchronizedInternalCache;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.parallel.CacheParallelService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.CacheServiceSupport;
import org.coconut.cache.spi.ConfigurationValidator;

/**
 * An unsynchronized cache implementation.
 * <p>
 * If multiple threads access this cache concurrently, and at least one of the threads
 * modifies the cache structurally, it <i>must</i> be synchronized externally. (A
 * structural modification is any operation that adds, deletes or changes one or more
 * mappings.) This is typically accomplished by synchronizing on some object that
 * naturally encapsulates the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
@NotThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheEvictionService.class,
        CacheExpirationService.class, CacheLoadingService.class, CacheParallelService.class,
        CacheServiceManagerService.class, CacheStatisticsService.class })
public class UnsynchronizedCache<K, V> extends AbstractCache<K, V> {
    private final UnsynchronizedInternalCache<K, V> cache;

    /**
     * Creates a new UnsynchronizedCache with a default configuration.
     */
    public UnsynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    /**
     * Creates a new UnsynchronizedCache from the specified configuration.
     * 
     * @param conf
     *            the configuration to create the cache from
     * @throws NullPointerException
     *             if the specified configuration is <code>null</code>
     */
    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        ConfigurationValidator.getInstance().verify(conf, UnsynchronizedCache.class);
        cache = new UnsynchronizedInternalCache(this, conf);
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
    public long getVolume() {
        return cache.getVolume();
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

    public void prestart() {
        cache.prestart();
    }

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
    public boolean remove(Object key, Object value) {
        return cache.remove(key, value);
    }

    /** {@inheritDoc} */
    public V remove(Object key) {
        return cache.remove(key);
    }

    /** {@inheritDoc} */
    public void removeAll(Collection<? extends K> keys) {
        cache.removeAll(keys);
    }

    /** {@inheritDoc} */
    public boolean replace(K key, V oldValue, V newValue) {
        return cache.replace(key, oldValue, newValue);
    }

    /** {@inheritDoc} */
    public V replace(K key, V value) {
        return cache.replace(key, value);
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
    public final int size() {
        return cache.size();
    }

    /** {@inheritDoc} */
    public Collection<V> values() {
        return cache.values();
    }
}

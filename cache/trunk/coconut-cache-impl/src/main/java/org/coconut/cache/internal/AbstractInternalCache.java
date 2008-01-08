package org.coconut.cache.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.servicemanager.AbstractCacheServiceManager;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.internal.util.CollectionUtils;
import org.coconut.operations.Predicates;

public abstract class AbstractInternalCache<K, V> implements InternalCache<K, V> {

    private final String name;

    final MemoryStore<K, V> memoryCache;

    final InternalCacheListener<K, V> listener;

    final AbstractCacheServiceManager serviceManager;

    public AbstractInternalCache(Cache cache, CacheConfiguration conf, Collection<Class<?>> classes) {
        name = getName(conf);
        ServiceComposer composer = ServiceComposer.compose(cache, this, name, conf, classes);
        serviceManager = composer.getInternalService(AbstractCacheServiceManager.class);
        memoryCache = composer.getInternalService(MemoryStore.class);
        listener = composer.getInternalService(InternalCacheListener.class);
    }

    public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return serviceManager.awaitTermination(timeout, unit);
    }

    public boolean containsKey(Object key) {
        serviceManager.lazyStart();
        return memoryCache.get(key) != null;
    }

    public boolean containsValue(Object value) {
        serviceManager.lazyStart();
        return memoryCache.withFilterOnValues(Predicates.isEquals(value)).any() != null;
    }

    public final String getName() {
        return name;
    }

    public final <T> T getService(Class<T> serviceType) {
        return serviceManager.getServiceFromCache(serviceType);
    }

    public long getVolume() {
        serviceManager.lazyStart();
        return memoryCache.volume();
    }

    public final boolean isEmpty() {
        return size() == 0;
    }

    public final boolean isShutdown() {
        return serviceManager.isShutdown();
    }

    public final boolean isStarted() {
        return serviceManager.isStarted();
    }

    public final boolean isTerminated() {
        return serviceManager.isTerminated();
    }

    public V peek(K key) {
        serviceManager.lazyStart();
        CacheEntry<K, V> prev = memoryCache.get(key);
        return prev == null ? null : prev.getValue();
    }

    public CacheEntry<K, V> peekEntry(K key) {
        serviceManager.lazyStart();
        return memoryCache.get(key);
    }

    public final void shutdown() {
        serviceManager.shutdown();
    }

    public final void shutdownNow() {
        serviceManager.shutdownNow();
    }

    public int size() {
        serviceManager.lazyStart();
        return memoryCache.size();
    }

    /** {@inheritDoc} */
    public final V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> removed = doRemove(key, null);
        return removed == null ? null : removed.getValue();
    }

    /** {@inheritDoc} */
    public final boolean remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        return doRemove(key, value) != null;
    }

    public final void removeAll(Collection<? extends K> keys) {
        removeKeys(keys);
    }

    abstract CacheEntry<K, V> doRemove(Object key, Object value);

    public final V put(K key, V value) {
        CacheEntry<K, V> prev = put(key, value, Attributes.EMPTY_ATTRIBUTE_MAP);
        return prev == null ? null : prev.getValue();
    }

    public final CacheEntry<K, V> put(K key, V value, AttributeMap attributes) {
        return put(key, value, Attributes.EMPTY_ATTRIBUTE_MAP, false);
    }

    abstract CacheEntry<K, V> put(K key, V value, AttributeMap attributes, boolean OnlyIfAbsent);

    public final V putIfAbsent(K key, V value) {
        CacheEntry<K, V> prev = put(key, value, Attributes.EMPTY_ATTRIBUTE_MAP, true);
        return prev == null ? null : prev.getValue();
    }

    public final void putAll(Map<? extends K, ? extends V> t) {
        HashMap map = new HashMap();
        for (Map.Entry me : t.entrySet()) {
            map.put(me.getKey(), new CollectionUtils.SimpleImmutableEntry(me.getValue(),
                    Attributes.EMPTY_ATTRIBUTE_MAP));
        }
        putAllWithAttributes(map);
    }

    static String getName(CacheConfiguration configuration) {
        String name = configuration.getName();
        if (name == null) {
            return UUID.randomUUID().toString();
        } else {
            return name;
        }
    }
}

package org.coconut.cache.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.SynchronizedEntryFactoryService;
import org.coconut.cache.internal.service.entry.SynchronizedEntryMap;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.SynchronizedCacheEvictionService;
import org.coconut.cache.internal.service.exceptionhandling.DefaultCacheExceptionService;
import org.coconut.cache.internal.service.expiration.SynchronizedCacheExpirationService;
import org.coconut.cache.internal.service.listener.DefaultCacheListener;
import org.coconut.cache.internal.service.loading.SynchronizedCacheLoaderService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.cache.internal.service.servicemanager.SynchronizedCacheServiceManager;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.internal.service.worker.SynchronizedCacheWorkerService;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

public class SynchronizedInternalCache<K, V> extends AbstractInternalCache<K, V> {

    private final static Collection<Class<?>> components;

    static {
        List<Class<?>> c = new ArrayList<Class<?>>();
        c.add(DefaultCacheStatisticsService.class);
        c.add(DefaultCacheListener.class);
        c.add(SynchronizedCacheEvictionService.class);
        c.add(SynchronizedCacheExpirationService.class);
        c.add(SynchronizedCacheLoaderService.class);
        c.add(DefaultCacheManagementService.class);
        c.add(DefaultCacheEventService.class);
        c.add(SynchronizedCacheWorkerService.class);
        c.add(SynchronizedCacheServiceManager.class);
        c.add(SynchronizedEntryFactoryService.class);
        c.add(SynchronizedEntryMap.class);
        /* c.add(SynchronizedInternalCache.class); */
        /* SynchronizedParallelCacheService.class);c.add( */
        c.add(DefaultCacheExceptionService.class);
        components = Collections.unmodifiableCollection(c);
    }

    private final Object mutex;
    public SynchronizedInternalCache(Cache cache, CacheConfiguration conf) {
        super(null, null, null);
        this.mutex = cache;
    }
    public SynchronizedInternalCache(ServiceComposer sc) {
        super(null, null, null);
        this.mutex = sc.getInternalService(Cache.class);
    }

    public void clear() {
        long started = listener.beforeCacheClear();
        Collection<? extends CacheEntry<K, V>> list = Collections.EMPTY_LIST;
        long volume = 0;

        synchronized (mutex) {
            serviceManager.lazyStart();
            volume = memoryCache.volume();
            //list = memoryCache.clear();
        }

        listener.afterCacheClear(started, list, volume);
    }

    @Override
    public int size() {
        synchronized (mutex) {
            return super.size();
        }
    }

    @Override
    public long getVolume() {
        synchronized (mutex) {
            return super.getVolume();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        synchronized (mutex) {
            return super.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        synchronized (mutex) {
            return super.containsValue(value);
        }
    }

    @Override
    public V peek(K key) {
        synchronized (mutex) {
            return super.peek(key);
        }
    }

    @Override
    public CacheEntry<K, V> peekEntry(K key) {
        synchronized (mutex) {
            return super.peekEntry(key);
        }
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        long started = listener.beforeRemove(key, value);
        CacheEntry<K, V> e = null;

        synchronized (mutex) {
            serviceManager.lazyStart();
            e = memoryCache.remove(key, value);
        }

        listener.afterRemove(started, e);
        return e;
    }

    public boolean removeValue(Object value) {
        long started = listener.beforeRemove(null, value);
        CacheEntry<K, V> e = null;

        synchronized (mutex) {
            serviceManager.lazyStart();
           // e = memoryCache.removeValue(value);
        }

        listener.afterRemove(started, e);
        return e != null;
    }

    @Override
    CacheEntry<K, V> put(K key, V value, AttributeMap attributes, boolean OnlyIfAbsent) {
        return null;
    }

    public void clearView(Mapper pre, Predicate p) {}

    public void putAllWithAttributes(Map<K, java.util.Map.Entry<V, AttributeMap>> data) {}

    public boolean removeEntries(Collection<?> entries) {
        return false;
    }

    public boolean removeKeys(Collection<?> keys) {
        return false;
    }

    public boolean removeValues(Collection<?> values) {
        return false;
    }

    public boolean retainAll(Mapper pre, Predicate selector, Collection<?> c) {
        return false;
    }

    public V get(Object key) {
        return null;
    }

    public Map<K, V> getAll(Collection<? extends K> keys) {
        return null;
    }

    public CacheEntry<K, V> getEntry(K key) {
        return null;
    }

    public V replace(K key, V value) {
        return null;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    public <T> void apply(Predicate<? super CacheEntry<K, V>> selector,
            Mapper<? super CacheEntry<K, V>, T> mapper, Procedure<T> procedure) {}

    public Object getMutex() {
        return null;
    }

    public boolean isSynchronized() {
        return false;
    }

    public Collection<V> values() {
        return null;
    }

    public void prestart() {}

    public Set<K> keySet() {
        return null;
    }

    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}

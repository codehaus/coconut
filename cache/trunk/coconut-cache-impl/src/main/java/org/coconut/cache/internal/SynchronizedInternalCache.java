package org.coconut.cache.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.SynchronizedEntryFactoryService;
import org.coconut.cache.internal.service.entry.SynchronizedEntryMap;
import org.coconut.cache.internal.service.eviction.SynchronizedCacheEvictionService;
import org.coconut.cache.internal.service.expiration.SynchronizedCacheExpirationService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.cache.internal.service.servicemanager.SynchronizedCacheServiceManager;
import org.coconut.cache.internal.service.worker.SynchronizedCacheWorkerService;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;

public class SynchronizedInternalCache<K, V> extends AbstractInternalCache<K, V> {

    private final Object mutex;

    public SynchronizedInternalCache(ServiceComposer sc) {
        super(null, null, null);
        this.mutex = sc.getInternalService(Cache.class);
    }

    private SynchronizedInternalCache(Cache cache, CacheConfiguration conf,
            Collection<Class<?>> components) {
        super(cache, conf, components, Collections.singleton(CacheMutex.from(cache)));
        this.mutex = cache;
    }

    public void clear() {
        long started = listener.beforeCacheClear();
        Collection<? extends CacheEntry<K, V>> list = Collections.EMPTY_LIST;
        long volume = 0;

        synchronized (mutex) {
            lazyStart();
            volume = memoryCache.volume();
            // list = memoryCache.clear();
        }

        listener.afterCacheClear(started, list, volume);
    }

    public void clearView(Predicate p) {}

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

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = entrySet;
        return (es != null) ? es : (entrySet = new SynchronizedEntrySet(mutex));
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

    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null) ? ks : (keySet = new SynchronizedKeySet(mutex));
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

    public void putAllWithAttributes(Map<K, java.util.Map.Entry<V, AttributeMap>> data) {}

    public boolean removeEntries(Collection<?> entries) {
        return false;
    }

    public boolean removeKeys(Collection<?> keys) {
        return false;
    }

    public boolean removeValue(Object value) {
        long started = listener.beforeRemove(null, value);
        CacheEntry<K, V> e = null;

        synchronized (mutex) {
            lazyStart();
            // e = memoryCache.removeValue(value);
        }

        listener.afterRemove(started, e);
        return e != null;
    }

    public boolean removeValues(Collection<?> values) {
        return false;
    }

    public V replace(K key, V value) {
        return null;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    public boolean retainAll(Mapper pre, Collection<?> c) {
        return false;
    }

    @Override
    public int size() {
        synchronized (mutex) {
            return super.size();
        }
    }

    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new SynchronizedValues(mutex));
    }

    @Override
    public long volume() {
        synchronized (mutex) {
            return super.volume();
        }
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        long started = listener.beforeRemove(key, value);
        CacheEntry<K, V> e = null;

        synchronized (mutex) {
            lazyStart();
            e = memoryCache.remove(key, value);
        }

        listener.afterRemove(started, e);
        return e;
    }

    @Override
    CacheEntry<K, V> put(K key, V value, AttributeMap attributes, boolean OnlyIfAbsent) {
        return null;
    }

    final class SynchronizedEntrySet extends EntrySet {
        private final Object mutex;

        SynchronizedEntrySet(Object mutex) {
            this.mutex = mutex;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        @Override
        public boolean equals(Object o) {
            synchronized (mutex) {
                return super.equals(o);
            }
        }

        @Override
        public int hashCode() {
            synchronized (mutex) {
                return super.hashCode();
            }
        }

        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        @Override
        public String toString() {
            synchronized (mutex) {
                return toString();
            }
        }

    }

    static class SynchronizedInternalCacheFactory<K, V> implements InternalCacheFactory<K, V> {
        public Cache<K, V> create(Cache<K, V> cache, CacheConfiguration<K, V> configuration) {
            Collection<Class<?>> components = defaultComponents(configuration);

            components.add(SynchronizedCacheEvictionService.class);
            components.add(SynchronizedCacheExpirationService.class);
            if (configuration.management().isEnabled()) {
                components.add(DefaultCacheManagementService.class);
            }
            components.add(SynchronizedCacheWorkerService.class);
            components.add(SynchronizedCacheServiceManager.class);
            components.add(SynchronizedEntryFactoryService.class);
            components.add(SynchronizedEntryMap.class);
            return new SynchronizedInternalCache(cache, configuration, components);
        }
    }

    final class SynchronizedKeySet extends KeySet {
        private final Object mutex;

        SynchronizedKeySet(Object mutex) {
            this.mutex = mutex;
        }

        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        @Override
        public boolean equals(Object o) {
            synchronized (mutex) {
                return super.equals(o);
            }
        }

        @Override
        public int hashCode() {
            synchronized (mutex) {
                return super.hashCode();
            }
        }

        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        @Override
        public String toString() {
            synchronized (mutex) {
                return toString();
            }
        }
    }

    final class SynchronizedValues extends Values {
        private final Object mutex;

        SynchronizedValues(Object mutex) {
            this.mutex = mutex;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        @Override
        public String toString() {
            synchronized (mutex) {
                return toString();
            }
        }
    }
}

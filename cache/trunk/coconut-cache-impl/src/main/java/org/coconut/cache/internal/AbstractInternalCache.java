package org.coconut.cache.internal;

import static org.coconut.operations.Mappers.CONSTANT_MAPPER;
import static org.coconut.operations.Mappers.MAP_ENTRY_TO_KEY_MAPPER;
import static org.coconut.operations.Mappers.MAP_ENTRY_TO_VALUE_MAPPER;
import static org.coconut.operations.Mappers.constant;
import static org.coconut.operations.Mappers.mapEntryToKey;
import static org.coconut.operations.Mappers.mapEntryToValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.internal.memory.MemoryStoreWithMapping;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.servicemanager.AbstractCacheServiceManager;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.cache.service.parallel.ParallelCache;
import org.coconut.internal.util.CollectionUtils;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;

public abstract class AbstractInternalCache<K, V> implements InternalCache<K, V> {
    private static Mapper SAFE_MAPPER = constant();
    private final String name;

    final MemoryStore<K, V> memoryCache;

    final InternalCacheListener<K, V> listener;

    final AbstractCacheServiceManager serviceManager;

    final ParallelCache<K, V> parallelCache;

    Set<Map.Entry<K, V>> entrySet;

    Set<K> keySet;

    Collection<V> values;
    
    public AbstractInternalCache(Cache cache, CacheConfiguration conf, Collection<Class<?>> classes) {
        name = getName(conf);
        ServiceComposer composer = ServiceComposer.compose(cache, this, name, conf, classes);
        serviceManager = composer.getInternalService(AbstractCacheServiceManager.class);
        memoryCache = composer.getInternalService(MemoryStore.class);
        listener = composer.getInternalService(InternalCacheListener.class);
        parallelCache = composer.getInternalService(ParallelCache.class);
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
        serviceManager.lazyStart();
        return memoryCache.size() > 0;
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

    protected void checkStarted() {
        
    }
    protected InternalCache<K,V> cache() {
        return this;
    }
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

    abstract class AbstractCollectionView<E> implements Collection<E> {
        MemoryStoreWithMapping<E> mapping;
     
        AbstractCollectionView(MemoryStoreWithMapping<E> mapper) {
            mapping = mapper;
        }
    
        public final boolean add(E e) {
            throw new UnsupportedOperationException();
        }
    
        public final boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }
    
        public final void clear() {
            AbstractInternalCache.this.clear();
        }
    
        public final boolean isEmpty() {
            return AbstractInternalCache.this.isEmpty();
        }
    
        public final Iterator<E> iterator() {
            checkStarted();
            return mapping.sequentially();
        }
    
        public Iterator<E> unsafeIterator() {
            return iterator();
        }
    
        public final int size() {
            return AbstractInternalCache.this.size();
        }
    
        public Object[] toArray() {
            checkStarted();
            return mapping.all().getArray();
        }
    
        public <T> T[] toArray(T[] a) {
            checkStarted();
            Object[] result = mapping.all().getArray();
            int size = result.length;
            T[] r = a.length >= size ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass()
                    .getComponentType(), size);
            System.arraycopy(result, 0, r, 0, size);
            if (a.length > size) {
                a[size] = null;
            }
            return r;
        }
    
        @Override
        public String toString() {
            checkStarted();
            Iterator<E> i = unsafeIterator();
            if (!i.hasNext())
                return "[]";
    
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (;;) {
                E e = i.next();
                sb.append(e == this ? "(this Collection)" : e);
                if (!i.hasNext())
                    return sb.append(']').toString();
                sb.append(", ");
            }
        }
    }

    abstract class AbstractSetView<E> extends AbstractCollectionView<E> implements Set<E> {
        public AbstractSetView(MemoryStoreWithMapping<E> mapper) {
            super(mapper);
        }
    
        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof Set))
                return false;
            Collection c = (Collection) o;
            if (c.size() != size())
                return false;
            try {
                return containsAll(c);
            } catch (ClassCastException unused) {
                return false;
            } catch (NullPointerException unused) {
                return false;
            }
        }
    
        @Override
        public int hashCode() {
            checkStarted();
            int h = 0;
            Iterator<E> i = unsafeIterator();
            while (i.hasNext()) {
                E obj = i.next();
    
                if (obj != null) {
                    h += obj.hashCode();
                }
            }
            return h;
        }
    }

    class EntrySet extends AbstractSetView<Map.Entry<K, V>> {
    
        public EntrySet() {
            super(memoryCache.withMapping(SAFE_MAPPER));
        }
    
        @Override
        public Iterator<Map.Entry<K, V>> unsafeIterator() {
            return memoryCache.withMapping(CONSTANT_MAPPER).sequentially();
        }
    
        public final boolean contains(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            V v = peek((K) e.getKey());
            return v != null && v.equals(e.getValue());
        }
    
        public boolean containsAll(Collection<?> c) {
            checkStarted();
            for (Map.Entry entry : (Collection<Map.Entry>) c) {
                Map.Entry<K, V> e = memoryCache.get(entry.getKey());
                if (e == null || !e.getValue().equals(entry.getValue())) {
                    return false;
                }
            }
            return true;
        }
    
        public final boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return AbstractInternalCache.this.remove(e.getKey(), e.getValue());
        }
    
        public final boolean removeAll(Collection<?> c) {
            return removeEntries(c);
        }
    
        public final boolean retainAll(Collection<?> c) {
            return AbstractInternalCache.this.retainAll(constant(), Predicates.TRUE, c);
        }
    }


    class KeySet extends AbstractSetView<K> {
    
        public KeySet() {
            super(memoryCache.withMapping(MAP_ENTRY_TO_KEY_MAPPER));
        }
    
        public final boolean contains(Object o) {
            return containsKey(o);
        }
    
        public boolean containsAll(Collection<?> c) {
            checkStarted();
            for (Object entry : c) {
                if (memoryCache.get(entry) == null) {
                    return false;
                }
            }
            return true;
        }
    
        public final boolean remove(Object o) {
            return AbstractInternalCache.this.remove(o) != null;
        }
    
        public final boolean removeAll(Collection<?> c) {
            return removeKeys(c);
        }
    
        public final boolean retainAll(Collection<?> c) {
            return AbstractInternalCache.this.retainAll(mapEntryToKey(), Predicates.TRUE, c);
        }
    
    }



    class Values extends AbstractCollectionView<V> {
    
        public Values() {
            super(memoryCache.withMapping(MAP_ENTRY_TO_VALUE_MAPPER));
        }
    
        public final boolean contains(Object o) {
            return containsValue(o);
        }
    
        public boolean containsAll(Collection<?> c) {
            checkStarted();
            Iterator<?> e = c.iterator();
            while (e.hasNext()) {
                if (memoryCache.withFilterOnValues((Predicate) Predicates.isEquals(e.next())).any() == null) {
                    return false;
                }
            }
            return true;
        }
    
        public final boolean remove(Object o) {
            return removeValue(o);
        }
    
        public final boolean removeAll(Collection<?> c) {
            return removeValues(c);
        }
    
        public final boolean retainAll(Collection<?> c) {
            return cache().retainAll(mapEntryToValue(), Predicates.TRUE, c);
        }
    }
}

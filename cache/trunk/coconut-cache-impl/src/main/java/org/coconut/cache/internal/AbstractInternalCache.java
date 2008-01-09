package org.coconut.cache.internal;

import static org.coconut.operations.Mappers.CONSTANT_MAPPER;
import static org.coconut.operations.Mappers.MAP_ENTRY_TO_KEY_MAPPER;
import static org.coconut.operations.Mappers.MAP_ENTRY_TO_VALUE_MAPPER;
import static org.coconut.operations.Mappers.constant;
import static org.coconut.operations.Mappers.mapEntryToKey;
import static org.coconut.operations.Mappers.mapEntryToValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.coconut.cache.CacheServices;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.exceptionhandling.DefaultCacheExceptionService;
import org.coconut.cache.internal.service.listener.DefaultCacheListener;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.memorystore.DefaultEvictableMemoryStore;
import org.coconut.cache.internal.service.memorystore.MemoryStore;
import org.coconut.cache.internal.service.memorystore.MemoryStoreWithMapping;
import org.coconut.cache.internal.service.servicemanager.AbstractCacheServiceManager;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.internal.util.CollectionUtils;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;

public abstract class AbstractInternalCache<K, V> implements InternalCache<K, V> {
    private static Mapper SAFE_MAPPER = constant();

    private final String name;

    private final AbstractCacheServiceManager serviceManager;

    private CacheServices<K, V> services;

    Set<Map.Entry<K, V>> entrySet;

    Set<K> keySet;

    final InternalCacheListener<K, V> listener;

    final MemoryStore<K, V> memoryCache;

    Collection<V> values;

    AbstractInternalCache(Cache cache, CacheConfiguration conf, Collection<Class<?>> classes) {
        this(cache, conf, classes, Collections.EMPTY_LIST);
    }

    AbstractInternalCache(Cache cache, CacheConfiguration conf, Collection<Class<?>> classes,
            Collection instantiatedComponents) {
        name = getName(conf);
        ServiceComposer composer = ServiceComposer.compose(cache, this, name, conf, classes,
                instantiatedComponents);
        serviceManager = composer.getInternalService(AbstractCacheServiceManager.class);
        memoryCache = composer.getInternalService(MemoryStore.class);
        listener = composer.getInternalService(InternalCacheListener.class);
    }

    public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return serviceManager.awaitTermination(timeout, unit);
    }

    public boolean containsKey(Object key) {
        lazyStart();
        return memoryCache.get(key) != null;
    }

    public boolean containsValue(Object value) {
        lazyStart();
        return memoryCache.withFilterOnValues(Predicates.isEquals(value)).any() != null;
    }

    public final String getName() {
        return name;
    }

    public final <T> T getService(Class<T> serviceType) {
        return serviceManager.getServiceFromCache(serviceType);
    }

    public final boolean isEmpty() {
        lazyStart();
        return memoryCache.size() == 0;
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

    public boolean lazyStart() {
        return serviceManager.lazyStart(false);
    }

    public void lazyStartFailIfShutdown() {
        serviceManager.lazyStartFailIfShutdown();
    }

    public V peek(K key) {
        lazyStart();
        CacheEntry<K, V> prev = memoryCache.get(key);
        return prev == null ? null : prev.getValue();
    }

    public CacheEntry<K, V> peekEntry(K key) {
        lazyStart();
        return memoryCache.get(key);
    }

    public void prestart() {
        lazyStart();
    }

    public final V put(K key, V value) {
        CacheEntry<K, V> prev = put(key, value, Attributes.EMPTY_ATTRIBUTE_MAP);
        return prev == null ? null : prev.getValue();
    }

    public final CacheEntry<K, V> put(K key, V value, AttributeMap attributes) {
        return put(key, value, attributes, false);
    }

    public final void putAll(Map<? extends K, ? extends V> t) {
        HashMap map = new HashMap();
        for (Map.Entry me : t.entrySet()) {
            map.put(me.getKey(), new CollectionUtils.SimpleImmutableEntry(me.getValue(),
                    Attributes.EMPTY_ATTRIBUTE_MAP));
        }
        putAllWithAttributes(map);
    }

    public final V putIfAbsent(K key, V value) {
        CacheEntry<K, V> prev = put(key, value, Attributes.EMPTY_ATTRIBUTE_MAP, true);
        return prev == null ? null : prev.getValue();
    }

    public final V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> removed = doRemove(key, null);
        return removed == null ? null : removed.getValue();
    }

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

    public CacheServices<K, V> services() {
        if (services == null) {
            services = new CacheServices<K, V>(this);
        }
        return services;
    }

    public final void shutdown() {
        serviceManager.shutdown();
    }

    public final void shutdownNow() {
        serviceManager.shutdownNow();
    }

    public int size() {
        lazyStart();
        return memoryCache.size();
    }

    public long volume() {
        lazyStart();
        return memoryCache.volume();
    }

    abstract CacheEntry<K, V> doRemove(Object key, Object value);

    abstract CacheEntry<K, V> put(K key, V value, AttributeMap attributes, boolean OnlyIfAbsent);

    abstract boolean removeKeys(Collection<?> keys);

    abstract boolean removeValue(Object value);

    abstract boolean removeValues(Collection<?> values);

    abstract boolean retainAll(Mapper pre, Collection<?> c);

    static Collection<Class<?>> defaultComponents(CacheConfiguration<?, ?> configuration) {
        Collection<Class<?>> c = new ArrayList<Class<?>>();
        c.add(DefaultCacheExceptionService.class);
        c.add(DefaultCacheStatisticsService.class);
        c.add(DefaultCacheListener.class);
        if (configuration.event().isEnabled()) {
            c.add(DefaultCacheEventService.class);
        }
        c.add(DefaultEvictableMemoryStore.class);
        return c;
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

        AbstractCollectionView() {
            mapping = memoryCache.withMapping(mapper());
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
            lazyStart();
            return mapping.sequentially();
        }

        public final int size() {
            return AbstractInternalCache.this.size();
        }

        public Object[] toArray() {
            lazyStart();
            return mapping.all().getArray();
        }

        public <T> T[] toArray(T[] a) {
            lazyStart();
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
            lazyStart();
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

        abstract Mapper mapper();

        final Iterator<E> unsafeIterator() {
            return memoryCache.withMapping(unsafeMapper()).sequentially();
        }

        Mapper unsafeMapper() {
            return mapper();
        }
    }

    abstract class AbstractSetView<E> extends AbstractCollectionView<E> implements Set<E> {

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
            lazyStart();
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
            lazyStart();
            for (Object o : c) {
                if (o == null) {
                    throw new NullPointerException();
                }
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<K, V> e = (Map.Entry<K, V>) o;
                Map.Entry<K, V> candidate = memoryCache.get(e.getKey());
                if (candidate == null || !e.getValue().equals(candidate.getValue())) {
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
            return AbstractInternalCache.this.retainAll(SAFE_MAPPER, c);
        }

        @Override
        Mapper mapper() {
            return SAFE_MAPPER;
        }

        @Override
        Mapper unsafeMapper() {
            return CONSTANT_MAPPER;
        }
    }

    class KeySet extends AbstractSetView<K> {

        public final boolean contains(Object o) {
            return containsKey(o);
        }

        public boolean containsAll(Collection<?> c) {
            lazyStart();
            for (Object key : c) {
                if (memoryCache.get(key) == null) {
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
            return AbstractInternalCache.this.retainAll(mapEntryToKey(), c);
        }

        @Override
        Mapper mapper() {
            return MAP_ENTRY_TO_KEY_MAPPER;
        }

    }

    class Values extends AbstractCollectionView<V> {

        public final boolean contains(Object o) {
            return containsValue(o);
        }

        public boolean containsAll(Collection<?> c) {
            lazyStart();
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
            return AbstractInternalCache.this.retainAll(mapEntryToValue(), c);
        }

        @Override
        Mapper mapper() {
            return MAP_ENTRY_TO_VALUE_MAPPER;
        }
    }
}

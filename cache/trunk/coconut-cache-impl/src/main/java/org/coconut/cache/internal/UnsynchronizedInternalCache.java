package org.coconut.cache.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.AbstractCache;
import org.coconut.cache.internal.service.entry.UnsynchronizedEntryFactoryService;
import org.coconut.cache.internal.service.eviction.UnsynchronizedCacheEvictionService;
import org.coconut.cache.internal.service.expiration.UnsynchronizedCacheExpirationService;
import org.coconut.cache.internal.service.loading.UnsynchronizedCacheLoaderService;
import org.coconut.cache.internal.service.parallel.UnsynchronizedParallelCacheService;
import org.coconut.cache.internal.service.servicemanager.UnsynchronizedCacheServiceManager;
import org.coconut.internal.forkjoin.ParallelArray;
import org.coconut.internal.util.CollectionUtils;
import org.coconut.operations.CollectionPredicates;
import org.coconut.operations.Mappers;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

public class UnsynchronizedInternalCache<K, V> extends AbstractInternalCache<K, V> {

    private UnsynchronizedInternalCache(Cache cache, CacheConfiguration conf,
            Collection<Class<?>> components) {
        super(cache, conf, components);
    }

    static class UnsynchronizedInternalCacheFactory<K, V> implements InternalCacheFactory<K, V> {
        public Cache<K, V> create(Cache<K, V> cache, CacheConfiguration<K, V> configuration) {
            Collection<Class<?>> components = defaultComponents(configuration);
            components.add(UnsynchronizedCacheEvictionService.class);
            components.add(UnsynchronizedCacheExpirationService.class);
            if (configuration.loading().getLoader() != null) {
                components.add(UnsynchronizedCacheLoaderService.class);
            }
            components.add(UnsynchronizedParallelCacheService.class);
            components.add(UnsynchronizedCacheServiceManager.class);
            components.add(UnsynchronizedEntryFactoryService.class);
            return new UnsynchronizedInternalCache(cache, configuration, components);
        }
    }

    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new Values());
    }

    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null) ? ks : (keySet = new KeySet());
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    public void clear() {
        long started = listener.beforeCacheClear();

        lazyStart();
        long volume = memoryCache.volume();
        ParallelArray<CacheEntry<K, V>> list = memoryCache.removeAll();

        listener.afterCacheClear(started, list.asList(), volume);
    }

    /** {@inheritDoc} */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        long started = listener.beforeRemove(key, value);

        lazyStart();
        CacheEntry<K, V> e = memoryCache.remove(key, value);

        listener.afterRemove(started, e);
        return e;
    }

    public boolean removeValue(Object value) {
        long started = listener.beforeRemove(null, value);

        lazyStart();
        CacheEntry<K, V> e = memoryCache.removeAny(Predicates.mapAndEvaluate(
                Mappers.MAP_ENTRY_TO_VALUE_MAPPER, Predicates.isEquals(value)));

        listener.afterRemove(started, e);
        return e != null;
    }

    public boolean removeKeys(Collection<?> keys) {
        if (keys == null) {
            throw new NullPointerException("collection is null");
        }
        CollectionUtils.checkCollectionForNulls(keys);
        long started = listener.beforeRemoveAll((Collection) keys);

        lazyStart();
        ParallelArray<CacheEntry<K, V>> list = memoryCache.removeAll(keys);

        listener.afterRemoveAll(started, (Collection) keys, list.asList());

        return list.size() > 0;
    }

    public boolean removeEntries(Collection<?> entries) {
        if (entries == null) {
            throw new NullPointerException("collection is null");
        }
        CollectionUtils.checkCollectionForNulls(entries);
        long started = listener.beforeRemoveAll((Collection) entries);

        lazyStart();
        ParallelArray<CacheEntry<K, V>> list = memoryCache.removeEntries(entries);

        listener.afterRemoveAll(started, (Collection) entries, list.asList());

        return list.size() > 0;
    }

    public boolean removeValues(Collection<?> values) {
        if (values == null) {
            throw new NullPointerException("collection is null");
        }
        CollectionUtils.checkCollectionForNulls(values);
        long started = listener.beforeRemoveAll((Collection) values);

        lazyStart();
        ParallelArray<CacheEntry<K, V>> list = memoryCache.removeValues(values);

        listener.afterRemoveAll(started, (Collection) values, list.asList());

        return list.size() > 0;
    }

    boolean retainAll(Mapper pre, Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("collection is null");
        }
        CollectionUtils.checkCollectionForNulls(c);
        long started = listener.beforeRemoveAll((Collection) c);

        lazyStart();
        ParallelArray<CacheEntry<K, V>> list = memoryCache.withFilter(
                Predicates.mapAndEvaluate(pre, Predicates.not(CollectionPredicates
                        .containedWithin(c)))).removeAll();

        listener.afterRemoveAll(started, (Collection) c, list.asList());

        return list.size() > 0;
    }

    @Override
    public CacheEntry<K, V> put(K key, V value, AttributeMap attributes, boolean OnlyIfAbsent) {
        long started = listener.beforePut(key, value, false);

        lazyStartFailIfShutdown();
        Map.Entry<CacheEntry<K, V>, CacheEntry<K, V>> prev = memoryCache.put(key, value,
                attributes, OnlyIfAbsent);
        ParallelArray<CacheEntry<K, V>> trimmed = memoryCache.trim();

        listener.afterPut(started, trimmed.asList(), (InternalCacheEntry) prev.getKey(),
                (InternalCacheEntry) prev.getValue(), false);
        return prev.getKey();
    }

    public void clearView(Predicate p) {
        throw new UnsupportedOperationException();
    }

    public void putAllWithAttributes(Map<K, Map.Entry<V, AttributeMap>> data) {
        long started = listener.beforePutAll(null, null, false);

        lazyStartFailIfShutdown();

        Map<CacheEntry<K, V>, CacheEntry<K, V>> result = memoryCache.putAllWithAttributes(data);
        ParallelArray<CacheEntry<K, V>> trimmed = memoryCache.trim();

        listener.afterPutAll(started, trimmed.asList(), (Map) result, false);
    }

    public V get(Object key) {
        throw new UnsupportedOperationException();
    }

    public Map<K, V> getAll(Collection<? extends K> keys) {
        throw new UnsupportedOperationException();
    }

    public CacheEntry<K, V> getEntry(K key) {
        throw new UnsupportedOperationException();
    }

    public V replace(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

    public <T> void apply(Predicate<? super CacheEntry<K, V>> selector,
            Mapper<? super CacheEntry<K, V>, T> mapper, Procedure<T> procedure) {
        memoryCache.withFilter(selector).withMapping(mapper).apply(procedure);
    }

}

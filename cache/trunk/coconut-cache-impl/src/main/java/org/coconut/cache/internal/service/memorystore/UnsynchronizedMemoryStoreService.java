/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.memorystore;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.ParallelCache;
import org.coconut.cache.ParallelCache.WithFilter;
import org.coconut.cache.ParallelCache.WithMapping;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

/**
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: UnsynchronizedCacheEvictionService.java 559 2008-01-09 16:28:27Z kasper $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class UnsynchronizedMemoryStoreService<K, V, T extends CacheEntry<K, V>> extends
        AbstractMemoryService<K, V, T> {

    final InternalCacheListener listener;
    private final ParallelCache pc;
    // @SuppressWarnings("unchecked")
    public UnsynchronizedMemoryStoreService(InternalCache<K, V> cache,MemoryStore<K, V> ms, InternalCacheListener listener,
            AbstractCacheEntryFactoryService<K, V> factory) {
        super(cache, ms, factory);
        this.listener = listener;
        pc = new UnsynchronizedParallelCache();
    }

    

    @Override
    void trimCache(int toSize, long toVolume) {
        long started = listener.beforeTrim(toSize, toVolume);

        // manager.lazyStart(true);
        // int size = map.size();
        // long volume = map.volume();
        // List<CacheEntry<K, V>> l = map.trimCache(toSize, toVolume);

        // listener.afterTrimCache(started, l, size, map.size(), volume, map.volume());
    }
    public ParallelCache get() {
        return pc;
    }

    void checkStart() {}

    class UnsynchronizedParallelCache extends ParallelCache<K, V> {

        @Override
        public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
            checkStart();
            ms.apply(procedure);
        }

        @Override
        public int size() {
            checkStart();
            return ms.size();
        }

        @Override
        public long volume() {
            checkStart();
            return ms.volume();
        }

        @Override
        public ParallelCache.WithFilter<K, V> withFilter(
                Predicate<? super CacheEntry<K, V>> selector) {
            return new UnsynchronizedWithFilter(ms.withFilter(selector));
        }

        @Override
        public ParallelCache.WithMapping<K> withKeys() {
            return new UnsynchronizedWithMapping(ms.withKeys());
        }

        @Override
        public <U> ParallelCache.WithMapping<U> withMapping(
                Mapper<? super CacheEntry<K, V>, ? extends U> mapper) {
            return new UnsynchronizedWithMapping(ms.withMapping(mapper));
        }

        @Override
        public ParallelCache.WithMapping<V> withValues() {
            return new UnsynchronizedWithMapping(ms.withValues());
        }
    }

    class UnsynchronizedWithFilter extends ParallelCache.WithFilter<K, V> {
        private final MemoryStoreWithFilter<K, V> filter;

        UnsynchronizedWithFilter(MemoryStoreWithFilter<K, V> filter) {
            this.filter = filter;
        }

        @Override
        public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
            checkStart();
            filter.apply(procedure);
        }

        @Override
        public int size() {
            checkStart();
            return filter.size();
        }

        @Override
        public WithFilter<K, V> withFilter(Predicate<? super CacheEntry<K, V>> selector) {
            return new UnsynchronizedWithFilter(filter.withFilter(selector));
        }

    }

    class UnsynchronizedWithMapping<T> extends ParallelCache.WithMapping<T> {

        final MemoryStoreWithMapping<T> withMapping;

        UnsynchronizedWithMapping(MemoryStoreWithMapping<T> withMapping) {
            this.withMapping = withMapping;
        }

        @Override
        public void apply(Procedure<? super T> procedure) {
            checkStart();
            withMapping.apply(procedure);
        }

        @Override
        public int size() {
            checkStart();
            return withMapping.size();
        }

        @Override
        public <U> WithMapping<U> withMapping(Mapper<? super T, ? extends U> mapper) {
            return new UnsynchronizedWithMapping(withMapping.withMapping(mapper));
        }

    }
}

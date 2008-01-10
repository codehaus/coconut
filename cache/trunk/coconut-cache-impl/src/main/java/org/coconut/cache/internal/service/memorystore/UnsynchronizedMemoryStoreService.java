/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.memorystore;

import java.util.Iterator;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.ParallelCache;
import org.coconut.cache.ParallelCache.WithFilter;
import org.coconut.cache.ParallelCache.WithMapping;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.operations.Mappers;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;
import static org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService.SAFE_MAPPER;

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

    private final ParallelCache pc;

    final InternalCacheListener listener;

    private final MemoryStoreWithMapping safeMs;

    // @SuppressWarnings("unchecked")
    public UnsynchronizedMemoryStoreService(InternalCache<K, V> cache, MemoryStore<K, V> ms,
            InternalCacheListener listener, AbstractCacheEntryFactoryService<K, V> factory) {
        super(cache, ms, factory);
        this.listener = listener;
        pc = new UnsynchronizedParallelCache();
        safeMs = ms.withMapping(SAFE_MAPPER);
    }

    public ParallelCache getParallelCache() {
        return pc;
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

    class UnsynchronizedParallelCache extends ParallelCache<K, V> {

        @Override
        public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
            checkStarted();
            safeMs.apply(procedure);
        }

        @Override
        public int size() {
            checkStarted();
            return ms.size();
        }

        @Override
        public long volume() {
            checkStarted();
            return ms.volume();
        }

        @Override
        public ParallelCache.WithFilter<K, V> withFilter(
                Predicate<? super CacheEntry<K, V>> selector) {
            return new UnsynchronizedWithFilter(ms.withFilter(Predicates.mapAndEvaluate(
                    SAFE_MAPPER, selector)), true);
        }

        @Override
        public ParallelCache.WithMapping<K> withKeys() {
            return new UnsynchronizedWithMapping(ms.withKeys());
        }

        @Override
        public <U> ParallelCache.WithMapping<U> withMapping(
                Mapper<? super CacheEntry<K, V>, ? extends U> mapper) {
            return new UnsynchronizedWithMapping(safeMs.withMapping(mapper));
        }

        @Override
        public ParallelCache.WithMapping<V> withValues() {
            return new UnsynchronizedWithMapping(ms.withValues());
        }

        public Iterator<CacheEntry<K, V>> iterator() {
            return safeMs.sequentially();
        }
    }

    class UnsynchronizedWithFilter extends ParallelCache.WithFilter<K, V> {
        private final MemoryStoreWithFilter<K, V> filter;

        private final boolean isSafe;

        UnsynchronizedWithFilter(MemoryStoreWithFilter<K, V> filter, boolean isSafe) {
            this.filter = filter;
            this.isSafe = isSafe;
        }

        @Override
        public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
            checkStarted();
            filter.apply(procedure);
        }

        @Override
        public int size() {
            checkStarted();
            return filter.size();
        }

        @Override
        public long volume() {
            checkStarted();
            return filter.size();
        }

        @Override
        public WithFilter<K, V> withFilter(Predicate<? super CacheEntry<K, V>> selector) {
            if (isSafe) {
                return new UnsynchronizedWithFilter(filter.withFilter(selector), true);
            } else {
                return new UnsynchronizedWithFilter(filter.withFilter(Predicates.mapAndEvaluate(
                        SAFE_MAPPER, selector)), true);
            }
        }

        @Override
        public ParallelCache.WithMapping<K> withKeys() {
            return new UnsynchronizedWithMapping(filter.withKeys());
        }

        @Override
        public <U> WithMapping<U> withMapping(Mapper<? super CacheEntry<K, V>, ? extends U> mapper) {
            if (isSafe) {
                return new UnsynchronizedWithMapping<U>(filter.withMapping(mapper));
            } else {
                return new UnsynchronizedWithMapping<U>(filter.withMapping(Mappers.compoundMapper(
                        SAFE_MAPPER, mapper)));
            }
        }

        @Override
        public ParallelCache.WithMapping<V> withValues() {
            return new UnsynchronizedWithMapping(filter.withValues());
        }

        @Override
        public Iterator<CacheEntry<K, V>> sequentially() {
            checkStarted();
            return filter.sequentially();
        }
    }

    class UnsynchronizedWithMapping<T> extends ParallelCache.WithMapping<T> {

        final MemoryStoreWithMapping<T> withMapping;

        UnsynchronizedWithMapping(MemoryStoreWithMapping<T> withMapping) {
            this.withMapping = withMapping;
        }

        @Override
        public void apply(Procedure<? super T> procedure) {
            checkStarted();
            withMapping.apply(procedure);
        }

        @Override
        public int size() {
            checkStarted();
            return withMapping.size();
        }

        @Override
        public long volume() {
            checkStarted();
            return withMapping.size();
        }

        @Override
        public <U> WithMapping<U> withMapping(Mapper<? super T, ? extends U> mapper) {
            return new UnsynchronizedWithMapping(withMapping.withMapping(mapper));
        }

        @Override
        public Iterator<T> sequentially() {
            return withMapping.sequentially();
        }

    }
}

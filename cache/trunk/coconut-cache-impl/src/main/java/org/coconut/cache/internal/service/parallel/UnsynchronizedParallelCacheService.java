package org.coconut.cache.internal.service.parallel;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.internal.memory.MemoryStoreWithFilter;
import org.coconut.cache.internal.memory.MemoryStoreWithMapping;
import org.coconut.cache.service.parallel.ParallelCache;
import org.coconut.cache.service.parallel.ParallelCache.WithFilter;
import org.coconut.cache.service.parallel.ParallelCache.WithMapping;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

public class UnsynchronizedParallelCacheService<K, V> extends AbstractParallelCacheService {

    private final ParallelCache pc;

    public UnsynchronizedParallelCacheService(InternalCache<K, V> cache,
            MemoryStore<K, V> memoryStore) {
        super(cache, memoryStore);
        pc = new UnsynchronizedParallelCache();
    }

    public ParallelCache get() {
        return pc;
    }

    void checkStart() {}

    class UnsynchronizedParallelCache extends ParallelCache<K, V> {

        @Override
        public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
            checkStart();
            memoryStore.apply(procedure);
        }

        @Override
        public int size() {
            checkStart();
            return memoryStore.size();
        }

        @Override
        public long volume() {
            checkStart();
            return memoryStore.volume();
        }

        @Override
        public ParallelCache.WithFilter<K, V> withFilter(
                Predicate<? super CacheEntry<K, V>> selector) {
            return new UnsynchronizedWithFilter(memoryStore.withFilter(selector));
        }

        @Override
        public ParallelCache.WithMapping<K> withKeys() {
            return new UnsynchronizedWithMapping(memoryStore.withKeys());
        }

        @Override
        public <U> ParallelCache.WithMapping<U> withMapping(
                Mapper<? super CacheEntry<K, V>, ? extends U> mapper) {
            return new UnsynchronizedWithMapping(memoryStore.withMapping(mapper));
        }

        @Override
        public ParallelCache.WithMapping<V> withValues() {
            return new UnsynchronizedWithMapping(memoryStore.withValues());
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

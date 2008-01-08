package org.coconut.cache.internal.service.parallel;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.service.parallel.ParallelCache;
import org.coconut.cache.service.parallel.ParallelCache.WithFilter;
import org.coconut.operations.Mappers;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

public class UnsynchronizedParallelCacheService<K, V> extends AbstractParallelCacheService {
   private final MemoryStore<K, V> memoryStore;

    private final ParallelCache pc;

    private final InternalCache<K, V> ic;

    public UnsynchronizedParallelCacheService(InternalCache<K, V> ic, MemoryStore<K, V> memoryStore) {
        this.ic = ic;
        this.memoryStore = memoryStore;
        pc = new UnsynchronizedParallelCache();
    }

    public ParallelCache get() {
        return pc;
    }

    static <K, V, T> void applyIterable(Iterable<? extends CacheEntry<K, V>> iterable,
            Procedure<? super T> procedure, Predicate<? super CacheEntry<K, V>> selector,
            Mapper<? super CacheEntry<K, V>, ? extends T> mapper) {
        if (procedure == null) {
            throw new NullPointerException("procedure is null");
        }
        for (CacheEntry<K, V> entry : iterable) {
            if (selector.evaluate(entry)) {
                T t = mapper.map(entry);
                procedure.apply(t);
            }
        }
    }

    static <T> int calculateSize(Iterable<T> iterable, Predicate<? super T> selector) {
        int count = 0;
        for (T t : iterable) {
            if (selector.evaluate(t)) {
                count++;
            }
        }
        return count;
    }

    class UnsynchronizedParallelCache extends ParallelCache<K, V> {

        @Override
        public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
            ic.apply(Predicates.TRUE, Mappers.CONSTANT_MAPPER, procedure);
        }

        @Override
        public int size() {
            return ic.size();
        }

        @Override
        public ParallelCache.WithFilter<K, V> withFilter(
                Predicate<? super CacheEntry<K, V>> selector) {
            return new UnsynchronizedWithFilter(selector);
        }

        @Override
        public ParallelCache.WithKeyValues<K> withKeys() {
            return new UnsynchronizedWithKeys();
        }

        @Override
        public <U> ParallelCache.WithMapping<U> withMapping(
                Mapper<? super CacheEntry<K, V>, ? extends U> mapper) {
            return new UnsynchronizedWithMapping(mapper);
        }

        @Override
        public ParallelCache.WithKeyValues<V> withValues() {
            return new UnsynchronizedWithValues();
        }
    }

    class UnsynchronizedWithFilter extends ParallelCache.WithFilter<K, V> {
        private final Predicate selector;

        UnsynchronizedWithFilter(Predicate selector) {
            if (selector == null) {
                throw new NullPointerException("selector is null");
            }
            this.selector = selector;
        }

        @Override
        public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
            ic.apply(selector, Mappers.CONSTANT_MAPPER, procedure);
        }

        @Override
        public int size() {
            return memoryStore.withFilter(selector).size();
        }

        @Override
        public WithFilter<K, V> withFilter(Predicate<? super WithFilter<K, V>> selector) {
            return new UnsynchronizedWithFilter(Predicates.and(this.selector, selector));
        }
    }

    class UnsynchronizedWithKeys extends ParallelCache.WithKeyValues<K> {

        @Override
        public void apply(Procedure<? super K> procedure) {
            ic.apply(
                    Predicates.mapAndEvaluate(Mappers.mapEntryToKey(), Predicates.TRUE),
                    Mappers.CONSTANT_MAPPER, procedure);
        }

        @Override
        public int size() {
            return ic.size();
        }
    }

    class UnsynchronizedWithMapping<T> extends ParallelCache.WithMapping<T> {

        private final Mapper<? super CacheEntry<K, V>, ? extends T> mapper;

        UnsynchronizedWithMapping(Mapper<? super CacheEntry<K, V>, ? extends T> mapper) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            }
            this.mapper = mapper;

        }

        @Override
        public void apply(Procedure<? super T> procedure) {
            ic.apply(Predicates.TRUE, mapper, procedure);
        }

        @Override
        public int size() {
            return ic.size();
        }

    }

    class UnsynchronizedWithValues extends ParallelCache.WithKeyValues<V> {

        @Override
        public void apply(Procedure<? super V> procedure) {
            ic.apply(Predicates.mapAndEvaluate(Mappers.mapEntryToValue(),
                    Predicates.TRUE), Mappers.CONSTANT_MAPPER, procedure);
        }

        @Override
        public int size() {
            return ic.size();
        }
    }

}

package org.coconut.cache.internal.service.parallel;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.service.parallel.ParallelCache;
import org.coconut.cache.service.parallel.ParallelCache.WithFilter;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

public class UnsynchronizedParallelCacheService<K, V> extends AbstractParallelCacheService {
    private final EntryMap<K, V> map;

    private ParallelCache pc;

    public UnsynchronizedParallelCacheService(EntryMap map) {
        this.map = map;
    }

    public ParallelCache get() {
        return pc;
    }

    @Override
    public void started(Cache<?, ?> cache) {
        pc = new UnsynchronizedParallelCache((Map) cache);
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

    static <T> void applyIterable(Iterable<T> iterable, Procedure<? super T> procedure) {
        if (procedure == null) {
            throw new NullPointerException("procedure is null");
        }
        for (T t : iterable) {
            procedure.apply(t);
        }
    }

    static <T> void applyIterable(Iterable<T> iterable, Procedure<? super T> procedure,
            Predicate<? super T> selector) {
        if (procedure == null) {
            throw new NullPointerException("procedure is null");
        }
        for (T t : iterable) {
            if (selector.evaluate(t)) {
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
        private final Map<K, V> cache;

        UnsynchronizedParallelCache(Map<K, V> cache) {
            this.cache = cache;
        }

        @Override
        public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
            applyIterable(map, procedure);
        }

        @Override
        public int size() {
            return cache.size();
        }

        @Override
        public ParallelCache.WithFilter<K, V> withFilter(
                Predicate<? super CacheEntry<K, V>> selector) {
            return new UnsynchronizedWithFilter(selector);
        }

        @Override
        public ParallelCache.WithKeyValues<K> withKeys() {
            return new UnsynchronizedWithKeys(cache);
        }

        @Override
        public <U> ParallelCache.WithMapping<U> withMapping(
                Mapper<? super CacheEntry<K, V>, ? extends U> mapper) {
            return new UnsynchronizedWithMapping(cache, mapper);
        }

        @Override
        public ParallelCache.WithKeyValues<V> withValues() {
            return new UnsynchronizedWithValues(cache);
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
            applyIterable(map, procedure, selector);
        }

        @Override
        public int size() {
            return calculateSize(map, selector);
        }

        @Override
        public WithFilter<K, V> withFilter(Predicate<? super WithFilter<K, V>> selector) {
            return new UnsynchronizedWithFilter(Predicates.and(this.selector, selector));
        }
    }

    class UnsynchronizedWithKeys extends ParallelCache.WithKeyValues<K> {
        private final Map<K, V> cache;

        UnsynchronizedWithKeys(Map<K, V> cache) {
            this.cache = cache;
        }

        @Override
        public void apply(Procedure<? super K> procedure) {
            applyIterable(cache.keySet(), procedure);
        }

        @Override
        public int size() {
            return cache.size();
        }
    }

    class UnsynchronizedWithMapping<T> extends ParallelCache.WithMapping<T> {
        private final Map<?, ?> cache;

        private final Mapper<? super CacheEntry<K, V>, ? extends T> mapper;

        UnsynchronizedWithMapping(Map<?, ?> cache,
                Mapper<? super CacheEntry<K, V>, ? extends T> mapper) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            }
            this.mapper = mapper;
            this.cache = cache;
        }

        @Override
        public void apply(Procedure<? super T> procedure) {
            applyIterable(map, procedure, Predicates.truePredicate(), mapper);
        }

        @Override
        public int size() {
            return cache.size();
        }

    }

    class UnsynchronizedWithValues extends ParallelCache.WithKeyValues<V> {
        private final Map<K, V> cache;

        UnsynchronizedWithValues(Map<K, V> cache) {
            this.cache = cache;
        }

        @Override
        public void apply(Procedure<? super V> procedure) {
            applyIterable(cache.values(), procedure);
        }

        @Override
        public int size() {
            return cache.size();
        }
    }

}

package org.coconut.cache.internal.service.parallel;

import static org.coconut.operations.Mappers.constant;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheParallelService;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.memory.MemoryStore;
import org.coconut.cache.internal.memory.MemoryStoreWithFilter;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.operations.CollectionPredicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;

public abstract class AbstractParallelCacheService<K, V> extends AbstractCacheLifecycle implements
        CacheParallelService<K, V> {

    private static Mapper SAFE_MAPPER = constant();

    final MemoryStore<K, V> memoryStore;

    final InternalCache<K, V> internalCache;

    final Cache<K, V> cache;

    public AbstractParallelCacheService(Cache<K, V> cache, InternalCache<K, V> internalCache,
            MemoryStore<K, V> memoryStore) {
        this.memoryStore = memoryStore;
        this.internalCache = internalCache;
        this.cache = cache;
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheParallelService.class, ParallelUtils.wrapService(this));
    }

    @Override
    public String toString() {
        return "Parallel Service";
    }

    void checkStarted() {

    }

    abstract class AbstractFilteredCollectionView<E> implements Collection<E> {
        MemoryStoreWithFilter<K, V> withFilter;

        AbstractFilteredCollectionView(MemoryStoreWithFilter<K, V> withFilter) {
            this.withFilter = withFilter;
        }

        public final boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        public final void clear() {
        // TODO fix
        }

        public boolean isEmpty() {
            checkStarted();
            return withFilter.any() != null;
        }

        public Iterator<E> iterator() {
            checkStarted();
            return null;
        }

        public Iterator<E> unsafeIterator() {
            return null;
        }

        public int size() {
            checkStarted();
            return withFilter.size();
        }

        public Object[] toArray() {
            return withFilter.all().getArray();
        }

        public <T> T[] toArray(T[] a) {
            checkStarted();
            Object[] result = withFilter.all().getArray();
            int size = result.length;
            T[] r = a.length >= size ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass()
                    .getComponentType(), size);
            System.arraycopy(result, 0, r, 0, size);
            if (a.length > size) {
                a[size] = null;
            }
            return r;
        }
    }

    abstract class AbstractFilteredSetView<E> extends AbstractFilteredCollectionView<E> implements
            Set<E> {
        public AbstractFilteredSetView(MemoryStoreWithFilter<K, V> mapper) {
            super(mapper);
        }
    }

    class EntrySetFiltered extends AbstractFilteredSetView<Map.Entry<K, V>> {

        Predicate selector = null;

        public EntrySetFiltered(MemoryStoreWithFilter<K, V> filterImpl) {
            super(filterImpl);
        }

        public final boolean contains(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry) || !selector.evaluate(o)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            V v = cache.peek((K) e.getKey());
            return v != null && v.equals(e.getValue());
        }

        public boolean containsAll(Collection<?> c) {
            return false;
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry) || !selector.evaluate(o)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return cache.remove(e.getKey(), e.getValue());
        }

        public boolean removeAll(Collection<?> c) {
            List l = CollectionPredicates.filter(c, selector);
            return l.size() > 0 ? internalCache.removeEntries(l) : false;
        }

        public final boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }
    }

}

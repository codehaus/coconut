/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.memorystore;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.service.memorystore.MemoryStoreMXBean;
import org.coconut.cache.service.memorystore.MemoryStoreService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.operations.CollectionPredicates;
import org.coconut.operations.Ops.Predicate;

/**
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AbstractEvictionService.java 559 2008-01-09 16:28:27Z kasper $
 * @param <K>
 *            the type of keys maintained by this service
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractMemoryService<K, V, T extends CacheEntry<K, V>> extends
        AbstractCacheLifecycle implements MemoryStoreService<K, V>, MemoryStoreMXBean {

    private final AbstractCacheEntryFactoryService<K, V> entryFactory;

    final MemoryStore<K, V> ms;

    private final InternalCache<K, V> cache;

    /**
     * Creates a new AbstractEvictionService.
     * 
     * @param evictionSupport
     *            the InternalCacheSupport for the cache
     */
    public AbstractMemoryService(InternalCache<K, V> cache, MemoryStore<K, V> ms,
            AbstractCacheEntryFactoryService<K, V> factory) {
        this.entryFactory = factory;
        this.ms = ms;
        this.cache = cache;
    }
    void checkStarted() {

    }
    public int getMaximumSize() {
        return ms.getMaximumSize();
    }

    public long getMaximumVolume() {
        return ms.getMaximumVolume();
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(MemoryStoreService.class, MemoryStoreUtils.wrapService(this));
    }

    public boolean isDisabled() {
        return entryFactory.isDisabled();
    }

    public void setDisabled(boolean isDisabled) {
        entryFactory.setDisabled(isDisabled);
    }

    public void setMaximumSize(int size) {
        ms.setMaximumSize(size);
    }

    public void setMaximumVolume(long volume) {
        ms.setMaximumVolume(volume);
    }

    @Override
    public String toString() {
        return "Eviction Service";
    }

    /** {@inheritDoc} */
    public void trimToSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be a negative number, was " + size);
        }
        trimCache(size, Long.MAX_VALUE);
    }

    /** {@inheritDoc} */
    public void trimToVolume(long volume) {
        if (volume < 0) {
            throw new IllegalArgumentException("volume cannot be a negative number, was " + volume);
        }
        trimCache(Integer.MAX_VALUE, volume);
    }

    abstract void trimCache(int size, long capacity);
    
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
            return l.size() > 0 ? cache.removeEntries(l) : false;
        }

        public final boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }
    }

}

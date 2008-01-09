package org.coconut.cache.internal.memory;

import static org.coconut.operations.Mappers.CONSTANT_MAPPER;
import static org.coconut.operations.Mappers.MAP_ENTRY_TO_KEY_MAPPER;
import static org.coconut.operations.Mappers.MAP_ENTRY_TO_VALUE_MAPPER;
import static org.coconut.operations.Mappers.compoundMapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.internal.forkjoin.ParallelArray;
import org.coconut.internal.util.CollectionUtils;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;
import org.coconut.operations.Ops.Reducer;

public abstract class AbstractSequentialMemoryStore<K, V> implements MemoryStore<K, V> {

    static final int MAXIMUM_CAPACITY = 1 << 30;

    final Cache<K, V> cache;

    /**
     * The load factor for the hash table.
     */
    final float loadFactor = 0.75f;

    int modCount;

    int size;

    ChainingEntry<K, V>[] table;

    int threshold;

    long volume;

    private final MemoryStoreWithMapping<CacheEntry<K, V>> constantUnsafe = new WithMappingImpl(
            CONSTANT_MAPPER);

    private final MemoryStoreWithMapping<CacheEntry<K, V>> constantSafe = new WithMappingImpl(
            CONSTANT_MAPPER);// replace with safe mapper

    AbstractSequentialMemoryStore(Cache<K, V> cache) {
        if (cache == null) {
            throw new NullPointerException("cache is null");
        }
        this.cache = cache;
    }

    public ParallelArray<CacheEntry<K, V>> all() {
        return constantUnsafe.all();
    }

    public ParallelArray<CacheEntry<K, V>> all(Class<? super CacheEntry<K, V>> elementType) {
        return constantUnsafe.all(elementType);
    }

    public CacheEntry<K, V> any() {
        if (size != 0) {
            ChainingEntry<K, V>[] table = this.table;
            int len = table.length;
            for (int i = 0; i < len; i++) {
                for (ChainingEntry<K, V> e = table[i]; e != null; e = e.next()) {
                    return e;
                }
            }
        }
        return null;
    }

    public void apply(Procedure<? super CacheEntry<K, V>> procedure) {
        constantUnsafe.apply(procedure);
    }

    public void clear() {
        modCount++;
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    public ChainingEntry<K, V> get(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        if (size != 0) {
            int hash = hash(key.hashCode());
            ChainingEntry<K, V> e = getFirst(hash);
            while (e != null) {
                if (e.getHash() == hash && key.equals(e.getKey())) {
                    return e;
                }
                e = e.next();
            }
        }
        return null;
    }

    public int getMaximumSize() {
        return Integer.MAX_VALUE;
    }

    public long getMaximumVolume() {
        return Long.MAX_VALUE;
    }

    public Map.Entry<CacheEntry<K, V>, CacheEntry<K, V>> put(K key, V value, AttributeMap map,
            boolean onlyIfAbsent) {
        int hash = hash(key.hashCode());
        ChainingEntry<K, V>[] tab = table;
        int index = hash & tab.length - 1;
        ChainingEntry<K, V> e = tab[index];
        ChainingEntry<K, V> prev = e;
        while (e != null) {
            ChainingEntry<K, V> next = e.next();
            if (e.getHash() == hash && key.equals(e.getKey())) {
                if (onlyIfAbsent) {
                    return new CollectionUtils.SimpleImmutableEntry(e, null);
                }
                ++modCount;
                ChainingEntry<K, V> entry = updated(e, key, value, map);
                volume += (entry.getSize() - e.getSize());
                if (prev == e) {
                    tab[index] = entry;
                } else {
                    prev.setNext(entry);
                }
                entry.setNext(e.next());
                return new CollectionUtils.SimpleImmutableEntry(e, entry);
            }
            prev = e;
            e = next;
        }
        ++modCount;
        ChainingEntry<K, V> entry = created(key, value, map);
        volume += entry.getSize();
        entry.setNext(tab[index]);
        tab[index] = entry;
        if (size++ >= threshold) {
            // ensure capacity
            rehash();
        }
        return new CollectionUtils.SimpleImmutableEntry(null, entry);
    }

    public Map<CacheEntry<K, V>, CacheEntry<K, V>> putAllWithAttributes(
            Map<K, Entry<V, AttributeMap>> data) {
        HashMap<CacheEntry<K, V>, CacheEntry<K, V>> result = new HashMap<CacheEntry<K, V>, CacheEntry<K, V>>();
        for (Map.Entry<K, Entry<V, AttributeMap>> e : data.entrySet()) {
            Map.Entry<CacheEntry<K, V>, CacheEntry<K, V>> me = put(e.getKey(), e.getValue()
                    .getKey(), e.getValue().getValue(), false);
            result.put(me.getKey(), me.getValue());
        }
        return result;
    }

    public CacheEntry<K, V> reduce(Reducer<CacheEntry<K, V>> reducer, CacheEntry<K, V> base) {
        return constantSafe.reduce(reducer, base);
    }

    public final CacheEntry<K, V> remove(Object key) {
        return doRemove(key, null);
    }

    public final CacheEntry<K, V> remove(Object key, Object value) {
        return doRemove(key, value);
    }

    public ParallelArray<CacheEntry<K, V>> removeAll() {
        modCount++;
        CacheEntry<K, V>[] entries = new CacheEntry[size];
        int count = 0;
        for (int i = 0; i < table.length; i++) {
            for (ChainingEntry e = table[i]; e != null; e = e.next()) {
                entries[count++] = e;
                e.setNext(null);
            }
            table[i] = null;
        }
        // TODO call clear.callback() for sub classes
        size = 0;
        volume = 0;
        return fromArray(entries);
    }

    public ParallelArray<CacheEntry<K, V>> removeAll(Collection keys) {
        ArrayList<ChainingEntry<K, V>> list = new ArrayList<ChainingEntry<K, V>>(keys.size());
        for (Object key : keys) {
            ChainingEntry<K, V> e = remove(key, null, false);
            if (e != null) {
                list.add(e);
            }
        }
        return fromArray((CacheEntry<K, V>[]) list.toArray(new CacheEntry[list.size()]));
    }

    public CacheEntry<K, V> removeAny(Predicate<? super CacheEntry<K, V>> selector) {
        if (size != 0) {
            for (int i = 0; i < table.length; i++) {
                ChainingEntry<K, V> e = table[i];
                ChainingEntry<K, V> prev = e;
                while (e != null) {
                    ChainingEntry<K, V> next = e.next();
                    if (selector.evaluate(e)) {
                        modCount++;
                        size--;
                        if (prev == e) {
                            table[i] = next;
                        } else {
                            prev.setNext(next);
                        }
                        deleted(e, false);
                        return e;
                    }
                    prev = e;
                    e = next;
                }
            }
        }
        return null;
    }

    public ParallelArray<CacheEntry<K, V>> removeEntries(Collection entries) {
        ArrayList<CacheEntry> list = new ArrayList<CacheEntry>();
        for (Map.Entry<K, V> entry : (Collection<Map.Entry>) entries) {
            CacheEntry ce = doRemove(entry.getKey(), entry.getValue());
            if (ce != null) {
                list.add(ce);
            }
        }
        return (ParallelArray) fromArray(list.toArray(new CacheEntry[list.size()]));
    }

    public CacheEntry<K, V> removeValue(Object value) {
        return removeAny(Predicates.mapAndEvaluate(MAP_ENTRY_TO_VALUE_MAPPER, Predicates
                .isEquals(value)));
    }

    public ParallelArray<CacheEntry<K, V>> removeValues(Collection values) {
        ArrayList<CacheEntry<K, V>> list = new ArrayList<CacheEntry<K, V>>();
        for (Object value : values) {
            CacheEntry<K, V> e = removeValue(value);
            if (e != null) {
                list.add(e);
            }
        }
        return fromArray((CacheEntry<K, V>[]) list.toArray(new CacheEntry[list.size()]));
    }

    public ParallelArray<CacheEntry<K, V>> retainAll(Collection<? super CacheEntry<K, V>> procedure) {
        return null;
    }

    public Collection<CacheEntry<K, V>> retainAll(Mapper m, Predicate p, Collection c) {
        ArrayList<ChainingEntry<K, V>> list = new ArrayList<ChainingEntry<K, V>>();
        if (size != 0) {
            ChainingEntry<K, V>[] table = this.table;
            int len = table.length;
            for (int i = 0; i < len; i++) {
                for (ChainingEntry<K, V> e = table[i]; e != null;) {
                    ChainingEntry<K, V> prev = null;
                    ChainingEntry<K, V> next = e.next();
                    Object o = m.map(e);
                    if (p.evaluate(o) && !c.contains(o)) {
                        modCount++;
                        size--;
                        if (prev == null) {
                            table[i] = next;
                        } else {
                            prev.setNext(next);
                        }
                        deleted(e, false);
                        list.add(e);
                    }
                    prev = e;
                    e = next;
                }
            }
        }
        return (Collection) list;
    }

    public <T> ParallelArray<CacheEntry<K, V>> retainAll(
            Mapper<? super CacheEntry<K, V>, ? extends T> mapper, Collection<? super T> procedure) {
        return null;
    }

    public Iterator<CacheEntry<K, V>> sequentially() {
        return new HashIterator<CacheEntry<K, V>>(CONSTANT_MAPPER);
    }

    public void setMaximumSize(int size) {
        throw new UnsupportedOperationException("size limitation not supported for this cache");
    }

    public void setMaximumVolume(long volume) {
        throw new UnsupportedOperationException("volume limitation not supported for this cache");
    }

    public int size() {
        return size;
    }

    public ParallelArray<CacheEntry<K, V>> trim() {
        if (this.size == 0 || size == 0 || volume == 0) {
            return removeAll();
        } else {
            ArrayList<ChainingEntry<K, V>> list = new ArrayList<ChainingEntry<K, V>>(Math.max(0,
                    this.size - size));
// while (this.size > size && this.volume > volume) {
//
// }
            return fromArray((CacheEntry<K, V>[]) list.toArray(new CacheEntry[list.size()]));
        }
    }

    public ParallelArray<CacheEntry<K, V>> trimTo(int size, long volume) {
        if (this.size == 0 || size == 0 || volume == 0) {
            return removeAll();
        } else {
            ArrayList<ChainingEntry<K, V>> list = new ArrayList<ChainingEntry<K, V>>(Math.max(0,
                    this.size - size));
// while (this.size > size && this.volume > volume) {
//
// }
            return fromArray((CacheEntry<K, V>[]) list.toArray(new CacheEntry[list.size()]));
        }
    }

    public long volume() {
        return volume;
    }

    public MemoryStoreWithFilter<K, V> withFilter(Predicate<? super CacheEntry<K, V>> selector) {
        return new WithFilterImpl(selector);
    }

    public final MemoryStoreWithFilter<K, V> withFilterOnAttributes(
            Predicate<? super AttributeMap> selector) {
        return withFilter(Predicates.mapAndEvaluate(Attributes.WITHATTRIBUTES_TO_ATTRIBUTES_MAPPER,
                selector));
    }

    public final MemoryStoreWithFilter<K, V> withFilterOnKeys(Predicate<? super K> selector) {
        return withFilter(Predicates.mapAndEvaluate(MAP_ENTRY_TO_KEY_MAPPER, selector));
    }

    public final MemoryStoreWithFilter<K, V> withFilterOnValues(Predicate<? super V> selector) {
        return withFilter(Predicates.mapAndEvaluate(MAP_ENTRY_TO_VALUE_MAPPER, selector));
    }

    public final MemoryStoreWithMapping<K> withKeys() {
        return withMapping(MAP_ENTRY_TO_KEY_MAPPER);
    }

    public <T> MemoryStoreWithMapping<T> withMapping(
            Mapper<? super CacheEntry<K, V>, ? extends T> mapper) {
        return new WithMappingImpl(mapper);
    }

    public final MemoryStoreWithMapping<V> withValues() {
        return withMapping(MAP_ENTRY_TO_VALUE_MAPPER);
    }

    private void deleted(ChainingEntry<K, V> entry, boolean isEvicted) {
        entry.setNext(null);// the entry might have a long lifespan, so clear references
        removed(entry, isEvicted);
        volume -= entry.getSize();
    }

    /**
     * Return properly casted first entry of bin for given hash.
     */
    private ChainingEntry<K, V> getFirst(int hash) {
        ChainingEntry<?, ?>[] tab = table;
        return (ChainingEntry<K, V>) tab[hash & tab.length - 1];
    }

    private void rehash() {
        ChainingEntry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity >= MAXIMUM_CAPACITY) {
            return;
        }
        ChainingEntry<K, V>[] newTable = new ChainingEntry[oldCapacity << 1];
        threshold = (int) (newTable.length * loadFactor);
        int sizeMask = newTable.length - 1;

        for (int i = 0; i < oldCapacity; i++) {
            ChainingEntry<K, V> e = oldTable[i];

            if (e != null) {
                oldTable[i] = null;
                do {
                    ChainingEntry<K, V> nextEntry = e.next();
                    int tableIndex = e.getHash() & sizeMask;
                    e.setNext(newTable[tableIndex]);
                    newTable[tableIndex] = e;
                    e = nextEntry;
                } while (e != null);
            }
        }
        table = newTable;
    }

    abstract ChainingEntry<K, V> created(K key, V value, AttributeMap attributes);

    ChainingEntry<K, V> doRemove(Object key, Object value) {
        return remove(key, value, false);
    }

    // ForkJoinExecutor
    <T> ParallelArray<T> fromArray(T[] entries) {
        return ParallelArray.createUsingHandoff(entries, ParallelArray.defaultExecutor());
    }

    ChainingEntry<K, V> remove(Object key, Object value, boolean isEvicted) {
        int hash = hash(key.hashCode());
        ChainingEntry<K, V>[] tab = table;
        int index = hash & tab.length - 1;
        ChainingEntry<K, V> e = tab[index];
        ChainingEntry<K, V> prev = e;

        while (e != null) {
            ChainingEntry<K, V> next = e.next();
            if (e.getHash() == hash && key.equals(e.getKey())) {
                if (value == null || value.equals(e.getValue())) {
                    modCount++;
                    size--;
                    if (prev == e) {
                        table[index] = next;
                    } else {
                        prev.setNext(next);
                    }
                    deleted(e, isEvicted);
                    return e;
                } else {
                    return null; // was next = null;
                }
            }
            prev = e;
            e = next;
        }
        return null;
    }

    void removed(ChainingEntry<K, V> entry, boolean isEvicted) {};

    abstract ChainingEntry<K, V> updated(ChainingEntry<K, V> old, K key, V value,
            AttributeMap attributes);

    /**
     * Applies a supplemental hash function to a given hashCode, which defends against
     * poor quality hash functions. This is critical because this class uses power-of-two
     * length hash tables, that otherwise encounter collisions for hashCodes that do not
     * differ in lower bits.
     */
    static int hash(int h) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= h >>> 20 ^ h >>> 12;
        return h ^ h >>> 7 ^ h >>> 4;
    }

    final class HashIterator<E> implements Iterator<E> {
        private ChainingEntry<K, V> current; // current entry

        private int expectedModCount; // For fast-fail

        private int index; // current slot

        private final Mapper<Entry<K, V>, E> m;

        private ChainingEntry<K, V> next; // next entry to return

        private final Mapper postSelect;

        private final Predicate<E> predicate;

        HashIterator(Mapper<Entry<K, V>, E> mapper) {
            this(mapper, Predicates.<E> truePredicate(), null);
        }

        HashIterator(Mapper<Entry<K, V>, E> mapper, Predicate<E> predicate, Mapper postSelect) {
            expectedModCount = modCount;
            this.m = mapper;
            this.predicate = predicate;
            if (postSelect == null) {
                this.postSelect = mapper;
            } else {
                this.postSelect = compoundMapper(mapper, postSelect);
            }
            if (size > 0) { // advance to first entry
                ChainingEntry[] t = table;
                while (index < t.length
                        && ((next = t[index++]) == null || !predicate.evaluate(mapper.map(next)))) {}
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        public E next() {
            return (E) postSelect.map(nextEntry());
        }

        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            K k = current.getKey();
            current = null;
            cache.remove(k);
            expectedModCount = modCount;
        }

        final Entry<K, V> nextEntry() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            ChainingEntry<K, V> e = current = next;
            if (e == null) {
                throw new NoSuchElementException();
            }

            if ((next = e.next()) == null) {
                ChainingEntry[] t = table;
                while (index < t.length
                        && ((next = t[index++]) == null || !predicate.evaluate(m.map(next)))) {}
            }
            return e;
        }
    }

    class WithFilteredMapping<T> implements MemoryStoreWithMapping<T> {
        final Mapper<? super CacheEntry<K, V>, ? extends T> mapper;

        final Predicate<? super CacheEntry<K, V>> selector;

        WithFilteredMapping(Predicate<? super CacheEntry<K, V>> selector,
                Mapper<? super CacheEntry<K, V>, ? extends T> mapper) {
            if (selector == null) {
                throw new NullPointerException("selector is null");
            } else if (mapper == null) {
                throw new NullPointerException("mapper is null");
            }
            this.mapper = mapper;
            this.selector = selector;
        }

        public final ParallelArray<T> all() {
            if (size != 0) {
                ArrayList<T> al = new ArrayList<T>();
                for (int i = 0; i < table.length; i++) {
                    for (ChainingEntry e = table[i]; e != null; e = e.next()) {
                        if (selector.evaluate(e)) {
                            T o = mapper.map(e);
                            al.add(o);
                        }
                    }
                }
                return fromArray((T[]) al.toArray());
            }
            return fromArray((T[]) new Object[0]);
        }

        public final ParallelArray<T> all(Class<? super T> elementType) {
            if (size != 0) {
                ArrayList<T> al = new ArrayList<T>();
                for (int i = 0; i < table.length; i++) {
                    for (ChainingEntry e = table[i]; e != null; e = e.next()) {
                        if (selector.evaluate(e)) {
                            T o = mapper.map(e);
                            al.add(o);
                        }
                    }
                }
                return fromArray(al.toArray((T[]) Array.newInstance(elementType, al.size())));
            }
            return fromArray((T[]) Array.newInstance(elementType, 0));
        }

        public final T any() {
            if (size != 0) {
                for (int i = 0; i < table.length; i++) {
                    for (ChainingEntry<K, V> e = table[i]; e != null; e = e.next()) {
                        if (selector.evaluate(e)) {
                            return mapper.map(e);
                        }
                    }
                }
            }
            return null;
        }

        public final void apply(Procedure<? super T> procedure) {
            if (procedure == null) {
                throw new NullPointerException("procedure is null");
            }
            if (size != 0) {
                for (int i = 0; i < table.length; i++) {
                    for (ChainingEntry<K, V> e = table[i]; e != null; e = e.next()) {
                        if (selector.evaluate(e)) {
                            T t = mapper.map(e);
                            procedure.apply(t);
                        }
                    }
                }
            }
        }

        public T reduce(Reducer<T> reducer, T base) {
            T result = base;
            if (size != 0) {
                for (int i = 0; i < table.length; i++) {
                    for (ChainingEntry<K, V> e = table[i]; e != null; e = e.next()) {
                        if (selector.evaluate(e)) {
                            T t = mapper.map(e);
                            result = reducer.combine(result, t);
                        }
                    }
                }
            }
            return result;
        }

        public Iterator<T> sequentially() {
            return new HashIterator<T>(CONSTANT_MAPPER, (Predicate) selector, mapper);
        }

        public final int size() {
            int count = 0;
            if (size != 0) {
                for (int i = 0; i < table.length; i++) {
                    for (ChainingEntry<K, V> e = table[i]; e != null; e = e.next()) {
                        if (selector.evaluate(e)) {
                            count++;
                        }
                    }
                }
            }
            return count;
        }

        public final <U> MemoryStoreWithMapping<U> withMapping(Mapper<? super T, ? extends U> mapper) {
            return new WithFilteredMapping<U>(selector, compoundMapper(this.mapper, mapper));
        }
    }

    class WithFilterImpl extends WithFilteredMapping<CacheEntry<K, V>> implements
            MemoryStoreWithFilter<K, V> {
        WithFilterImpl(Predicate<? super CacheEntry<K, V>> selector) {
            super(selector, CONSTANT_MAPPER);
        }

        WithFilterImpl(Predicate<? super CacheEntry<K, V>> selector,
                Mapper<? super CacheEntry<K, V>, ? super CacheEntry<K, V>> mapper) {
            super(selector, (Mapper) mapper);
        }

        public void clear() {
            if (size != 0) {
                for (int i = 0; i < table.length; i++) {
                    ChainingEntry<K, V> e = table[i];
                    ChainingEntry<K, V> prev = e;
                    while (e != null) {
                        ChainingEntry<K, V> next = e.next();
                        if (selector.evaluate(e)) {
                            modCount++;
                            size--;
                            if (prev == e) {
                                table[i] = next;
                            } else {
                                prev.setNext(next);
                            }
                            deleted(e, false);
                        }
                        prev = e;
                        e = next;
                    }
                }
            }
        }

        public ParallelArray<CacheEntry<K, V>> removeAll() {
            if (size != 0) {
                ArrayList<CacheEntry<K, V>> al = new ArrayList<CacheEntry<K, V>>();
                for (int i = 0; i < table.length; i++) {
                    ChainingEntry<K, V> e = table[i];
                    ChainingEntry<K, V> prev = e;
                    while (e != null) {
                        ChainingEntry<K, V> next = e.next();
                        if (selector.evaluate(e)) {
                            modCount++;
                            size--;
                            if (prev == e) {
                                table[i] = next;
                            } else {
                                prev.setNext(next);
                            }
                            deleted(e, false);
                            al.add(e);
                        }
                        prev = e;
                        e = next;
                    }
                }
                return fromArray((CacheEntry<K, V>[]) al.toArray(new CacheEntry[al.size()]));
            }
            return fromArray((CacheEntry<K, V>[]) new CacheEntry[0]);
        }

        public ParallelArray<CacheEntry<K, V>> retainAll(
                Collection<? super CacheEntry<K, V>> procedure) {
            return null;
        }

        public <T> ParallelArray<CacheEntry<K, V>> retainAll(
                Mapper<? super CacheEntry<K, V>, ? extends T> mapper,
                Collection<? super T> procedure) {
            return null;
        }

        public MemoryStoreWithFilter<K, V> withFilter(Predicate<? super CacheEntry<K, V>> selector) {
            return new WithFilterImpl(Predicates.and(this.selector, selector));
        }

        public MemoryStoreWithFilter<K, V> withFilterOnAttributes(
                Predicate<? super AttributeMap> selector) {
            return new WithFilterImpl(Predicates.and(this.selector, Predicates.mapAndEvaluate(
                    Attributes.WITHATTRIBUTES_TO_ATTRIBUTES_MAPPER, selector)));
        }

        public MemoryStoreWithFilter<K, V> withFilterOnKeys(Predicate<? super K> selector) {
            return new WithFilterImpl(Predicates.and(this.selector, Predicates.mapAndEvaluate(
                    MAP_ENTRY_TO_KEY_MAPPER, selector)));
        }

        public MemoryStoreWithFilter<K, V> withFilterOnValues(Predicate<? super V> selector) {
            return new WithFilterImpl(Predicates.and(this.selector, Predicates.mapAndEvaluate(
                    MAP_ENTRY_TO_VALUE_MAPPER, selector)));
        }

        public final MemoryStoreWithMapping<K> withKeys() {
            return withMapping(MAP_ENTRY_TO_KEY_MAPPER);
        }

        public final MemoryStoreWithMapping<V> withValues() {
            return withMapping(MAP_ENTRY_TO_VALUE_MAPPER);
        }
    }

    final class WithMappingImpl<T> implements MemoryStoreWithMapping<T> {
        private final Mapper<? super CacheEntry<K, V>, ? extends T> mapper;

        WithMappingImpl(Mapper<? super CacheEntry<K, V>, ? extends T> mapper) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            }
            this.mapper = mapper;
        }

        public ParallelArray<T> all() {
            T[] entries = (T[]) new Object[size];
            int count = 0;
            for (int i = 0; i < table.length; i++) {
                for (ChainingEntry e = table[i]; e != null; e = e.next()) {
                    entries[count++] = mapper.map(e);
                }
            }
            return fromArray(entries);
        }

        public ParallelArray<T> all(Class<? super T> elementType) {
            T[] entries = (T[]) Array.newInstance(elementType, size);
            int count = 0;
            for (int i = 0; i < table.length; i++) {
                for (ChainingEntry e = table[i]; e != null; e = e.next()) {
                    entries[count++] = mapper.map(e);
                }
            }
            return fromArray(entries);

        }

        public T any() {
            CacheEntry ce = AbstractSequentialMemoryStore.this.any();
            return mapper.map(ce);
        }

        public void apply(Procedure<? super T> procedure) {
            if (procedure == null) {
                throw new NullPointerException("procedure is null");
            }
            if (size != 0) {
                for (int i = 0; i < table.length; i++) {
                    for (ChainingEntry<K, V> e = table[i]; e != null; e = e.next()) {

                        T userMapped = mapper.map(e);
                        procedure.apply(userMapped);

                    }
                }
            }
        }

        public T reduce(Reducer<T> reducer, T base) {
            T result = base;
            for (int i = 0; i < table.length; i++) {
                for (ChainingEntry<K, V> e = table[i]; e != null; e = e.next()) {
                    result = reducer.combine(result, mapper.map(e));
                }
            }
            return result;
        }

        public Iterator<T> sequentially() {
            return new HashIterator<T>((Mapper) mapper);
        }

        public int size() {
            return size;
        }

        public <U> MemoryStoreWithMapping<U> withMapping(Mapper<? super T, ? extends U> mapper) {
            return new WithMappingImpl(compoundMapper(this.mapper, mapper));
        }
    }
}

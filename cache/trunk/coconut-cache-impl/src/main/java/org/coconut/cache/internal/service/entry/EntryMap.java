/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.eviction.InternalCacheEvictionService;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;

/**
 * This class is partly adopted from ConcurrentHashMap.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EntryMap<K, V> implements Iterable<AbstractCacheEntry<K, V>> {

    /* ---------------- Constants -------------- */

    /**
     * The default initial capacity for this table, used when not otherwise specified in a
     * constructor.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The default load factor for this table, used when not otherwise specified in a
     * constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The maximum capacity, used if a higher value is implicitly specified by either of
     * the constructors with arguments. MUST be a power of two <= 1<<30 to ensure that
     * entries are indexable using ints.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /* ---------------- Fields -------------- */

    /**
     * The table.
     */
    AbstractCacheEntry<K, V>[] table;

    /**
     * The load factor for the hash table.
     */
    final float loadFactor;

    /**
     * Number of updates that alter the size of the table. This is used during bulk-read
     * methods to make sure they see a consistent snapshot.
     */
    int modCount;

    /**
     * The number of elements in this hash map.
     */
    int size;

    long volume;

    /**
     * The table is rehashed when its size exceeds this threshold. (The value of this
     * field is always (int)(capacity * loadFactor).)
     */
    int threshold;

    Set<K> keySet;

    Set<Map.Entry<K, V>> entrySet;

    Collection<V> values;

    private final InternalCacheSupport<K, V> ics;

    private final InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService;

    /**
     * Creates a new, empty map with a default initial capacity, and load factor.
     */
    public EntryMap(InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService,
            InternalCacheSupport<K, V> ics) {
        this(evictionService, ics, DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a new, empty map with the specified initial capacity, and with default load
     * factor.
     *
     * @param initialCapacity
     *            the initial capacity. The implementation performs internal sizing to
     *            accommodate this many elements.
     * @throws IllegalArgumentException
     *             if the initial capacity of elements is negative.
     */
    public EntryMap(InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService,
            InternalCacheSupport<K, V> ics, int initialCapacity) {
        this(evictionService, ics, initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates a new, empty map with the specified initial capacity, and load factor.
     *
     * @param initialCapacity
     *            the initial capacity. The implementation performs internal sizing to
     *            accommodate this many elements.
     * @param loadFactor
     *            the load factor threshold, used to control resizing. Resizing may be
     *            performed when the average number of elements per bin exceeds this
     *            threshold.
     * @throws IllegalArgumentException
     *             if the initial capacity is negative or the load factor is nonpositive.
     */
    public EntryMap(InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService,
            InternalCacheSupport<K, V> ics, int initialCapacity, float loadFactor) {
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        this.evictionService = evictionService;
        this.ics = ics;
        this.loadFactor = loadFactor;
        threshold = (int) (16 * loadFactor);
        table = new AbstractCacheEntry[16];
    }

    /* ---------------- Small Utilities -------------- */

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

    /* ---------------- Inner Classes -------------- */

    /**
     * Return properly casted first entry of bin for given hash.
     */
    private AbstractCacheEntry<K, V> getFirst(int hash) {
        AbstractCacheEntry<?, ?>[] tab = table;
        return (AbstractCacheEntry<K, V>) tab[hash & tab.length - 1];
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (size != 0) {
            AbstractCacheEntry<K, V>[] tab = table;
            int len = tab.length;
            for (int i = 0; i < len; i++) {
                for (AbstractCacheEntry<K, V> e = tab[i]; e != null; e = e.next) {
                    V v = e.getValue();
                    if (value == v || value.equals(v)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Set<Map.Entry<K, V>> entrySet(ConcurrentMap<K, V> cache) {
        return entrySet != null ? entrySet : (entrySet = (Set) new EntrySet<K, V>(cache, this));
    }

    /**
     * @param entry
     * @return the
     */
    public boolean addElement(AbstractCacheEntry<K, V> prev, AbstractCacheEntry<K, V> entry) {
        if (entry == null) {
            if (prev != null) {
                prev.setPolicyIndex(-1);
                remove(prev.getKey());
            }
            return false;
        }
        if (entry.getPolicyIndex() == Integer.MIN_VALUE) {
            return false;
        }
        if (entry.getPolicyIndex() == -1) { // entry is newly added
            entry.setPolicyIndex(evictionService.add(entry));
            if (entry.getPolicyIndex() == -1) {
                return false; // entry was rejected
            }
        } else if (!evictionService.replace(entry.getPolicyIndex(), entry)) {
            entry.setPolicyIndex(-1);
            remove(entry.getKey());
            return false;
        }
        put(entry);
        return true;
    }

    public List<CacheEntry<K, V>> trimCache() {
        List<CacheEntry<K, V>> list = null;
        while (evictionService.isSizeOrVolumeBreached(size, volume)) {
            if (list == null) {
                list = new ArrayList<CacheEntry<K, V>>(2);
            }
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            remove(e.getKey());
            list.add(e);
        }
        return list == null ? Collections.EMPTY_LIST : list;
    }

    public Collection<? extends AbstractCacheEntry<K, V>> clearAndGetAll() {
        int s = size;

        if (s != 0) {
            ArrayList<AbstractCacheEntry<K, V>> list = new ArrayList<AbstractCacheEntry<K, V>>(size);
            modCount++;
            AbstractCacheEntry<K, V>[] tab = table;
            for (int i = 0; i < tab.length; i++) {
                AbstractCacheEntry<K, V> next = tab[i];
                while (next != null) {
                    list.add(next);
                    next = next.next;
                }
                tab[i] = null;
            }
            size = 0;
            volume = 0;
            evictionService.clear();
            return list;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public AbstractCacheEntry<K, V> get(Object key) {
        // DONE
        if (size != 0) {
            int hash = hash(key.hashCode());
            AbstractCacheEntry<K, V> e = getFirst(hash);
            while (e != null) {
                if (e.getHash() == hash && key.equals(e.getKey())) {
                    return e;
                }
                e = e.next;
            }
        }
        return null;
    }

    /**
     * @see java.util.AbstractMap#keySet()
     */
    public Set<K> keySet(ConcurrentMap<K, V> cache) {
        return keySet != null ? keySet : (keySet = new KeySet<K, V>(cache, this));
    }

    public AbstractCacheEntry<K, V> put(AbstractCacheEntry<K, V> entry) {
        K key = entry.getKey();
        int hash = entry.getHash();
        AbstractCacheEntry<K, V>[] tab = table;
        int index = hash & tab.length - 1;
        AbstractCacheEntry<K, V> e = tab[index];
        AbstractCacheEntry<K, V> prev = e;
        while (e != null) {
            AbstractCacheEntry<K, V> next = e.next;
            if (e.getHash() == hash && key.equals(e.getKey())) {
                ++modCount;
                volume = volume + entry.getSize() - e.getSize();
                if (prev == e) {
                    tab[index] = entry;
                } else {
                    prev.next = entry;
                }
                entry.next = e.next;
                return e;
            }
            prev = e;
            e = next;
        }
        ++modCount;
        entry.next = tab[index];
        tab[index] = entry;
        volume += entry.getSize();
        if (size++ >= threshold) {
            // ensure capacity
            rehash();
        }
        return null;
    }

    public void do_removeAll(List<CacheEntry<K, V>> list, Collection<? extends K> keys) {
        for (K key : keys) {
            AbstractCacheEntry<K, V> e = remove(key, null);
            if (e != null) {
                evictionService.remove(e.getPolicyIndex());
                list.add(e);
            }
        }
    }

    public CacheEntry<K, V> do_remove(Object key, Object value) {
        AbstractCacheEntry<K, V> e = remove(key, value);
        if (e != null) {
            evictionService.remove(e.getPolicyIndex());
        }
        return e;
    }

    public AbstractCacheEntry<K, V> remove(Object key) {
        return remove(key, null);
    }

    AbstractCacheEntry<K, V> remove(Object key, Object value) {
        int hash = hash(key.hashCode());
        AbstractCacheEntry<K, V>[] tab = table;
        int index = hash & tab.length - 1;
        AbstractCacheEntry<K, V> e = tab[index];
        AbstractCacheEntry<K, V> prev = e;

        while (e != null) {
            AbstractCacheEntry<K, V> next = e.next;
            if (e.getHash() == hash && key.equals(e.getKey())) {
                if (value == null || value.equals(e.getValue())) {
                    modCount++;
                    size--;
                    volume -= e.getSize();
                    if (prev == e) {
                        table[index] = next;
                    } else {
                        prev.next = next;
                    }
                    // e.entryRemoved();
                    return e;
                } else {
                    next = null;
                }
            }
            prev = e;
            e = next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public long volume() {
        return volume;
    }

    /**
     * @see java.util.AbstractMap#values()
     */
    public Collection<V> values(Map<K, V> cache) {
        return values != null ? values : (values = new Values<K, V>(cache, this));
    }

    /**
     * @param i
     */
    private void rehash() {
        AbstractCacheEntry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity >= MAXIMUM_CAPACITY) {
            return;
        }
        AbstractCacheEntry<K, V>[] newTable = new AbstractCacheEntry[oldCapacity << 1]; // HashEntry.newArray(oldCapacity
        // <<
        // 1);
        threshold = (int) (newTable.length * loadFactor);
        int sizeMask = newTable.length - 1;

        for (int i = 0; i < oldCapacity; i++) {
            AbstractCacheEntry<K, V> e = oldTable[i];

            if (e != null) {
                oldTable[i] = null;
                do {
                    AbstractCacheEntry<K, V> nextEntry = e.next;
                    int tableIndex = e.getHash() & sizeMask;
                    e.next = newTable[tableIndex];
                    newTable[tableIndex] = e;
                    e = nextEntry;
                } while (e != null);
            }
        }
        table = newTable;
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<AbstractCacheEntry<K, V>> iterator() {
        return new EntrySetIterator<K, V>(null, this);
    }

    static abstract class BaseIterator<K, V, E> implements Iterator<E> {

        final EntryMap<K, ?> map;

        final Map<K, ?> cache;

        private AbstractCacheEntry<K, V> entry;

        private int expectedModCount;

        private int index;

        private AbstractCacheEntry<K, V> nextEntry;

        BaseIterator(Map<K, ?> cache, EntryMap<K, ?> map) {
            expectedModCount = map.modCount;
            this.cache = cache;
            this.map = map;
            if (map.size > 0) {
                findNextBucket();
            }
        }

        public boolean hasNext() {
            return nextEntry != null;
        }

        /** {@inheritDoc} */
        public abstract E next();

        /** {@inheritDoc} */
        public void remove() {
            if (entry == null) {
                throw new IllegalStateException();
            }
            checkForConcurrentMod();
            AbstractCacheEntry<?, ?> e = entry;
            entry = null;
            if (cache != null) {
                cache.remove(e.getKey());
            } else {
                map.remove(e.getKey(), null);
            }
            expectedModCount = map.modCount;
        }

        private void findNextBucket() {
            AbstractCacheEntry[] entries = map.table;
            while (index < entries.length) {
                nextEntry = entries[index++];
                if (nextEntry != null) {
                    break;
                }
            }
        }

        void checkForConcurrentMod() throws ConcurrentModificationException {
            if (expectedModCount != map.modCount) {
                throw new ConcurrentModificationException();
            }
        }

        AbstractCacheEntry<K, V> nextEntry() {
            checkForConcurrentMod();
            AbstractCacheEntry<K, V> e = nextEntry;
            entry = nextEntry;
            if (e == null) {
                throw new NoSuchElementException();
            }
            nextEntry = nextEntry.next;
            if (nextEntry == null) {
                findNextBucket();
            }
            return e;
        }
    }

    static class EntrySet<K, V> extends AbstractSet<CacheEntry<K, V>> {
        ConcurrentMap<K, V> cache;

        EntryMap<K, V> map;

        final boolean copyEntries;

        EntrySet(ConcurrentMap<K, V> cache, EntryMap<K, V> map) {
            this.map = map;
            this.cache = cache;
            this.copyEntries = false;
        }

        /** {@inheritDoc} */
        @Override
        public void clear() {
            cache.clear();
        }

        /** {@inheritDoc} */
        @Override
        public boolean contains(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            map.ics.checkRunning("contains", false);
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            AbstractCacheEntry<K, V> ace = map.get(e.getKey());
            V v = ace != null ? ace.getValue() : null;
            Object val = e.getValue();
            return v != null && (v == val || v.equals(val));
        }

        /** {@inheritDoc} */
        @Override
        public Iterator<CacheEntry<K, V>> iterator() {
            map.ics.checkRunning("iterator");
            return (Iterator) new EntrySetIterator<K, V>(cache, map);
        }

        /** {@inheritDoc} */
        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            map.ics.checkRunning("remove");
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return cache.remove(e.getKey(), e.getValue());
        }

        /** {@inheritDoc} */
        @Override
        public int size() {
            return cache.size();
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        /** {@inheritDoc} */
        @Override
        public boolean containsAll(Collection<?> c) {
            map.ics.checkRunning("contains", false);
            return super.containsAll(c);
        }

        /** {@inheritDoc} */
        @Override
        public boolean retainAll(Collection<?> c) {
            return super.retainAll(c);
        }

        /** {@inheritDoc} */
        @Override
        public Object[] toArray() {
            if (size() == 0) {
                return new Object[0];
            }
            return super.toArray();
        }

        /** {@inheritDoc} */
        @Override
        public <T> T[] toArray(T[] a) {
            if (size() == 0) {
                return a;
            }
            return super.toArray(a);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return super.toString();
        }

        /** {@inheritDoc} */
        @Override
        public boolean removeAll(Collection<?> c) {
            return super.removeAll(c);
        }
    }

    static class EntrySetIterator<K, V> extends BaseIterator<K, V, AbstractCacheEntry<K, V>> {
        EntrySetIterator(Map<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
        }

        /** {@inheritDoc} */
        @Override
        public AbstractCacheEntry<K, V> next() {
            return nextEntry();
        }
    }

    static class KeyIterator<K, V> extends BaseIterator<K, V, K> {
        KeyIterator(Map<K, V> cache, EntryMap<K, ?> map) {
            super(cache, map);
        }

        /** {@inheritDoc} */
        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    static class KeySet<K, V> extends AbstractSet<K> {
        final EntryMap<K, V> map;

        final Map<K, V> cache;

        KeySet(Map<K, V> cache, EntryMap<K, V> map) {
            this.map = map;
            this.cache = cache;
        }

        /** {@inheritDoc} */
        @Override
        public void clear() {
            cache.clear();
        }

        /** {@inheritDoc} */
        @Override
        public boolean contains(Object o) {
            return cache.containsKey(o);
        }

        /** {@inheritDoc} */
        @Override
        public Iterator<K> iterator() {
            map.ics.checkRunning("iterator", false);
            return new KeyIterator<K, V>(cache, map);
        }

        /** {@inheritDoc} */
        @Override
        public int size() {
            return cache.size();
        }

        /** {@inheritDoc} */
        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            map.ics.checkRunning("remove");
            return cache.remove(o) != null;
        }

        /** {@inheritDoc} */
        @Override
        public Object[] toArray() {
            if (size() == 0) {
                return new Object[0];
            }
            return super.toArray();
        }

        /** {@inheritDoc} */
        @Override
        public <T> T[] toArray(T[] a) {
            if (size() == 0) {
                return a;
            }
            return super.toArray(a);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            map.ics.checkRunning("remove");
            return super.removeAll(c);
        }
    }

    static class ValueIterator<K, V> extends BaseIterator<K, V, V> {
        ValueIterator(Map<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
        }

        /** {@inheritDoc} */
        @Override
        public V next() {
            return nextEntry().getValue();
        }
    }

    static class Values<K, V> extends AbstractCollection<V> {
        final EntryMap<K, V> map;

        final Map<K, V> cache;

        Values(Map<K, V> cache, EntryMap<K, V> map) {
            this.map = map;
            this.cache = cache;
        }

        /** {@inheritDoc} */
        @Override
        public boolean removeAll(Collection<?> c) {
            if (c == null) {
                throw new NullPointerException("c is null");
            }
            map.ics.checkRunning("remove");
            return super.removeAll(c);
        }

        /** {@inheritDoc} */
        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            map.ics.checkRunning("remove");
            return super.remove(o);
        }

        /** {@inheritDoc} */
        @Override
        public Object[] toArray() {
            if (size() == 0) {
                return new Object[0];
            }
            return super.toArray();
        }

        /** {@inheritDoc} */
        @Override
        public <T> T[] toArray(T[] a) {
            if (size() == 0) {
                return a;
            }
            return super.toArray(a);
        }

        /** {@inheritDoc} */
        @Override
        public void clear() {
            cache.clear();
        }

        /** {@inheritDoc} */
        @Override
        public boolean contains(Object o) {
            return cache.containsValue(o);
        }

        /** {@inheritDoc} */
        @Override
        public Iterator<V> iterator() {
            map.ics.checkRunning("iterator", false);
            return new ValueIterator<K, V>(cache, map);
        }

        /** {@inheritDoc} */
        @Override
        public int size() {
            return cache.size();
        }
    }
}

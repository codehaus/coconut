/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.coconut.cache.CacheEntry;

/**
 * This class is partly adopted from ConcurrentHashMap.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EntryMap<K, V> implements Iterable<AbstractCacheEntry<K, V>> {

    /* ---------------- Constants -------------- */

    /**
     * The default initial capacity for this table, used when not otherwise
     * specified in a constructor.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The default load factor for this table, used when not otherwise specified
     * in a constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The maximum capacity, used if a higher value is implicitly specified by
     * either of the constructors with arguments. MUST be a power of two <= 1<<30
     * to ensure that entries are indexable using ints.
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
     * Number of updates that alter the size of the table. This is used during
     * bulk-read methods to make sure they see a consistent snapshot.
     */
    int modCount;

    /**
     * The number of elements in this hash map.
     */
    int size;

    long capacity;

    /**
     * The table is rehashed when its size exceeds this threshold. (The value of
     * this field is always (int)(capacity * loadFactor).)
     */
    int threshold;

    Set<K> keySet;

    Set<CacheEntry<K, V>> entrySet;

    Collection<V> values;

    private final boolean isThreadSafe;

    // Collection<AbstractCacheEntry<K, V>> entries;

    /* ---------------- Small Utilities -------------- */

    /**
     * Applies a supplemental hash function to a given hashCode, which defends
     * against poor quality hash functions. This is critical because this class
     * uses power-of-two length hash tables, that otherwise encounter collisions
     * for hashCodes that do not differ in lower bits.
     */
    static int hash(int h) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns the table entry that should be used for key with given hash
     * 
     * @param hash
     *            the hash code for the key
     * @return the table entry
     */
    static int indexFor(int hash, int tableLength) {
        return hash & (tableLength - 1);
    }

    /* ---------------- Inner Classes -------------- */

    /**
     * Creates a new, empty map with a default initial capacity, and load
     * factor.
     */
    public EntryMap(boolean isThreadSafe) {
        this(isThreadSafe, DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a new, empty map with the specified initial capacity, and with
     * default load factor.
     * 
     * @param initialCapacity
     *            the initial capacity. The implementation performs internal
     *            sizing to accommodate this many elements.
     * @throws IllegalArgumentException
     *             if the initial capacity of elements is negative.
     */
    public EntryMap(boolean isThreadSafe, int initialCapacity) {
        this(isThreadSafe, initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates a new, empty map with the specified initial capacity, and load
     * factor.
     * 
     * @param initialCapacity
     *            the initial capacity. The implementation performs internal
     *            sizing to accommodate this many elements.
     * @param loadFactor
     *            the load factor threshold, used to control resizing. Resizing
     *            may be performed when the average number of elements per bin
     *            exceeds this threshold.
     * @throws IllegalArgumentException
     *             if the initial capacity is negative or the load factor is
     *             nonpositive.
     */
    public EntryMap(boolean isThreadSafe, int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >=0, was "
                    + initialCapacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("load factor must be >0, was "
                    + loadFactor);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }

        this.loadFactor = loadFactor;
        threshold = (int) (16 * loadFactor);
        table = new AbstractCacheEntry[16];
        this.isThreadSafe = isThreadSafe;
    }

    /**
     * Return properly casted first entry of bin for given hash
     */
    private AbstractCacheEntry<K, V> getFirst(int hash) {
        AbstractCacheEntry[] tab = table;
        return (AbstractCacheEntry<K, V>) tab[hash & (tab.length - 1)];
    }

    public int clear() {
        int s = size;
        if (s != 0) {
            modCount++;
            AbstractCacheEntry<K, V>[] tab = table;
            for (int i = 0; i < tab.length; i++) {
                tab[i] = null;
            }
            size = 0;
            capacity = 0;
        }
        return s;
    }

    public boolean containsKey(Object key) {
        // DONE
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        if (size != 0) {
            int hash = hash(key.hashCode());
            AbstractCacheEntry<K, V> e = getFirst(hash);
            while (e != null) {
                if (e.getHash() == hash && key.equals(e.getKey())) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
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

    /**
     * @see java.util.AbstractMap#entrySet()
     */
    public Set<Map.Entry<K, V>> entrySetPublic(ConcurrentMap<K, V> cache) {
        return (Set) entrySet(cache, true);
    }

    /**
     * @see java.util.AbstractMap#entrySet()
     */
    public Set<CacheEntry<K, V>> entrySet(ConcurrentMap<K, V> cache, boolean copyEntries) {
        Set<CacheEntry<K, V>> es = entrySet;
        return (es != null) ? es
                : (entrySet = isThreadSafe ? new EntrySetSynchronized<K, V>(cache, this,
                        copyEntries) : new EntrySet<K, V>(cache, this, copyEntries));
    }

    public Collection<? extends AbstractCacheEntry<K, V>> getAll() {
        ArrayList<AbstractCacheEntry<K, V>> list = new ArrayList<AbstractCacheEntry<K, V>>(
                size);
        for (AbstractCacheEntry<K, V> e : this) {
            list.add(e);
        }
        return list;
    }

    public AbstractCacheEntry<K, V> get(Object key) {
        // DONE
        int hash = hash(key.hashCode());
        if (size != 0) {
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

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @see java.util.AbstractMap#keySet()
     */
    public Set<K> keySet(ConcurrentMap<K, V> cache) {
        Set<K> ks = keySet;
        if (isThreadSafe) {
            return (ks != null) ? ks : (keySet = new KeySet<K, V>(cache, this));
        } else {
            return (ks != null) ? ks
                    : (keySet = new KeySetSynchronized<K, V>(cache, this));
        }
    }

    public AbstractCacheEntry<K, V> put(AbstractCacheEntry<K, V> entry) {
        if (size + 1 > threshold) {
            // ensure capacity
            rehash();
        }
        K key = entry.getKey();
        int hash = entry.getHash();
        AbstractCacheEntry<K, V>[] tab = table;
        int index = hash & (tab.length - 1);
        AbstractCacheEntry<K, V> first = tab[index];
        AbstractCacheEntry<K, V> e = first;
        AbstractCacheEntry<K, V> prev = first;
        while (e != null && (e.getHash() != hash || !key.equals(e.getKey()))) {
            e = e.next;
            prev = e;
        }
        AbstractCacheEntry<K, V> oldValue;
        if (e != null) {
            oldValue = e;
            entry.next = oldValue.next;
            if (prev == e) { // first entry
                table[index] = entry;
            } else {
                prev.next = entry;
            }
            capacity -= oldValue.getSize();
        } else {
            oldValue = null;
            ++modCount;
            tab[index] = entry;
            entry.next = first;
            size++;
        }
        capacity += entry.getSize();
        return oldValue;

    }

    public AbstractCacheEntry<K, V> remove(Object key) {
        return remove(key, null);
    }

    public AbstractCacheEntry<K, V> remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        int hash = hash(key.hashCode());
        AbstractCacheEntry<K, V>[] tab = table;
        int index = hash & (tab.length - 1);
        AbstractCacheEntry<K, V> first = tab[index];
        AbstractCacheEntry<K, V> e = first;
        AbstractCacheEntry<K, V> prev = first;

        while (e != null) {
            AbstractCacheEntry<K, V> next = e.next;
            if (e.getHash() == hash && key.equals(e.getKey())) {
                if (value == null || value.equals(e.getValue())) {
                    modCount++;
                    size--;
                    capacity -= e.getSize();
                    if (prev == e)
                        table[index] = next;
                    else
                        prev.next = next;
                    e.entryRemoved();
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

    public long capacity() {
        return capacity;
    }

    /**
     * @see java.util.AbstractMap#values()
     */
    public Collection<V> values(Map<K, V> cache) {
        Collection<V> vs = values;
        if (isThreadSafe) {
            return (vs != null) ? vs : (values = new Values<K, V>(cache, this));
        } else {
            return (vs != null) ? vs
                    : (values = new ValuesSynchronized<K, V>(cache, this));
        }
    }

    /**
     * @param i
     */
    private void rehash() {
        AbstractCacheEntry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity >= MAXIMUM_CAPACITY)
            return;
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
        private AbstractCacheEntry<K, V> entry;

        private int expectedModCount;

        private int index;

        private AbstractCacheEntry<K, V> nextEntry;

        final EntryMap<K, ?> map;

        final Map<K, ?> cache;

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

        /**
         * @see java.util.Iterator#next()
         */
        public abstract E next();

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            if (entry == null)
                throw new IllegalStateException();
            checkForConcurrentMod();
            AbstractCacheEntry e = entry;
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
            if (e == null)
                throw new NoSuchElementException();
            nextEntry = nextEntry.next;
            if (nextEntry == null) {
                findNextBucket();
            }
            return e;
        }
    }

    static class EntrySetSynchronized<K, V> extends EntrySet<K, V> {
        private final Object mutex;

        EntrySetSynchronized(ConcurrentMap<K, V> cache, EntryMap<K, V> map,
                boolean copyEntries) {
            super(cache, map, copyEntries);
            this.mutex = cache;
        }

        /**
         * @see java.util.AbstractSet#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            synchronized (mutex) {
                return super.equals(o);
            }
        }

        /**
         * @see java.util.AbstractSet#hashCode()
         */
        @Override
        public int hashCode() {
            synchronized (mutex) {
                return super.hashCode();
            }
        }

        /**
         * @see java.util.AbstractSet#removeAll(java.util.Collection)
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            synchronized (mutex) {
                return super.removeAll(c);
            }
        }

        /**
         * @see java.util.AbstractCollection#containsAll(java.util.Collection)
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        /**
         * @see java.util.AbstractCollection#retainAll(java.util.Collection)
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            synchronized (mutex) {
                return super.retainAll(c);
            }
        }

        /**
         * @see java.util.AbstractCollection#toArray()
         */
        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        /**
         * @see java.util.AbstractCollection#toArray(T[])
         */
        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        /**
         * @see java.util.AbstractCollection#toString()
         */
        @Override
        public String toString() {
            synchronized (mutex) {
                return super.toString();
            }
        }

    }

    static class EntrySet<K, V> extends AbstractSet<CacheEntry<K, V>> {
        ConcurrentMap<K, V> cache;

        EntryMap<K, V> map;

        final boolean copyEntries;

        /* Whacked */
        final EntrySet<K, V> noCopySet;

        EntrySet(ConcurrentMap<K, V> cache, EntryMap<K, V> map, boolean copyEntries) {
            this.map = map;
            this.cache = cache;
            this.copyEntries = copyEntries;
            noCopySet = copyEntries ? new EntrySet(cache, map, false) : null;
        }

        @Override
        public void clear() {
            cache.clear();
        }

        @Override
        public boolean contains(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            AbstractCacheEntry<K, V> ace = map.get(e.getKey());
            V v = ace != null ? ace.getValue() : null;
            Object val = e.getValue();
            return v != null && (v == val || v.equals(val));
        }

        /**
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<CacheEntry<K, V>> iterator() {
            if (copyEntries) {
                return new EntrySetCopyIterator<K, V>(cache, map);
            } else {
                return (Iterator) new EntrySetIterator<K, V>(cache, map);
            }
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return cache.remove(e.getKey(), e.getValue());
        }

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return cache.size();
        }

        /**
         * @see java.util.AbstractSet#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            return noCopySet == null ? super.equals(o) : noCopySet.equals(o);
        }

        /**
         * @see java.util.AbstractSet#hashCode()
         */
        @Override
        public int hashCode() {
            return noCopySet == null ? super.hashCode() : noCopySet.hashCode();
        }

        /**
         * @see java.util.AbstractCollection#containsAll(java.util.Collection)
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            return noCopySet == null ? super.containsAll(c) : noCopySet.containsAll(c);
        }

        /**
         * @see java.util.AbstractCollection#retainAll(java.util.Collection)
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            return noCopySet == null ? super.retainAll(c) : noCopySet.retainAll(c);
        }

        /**
         * @see java.util.AbstractCollection#toArray()
         */
        @Override
        public Object[] toArray() {
            return noCopySet == null ? super.toArray() : noCopySet.toArray();
        }

        /**
         * @see java.util.AbstractCollection#toArray(T[])
         */
        @Override
        public <T> T[] toArray(T[] a) {
            return noCopySet == null ? super.toArray(a) : noCopySet.toArray(a);
        }

        /**
         * @see java.util.AbstractCollection#toString()
         */
        @Override
        public String toString() {
            return noCopySet == null ? super.toString() : noCopySet.toString();
        }

        /**
         * @see java.util.AbstractSet#removeAll(java.util.Collection)
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            return noCopySet == null ? super.removeAll(c) : noCopySet.removeAll(c);
        }
    }

    static class EntrySetIterator<K, V> extends
            BaseIterator<K, V, AbstractCacheEntry<K, V>> {
        EntrySetIterator(Map<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
        }

        @Override
        public AbstractCacheEntry<K, V> next() {
            return nextEntry();
        }
    }

    static class EntrySetCopyIterator<K, V> extends BaseIterator<K, V, CacheEntry<K, V>> {
        private final Map<K, V> cache;

        EntrySetCopyIterator(Map<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
            this.cache = cache;
        }

        @Override
        public CacheEntry<K, V> next() {
            return new ImmutableCacheEntry<K, V>(cache, nextEntry());
        }
    }

    static class KeyIterator<K, V> extends BaseIterator<K, V, K> {
        KeyIterator(Map<K, V> cache, EntryMap<K, ?> map) {
            super(cache, map);
        }

        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    static class KeySetSynchronized<K, V> extends KeySet<K, V> {
        private final Object mutex;

        KeySetSynchronized(ConcurrentMap<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
            this.mutex = cache;
        }

        /**
         * @see java.util.AbstractSet#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            synchronized (mutex) {
                return super.equals(o);
            }
        }

        /**
         * @see java.util.AbstractSet#hashCode()
         */
        @Override
        public int hashCode() {
            synchronized (mutex) {
                return super.hashCode();
            }
        }

        /**
         * @see java.util.AbstractSet#removeAll(java.util.Collection)
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            synchronized (mutex) {
                return super.removeAll(c);
            }
        }

        /**
         * @see java.util.AbstractCollection#containsAll(java.util.Collection)
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        /**
         * @see java.util.AbstractCollection#retainAll(java.util.Collection)
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            synchronized (mutex) {
                return super.retainAll(c);
            }
        }

        /**
         * @see java.util.AbstractCollection#toArray()
         */
        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        /**
         * @see java.util.AbstractCollection#toArray(T[])
         */
        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        /**
         * @see java.util.AbstractCollection#toString()
         */
        @Override
        public String toString() {
            synchronized (mutex) {
                return super.toString();
            }
        }

    }

    static class KeySet<K, V> extends AbstractSet<K> {
        final EntryMap<K, V> map;

        final Map<K, V> cache;

        KeySet(Map<K, V> cache, EntryMap<K, V> map) {
            this.map = map;
            this.cache = cache;
        }

        @Override
        public void clear() {
            cache.clear();
        }

        @Override
        public boolean contains(Object o) {
            return cache.containsKey(o);
        }

        /**
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator<K, V>(cache, map);
        }

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return cache.size();
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            return cache.remove(o) != null;
        }
    }

    static class ValueIterator<K, V> extends BaseIterator<K, V, V> {
        ValueIterator(Map<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
        }

        @Override
        public V next() {
            return nextEntry().getValue();
        }
    }

    static class ValuesSynchronized<K, V> extends Values<K, V> {
        final Object mutex;

        ValuesSynchronized(Map<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
            this.mutex = cache;
        }

        @Override
        public boolean remove(Object o) {
            synchronized (mutex) {
                return super.remove(o);
            }
        }

        /**
         * @see java.util.AbstractCollection#containsAll(java.util.Collection)
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        /**
         * @see java.util.AbstractCollection#retainAll(java.util.Collection)
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            synchronized (mutex) {
                return super.retainAll(c);
            }
        }

        /**
         * @see java.util.AbstractCollection#toArray()
         */
        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        /**
         * @see java.util.AbstractCollection#toArray(T[])
         */
        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        /**
         * @see java.util.AbstractCollection#toString()
         */
        @Override
        public String toString() {
            synchronized (mutex) {
                return super.toString();
            }
        }

        /**
         * @see org.coconut.cache.defaults.EntryMap.Values#removeAll(java.util.Collection)
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            synchronized (mutex) {
                return super.removeAll(c);
            }
        }
    }

    static class Values<K, V> extends AbstractCollection<V> {
        final EntryMap<K, V> map;

        final Map<K, V> cache;

        /**
         * @see java.util.AbstractCollection#removeAll(java.util.Collection)
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            if (c == null) {
                throw new NullPointerException("c is null");
            }
            return super.removeAll(c);
        }

        Values(Map<K, V> cache, EntryMap<K, V> map) {
            this.map = map;
            this.cache = cache;
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            return super.remove(o);
        }

        @Override
        public void clear() {
            cache.clear();
        }

        @Override
        public boolean contains(Object o) {
            return cache.containsValue(o);
        }

        /**
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator<K, V>(cache, map);
        }

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return cache.size();
        }
    }

}

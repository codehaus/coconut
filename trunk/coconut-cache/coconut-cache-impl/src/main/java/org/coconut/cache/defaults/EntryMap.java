/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class is partly adopted from ConcurrentHashMap.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EntryMap<K, V> {

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

    /**
     * The table is rehashed when its size exceeds this threshold. (The value of
     * this field is always (int)(capacity * loadFactor).)
     */
    int threshold;

    Set<K> keySet;

    Set<AbstractCacheEntry<K, V>> entrySet;

    Collection<V> values;

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
    public EntryMap() {
        this(DEFAULT_INITIAL_CAPACITY);
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
    public EntryMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
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
    public EntryMap(int initialCapacity, float loadFactor) {
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
    }

    /**
     * Return properly casted first entry of bin for given hash
     */
    private AbstractCacheEntry<K, V> getFirst(int hash) {
        AbstractCacheEntry[] tab = table;
        return (AbstractCacheEntry<K, V>) tab[hash & (tab.length - 1)];
    }

    public void clear() {
        if (size != 0) {
            modCount++;
            AbstractCacheEntry<K, V>[] tab = table;
            for (int i = 0; i < tab.length; i++) {
                tab[i] = null;
            }
            size = 0;
        }
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
        if (size != 0) { // read-volatile
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
    public Set<AbstractCacheEntry<K, V>> entrySet() {
        Set<AbstractCacheEntry<K, V>> es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet<K, V>(this));
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
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null) ? ks : (keySet = new KeySet<K>(this));
    }

    AbstractCacheEntry<K, V> put(AbstractCacheEntry<K, V> entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        if (elementAdded(entry)) {
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
            } else {
                oldValue = null;
                ++modCount;
                tab[index] = entry;
                entry.next = first;
                size++;
            }
            return oldValue;
        } else {
            remove(entry.getKey(),null);
            return null;
        }
    }

    protected boolean elementAdded(AbstractCacheEntry<K, V> entry) {
        return true;
    }

    public Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> putAll(
            Collection<? extends AbstractCacheEntry<K, V>> t) {
        Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> m = new HashMap<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>>();
        for (AbstractCacheEntry<K, V> e : t) {
            m.put(e, put(e));
        }
        return m;
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
        while (e != null && (e.getHash() != hash || !key.equals(e.getKey()))) {
            e = e.next;
            prev = e;
        }
        AbstractCacheEntry<K, V> oldValue = e;
        if (e != null) {
            V v = e.getValue();
            if (value == null || value.equals(v)) {
                ++modCount;
                if (prev == e) { // first entry
                    table[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                e.entryRemoved();
                size--;
            } else {
                return null;
            }
        }
        return oldValue;
    }

    public int size() {
        return size;
    }

    /**
     * @see java.util.AbstractMap#values()
     */
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new Values<V>(this));
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

    Iterator<AbstractCacheEntry<K, V>> newEntrySetIterator() {
        return new EntrySetIterator<K, V>(this);
    }

    Iterator<K> newKeyIterator() {
        return new KeyIterator<K, V>(this);
    }

    Iterator<V> newValueIterator() {
        return new ValueIterator<K, V>(this);
    }

    static abstract class BaseIterator<K, V, E> implements Iterator<E> {
        private AbstractCacheEntry<K, V> entry;

        private int expectedModCount;

        private int index;

        private AbstractCacheEntry<K, V> nextEntry;

        EntryMap<K, ?> map;

        BaseIterator(EntryMap<K, ?> map) {
            expectedModCount = map.modCount;
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
            map.remove(e.getKey(), null);
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

    protected AbstractCacheEntry<K, V> newEntry(K key, int hash,
            AbstractCacheEntry<K, V> next, V value) {
        return null;
    }

    static class EntrySet<K, V> extends AbstractSet<AbstractCacheEntry<K, V>> {
        EntryMap<K, V> map;

        EntrySet(EntryMap<K, V> map) {
            this.map = map;
        }

        @Override
        public void clear() {
            map.clear();
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
        public Iterator<AbstractCacheEntry<K, V>> iterator() {
            return map.newEntrySetIterator();
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return map.remove(e.getKey(), e.getValue()) != null;
        }

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return map.size();
        }
    }

    static class EntrySetIterator<K, V> extends
            BaseIterator<K, V, AbstractCacheEntry<K, V>> {
        EntrySetIterator(EntryMap<K, V> map) {
            super(map);
        }

        @Override
        public AbstractCacheEntry<K, V> next() {
            return nextEntry();
        }
    }

    static class KeyIterator<K, V> extends BaseIterator<K, V, K> {
        KeyIterator(EntryMap<K, ?> map) {
            super(map);
        }

        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    static class KeySet<K> extends AbstractSet<K> {
        EntryMap<K, ?> map;

        KeySet(EntryMap<K, ?> map) {
            this.map = map;
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public boolean contains(Object o) {
            return map.containsKey(o);
        }

        /**
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<K> iterator() {
            return map.newKeyIterator();
        }

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            return map.remove(o, null) != null;
        }
    }

    static class ValueIterator<K, V> extends BaseIterator<K, V, V> {
        ValueIterator(EntryMap<K, V> map) {
            super(map);
        }

        @Override
        public V next() {
            return nextEntry().getValue();
        }
    }

    static class Values<V> extends AbstractCollection<V> {
        EntryMap<?, V> map;

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

        Values(EntryMap<?, V> map) {
            this.map = map;
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
            map.clear();
        }

        @Override
        public boolean contains(Object o) {
            return map.containsValue(o);
        }

        /**
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<V> iterator() {
            return map.newValueIterator();
        }

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return map.size();
        }
    }
}

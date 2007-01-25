/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * This class is partly adopted from ConcurrentHashMap.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class OpenHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

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
    transient HashEntry<K, V>[] table;

    /**
     * The load factor for the hash table.
     */
    final float loadFactor;

    /**
     * Number of updates that alter the size of the table. This is used during
     * bulk-read methods to make sure they see a consistent snapshot.
     */
    transient int modCount;

    /**
     * The number of elements in this hash map.
     */
    transient int size;

    /**
     * The table is rehashed when its size exceeds this threshold. (The value of
     * this field is always (int)(capacity * loadFactor).)
     */
    transient int threshold;

    transient Set<K> keySet;

    transient Set<Map.Entry<K, V>> entrySet;

    transient Collection<V> values;

    /* ---------------- Small Utilities -------------- */

    /**
     * Applies a supplemental hash function to a given hashCode, which defends
     * against poor quality hash functions. This is critical because this class
     * uses power-of-two length hash tables, that otherwise encounter collisions
     * for hashCodes that do not differ in lower bits.
     */
    private static int hash(int h) {
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
    public OpenHashMap() {
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
    public OpenHashMap(int initialCapacity) {
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
    public OpenHashMap(int initialCapacity, float loadFactor) {
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
        table = new HashEntry[16];
    }

    /**
     * Return properly casted first entry of bin for given hash
     */
    private HashEntry<K, V> getFirst(int hash) {
        HashEntry[] tab = table;
        return (HashEntry<K, V>) tab[hash & (tab.length - 1)];
    }

    public void clear() {
        if (size != 0) {
            modCount++;
            HashEntry<K, V>[] tab = table;
            for (int i = 0; i < tab.length; i++) {
                tab[i] = null;
            }
            size = 0;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        // DONE
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        if (size != 0) {
            int hash = hash(key.hashCode());
            HashEntry<K, V> e = getFirst(hash);
            while (e != null) {
                if (e.hash == hash && key.equals(e.key)) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        // DONE
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (size != 0) { // read-volatile
            HashEntry<K, V>[] tab = table;
            int len = tab.length;
            for (int i = 0; i < len; i++) {
                for (HashEntry<K, V> e = tab[i]; e != null; e = e.next) {
                    V v = e.value;
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
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet<K, V>(this));
    }

    @Override
    public V get(Object key) {
        // DONE
        int hash = hash(key.hashCode());
        if (size != 0) {
            HashEntry<K, V> e = getFirst(hash);
            while (e != null) {
                if (e.hash == hash && key.equals(e.key)) {
                    return e.value;
                }
                e = e.next;
            }
        }
        return valueNotFound(key, hash);
    }

    V valueNotFound(Object key, int hash) {
        return null;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @see java.util.AbstractMap#keySet()
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null) ? ks : (keySet = new KeySet<K>(this));
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, false);
    }

    V put(K key, V value, boolean onlyIfAbsent) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        int hash = hash(key.hashCode());
        if (size + 1 > threshold) {
            // ensure capacity
            rehash();
        }
        HashEntry<K, V>[] tab = table;
        int index = hash & (tab.length - 1);
        HashEntry<K, V> first = tab[index];
        HashEntry<K, V> e = first;
        while (e != null && (e.hash != hash || !key.equals(e.key))) {
            e = e.next;
        }
        V oldValue;
        if (e != null) {
            oldValue = e.value;
            if (!onlyIfAbsent) {
                e.value = value;
            }
        } else {
            oldValue = null;
            ++modCount;
            tab[index] = newEntry(key, hash, first, value);
            size++;
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        // TODO only do one check for resize
        super.putAll(t);
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
    public V putIfAbsent(K key, V value) {
        return put(key, value, true);
    }

    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        int hash = hash(key.hashCode());
        return remove(key, hash, null);
    }

    public V remove(Object key, int hash, Object value) {
        HashEntry<K, V>[] tab = table;
        int index = hash & (tab.length - 1);
        HashEntry<K, V> first = tab[index];
        HashEntry<K, V> e = first;
        HashEntry<K, V> prev = first;
        while (e != null && (e.hash != hash || !key.equals(e.key))) {
            e = e.next;
            prev = e;
        }
        V oldValue = null;
        if (e != null) {
            V v = e.value;
            if (value == null || value.equals(v)) {
                oldValue = v;
                ++modCount;
                if (prev == e) { // first entry
                    table[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                e.entryRemoved(this);
                size--;
            }
        }
        return oldValue;
    }
    
    /**
     * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object,
     *      java.lang.Object)
     */
    public boolean remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        int hash = hash(key.hashCode());
        return remove(key, hash, value) != null;
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
     *      java.lang.Object)
     */
    public V replace(K key, V value) {
        // DONE
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }

        int hash = hash(key.hashCode());
        HashEntry<K, V> e = getFirst(hash);
        while (e != null && (e.hash != hash || !key.equals(e.key))) {
            e = e.next;
        }
        V oldValue = null;
        if (e != null) {
            oldValue = e.value;
            e.value = value;
        }
        return oldValue;

    }

    /**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (oldValue == null) {
            throw new NullPointerException("oldValue is null");
        } else if (newValue == null) {
            throw new NullPointerException("newValue is null");
        }
        int hash = hash(key.hashCode());
        HashEntry<K, V> e = getFirst(hash);
        while (e != null && (e.hash != hash || !key.equals(e.key))) {
            e = e.next;
        }
        
        boolean replaced = false;
        if (e != null && (e.value == oldValue || oldValue.equals(e.value))) {
            replaced = true;
            e.value = newValue;
        }
        return replaced;
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * @see java.util.AbstractMap#values()
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new Values<V>(this));
    }

    /**
     * @param i
     */
    private void rehash() {
        HashEntry<K,V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity >= MAXIMUM_CAPACITY)
            return;
        HashEntry<K,V>[] newTable = HashEntry.newArray(oldCapacity<<1);
        threshold = (int)(newTable.length * loadFactor);
        int sizeMask = newTable.length - 1;
        
        for (int i = 0; i < oldCapacity; i++) {
            HashEntry<K, V> e = oldTable[i];

            if (e != null) {
                oldTable[i] = null;
                do {
                    HashEntry<K, V> nextEntry = e.next;
                    int tableIndex = e.hash & sizeMask;
                    e.next = newTable[tableIndex];
                    newTable[tableIndex] = e;
                    e = nextEntry;
                } while (e != null);
            }
        }
        table = newTable;
    }

    Iterator<Map.Entry<K, V>> newEntrySetIterator() {
        return new EntrySetIterator<K, V>(this);
    }

    Iterator<K> newKeyIterator() {
        return new KeyIterator<K, V>(this);
    }

    Iterator<V> newValueIterator() {
        return new ValueIterator<K, V>(this);
    }

    static abstract class BaseIterator<K, V, E> implements Iterator<E> {
        private HashEntry<K, V> entry;

        private int expectedModCount;

        private int index;

        private HashEntry<K, V> nextEntry;

        OpenHashMap<K, ?> map;

        BaseIterator(OpenHashMap<K, ?> map) {
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
            HashEntry e = entry;
            entry = null;
            map.remove(e.key, e.value);
            expectedModCount = map.modCount;
        }

        private void findNextBucket() {
            HashEntry[] entries = map.table;
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

        HashEntry<K, V> nextEntry() {
            checkForConcurrentMod();
            HashEntry<K, V> e = nextEntry;
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

    protected HashEntry<K, V> newEntry(K key, int hash, HashEntry<K, V> next, V value) {
        return new HashEntry<K, V>(key, hash, next, value);
    }
    
    static class HashEntry<K, V> implements Map.Entry<K, V> {
        final int hash;

        final K key;

        HashEntry<K, V> next;

        V value;

        HashEntry(K key, int hash, HashEntry<K, V> next, V value) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry) o;
            Object key1 = getKey();
            Object key2 = e.getKey();
            if (key1 == key2 || (key1 != null && key1.equals(key2))) {
                Object value1 = getValue();
                Object value2 = e.getValue();
                if (value1 == value2 || (value1 != null && value1.equals(value2)))
                    return true;
            }
            return false;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final int hashCode() {
            return (key == null ? 0 : key.hashCode())
                    ^ (value == null ? 0 : value.hashCode());
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final String toString() {
            return key + " = " + value;
        }
        
        @SuppressWarnings("unchecked")
        static final <K,V> HashEntry<K,V>[] newArray(int i) {
            return new HashEntry[i];
        }
        
        /**
         * This method is invoked whenever the entry is removed from the table.
         */
        void entryRemoved(OpenHashMap<K, V> m) {
        }

        /**
         * This method is invoked whenever the value in an entry is overwritten
         * by an invocation of put(k,v) for a key k that's already in the
         * HashMap.
         */
        void entryValueUpdated(OpenHashMap<K, V> m) {
        }
    }

    static class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
        OpenHashMap<K, V> map;

        EntrySet(OpenHashMap<K, V> map) {
            this.map = map;
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            V v = map.get(e.getKey());
            Object val = e.getValue();
            return v != null && (v == val || v.equals(val));
        }

        /**
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return map.newEntrySetIterator();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return map.remove(e.getKey(), e.getValue());
        }

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return map.size();
        }
    }

    static class EntrySetIterator<K, V> extends BaseIterator<K, V, Map.Entry<K, V>> {
        EntrySetIterator(OpenHashMap<K, V> map) {
            super(map);
        }

        @Override
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    static class KeyIterator<K, V> extends BaseIterator<K, V, K> {
        KeyIterator(OpenHashMap<K, ?> map) {
            super(map);
        }

        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    static class KeySet<K> extends AbstractSet<K> {
        OpenHashMap<K, ?> map;

        KeySet(OpenHashMap<K, ?> map) {
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
            return map.remove(o) != null;
        }
    }

    static class ValueIterator<K, V> extends BaseIterator<K, V, V> {
        ValueIterator(OpenHashMap<K, V> map) {
            super(map);
        }

        @Override
        public V next() {
            return nextEntry().getValue();
        }
    }

    static class Values<V> extends AbstractCollection<V> {
        OpenHashMap<?, V> map;

        Values(OpenHashMap<?, V> map) {
            this.map = map;
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

/**
 * 
 */
package org.coconut.cache.pocket;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;

/**
 * <p>
 * This class makes no guarantees as to the which order entries are evicted to
 * make room for new entries. Currently this implementation uses a LRU (Least
 * Recently Used) replacement policy. However, this might change in future
 * releases.
 * <p>
 * This class is partly based on the ConcurrentHashMap provided by JSR 166.
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

public class UnsafePocketCache<K, V> extends AbstractMap<K, V> implements
        PocketCache<K, V> {
    /* ---------------- Constants -------------- */

    /**
     * The default initial capacity for this table, used when not otherwise
     * specified in a constructor.
     */
    static final int DEFAULT_CAPACITY = 1000;

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

    private int capacity;

    private int evictWatermark;

    /**
     * The head of the doubly linked list.
     */
    private HashEntry<K, V> header;

    private long hits;

    /** The value loader used for constructing new values. */
    private final ValueLoader<K, V> loader;

    private long misses;

    transient Set<Map.Entry<K, V>> entrySet;

    transient Set<K> keySet;

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
     * The table.
     */
    transient HashEntry<K, V>[] table;

    /**
     * The table is rehashed when its size exceeds this threshold. (The value of
     * this field is always (int)(capacity * loadFactor).)
     */
    transient int threshold;

    transient Collection<V> values;

    /* ---------------- Inner Classes -------------- */

    static abstract class BaseIterator<K, V, E> implements Iterator<E> {
        private HashEntry<K, V> entry;

        private int expectedModCount;

        private int index;

        private HashEntry<K, V> nextEntry;

        UnsafePocketCache<K, ?> map;

        BaseIterator(UnsafePocketCache<K, ?> map) {
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

    static class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
        UnsafePocketCache<K, V> map;

        EntrySet(UnsafePocketCache<K, V> map) {
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
        EntrySetIterator(UnsafePocketCache<K, V> map) {
            super(map);
        }

        @Override
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    static class HashEntry<K, V> implements Map.Entry<K, V> {
        @SuppressWarnings("unchecked")
        static final <K, V> HashEntry<K, V>[] newArray(int i) {
            return new HashEntry[i];
        }

        HashEntry<K, V> before, after, next;

        int flag;

        final int hash;

        final K key;

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

        void accessed(UnsafePocketCache<K, V> m) {
            m.modCount++;
            remove();
            addBefore(m.header);
        }

        /**
         * Inserts this entry before the specified existing entry in the list.
         */
        void addBefore(HashEntry<K, V> existingEntry) {
            after = existingEntry;
            before = existingEntry.before;
            before.after = this;
            after.before = this;
        }

        /**
         * This method is invoked whenever the entry is removed from the table.
         */
        void entryRemoved(UnsafePocketCache<K, V> m) {
        }

        /**
         * This method is invoked whenever the value in an entry is overwritten
         * by an invocation of put(k,v) for a key k that's already in the
         * HashMap.
         */
        void entryValueUpdated(UnsafePocketCache<K, V> m) {
        }

        /**
         * Removes this entry from the linked list.
         */
        void remove() {
            before.after = after;
            after.before = before;
        }
    }

    static class KeyIterator<K, V> extends BaseIterator<K, V, K> {
        KeyIterator(UnsafePocketCache<K, ?> map) {
            super(map);
        }

        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    static class KeySet<K> extends AbstractSet<K> {
        UnsafePocketCache<K, ?> map;

        KeySet(UnsafePocketCache<K, ?> map) {
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

        @Override
        public boolean remove(Object o) {
            return map.remove(o) != null;
        }

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return map.size();
        }
    }

    static class ValueIterator<K, V> extends BaseIterator<K, V, V> {
        ValueIterator(UnsafePocketCache<K, V> map) {
            super(map);
        }

        @Override
        public V next() {
            return nextEntry().getValue();
        }
    }

    static class Values<V> extends AbstractCollection<V> {
        UnsafePocketCache<?, V> map;

        Values(UnsafePocketCache<?, V> map) {
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

    /**
     * Constructs an empty <tt>UnsafePocketCache</tt> with the specified
     * loader, the default capacity (1000), and the default load factor (0.75).
     * 
     * @param loader
     *            the loader used for lazily construction missing values.
     * @throws NullPointerException
     *             if the specified loader is null
     */
    public UnsafePocketCache(ValueLoader<K, V> loader) {
        this(loader, DEFAULT_CAPACITY);
    }

    /**
     * Constructs an empty <tt>UnsafePocketCache</tt> with the specified
     * loader, capacity, and the default load factor (0.75).
     * 
     * @param loader
     *            the loader used for lazily construction missing values.
     * @param capacity
     *            the maximum number of entries allowed in the cache
     * @throws NullPointerException
     *             if the specified loader is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the capacity is nonpositive
     */
    public UnsafePocketCache(ValueLoader<K, V> loader, int capacity) {
        this(loader, capacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>UnsafePocketCache</tt> with the specified
     * loader, capacity, and load factor.
     * 
     * @param loader
     *            the loader used for lazily construction missing values.
     * @param capacity
     *            the maximum number of entries allowed in the cache
     * @param loadFactor
     *            the load factor threshold, used to control resizing. Resizing
     *            may be performed when the average number of elements per bin
     *            exceeds this threshold.
     * @throws IllegalArgumentException
     *             if the capacity or the load factor is nonpositive
     * @throws NullPointerException
     *             if the specified loader is <tt>null</tt>
     */
    public UnsafePocketCache(ValueLoader<K, V> loader, int capacity, float loadFactor) {
        if (loader == null) {
            throw new NullPointerException("loader is null");
        } else if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be >0, was " + capacity);
        } else if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("load factor must be >0, was "
                    + loadFactor);
        }
        if (capacity > MAXIMUM_CAPACITY) {
            capacity = MAXIMUM_CAPACITY;
        }
        int c = 1;
        while (c < (capacity / loadFactor))
            c <<= 1;

        this.loader = loader;
        this.loadFactor = loadFactor;
        this.capacity = capacity;
        threshold = (int) (c * loadFactor);
        table = new HashEntry[c];
        header = new HashEntry<K, V>(null, -1, null, null);
        header.before = header.after = header;
    }

    /**
     * @see java.util.AbstractMap#clear()
     */
    public void clear() {
        if (size != 0) {
            modCount++;
            HashEntry<K, V>[] tab = table;
            for (int i = 0; i < tab.length; i++) {
                tab[i] = null;
            }
            header.before = header.after = header;
            size = 0;
        }
    }

    /**
     * @see java.util.AbstractMap#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
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

    /**
     * @see java.util.AbstractMap#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
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

    /**
     * @see org.coconut.cache.pocket.PocketCache#evict()
     */
    public void evict() {
        trimToSize(evictWatermark);
    }

    /**
     * Evicts one entry and returns it.
     * 
     * @return the evicted entry or <tt>null</tt> if the cache was empy.
     */
    public Map.Entry<K, V> evictNext() {
        if (size >= 0) {
            ++modCount;
            HashEntry<K, V> e = header.after;
            remove(e.key);
            evicted(e);
            return e;
        }
        return null;
    }

    /**
     * @see java.util.AbstractMap#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
        int hash = hash(key.hashCode());
        if (size != 0) {
            HashEntry<K, V> e = getFirst(hash);
            while (e != null) {
                if (e.hash == hash && key.equals(e.key)) {
                    e.accessed(this);
                    hits++;
                    return e.value;
                }
                e = e.next;
            }
        }
        misses++;
        K k = (K) key;
        V v = loadValue(k);
        if (v != null) {
            put(k, v, false);
        }
        return v;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getAll(java.util.Collection)
     */
    public Map<K, V> getAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        HashMap<K, V> h = new HashMap<K, V>();
        for (K key : keys) {
            h.put(key, get(key));
        }
        return h;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getHardLimit()
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getDefaultTrimSize()
     */
    public int getEvictWatermark() {
        return evictWatermark;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getHitRatio()
     */
    public double getHitRatio() {
        return hits == 0 && misses == 0 ? Double.NaN : 100*((double) hits / (misses + hits));
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getNumberOfHits()
     */
    public long getNumberOfHits() {
        return hits;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#getNumberOfMisses()
     */
    public long getNumberOfMisses() {
        return misses;
    }

    /**
     * @see java.util.AbstractMap#isEmpty()
     */
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

    /**
     * @see org.coconut.cache.pocket.PocketCache#peek(java.lang.Object)
     */
    public V peek(Object key) {
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
        return null;
    }

    /**
     * @see java.util.AbstractMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(K key, V value) {
        return put(key, value, false);
    }

    /**
     * @see java.util.AbstractMap#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        int evictItems = (size + t.size()) - capacity;
        if (evictItems > 0) {
            trimToSize(Math.max(0, capacity - evictItems));
        }
        super.putAll(t);
    }

    // final HashEntry<K, V> getEntry(Object key, int hash) {
    // HashEntry<K, V> e = getFirst(hash);
    // while (e != null) {
    // if (e.hash == hash && key.equals(e.key)) {
    // return e;
    // }
    // e = e.next;
    // }
    // return null;
    // }

    /**
     * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
    public V putIfAbsent(K key, V value) {
        return put(key, value, true);
    }

    /**
     * @see java.util.AbstractMap#remove(java.lang.Object)
     */
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        int hash = hash(key.hashCode());
        return remove(key, hash, null);
    }

    private V remove(Object key, int hash, Object value) {
        HashEntry<K, V>[] tab = table;
        int index = hash & (tab.length - 1);
        HashEntry<K, V> first = tab[index];
        HashEntry<K, V> e = first;
        HashEntry<K, V> prev = first;
        while (e != null && (e.hash != hash || !key.equals(e.key))) {
            prev = e;
            e = e.next;
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
                e.remove();
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
        // DONE
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

    /**
     * @see org.coconut.cache.pocket.PocketCache#resetStatistics()
     */
    public void resetStatistics() {
        hits = 0;
        misses = 0;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#setCapacity(int)
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be >0, was " + capacity);
        }
        this.capacity = capacity;
        if (evictWatermark >= capacity) {
            evictWatermark = capacity - 1;
        }
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#setEvictWatermark(int)
     */
    public void setEvictWatermark(int trimSize) {
        // TODO Auto-generated method stub

    }

    /**
     * @see java.util.AbstractMap#size()
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * @see org.coconut.cache.pocket.PocketCache#trimToSize(int)
     */
    public void trimToSize(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("newSize must be >= 0, was " + newSize);
        }
        if (newSize >= size) {
            return;
        } else if (newSize == 0) {
            modCount++;
            for (HashEntry<K, V> e = header.after; e != header; e = e.after) {
                evicted(e);
            }
            clear();
        } else {
            int entries = size - newSize;
            HashEntry<K, V> entry = header.after;
            if (entries > table.length) {
                for (int i = 0; i < entries; i++) {
                    modCount++;
                    entry.flag = -1;
                    evicted(entry);
                    entry = entry.after;
                }
                HashEntry<K, V>[] tab = table;
                for (int i = 0; i < tab.length; i++) {
                    HashEntry<K, V> first = tab[i];
                    HashEntry<K, V> e = first;
                    HashEntry<K, V> prev = first;
                    while (e != null) {
                        if (e.flag == -1) {
                            if (prev == e) { // first entry
                                table[i] = e.next;
                            } else {
                                prev.next = e.next;
                            }
                            e.remove();
                            size--;
                        }
                        e = e.next;
                        prev = e;
                    }
                }
            } else {
                for (int i = 0; i < entries; i++) {
                    ++modCount;
                    remove(entry.key);
                    evicted(entry);
                    entry = entry.after;
                }
            }
        }
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
     * Return properly casted first entry of bin for given hash
     */
    private HashEntry<K, V> getFirst(int hash) {
        HashEntry[] tab = table;
        return (HashEntry<K, V>) tab[hash & (tab.length - 1)];
    }

    /**
     * @param i
     */
    private void rehash() {
        HashEntry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity >= MAXIMUM_CAPACITY)
            return;
        // TODO perhaps we should check with capacity
        HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
        threshold = (int) (newTable.length * loadFactor);
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

    /**
     * This can be overridden to provide custom handling for cases where the
     * cache is unable to find a mapping for a given key. This can be used, for
     * example, to provide a failfast behaviour if the cache is supposed to
     * contain a value for any given key.
     * 
     * <pre>
     * public class MyCacheImpl&lt;K, V&gt; extends AbstractCache&lt;K, V&gt; {
     *     protected V handleNullGet(K key) {
     *         throw new CacheRuntimeException(&quot;No value defined for Key [key=&quot; + key + &quot;]&quot;);
     *     }
     * }
     * </pre>
     * 
     * @param key
     *            the key for which no value could be found
     * @return <tt>null</tt> or any value that should be used instead
     */
    protected V handleNullGet(K key) {
        return null; // by default just return null
    }

    protected int itemsToEvictOnAdd() {
        return 1;
    }

    /**
     * Don't attempt to modify cache from within this method. Don't reuse the
     * entry.
     * 
     * @param entry
     */
    void evicted(Map.Entry<K, V> entry) {

    }

    protected V loadValue(K k) {
        V v = loader.load(k);
        if (v == null) {
            v = handleNullGet(k);
        }
        return v;
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

    V put(K key, V value, boolean onlyIfAbsent) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        int hash = hash(key.hashCode());
        if (size >= capacity) {
//            System.out.println("trying to insert " + key + ", " + value);
//            // System.out.println(size);
//            System.out.println("entries before insert: " + this);
            HashEntry<K, V> removed = header.after;

//            System.out.println("table before insert: " +Arrays.toString(table));
//            System.out.println("Removing: " + removed.key + ", " + removed.value);
//            if (key.equals(33)) {
//                System.out.println("Key:" + table[2].next.key);
//            }
            V v = remove(removed.key);
//            System.out.println("this after remove " + this);
//            System.out.println("table after remove " + Arrays.toString(table));
//            System.out.println(size());
//
//            if (v == null) {
//                throw new RuntimeException();
//            }
            evicted(removed);

        } else if (size >= threshold) {
            rehash(); // ensure capacity

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
            tab[index] = e = new HashEntry<K, V>(key, hash, first, value);
            e.addBefore(header);
            size++;
        }
//
//        System.out.println(Arrays.toString(table));
//        System.out.println(this);
//        System.out.println(size());
//        System.out.println("-------------");
//
        return oldValue;
    }
}

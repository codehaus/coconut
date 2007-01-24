/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults.memory;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
// TODO convert to Open HashMap
public abstract class CacheEntryMap<K, V> {

    /**
     * Default number of buckets.Must be a power of two.
     */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * The default load factor; this is explicitly specified by the spec for
     * HashMap.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The maximum capacity, used if a higher value is implicitly specified by
     * either of the constructors with arguments.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor for the hash table.
     */
    final float loadFactor;

    /**
     * This field is used to make iterators on Collection-views of the HashMap
     * fail-fast. (See ConcurrentModificationException).
     */
    transient volatile int modCount;

    /** The number of key-value pairs. */
    transient int size;

    /**
     * Array containing the actual key-value mappings. Length MUST Always be a
     * power of two.
     */
    transient AbstractCacheEntry<K, V>[] table;

    /**
     * The next size value at which to resize (capacity * load factor).
     */
    int threshold;

    /**
     * Construct a new CacheEntryMap with the default capacity (16) and the
     * default load factor (0.75).
     */
    public CacheEntryMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public CacheEntryMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public CacheEntryMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal capacity, was " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (!(loadFactor > 0)) // check for NaN too
            throw new IllegalArgumentException("Illegal load, was " + loadFactor);

        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        threshold = (int) (capacity * loadFactor);
        table = (AbstractCacheEntry<K, V>[]) new AbstractCacheEntry[capacity];
        this.loadFactor = loadFactor;
        threshold = (int) (capacity * loadFactor);
    }

    public void clear() {
        modCount++;
        Map.Entry[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }

    public boolean containsKey(Object key) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        AbstractCacheEntry<K, V> e = table[i];
        while (e != null) {
            if (e.getHash() == hash && eq(key, e.getKey()))
                return true;
            e = (AbstractCacheEntry<K, V>) e.getNext();
        }
        return false;
    }

    public Set<AbstractCacheEntry<K, V>> entrySet() {
        return (Set) new EntrySet();
    }

    public AbstractCacheEntry<K, V> get(Object key) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        AbstractCacheEntry<K, V> e = table[i];
        while (e != null && !(e.getHash() == hash && eq(key, e.getKey())))
            e = (AbstractCacheEntry<K, V>) e.getNext();
        return e;
    }

    public V getValue(Object key) {
        AbstractCacheEntry<K, V> entry = CacheEntryMap.this.get(key);
        return entry == null ? null : entry.value;
    }

    public Set<K> keySet() {
        return new KeySet();
    }

    public AbstractCacheEntry<K, V> put(K key, AbstractCacheEntry<K, V> value) {
        return put(key, value, true);
    }

    public AbstractCacheEntry<K, V> put(K key, AbstractCacheEntry<K, V> value,
            boolean useModCount) {
        int i = indexFor(value.hash, table.length);
        AbstractCacheEntry<K, V> prev = null;
        for (AbstractCacheEntry<K, V> e = table[i]; e != null; e = (AbstractCacheEntry<K, V>) e
                .getNext()) {
            if (e.getHash() == value.hash && eq(value.getKey(), e.getKey())) {
                value.next = e.next;
                if (prev == null) {
                    table[i] = value;
                } else {
                    prev.next = value;
                }
                return e;
            }
            prev = e;
        }
        // new entry, did not replace existing value
        if (elementAdded(value)) {
            if (useModCount) {
                modCount++;
            }
            // check evict
            addEntry(value, i);
        }
        return null;
    }

    public Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> putAllValues(
            Collection<AbstractCacheEntry<K, V>> m) {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0) {
            return Collections.emptyMap();
        }
        if (numKeysToBeAdded > threshold) {
            int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length)
                resize(newCapacity);
        }
        Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> prevValues = new HashMap<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>>();
        for (AbstractCacheEntry<K, V> mm : m) {
            AbstractCacheEntry<K, V> prev = put(mm.key, mm);
            prevValues.put(mm, prev);
        }
        return prevValues;
    }

    /**
     * Removes and returns the entry associated with the specified key in the
     * HashMap. Returns null if the HashMap contains no mapping for this key.
     */
    public AbstractCacheEntry<K, V> remove(Object key) {
        return remove(key, true);
    }

    public AbstractCacheEntry<K, V> remove(Object key, boolean useModCount) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        AbstractCacheEntry<K, V> prev = table[i];
        AbstractCacheEntry<K, V> e = prev;

        while (e != null) {
            AbstractCacheEntry<K, V> next = (AbstractCacheEntry<K, V>) e.next;
            if (e.hash == hash && eq(key, e.key)) {
                if (useModCount) {
                    modCount++;
                }
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.entryRemoved();
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        return size;
    }

    public boolean valueContainsValue(Object value) {
        AbstractCacheEntry<K, V>[] tab = table;
        for (int i = 0; i < tab.length; i++)
            for (AbstractCacheEntry<K, V> e = tab[i]; e != null; e = (AbstractCacheEntry<K, V>) e
                    .getNext()) {
                if (value.equals(e.value))
                    return true;
            }
        return false;
    }

    public Set<Map.Entry<K, V>> valueEntrySet() {
        return new EntrySet();
    }

    public Set<AbstractCacheEntry<K, V>> values() {
        return (Set) new EntrySet();
    }

    public Collection<V> valueValues() {
        return new ValueSet();
    }

    private void addEntry(AbstractCacheEntry<K, V> value, int i) {
        AbstractCacheEntry<K, V> next = table[i];
        table[i] = value;
        value.next = next;
        if (size++ >= threshold)
            resize(2 * table.length);

    }

    protected boolean elementAdded(AbstractCacheEntry<K, V> entry) {
        return true;
    }

    /**
     * Special version of remove for EntrySet.
     */
    Map.Entry<K, V> removeMapping(Object o) {
        if (!(o instanceof Map.Entry))
            return null;

        Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
        int hash = hash(entry.getKey());
        int i = indexFor(hash, table.length);
        AbstractCacheEntry<K, V> prev = table[i];
        AbstractCacheEntry<K, V> e = prev;

        while (e != null) {
            AbstractCacheEntry<K, V> next = (AbstractCacheEntry<K, V>) e.next;
            if (e.hash == hash && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.entryRemoved();
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    void resize(int newCapacity) {
        Map.Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        AbstractCacheEntry<K, V>[] newTable = (AbstractCacheEntry<K, V>[]) new Map.Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * Transfer all entries from current table to newTable.
     */
    void transfer(AbstractCacheEntry<K, V>[] newTable) {
        AbstractCacheEntry<K, V>[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            AbstractCacheEntry<K, V> e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    AbstractCacheEntry<K, V> next = (AbstractCacheEntry<K, V>) e
                            .getNext();
                    int i = indexFor(e.getHash(), newCapacity);
                    e.setNext(newTable[i]);
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }

    private static int hash(Object x) {
        int h = x.hashCode();
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        return h;
    }

    /**
     * Check for equality of non-null reference x and possibly-null y.
     */
    static boolean eq(Object x, Object y) {
        return x == y || x.equals(y);
    }

    /**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length) {
        return h & (length - 1);
    }

    /**
     * KeySet iterator.
     */
    protected class EntrySetIterator extends HashIterator<Map.Entry<K, V>> {
        public Map.Entry<K, V> next() {
            return super.nextEntry();
        }
    }

    /**
     * Base Iterator
     */
    protected abstract class HashIterator<E> implements Iterator<E> {

        /** The modification count expected */
        protected int expectedModCount;

        /** The current index into the array of buckets */
        protected int hashIndex;

        /** The last returned entry */
        protected AbstractCacheEntry<K, V> last;

        /** The next entry */
        protected AbstractCacheEntry<K, V> next;

        protected HashIterator() {
            int i = table.length;
            AbstractCacheEntry<K, V> next = null;
            while (i > 0 && next == null) {
                next = table[--i];
            }
            this.next = next;
            this.hashIndex = i;
            this.expectedModCount = modCount;
        }

        public boolean hasNext() {
            return (next != null);
        }

        public boolean isEntryValid(Map.Entry n) {
            return true;
        }

        public void remove() {
            if (last == null) {
                throw new IllegalStateException(
                        "remove() can only be called once after next()");
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            CacheEntryMap.this.remove(last.getKey()); // writelocked
            last = null;
            expectedModCount = modCount;
        }

        public String toString() {
            if (last != null) {
                return "Iterator[" + last.getKey() + "=" + last.getValue() + "]";
            } else {
                return "Iterator[]";
            }
        }

        protected Map.Entry currentEntry() {
            return last;
        }

        protected AbstractCacheEntry<K, V> nextEntry() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            AbstractCacheEntry<K, V> newCurrent = next;
            if (newCurrent == null) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            int i = hashIndex;
            AbstractCacheEntry<K, V> n = newCurrent.next;
            while ((n == null || !isEntryValid(n)) && i > 0) {
                n = table[--i];
            }
            next = n;
            hashIndex = i;
            last = newCurrent;
            return newCurrent;
        }
    }

    /**
     * EntrySet implementation.
     */
    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        public void clear() {
            CacheEntryMap.this.clear();
        }

        public boolean contains(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            AbstractCacheEntry<K, V> m = CacheEntryMap.this.get(e.getKey());
            return m != null && m.value.equals(e.getValue());
        }

        public Iterator<Map.Entry<K, V>> iterator() {
            return newEntrySetIterator();
        }

        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            return CacheEntryMap.this.removeMapping(o) != null;
        }

        public int size() {
            return CacheEntryMap.this.size();
        }
    }

    protected Iterator<Map.Entry<K, V>> newEntrySetIterator() {
        return new EntrySetIterator();
    }

    protected Iterator<K> newKeySetIterator() {
        return new KeySetIterator();
    }

    protected Iterator<V> newValueSetIterator() {
        return new ValueSetIterator();
    }

    /**
     * KeySet implementation.
     */
    final class KeySet extends AbstractSet<K> {
        public void clear() {
            CacheEntryMap.this.clear();
        }

        public boolean contains(Object value) {
            return CacheEntryMap.this.containsKey(value);
        }

        public Iterator<K> iterator() {
            return newKeySetIterator();
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            return CacheEntryMap.this.remove(o) != null;
        }

        public int size() {
            return CacheEntryMap.this.size();
        }
    }

    /**
     * KeySet iterator.
     */
    final class KeySetIterator extends HashIterator<K> {
        public K next() {
            return super.nextEntry().getKey();
        }
    }

    /**
     * ValueSet iterator.
     */
    final class MSetIterator extends HashIterator<AbstractCacheEntry<K, V>> {
        public AbstractCacheEntry<K, V> next() {
            return (AbstractCacheEntry<K, V>) super.nextEntry();
        }
    }

    public boolean containsValue(Object value) {
        Iterator<AbstractCacheEntry<K, V>> i = entrySet().iterator();
        if (value == null) {
            while (i.hasNext()) {
                AbstractCacheEntry<K, V> e = i.next();
                if (e.getValue() == null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                AbstractCacheEntry<K, V> e = i.next();
                if (value.equals(e.getValue()))
                    return true;
            }
        }
        return false;
    }

    /**
     * ValueSet implementation.
     */
    final class ValueSet extends AbstractSet<V> {
        public void clear() {
            CacheEntryMap.this.clear();
        }

        public boolean contains(Object value) {
            if (value == null) {
                throw new NullPointerException("value is null");
            }
            return CacheEntryMap.this.containsValue(value);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            if (c == null) {
                throw new NullPointerException("c is null");
            }
            return super.containsAll(c);
        }

        public Iterator<V> iterator() {
            return newValueSetIterator();
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("o is null");
            }
            return super.remove(o);
        }

        public int size() {
            return CacheEntryMap.this.size();
        }
    }

    /**
     * ValueSet iterator.
     */
    final class ValueSetIterator extends HashIterator<V> {
        public V next() {
            return super.nextEntry().value;
        }
    }

}

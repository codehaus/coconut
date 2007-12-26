/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Various collection utility functions.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class CollectionUtils {
    // /CLOVER:OFF
    /** Cannot instantiate. */
    private CollectionUtils() {}

    // /CLOVER:ON

    /**
     * Checks whether or not the specified collection contains a <code>null</code>.
     *
     * @param col
     *            the collection to check
     * @throws NullPointerException
     *             if the specified collection contains a null
     */
    public static void checkCollectionForNulls(Collection<?> col) {
        for (Object entry : col) {
            if (entry == null) {
                throw new NullPointerException("collection contains a null entry");
            }
        }
    }

    /**
     * Checks whether or not the specified map contains a null key or value
     * <code>null</code>.
     *
     * @param map
     *            the map to check
     * @throws NullPointerException
     *             if the specified map contains a null key or value
     */
    public static void checkMapForNulls(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException("map contains a null key");
            } else if (entry.getValue() == null) {
                throw new NullPointerException("map contains a null value for key = "
                        + entry.getKey());
            }
        }
    }

    /**
     * Returns true if the specified arguments are equal, or both null.
     */
    public static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public static class ImmutableCollection<E> implements Collection<E>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -4490792944315035867L;

        /** The collection we are wrapping. */
        final Collection<? extends E> col;

        public ImmutableCollection(Collection<? extends E> col) {
            if (col == null) {
                throw new NullPointerException("col is null");
            }
            this.col = col;
        }

        /** {@inheritDoc} */
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean addAll(Collection<? extends E> coll) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean contains(Object o) {
            return col.contains(o);
        }

        /** {@inheritDoc} */
        public boolean containsAll(Collection<?> coll) {
            return col.containsAll(coll);
        }

        /** {@inheritDoc} */
        public boolean isEmpty() {
            return col.isEmpty();
        }

        /** {@inheritDoc} */
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                Iterator<? extends E> i = col.iterator();

                /** {@inheritDoc} */
                public boolean hasNext() {
                    return i.hasNext();
                }

                /** {@inheritDoc} */
                public E next() {
                    return i.next();
                }

                /** {@inheritDoc} */
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        /** {@inheritDoc} */
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean removeAll(Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean retainAll(Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public int size() {
            return col.size();
        }

        /** {@inheritDoc} */
        public Object[] toArray() {
            return col.toArray();
        }

        /** {@inheritDoc} */
        public <T> T[] toArray(T[] a) {
            return col.toArray(a);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return col.toString();
        }
    }

    public static class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E>,
            Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -7936489435110973730L;

        public ImmutableSet(Set<? extends E> set) {
            super(set);
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            return o == this || col.equals(o);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return col.hashCode();
        }
    }

    /**
     * An Entry maintaining an immutable key and value. This class does not support method
     * <tt>setValue</tt>. This class may be convenient in methods that return
     * thread-safe snapshots of key-value mappings.
     */
    public static class SimpleImmutableEntry<K, V> implements Entry<K, V>, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -354750390197347279L;

        /** The key of the entry. */
        private final K key;

        /** The value of the entry. */
        private final V value;

        /**
         * Creates an entry representing the same mapping as the specified entry.
         *
         * @param entry
         *            the entry to copy
         */
        public SimpleImmutableEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        /**
         * Creates an entry representing a mapping from the specified key to the specified
         * value.
         *
         * @param key
         *            the key represented by this entry
         * @param value
         *            the value represented by this entry
         */
        public SimpleImmutableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry) o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        /** {@inheritDoc} */
        public K getKey() {
            return key;
        }

        /** {@inheritDoc} */
        public V getValue() {
            return value;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        /** {@inheritDoc} */
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    public static class ImmutableEntrySet<K, V> extends ImmutableSet<Map.Entry<K, V>> {
        private static final long serialVersionUID = 7854390611657943733L;

        public ImmutableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> s) {
            super((Set) s);
        }

        /**
         * This method is overridden to protect the backing set against an object with a
         * nefarious equals function that senses that the equality-candidate is Map.Entry
         * and calls its setValue method.
         */
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            return col.contains(new SimpleImmutableEntry<K, V>((Map.Entry<K, V>) o));
        }

        /**
         * The next two methods are overridden to protect against an unscrupulous List
         * whose contains(Object o) method senses when o is a Map.Entry, and calls
         * o.setValue.
         */
        @Override
        public boolean containsAll(Collection<?> coll) {
            Iterator<?> e = coll.iterator();
            while (e.hasNext()) {
                if (!contains(e.next())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Set)) {
                return false;
            }
            Set s = (Set) o;
            if (s.size() != col.size()) {
                return false;
            }
            return containsAll(s); // Invokes safe containsAll() above
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() {
                Iterator<? extends Map.Entry<? extends K, ? extends V>> i = col.iterator();

                public boolean hasNext() {
                    return i.hasNext();
                }

                public Map.Entry<K, V> next() {
                    return new SimpleImmutableEntry<K, V>(i.next());
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public Object[] toArray() {
            Object[] a = col.toArray();
            for (int i = 0; i < a.length; i++) {
                a[i] = new SimpleImmutableEntry<K, V>((Map.Entry<K, V>) a[i]);
            }
            return a;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            // We don't pass a to c.toArray, to avoid window of
            // vulnerability wherein an unscrupulous multithreaded client
            // could get his hands on raw (unwrapped) Entries from c.
            Object[] arr = col.toArray(a.length == 0 ? a : ArrayUtils.copyOf(a, 0));

            for (int i = 0; i < arr.length; i++) {
                arr[i] = new SimpleImmutableEntry<K, V>((Map.Entry<K, V>) arr[i]);
            }

            if (arr.length > a.length) {
                return (T[]) arr;
            }

            System.arraycopy(arr, 0, a, 0, arr.length);
            if (a.length > arr.length) {
                a[arr.length] = null;
            }
            return a;
        }
    }

}

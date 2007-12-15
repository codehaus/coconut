/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
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
        return (o1 == null ? o2 == null : o1.equals(o2));
    }

    /**
     * An Entry maintaining an immutable key and value. This class does not support method
     * <tt>setValue</tt>. This class may be convenient in methods that return
     * thread-safe snapshots of key-value mappings.
     */
    public static class SimpleImmutableEntry<K, V> implements Entry<K, V>, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -354750390197347279L;

        private final K key;

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
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
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
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        /** {@inheritDoc} */
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public String toString() {
            return key + "=" + value;
        }
    }

}

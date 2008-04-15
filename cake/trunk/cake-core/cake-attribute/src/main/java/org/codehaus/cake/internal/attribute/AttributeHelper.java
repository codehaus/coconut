package org.codehaus.cake.internal.attribute;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

public class AttributeHelper {
    /**
     * An Entry maintaining an immutable key and value. This class does not support method
     * <tt>setValue</tt>. This class may be convenient in methods that return thread-safe
     * snapshots of key-value mappings.
     */
    public static class SimpleImmutableEntry<K, V> implements Entry<K, V>, Serializable {

        /** serialVersionUID. */
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
         * Creates an entry representing a mapping from the specified key to the specified value.
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

    /**
     * Returns true if the specified arguments are equal, or both null.
     */
    public static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }
}

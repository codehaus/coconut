/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MapUtils {

    public static <K, V> Map.Entry<K, V> newMapEntry(K key, V value) {
        return new ImmutableMapEntry<K, V>(key, value);
    }

    public static <K, V> Map.Entry<K, V> newMapEntry(Map.Entry<K, V> entry) {
        return new ImmutableMapEntry<K, V>(entry);
    }
    

    public static final class ImmutableMapEntry<K, V> implements Entry<K, V>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -4374349503886412002L;


        /** The key for this entry. */
        private final K key;

        /** The value for this entry. */
        private final V value;

        public ImmutableMapEntry(Entry<K, V> entry) {
            this.value = entry.getValue();
            this.key = entry.getKey();
        }
        
        /**
         * Creates a new MapEntry.
         * 
         * @param key
         *            the key
         * @param value
         *            the value
         */
        public ImmutableMapEntry(final K key, final V value) {
            this.value = value;
            this.key = key;
        }

        public static <K, V> Entry<K, V> from(final K key, final V value) {
            return new ImmutableMapEntry<K, V>(key, value);
        }
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o != null && o instanceof Entry) {
                Entry e = (Entry) o;
                Object k = e.getKey();
                if (key == k || (key != null && key.equals(k))) {
                    Object v = e.getValue();
                    return (value == v || (value != null && value.equals(v)));
                }
            }
            return false;
        }

        /**
         * @see java.util.Map.Entry#getKey()
         */
        public K getKey() {
            return key;
        }


        public V getValue() {
            return value;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode())
                    ^ (value == null ? 0 : value.hashCode());
        }

        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }
    }
}

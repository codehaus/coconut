/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.test;

import java.util.Map.Entry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public final class ImmutableMapEntry<K, V> implements Entry<K, V> {

    /** The value for this entry. */
    private final V value;

    /** The key for this entry. */
    private final K key;

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

    public ImmutableMapEntry(Entry<K, V> entry) {
        this.value = entry.getValue();
        this.key = entry.getKey();
    }

    public static <K, V> Entry<K, V> from(final K key, final V value) {
        return new ImmutableMapEntry<K, V>(key, value);
    }

    /**
     * @see java.util.Map$Entry#getKey()
     */
    public K getKey() {
        return key;
    }

    /**
     * @see java.util.Map$Entry#getDouble()
     */
    public V getValue() {
        return value;
    }

    /**
     * @see java.util.Map$Entry#setValue(V)
     */
    public V setValue(V value) {
        throw new UnsupportedOperationException();
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode())
                ^ (value == null ? 0 : value.hashCode());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}

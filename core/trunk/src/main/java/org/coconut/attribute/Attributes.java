/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

import static org.coconut.internal.util.CollectionUtils.eq;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.coconut.internal.util.CollectionUtils.SimpleImmutableEntry;

/**
 * Contains various utility methods for a {@link AttributeMap}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributeMaps.java 472 2007-11-19 09:34:26Z kasper $
 */
public final class Attributes {

    /** The empty attribute map (immutable). This attribute map is serializable. */
    public final static AttributeMap EMPTY_MAP = new EmptyMap();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Attributes() {}

    // /CLOVER:ON

    /**
     * Returns an immutable AttributeMap containing only the specified attribute mapping
     * to the specified value.
     * 
     * @param attribute
     *            the attribute to map from
     * @param value
     *            the value to map to
     * @return a singleton attribute map
     * @param <T>
     *            the containing type of the attribute
     * @throws NullPointerException
     *             if the specified attribute is null
     */
    public static <T> AttributeMap singleton(Attribute<T> attribute, T value) {
        return new SingletonAttributeMap(attribute, value);
    }

    /**
     * Creates a new {@link Map} where all the specified keys maps to the specified
     * AttributeMap.
     * 
     * @param <K>
     *            the type of keys
     * @param keys
     *            the collection of keys that should map to the specified AttributeMap
     * @param attributeMap
     *            the AttributeMap that all the specified keys must map to
     * @return a new Map where all the specified keys maps to the specified AttributeMap
     */
    public static <K> Map<K, AttributeMap> toMap(Collection<? extends K> keys,
            AttributeMap attributeMap) {
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (K key : keys) {
            map.put(key, attributeMap);
        }
        return map;
    }

    /**
     * The default implementation of an immutable empty {@link AttributeMap}.
     */
    static final class EmptyMap extends AbstractMap<Attribute, Object> implements AttributeMap,
            Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -3037602713439417782L;

        /** {@inheritDoc} */
        public boolean containsKey(Object key) {
            return false;
        }

        /** {@inheritDoc} */
        public boolean containsValue(Object value) {
            return false;
        }

        /** {@inheritDoc} */
        public Set<Map.Entry<Attribute, Object>> entrySet() {
            return Collections.emptySet();
        }

        /** {@inheritDoc} */
        public boolean equals(Object o) {
            return (o instanceof Map) && ((Map) o).size() == 0;
        }

        /** {@inheritDoc} */
        public Object get(Object key) {
            return null;
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key) {
            return false;
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key, boolean defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key) {
            return 0;
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key, byte defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key) {
            return Character.MIN_VALUE;
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key, char defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key) {
            return 0;
        }

        /** {@inheritDoc} */
        public Object get(Attribute key, Object defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key, double defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key) {
            return 0f;
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key, float defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key) {
            return 0;
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key, int defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key) {
            return 0;
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key, long defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key) {
            return 0;
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key, short defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public int hashCode() {
            return 0;
        }

        /** {@inheritDoc} */
        public boolean isEmpty() {
            return true;
        }

        /** {@inheritDoc} */
        public Set<Attribute> keySet() {
            return Collections.<Attribute> emptySet();
        }

        /** {@inheritDoc} */
        public void putBoolean(Attribute key, boolean value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putByte(Attribute key, byte value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putChar(Attribute key, char value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putDouble(Attribute key, double value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putFloat(Attribute key, float value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putInt(Attribute key, int value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putLong(Attribute key, long value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putShort(Attribute key, short value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public int size() {
            return 0;
        }

        /** {@inheritDoc} */
        public Collection<Object> values() {
            return Collections.<Object> emptySet();
        }

        /**
         * Preserves singleton property.
         * 
         * @return the empty map
         */
        private Object readResolve() {
            return EMPTY_MAP;
        }
    }

    static class SingletonAttributeMap implements AttributeMap, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6979724477215052911L;

        /** The singleton key. */
        private final Attribute a;

        /** The singleton value. */
        private final Object v;

        SingletonAttributeMap(Attribute attribute, Object value) {
            if (attribute == null) {
                throw new NullPointerException("attribute is null");
            }
            a = attribute;
            v = value;
        }

        /** {@inheritDoc} */
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean containsKey(Object key) {
            return eq(key, a);
        }

        /** {@inheritDoc} */
        public boolean containsValue(Object value) {
            return eq(value, v);
        }

        /** {@inheritDoc} */
        public Set<Map.Entry<Attribute, Object>> entrySet() {
            return Collections
                    .<Map.Entry<Attribute, Object>> singleton(new SimpleImmutableEntry<Attribute, Object>(
                            a, v));
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof Map))
                return false;
            Map<Attribute, Object> map = (Map<Attribute, Object>) o;
            if (map.size() != size())
                return false;
            try {
                if (v == null) {
                    if (!(map.get(a) == null && map.containsKey(a)))
                        return false;
                } else {
                    if (!v.equals(map.get(a)))
                        return false;
                }
            } catch (ClassCastException unused) {
                return false;
            }
            return true;
        }

        /** {@inheritDoc} */
        public Object get(Attribute key, Object defaultValue) {
            return a == key ? v : defaultValue;
        }

        /** {@inheritDoc} */
        public Object get(Object key) {
            return (eq(key, a) ? v : null);
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key) {
            return getBoolean(key, false);
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key, boolean defaultValue) {
            return a == key ? v == null ? false : (Boolean) v : defaultValue;
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key) {
            return getByte(key, (byte) 0);
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key, byte defaultValue) {
            return a == key ? v == null ? 0 : (Byte) v : defaultValue;
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key) {
            return getChar(key, (char) 0);
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key, char defaultValue) {
            return a == key ? v == null ? 0 : (Character) v : defaultValue;
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key) {
            return getDouble(key, 0);
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key, double defaultValue) {
            return a == key ? v == null ? 0 : (Double) v : defaultValue;
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key) {
            return getFloat(key, 0);
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key, float defaultValue) {
            return a == key ? v == null ? 0 : (Float) v : defaultValue;
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key) {
            return getInt(key, 0);
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key, int defaultValue) {
            return a == key ? v == null ? 0 : (Integer) v : defaultValue;
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key) {
            return getLong(key, 0);
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key, long defaultValue) {
            return a == key ? v == null ? 0 : (Long) v : defaultValue;
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key) {
            return getShort(key, (short) 0);
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key, short defaultValue) {
            return a == key ? v == null ? 0 : (Short) v : defaultValue;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return a.hashCode() ^ (v == null ? 0 : v.hashCode());
        }

        /** {@inheritDoc} */
        public boolean isEmpty() {
            return false;
        }

        /** {@inheritDoc} */
        public Set<Attribute> keySet() {
            return Collections.singleton(a);
        }

        /** {@inheritDoc} */
        public Object put(Attribute key, Object value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putAll(Map<? extends Attribute, ? extends Object> t) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putBoolean(Attribute key, boolean value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putByte(Attribute key, byte value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putChar(Attribute key, char value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putDouble(Attribute key, double value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putFloat(Attribute key, float value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putInt(Attribute key, int value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putLong(Attribute key, long value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putShort(Attribute key, short value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public int size() {
            return 1;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("{");
            buf.append(a);
            buf.append("=");
            buf.append(v);
            buf.append("}");
            return buf.toString();
        }

        /** {@inheritDoc} */
        public Collection<Object> values() {
            return Collections.singleton(v);
        }
    }

}

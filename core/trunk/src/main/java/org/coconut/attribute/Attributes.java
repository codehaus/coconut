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

import org.coconut.internal.util.CollectionUtils;
import org.coconut.internal.util.CollectionUtils.SimpleImmutableEntry;

/**
 * Contains various utility methods for a {@link AttributeMap}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributeMaps.java 472 2007-11-19 09:34:26Z kasper $
 */
public final class Attributes {

    /** The empty attribute map (immutable). This attribute map is serializable. */
    public final static AttributeMap EMPTY_ATTRIBUTE_MAP = new EmptyAttributeMap();

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
     * Creates a new {@link Map} where all the specified keys maps to {@link #EMPTY_ATTRIBUTE_MAP}.
     *
     * @param <K>
     *            the type of keys
     * @param keys
     *            the collection of keys that should map to the empty AttributeMap
     * @return a new Map where all the specified keys maps to an empty AttributeMap
     */
    public static <K> Map<K, AttributeMap> toMap(Collection<? extends K> keys) {
        return toMap(keys, EMPTY_ATTRIBUTE_MAP);
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
     * Returns an unmodifiable view of the specified attribute map. This method allows
     * modules to provide users with "read-only" access to internal maps. Query operations
     * on the returned attribute map "read through" to the specified attribute map, and
     * attempts to modify the returned attribute map, whether direct or via its collection
     * views, result in an <tt>UnsupportedOperationException</tt>.
     * <p>
     * The returned attribute map will be serializable if the specified map is
     * serializable.
     *
     * @param attributes
     *            the attribute map for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of the specified attribute map.
     */
    public static AttributeMap unmodifiableAttributeMap(AttributeMap attributes) {
        return new ImmutableAttributeMap(attributes);
    }

    /**
     * The default implementation of an immutable empty {@link AttributeMap}.
     */
    static final class EmptyAttributeMap extends AbstractMap<Attribute, Object> implements
            AttributeMap, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -3037602713439417782L;

        /** {@inheritDoc} */
        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public Set<Map.Entry<Attribute, Object>> entrySet() {
            return Collections.emptySet();
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            return o instanceof Map && ((Map) o).size() == 0;
        }

        /** {@inheritDoc} */
        public Object get(Attribute key, Object defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        @Override
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
        @Override
        public int hashCode() {
            return 0;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isEmpty() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
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
        @Override
        public int size() {
            return 0;
        }

        /** {@inheritDoc} */
        @Override
        public Collection<Object> values() {
            return Collections.<Object> emptySet();
        }

        /**
         * Preserves singleton property.
         *
         * @return the empty map
         */
        private Object readResolve() {
            return EMPTY_ATTRIBUTE_MAP;
        }
    }

    /** An unmodifiable view of an attribute map. */
    static class ImmutableAttributeMap implements AttributeMap, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8792952517961074713L;

        /** The map that is being wrapped. */
        private final AttributeMap map;

        /**
         * Creates a new ImmutableAttributeMap.
         *
         * @param attributes
         *            the attribute map to wrap
         */
        ImmutableAttributeMap(AttributeMap attributes) {
            if (attributes == null) {
                throw new NullPointerException("attributes is null");
            }
            this.map = attributes;
        }

        /** {@inheritDoc} */
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        /** {@inheritDoc} */
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        /** {@inheritDoc} */
        public Set<Entry<Attribute, Object>> entrySet() {
            return new CollectionUtils.ImmutableEntrySet(map.entrySet());
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            return o == this || map.equals(o);
        }

        /** {@inheritDoc} */
        public Object get(Attribute key, Object defaultValue) {
            return map.get(key, defaultValue);
        }

        /** {@inheritDoc} */
        public Object get(Object key) {
            return map.get(key);
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key) {
            return map.getBoolean(key);
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key, boolean defaultValue) {
            return map.getBoolean(key, defaultValue);
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key) {
            return map.getByte(key);
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key, byte defaultValue) {
            return map.getByte(key, defaultValue);
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key) {
            return map.getChar(key);
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key, char defaultValue) {
            return map.getChar(key, defaultValue);
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key) {
            return map.getDouble(key);
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key, double defaultValue) {
            return map.getDouble(key, defaultValue);
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key) {
            return map.getFloat(key);
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key, float defaultValue) {
            return map.getFloat(key, defaultValue);
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key) {
            return map.getInt(key);
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key, int defaultValue) {
            return map.getInt(key, defaultValue);
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key) {
            return map.getLong(key);
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key, long defaultValue) {
            return map.getLong(key, defaultValue);
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key) {
            return map.getShort(key);
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key, short defaultValue) {
            return map.getShort(key, defaultValue);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return map.hashCode();
        }

        /** {@inheritDoc} */
        public boolean isEmpty() {
            return map.isEmpty();
        }

        /** {@inheritDoc} */
        public Set<Attribute> keySet() {
            return Collections.unmodifiableSet(map.keySet());
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
            return map.size();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return map.toString();
        }

        /** {@inheritDoc} */
        public Collection<Object> values() {
            return Collections.unmodifiableCollection(map.values());
        }
    }

    /** A singleton attribute map. */
    static class SingletonAttributeMap implements AttributeMap, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6979724477215052911L;

        /** The singleton key. */
        private final Attribute attribute;

        /** The singleton value. */
        private final Object value;

        /**
         * Creates a new SingletonAttributeMap.
         *
         * @param attribute
         *            the attribute to create the singleton from
         * @param value
         *            the value of the specified attribute
         */
        SingletonAttributeMap(Attribute attribute, Object value) {
            if (attribute == null) {
                throw new NullPointerException("attribute is null");
            }
            this.attribute = attribute;
            this.value = value;
        }

        /** {@inheritDoc} */
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean containsKey(Object key) {
            return eq(key, attribute);
        }

        /** {@inheritDoc} */
        public boolean containsValue(Object value) {
            return eq(value, value);
        }

        /** {@inheritDoc} */
        public Set<Map.Entry<Attribute, Object>> entrySet() {
            return Collections
                    .<Map.Entry<Attribute, Object>> singleton(new SimpleImmutableEntry<Attribute, Object>(
                            attribute, value));
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map)) {
                return false;
            }
            Map<Attribute, Object> map = (Map<Attribute, Object>) o;
            if (map.size() != size()) {
                return false;
            }
            try {
                if (value == null) {
                    if (!(map.get(attribute) == null && map.containsKey(attribute))) {
                        return false;
                    }
                } else {
                    if (!value.equals(map.get(attribute))) {
                        return false;
                    }
                }
            } catch (ClassCastException unused) {
                return false;
            }
            return true;
        }

        /** {@inheritDoc} */
        public Object get(Attribute key, Object defaultValue) {
            return attribute == key ? value : defaultValue;
        }

        /** {@inheritDoc} */
        public Object get(Object key) {
            return eq(key, attribute) ? value : null;
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key) {
            return getBoolean(key, false);
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key, boolean defaultValue) {
            return attribute == key ? value == null ? false : (Boolean) value : defaultValue;
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key) {
            return getByte(key, (byte) 0);
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key, byte defaultValue) {
            return attribute == key ? value == null ? 0 : (Byte) value : defaultValue;
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key) {
            return getChar(key, (char) 0);
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key, char defaultValue) {
            return attribute == key ? value == null ? 0 : (Character) value : defaultValue;
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key) {
            return getDouble(key, 0);
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key, double defaultValue) {
            return attribute == key ? value == null ? 0 : (Double) value : defaultValue;
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key) {
            return getFloat(key, 0);
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key, float defaultValue) {
            return attribute == key ? value == null ? 0 : (Float) value : defaultValue;
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key) {
            return getInt(key, 0);
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key, int defaultValue) {
            return attribute == key ? value == null ? 0 : (Integer) value : defaultValue;
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key) {
            return getLong(key, 0);
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key, long defaultValue) {
            return attribute == key ? value == null ? 0 : (Long) value : defaultValue;
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key) {
            return getShort(key, (short) 0);
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key, short defaultValue) {
            return attribute == key ? value == null ? 0 : (Short) value : defaultValue;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return attribute.hashCode() ^ (value == null ? 0 : value.hashCode());
        }

        /** {@inheritDoc} */
        public boolean isEmpty() {
            return false;
        }

        /** {@inheritDoc} */
        public Set<Attribute> keySet() {
            return Collections.singleton(attribute);
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
            buf.append(attribute);
            buf.append("=");
            buf.append(value);
            buf.append("}");
            return buf.toString();
        }

        /** {@inheritDoc} */
        public Collection<Object> values() {
            return Collections.singleton(value);
        }
    }

}

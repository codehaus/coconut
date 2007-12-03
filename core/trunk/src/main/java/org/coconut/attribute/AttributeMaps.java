/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains various utility methods for a {@link AttributeMap}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributeMaps.java 472 2007-11-19 09:34:26Z kasper $
 */
public final class AttributeMaps {

    /** The empty attribute map (immutable). This attribute map is serializable. */
    public final static AttributeMap EMPTY_MAP = new EmptyMap();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private AttributeMaps() {}

    // /CLOVER:ON

    /**
     * Returns an AttributeMap containing only the specified attribute mapping to the
     * specified value.
     * 
     * @param attribute
     *            the attribute to map from
     * @param value
     *            the value to map to
     * @return a singleton attribute map
     * @param <T>
     *            the containing type of the attribute
     */
    public static <T> AttributeMap singleton(Attribute<T> attribute, T value) {
        // make immutable.
        DefaultAttributeMap map = new DefaultAttributeMap();
        map.put(attribute, value);
        return map;
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
     * The default implementation of an {@link AttributeMap}.
     */
    public static class DefaultAttributeMap extends HashMap<Attribute, Object> implements
            AttributeMap {

        /** serialVersionUID. */
        private static final long serialVersionUID = -5954819329578687686L;

        /** Creates a new empty DefaultAttributeMap. */
        public DefaultAttributeMap() {}

        /**
         * Creates a new DefaultAttributeMap copying the existing attributes from the
         * specified map.
         * 
         * @param copyFrom
         *            the attributemap to copy existing attributes from
         */
        public DefaultAttributeMap(AttributeMap copyFrom) {
            super(copyFrom);
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key) {
            return getBoolean(key, false);
        }

        /** {@inheritDoc} */
        public boolean getBoolean(Attribute key, boolean defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Boolean) o;
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key) {
            return getByte(key, (byte) 0);
        }

        /** {@inheritDoc} */
        public byte getByte(Attribute key, byte defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Byte) o;
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key) {
            return getChar(key, (char) 0);
        }

        /** {@inheritDoc} */
        public char getChar(Attribute key, char defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Character) o;
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key) {
            return getDouble(key, 0);
        }

        /** {@inheritDoc} */
        public Object get(Attribute key, Object defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : o;
        }

        /** {@inheritDoc} */
        public double getDouble(Attribute key, double defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Double) o;
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key) {
            return getFloat(key, 0);
        }

        /** {@inheritDoc} */
        public float getFloat(Attribute key, float defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Float) o;
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key) {
            return getInt(key, 0);
        }

        /** {@inheritDoc} */
        public int getInt(Attribute key, int defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Integer) o;
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key) {
            return getLong(key, 0);
        }

        /** {@inheritDoc} */
        public long getLong(Attribute key, long defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Long) o;
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key) {
            return getShort(key, (short) 0);
        }

        /** {@inheritDoc} */
        public short getShort(Attribute key, short defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Short) o;
        }

        /** {@inheritDoc} */
        public void putBoolean(Attribute key, boolean value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putByte(Attribute key, byte value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putChar(Attribute key, char value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putDouble(Attribute key, double value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putFloat(Attribute key, float value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putInt(Attribute key, int value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putLong(Attribute key, long value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putShort(Attribute key, short value) {
            put(key, value);
        }
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
}

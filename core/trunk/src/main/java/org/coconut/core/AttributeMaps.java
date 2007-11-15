/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

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
 * @version $Id$
 */
public final class AttributeMaps {

    /** The empty attribute map (immutable). This attribute map is serializable. */
    public final static AttributeMap EMPTY_MAP = new EmptyMap();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private AttributeMaps() {}

    // /CLOVER:ON

    public static AttributeMap from(String name, Object value) {
        // rename to singleton?? and make immutable.
        DefaultAttributeMap map = new DefaultAttributeMap();
        map.put(name, value);
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
    public static class DefaultAttributeMap extends HashMap<String, Object> implements AttributeMap {

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
        public boolean getBoolean(String key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean getBoolean(String key, boolean defaultValue) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public byte getByte(String key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public byte getByte(String key, byte defaultValue) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public char getChar(String key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public char getChar(String key, char defaultValue) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public double getDouble(String key) {
            return getDouble(key, 0);
        }

        /** {@inheritDoc} */
        public double getDouble(String key, double defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Double) o;
        }

        /** {@inheritDoc} */
        public float getFloat(String key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public float getFloat(String key, float defaultValue) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public int getInt(String key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public int getInt(String key, int defaultValue) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public long getLong(String key) {
            return getLong(key, 0);
        }

        /** {@inheritDoc} */
        public long getLong(String key, long defaultValue) {
            Object o = get(key);
            return o == null ? defaultValue : (Long) o;
        }

        /** {@inheritDoc} */
        public short getShort(String key) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public short getShort(String key, short defaultValue) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putBoolean(String key, boolean value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putByte(String key, byte value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putChar(String key, char value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putDouble(String key, double value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putFloat(String key, float value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putInt(String key, int value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public void putLong(String key, long value) {
            put(key, value);
        }

        /** {@inheritDoc} */
        public void putShort(String key, short value) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * The default implementation of an immutable empty {@link AttributeMap}.
     */
    static final class EmptyMap extends AbstractMap<String, Object> implements AttributeMap,
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
        public Set<Map.Entry<String, Object>> entrySet() {
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
        public boolean getBoolean(String key) {
            return false;
        }

        /** {@inheritDoc} */
        public boolean getBoolean(String key, boolean defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public byte getByte(String key) {
            return 0;
        }

        /** {@inheritDoc} */
        public byte getByte(String key, byte defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public char getChar(String key) {
            return Character.MIN_VALUE;
        }

        /** {@inheritDoc} */
        public char getChar(String key, char defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public double getDouble(String key) {
            return 0;
        }

        /** {@inheritDoc} */
        public double getDouble(String key, double defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public float getFloat(String key) {
            return 0;
        }

        /** {@inheritDoc} */
        public float getFloat(String key, float defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public int getInt(String key) {
            return 0;
        }

        /** {@inheritDoc} */
        public int getInt(String key, int defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public long getLong(String key) {
            return 0;
        }

        /** {@inheritDoc} */
        public long getLong(String key, long defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public short getShort(String key) {
            return 0;
        }

        /** {@inheritDoc} */
        public short getShort(String key, short defaultValue) {
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
        public Set<String> keySet() {
            return Collections.<String> emptySet();
        }

        /** {@inheritDoc} */
        public void putBoolean(String key, boolean value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putByte(String key, byte value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putChar(String key, char value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putDouble(String key, double value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putFloat(String key, float value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putInt(String key, int value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putLong(String key, long value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public void putShort(String key, short value) {
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

/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
     * Returns an immutable AttributeMap containing only the specified attribute mapping to the
     * specified value. Attempts to modify the returned attribute map, whether direct or via its
     * collection views, result in an <tt>UnsupportedOperationException</tt>.
     * <p>
     * The returned attribute map will be serializable if the specified attribute and its value are
     * serializable.
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

//    /**
//     * Creates a new {@link Map} where all the specified keys maps to {@link #EMPTY_ATTRIBUTE_MAP}.
//     * 
//     * @param <K>
//     *            the type of keys
//     * @param keys
//     *            the collection of keys that should map to the empty AttributeMap
//     * @return a new Map where all the specified keys maps to an empty AttributeMap
//     */
//    public static <K> Map<K, AttributeMap> toMap(Collection<? extends K> keys) {
//        return toMap(keys, EMPTY_ATTRIBUTE_MAP);
//    }
//
//    /**
//     * Creates a new {@link Map} where all the specified keys maps to the specified AttributeMap.
//     * 
//     * @param <K>
//     *            the type of keys
//     * @param keys
//     *            the collection of keys that should map to the specified AttributeMap
//     * @param attributeMap
//     *            the AttributeMap that all the specified keys must map to
//     * @return a new Map where all the specified keys maps to the specified AttributeMap
//     */
//    public static <K> Map<K, AttributeMap> toMap(Collection<? extends K> keys,
//            AttributeMap attributeMap) {
//        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
//        for (K key : keys) {
//            map.put(key, attributeMap);
//        }
//        return map;
//    }

    /**
     * Returns an unmodifiable view of the specified attribute map. This method allows modules to
     * provide users with "read-only" access to internal attribute maps. Query operations on the
     * returned attribute map "read through" to the specified attribute map, and attempts to modify
     * the returned attribute map, whether direct or via its collection views, result in an
     * <tt>UnsupportedOperationException</tt>.
     * <p>
     * The returned attribute map will be serializable if the specified map is serializable.
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
    static final class EmptyAttributeMap implements AttributeMap, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -3037602713439417782L;

        public Set<Attribute> attributeSet() {
            return Collections.EMPTY_SET;
        }

        public void clear() {}

        /** {@inheritDoc} */
        public boolean contains(Attribute<?> attribute) {
            return false;
        }

        @Override
        public Set<Entry<Attribute, Object>> entrySet() {
            return Collections.EMPTY_SET;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            return o instanceof AttributeMap && ((AttributeMap) o).size() == 0;
        }

        public <T> T get(Attribute<T> key) {
            return key.getDefault();
        }

        public <T> T get(Attribute<T> key, T defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public boolean get(BooleanAttribute key) {
            return key.getDefault();
        }

        /** {@inheritDoc} */
        public boolean get(BooleanAttribute key, boolean defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public byte get(ByteAttribute key) {
            return key.getDefaultValue();
        }

        /** {@inheritDoc} */
        public byte get(ByteAttribute key, byte defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public char get(CharAttribute key) {
            return key.getDefaultValue();
        }

        /** {@inheritDoc} */
        public char get(CharAttribute key, char defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public double get(DoubleAttribute key) {
            return key.getDefaultValue();
        }

        /** {@inheritDoc} */
        public double get(DoubleAttribute key, double defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public float get(FloatAttribute key) {
            return key.getDefaultValue();
        }

        /** {@inheritDoc} */
        public float get(FloatAttribute key, float defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public int get(IntAttribute key) {
            return key.getDefaultValue();
        }

        /** {@inheritDoc} */
        public int get(IntAttribute key, int defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public long get(LongAttribute key) {
            return key.getDefaultValue();
        }

        /** {@inheritDoc} */
        public long get(LongAttribute key, long defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        public short get(ShortAttribute key) {
            return key.getDefaultValue();
        }

        /** {@inheritDoc} */
        public short get(ShortAttribute key, short defaultValue) {
            return defaultValue;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return 0;
        }

        /** {@inheritDoc} */
        public boolean isEmpty() {
            return true;
        }

        public <T> T put(Attribute<T> key, T value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public boolean put(BooleanAttribute key, boolean value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public byte put(ByteAttribute key, byte value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public char put(CharAttribute key, char value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public double put(DoubleAttribute key, double value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public float put(FloatAttribute key, float value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public int put(IntAttribute key, int value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public long put(LongAttribute key, long value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /** {@inheritDoc} */
        public short put(ShortAttribute key, short value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /**
         * Preserves singleton property.
         * 
         * @return the empty map
         */
        private Object readResolve() {
            return EMPTY_ATTRIBUTE_MAP;
        }

        public <T> T remove(Attribute<T> key) {
            return key.getDefault();
        }

        public boolean remove(BooleanAttribute key) {
            return key.getDefaultValue();
        }

        public byte remove(ByteAttribute key) {
            return key.getDefaultValue();
        }

        public char remove(CharAttribute key) {
            return key.getDefaultValue();
        }

        public double remove(DoubleAttribute key) {
            return key.getDefaultValue();
        }

        public float remove(FloatAttribute key) {
            return key.getDefaultValue();
        }

        public int remove(IntAttribute key) {
            return key.getDefaultValue();
        }

        public long remove(LongAttribute key) {
            return key.getDefaultValue();
        }

        public short remove(ShortAttribute key) {
            return key.getDefaultValue();
        }

        /** {@inheritDoc} */
        public int size() {
            return 0;
        }

        @Override
        public Collection<Object> values() {
            return Collections.EMPTY_SET;
        }

        public String toString() {
            return "{}";
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

        public Set<Attribute> attributeSet() {
            return Collections.unmodifiableSet(map.attributeSet());
        }

        public void clear() {
            throw new UnsupportedOperationException("map is immutable");
        }

        public boolean contains(Attribute<?> attribute) {
            return map.contains(attribute);
        }

        @Override
        public Set<Entry<Attribute, Object>> entrySet() {
            return Collections.unmodifiableSet(map.entrySet());
        }

        @Override
        public boolean equals(Object obj) {
            // TODO not sure this is safe
            return map.equals(obj);
        }

        public <T> T get(Attribute<T> key) {
            return map.get(key);
        }

        public <T> T get(Attribute<T> key, T defaultValue) {
            return map.get(key, defaultValue);
        }

        public boolean get(BooleanAttribute key) {
            return map.get(key);
        }

        public boolean get(BooleanAttribute key, boolean defaultValue) {
            return map.get(key, defaultValue);
        }

        public byte get(ByteAttribute key) {
            return map.get(key);
        }

        public byte get(ByteAttribute key, byte defaultValue) {
            return map.get(key, defaultValue);
        }

        public char get(CharAttribute key) {
            return map.get(key);
        }

        public char get(CharAttribute key, char defaultValue) {
            return map.get(key, defaultValue);
        }

        public double get(DoubleAttribute key) {
            return map.get(key);
        }

        public double get(DoubleAttribute key, double defaultValue) {
            return map.get(key, defaultValue);
        }

        public float get(FloatAttribute key) {
            return map.get(key);
        }

        public float get(FloatAttribute key, float defaultValue) {
            return map.get(key, defaultValue);
        }

        public int get(IntAttribute key) {
            return map.get(key);
        }

        public int get(IntAttribute key, int defaultValue) {
            return map.get(key, defaultValue);
        }

        public long get(LongAttribute key) {
            return map.get(key);
        }

        public long get(LongAttribute key, long defaultValue) {
            return map.get(key, defaultValue);
        }

        public short get(ShortAttribute key) {
            return map.get(key);
        }

        public short get(ShortAttribute key, short defaultValue) {
            return map.get(key, defaultValue);
        }

        @Override
        public int hashCode() {
            return map.hashCode();
        }

        public boolean isEmpty() {
            return map.isEmpty();
        }

        public <T> T put(Attribute<T> key, T value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public boolean put(BooleanAttribute key, boolean value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public byte put(ByteAttribute key, byte value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public char put(CharAttribute key, char value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public double put(DoubleAttribute key, double value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public float put(FloatAttribute key, float value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public int put(IntAttribute key, int value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public long put(LongAttribute key, long value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public short put(ShortAttribute key, short value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public <T> T remove(Attribute<T> key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public boolean remove(BooleanAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public byte remove(ByteAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public char remove(CharAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public double remove(DoubleAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public float remove(FloatAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public int remove(IntAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public long remove(LongAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public short remove(ShortAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public int size() {
            return map.size();
        }

        @Override
        public String toString() {
            return map.toString();
        }

        @Override
        public Collection<Object> values() {
            return Collections.unmodifiableCollection(map.values());
        }

    }

    /** A singleton attribute map. */
    static class SingletonAttributeMap implements AttributeMap, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6979724477215052911L;

        /** The singleton key. */
        private final Attribute<?> attribute;

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
        SingletonAttributeMap(Attribute<?> attribute, Object value) {
            if (attribute == null) {
                throw new NullPointerException("attribute is null");
            }
            this.attribute = attribute;
            this.value = value;
        }

        public Set<Attribute> attributeSet() {
            return (Set) Collections.singleton(attribute);
        }

        /** {@inheritDoc} */
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean contains(Attribute<?> attribute) {
            return this.attribute == attribute;
        }

        @Override
        public Set<Entry<Attribute, Object>> entrySet() {
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
            if (!(o instanceof AttributeMap)) {
                return false;
            }
            AttributeMap map = (AttributeMap) o;
            if (map.size() != size()) {
                return false;
            }
            Object other = map.get(attribute);
            return eq(value, other)
                    && (!eq(value, attribute.getDefault()) || map.contains(attribute));
        }

        public <T> T get(Attribute<T> key) {
            return get(key, key.getDefault());
        }

        public <T> T get(Attribute<T> key, T defaultValue) {
            return attribute == key ? (T) value : defaultValue;
        }

        /** {@inheritDoc} */
        public boolean get(BooleanAttribute key) {
            return get(key, key.getDefaultValue());
        }

        /** {@inheritDoc} */
        public boolean get(BooleanAttribute key, boolean defaultValue) {
            return attribute == key ? (Boolean) value : defaultValue;
        }

        /** {@inheritDoc} */
        public byte get(ByteAttribute key) {
            return get(key, key.getDefaultValue());
        }

        /** {@inheritDoc} */
        public byte get(ByteAttribute key, byte defaultValue) {
            return attribute == key ? (Byte) value : defaultValue;
        }

        /** {@inheritDoc} */
        public char get(CharAttribute key) {
            return get(key, key.getDefaultValue());
        }

        /** {@inheritDoc} */
        public char get(CharAttribute key, char defaultValue) {
            return attribute == key ? (Character) value : defaultValue;
        }

        /** {@inheritDoc} */
        public double get(DoubleAttribute key) {
            return get(key, key.getDefaultValue());
        }

        /** {@inheritDoc} */
        public double get(DoubleAttribute key, double defaultValue) {
            return attribute == key ? (Double) value : defaultValue;
        }

        /** {@inheritDoc} */
        public float get(FloatAttribute key) {
            return get(key, key.getDefaultValue());
        }

        /** {@inheritDoc} */
        public float get(FloatAttribute key, float defaultValue) {
            return attribute == key ? (Float) value : defaultValue;
        }

        /** {@inheritDoc} */
        public int get(IntAttribute key) {
            return get(key, key.getDefaultValue());
        }

        /** {@inheritDoc} */
        public int get(IntAttribute key, int defaultValue) {
            return attribute == key ? (Integer) value : defaultValue;
        }

        /** {@inheritDoc} */
        public long get(LongAttribute key) {
            return get(key, key.getDefaultValue());
        }

        /** {@inheritDoc} */
        public long get(LongAttribute key, long defaultValue) {
            return attribute == key ? (Long) value : defaultValue;
        }

        /** {@inheritDoc} */
        public short get(ShortAttribute key) {
            return get(key, key.getDefaultValue());
        }

        /** {@inheritDoc} */
        public short get(ShortAttribute key, short defaultValue) {
            return attribute == key ? (Short) value : defaultValue;
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

        public <T> T put(Attribute<T> key, T value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public boolean put(BooleanAttribute key, boolean value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public byte put(ByteAttribute key, byte value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public char put(CharAttribute key, char value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public double put(DoubleAttribute key, double value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public float put(FloatAttribute key, float value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public int put(IntAttribute key, int value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public long put(LongAttribute key, long value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public short put(ShortAttribute key, short value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public <T> T remove(Attribute<T> key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public boolean remove(BooleanAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public byte remove(ByteAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public char remove(CharAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public double remove(DoubleAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public float remove(FloatAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public int remove(IntAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public long remove(LongAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
        }

        public short remove(ShortAttribute key) {
            throw new UnsupportedOperationException("map is immutable");
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

    /**
     * An Entry maintaining an immutable key and value. This class does not support method
     * <tt>setValue</tt>. This class may be convenient in methods that return thread-safe
     * snapshots of key-value mappings.
     */
    static class SimpleImmutableEntry<K, V> implements Entry<K, V>, Serializable {

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
    static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }
}

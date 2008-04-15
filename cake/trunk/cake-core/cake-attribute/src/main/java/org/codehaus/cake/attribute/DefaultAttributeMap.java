/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * The default implementation of an {@link AttributeMap}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributeMaps.java 472 2007-11-19 09:34:26Z kasper $
 */
public class DefaultAttributeMap implements AttributeMap {

    /** serialVersionUID. */
    private static final long serialVersionUID = -5954819329578687686L;

    private final HashMap<Attribute, Object> map = new HashMap<Attribute, Object>();

    /** Creates a new empty DefaultAttributeMap. */
    public DefaultAttributeMap() {}

    /**
     * Creates a new DefaultAttributeMap copying the existing attributes from the specified map.
     * 
     * @param copyFrom
     *            the attributemap to copy existing attributes from
     */
    public DefaultAttributeMap(AttributeMap copyFrom) {
        for (Map.Entry<Attribute, Object> e : copyFrom.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public Set<Attribute> attributeSet() {
        return (Set) map.keySet();
    }

    @Override
    public boolean contains(Attribute<?> attribute) {
        return map.containsKey(attribute);
    }

    /** {@inheritDoc} */
    public boolean get(BooleanAttribute key) {
        if (map.containsKey(key)) {
            return (Boolean) map.get(key);
        } else {
            return key.getDefault();
        }
    }

    /** {@inheritDoc} */
    public boolean get(BooleanAttribute key, boolean defaultValue) {
        if (map.containsKey(key)) {
            return (Boolean) map.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public byte get(ByteAttribute key) {
        if (map.containsKey(key)) {
            return (Byte) map.get(key);
        } else {
            return key.getDefault();
        }
    }

    /** {@inheritDoc} */
    public byte get(ByteAttribute key, byte defaultValue) {
        if (map.containsKey(key)) {
            return (Byte) map.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public char get(CharAttribute key) {
        if (map.containsKey(key)) {
            return (Character) map.get(key);
        } else {
            return key.getDefault();
        }
    }

    /** {@inheritDoc} */
    public char get(CharAttribute key, char defaultValue) {
        if (map.containsKey(key)) {
            return (Character) map.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public double get(DoubleAttribute key) {
        return get(key, key.getDefaultValue());
    }

    /** {@inheritDoc} */
    public double get(DoubleAttribute key, double defaultValue) {
        if (map.containsKey(key)) {
            return (Double) map.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public float get(FloatAttribute key) {
        if (map.containsKey(key)) {
            return (Float) map.get(key);
        } else {
            return (Float) ((Attribute) key).getDefault();
        }
    }

    /** {@inheritDoc} */
    public float get(FloatAttribute key, float defaultValue) {
        if (map.containsKey(key)) {
            return (Float) map.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public int get(IntAttribute key) {
        return get(key, key.getDefaultValue());
    }

    /** {@inheritDoc} */
    public int get(IntAttribute key, int defaultValue) {
        if (map.containsKey(key)) {
            return (Integer) map.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public long get(LongAttribute key) {
        return get(key, key.getDefaultValue());
    }

    /** {@inheritDoc} */
    public long get(LongAttribute key, long defaultValue) {
        if (map.containsKey(key)) {
            return (Long) map.get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public <T> T get(Attribute<T> key) {
        if (map.containsKey(key)) {
            return (T) map.get(key);
        } else {
            return key.getDefault();
        }
    }

    @Override
    public <T> T get(Attribute<T> key, T defaultValue) {
        if (map.containsKey(key)) {
            return (T) map.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public short get(ShortAttribute key) {
        if (map.containsKey(key)) {
            return (Short) map.get(key);
        } else {
            return (Short) ((Attribute) key).getDefault();
        }
    }

    /** {@inheritDoc} */
    public short get(ShortAttribute key, short defaultValue) {
        if (map.containsKey(key)) {
            return (Short) map.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public boolean put(BooleanAttribute key, boolean value) {
        Object prev = map.put(key, value);
        return prev == null ? key.getDefault() : (Boolean) prev;

    }

    /** {@inheritDoc} */
    public byte put(ByteAttribute key, byte value) {
        Object prev = map.put(key, value);
        return prev == null ? key.getDefault() : (Byte) prev;

    }

    /** {@inheritDoc} */
    public char put(CharAttribute key, char value) {
        Object prev = map.put(key, value);
        return prev == null ? key.getDefault() : (Character) prev;
    }

    /** {@inheritDoc} */
    public double put(DoubleAttribute key, double value) {
        Object prev = map.put(key, value);
        return prev == null ? key.getDefaultValue() : (Double) prev;

    }

    /** {@inheritDoc} */
    public float put(FloatAttribute key, float value) {
        Object prev = map.put(key, value);
        return prev == null ? key.getDefault() : (Float) prev;
    }

    /** {@inheritDoc} */
    public int put(IntAttribute key, int value) {
        Object prev = map.put(key, value);
        return prev == null ? key.getDefaultValue() : (Integer) prev;
    }

    /** {@inheritDoc} */
    public long put(LongAttribute key, long value) {
        Object prev = map.put(key, value);
        return prev == null ? key.getDefaultValue() : (Long) prev;
    }

    @Override
    public <T> T put(Attribute<T> key, T value) {
        if (map.containsKey(key)) {
            return (T) map.put(key, value);
        } else {
            map.put(key, value);
            return key.getDefault();
        }
    }

    /** {@inheritDoc} */
    public short put(ShortAttribute key, short value) {
        Object prev = map.put(key, value);
        return prev == null ? key.getDefault() : (Short) prev;

    }

    @Override
    public boolean remove(BooleanAttribute key) {
        Object prev = map.remove(key);
        return prev == null ? key.getDefaultValue() : (Boolean) prev;
    }

    @Override
    public byte remove(ByteAttribute key) {
        Object prev = map.remove(key);
        return prev == null ? key.getDefaultValue() : (Byte) prev;

    }

    @Override
    public char remove(CharAttribute key) {
        Object prev = map.remove(key);
        return prev == null ? key.getDefaultValue() : (Character) prev;
    }

    @Override
    public double remove(DoubleAttribute key) {
        Object prev = map.remove(key);
        return prev == null ? key.getDefaultValue() : (Double) prev;
    }

    @Override
    public float remove(FloatAttribute key) {
        Object prev = map.remove(key);
        return prev == null ? key.getDefaultValue() : (Float) prev;
    }

    @Override
    public int remove(IntAttribute key) {
        Object prev = map.remove(key);
        return prev == null ? key.getDefaultValue() : (Integer) prev;
    }

    @Override
    public long remove(LongAttribute key) {
        Object prev = map.remove(key);
        return prev == null ? key.getDefaultValue() : (Long) prev;
    }

    @Override
    public <T> T remove(Attribute<T> key) {
        if (map.containsKey(key)) {
            return (T) map.remove(key);
        } else {
            return key.getDefault();
        }
    }

    @Override
    public short remove(ShortAttribute key) {
        Object prev = map.remove(key);
        return prev == null ? key.getDefaultValue() : (Short) prev;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Set<Entry<Attribute, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AttributeMap))
            return false;
        AttributeMap m = (AttributeMap) o;
        if (m.size() != size())
            return false;
        return m.entrySet().equals(entrySet());
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}

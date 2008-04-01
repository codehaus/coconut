/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import java.util.HashMap;

/**
 * The default implementation of an {@link AttributeMap}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributeMaps.java 472 2007-11-19 09:34:26Z kasper $
 */
public class DefaultAttributeMap extends HashMap<Attribute, Object> implements AttributeMap {

    /** serialVersionUID. */
    private static final long serialVersionUID = -5954819329578687686L;

    /** Creates a new empty DefaultAttributeMap. */
    public DefaultAttributeMap() {}

    /**
     * Creates a new DefaultAttributeMap copying the existing attributes from the specified map.
     * 
     * @param copyFrom
     *            the attributemap to copy existing attributes from
     */
    public DefaultAttributeMap(AttributeMap copyFrom) {
        super(copyFrom);
    }

    /** {@inheritDoc} */
    public Object get(Object key) {
        if (super.containsKey(key)) {
            return super.get(key);
        } else {
            return ((Attribute) key).getDefault();
        }
    }

    /** {@inheritDoc} */
    public Object get(Attribute key, Object defaultValue) {
        if (super.containsKey(key)) {
            return super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public boolean getBoolean(Attribute key) {
        if (super.containsKey(key)) {
            return (Boolean) super.get(key);
        } else {
            return (Boolean) ((Attribute) key).getDefault();
        }
    }

    /** {@inheritDoc} */
    public boolean getBoolean(Attribute key, boolean defaultValue) {
        if (super.containsKey(key)) {
            return (Boolean) super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public byte getByte(Attribute key) {
        if (super.containsKey(key)) {
            return (Byte) super.get(key);
        } else {
            return (Byte) ((Attribute) key).getDefault();
        }
    }

    /** {@inheritDoc} */
    public byte getByte(Attribute key, byte defaultValue) {
        if (super.containsKey(key)) {
            return (Byte) super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public char getChar(Attribute key) {
        if (super.containsKey(key)) {
            return (Character) super.get(key);
        } else {
            return (Character) ((Attribute) key).getDefault();
        }
    }

    /** {@inheritDoc} */
    public char getChar(Attribute key, char defaultValue) {
        if (super.containsKey(key)) {
            return (Character) super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public double getDouble(DoubleAttribute key) {
        return getDouble(key, key.getDefaultValue());
    }

    /** {@inheritDoc} */
    public double getDouble(DoubleAttribute key, double defaultValue) {
        if (super.containsKey(key)) {
            return (Double) super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public float getFloat(Attribute key) {
        if (super.containsKey(key)) {
            return (Float) super.get(key);
        } else {
            return (Float) ((Attribute) key).getDefault();
        }
    }

    /** {@inheritDoc} */
    public float getFloat(Attribute key, float defaultValue) {
        if (super.containsKey(key)) {
            return (Float) super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public int getInt(IntAttribute key) {
        return getInt(key, key.getDefaultValue());
    }

    /** {@inheritDoc} */
    public int getInt(IntAttribute key, int defaultValue) {
        if (super.containsKey(key)) {
            return (Integer) super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public long getLong(LongAttribute key) {
        return getLong(key, key.getDefaultValue());
    }

    /** {@inheritDoc} */
    public long getLong(LongAttribute key, long defaultValue) {
        if (super.containsKey(key)) {
            return (Long) super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public short getShort(Attribute key) {
        if (super.containsKey(key)) {
            return (Short) super.get(key);
        } else {
            return (Short) ((Attribute) key).getDefault();
        }
    }

    /** {@inheritDoc} */
    public short getShort(Attribute key, short defaultValue) {
        if (super.containsKey(key)) {
            return (Short) super.get(key);
        } else {
            return defaultValue;
        }
    }

    /** {@inheritDoc} */
    public boolean putBoolean(Attribute<Boolean> key, boolean value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefault() : (Boolean) prev;

    }

    /** {@inheritDoc} */
    public byte putByte(Attribute<Byte> key, byte value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefault() : (Byte) prev;

    }

    /** {@inheritDoc} */
    public char putChar(Attribute<Character> key, char value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefault() : (Character) prev;
    }

    /** {@inheritDoc} */
    public double putDouble(DoubleAttribute key, double value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefaultValue() : (Double) prev;

    }

    /** {@inheritDoc} */
    public float putFloat(Attribute<Float> key, float value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefault() : (Float) prev;
    }

    /** {@inheritDoc} */
    public int putInt(IntAttribute key, int value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefaultValue() : (Integer) prev;
    }

    /** {@inheritDoc} */
    public long putLong(LongAttribute key, long value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefaultValue() : (Long) prev;
    }

    /** {@inheritDoc} */
    public short putShort(Attribute<Short> key, short value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefault() : (Short) prev;

    }

    @Override
    public Object put(Attribute key, Object value) {
        Object prev = super.put(key, value);
        return prev == null ? key.getDefault() : prev;
    }
}

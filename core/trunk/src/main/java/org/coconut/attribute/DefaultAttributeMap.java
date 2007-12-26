/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

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
    public Object get(Attribute key, Object defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : o;
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

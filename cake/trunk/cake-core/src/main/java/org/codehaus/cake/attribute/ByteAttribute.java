/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An abstract implementation of an {@link Attribute} mapping to a int. This implementation adds a
 * number of methods that works on primitive ints instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class ByteAttribute extends Attribute<Byte> implements Comparator<WithAttributes> {

    static final AtomicLong NAME = new AtomicLong();
    /** The default value of this attribute. */
    private final transient byte defaultIntValue;

    /**
     * Creates a new IntAttribute with a default value of <tt>0</tt>.
     * 
     * @throws IllegalArgumentException
     *             if 0 is not a valid value
     */
    public ByteAttribute() {
        this((byte) 0);
    }

    /**
     * Creates a new IntAttribute.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value
     */
    public ByteAttribute(byte defaultValue) {
        this("ByteAttribute" + NAME.incrementAndGet(),defaultValue);
    }

    /**
     * Creates a new IntAttribute with a default value of <tt>0</tt>.
     * 
     * @param name
     *            the name of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if 0 is not a valid value
     */
    public ByteAttribute(String name) {
        this(name,(byte) 0);
    }

    /**
     * Creates a new IntAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of this attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value
     */
    public ByteAttribute(String name, byte defaultValue) {
        super(name, Byte.TYPE, defaultValue);
        this.defaultIntValue = defaultValue;
    }

    /**
     * Analogous to {@link #checkValid(Byte)} except taking a scalar byte.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(byte value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Byte o) {
        checkValid(o.byteValue());
    }

    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        byte thisVal = getValue(w1);
        byte anotherVal = getValue(w2);
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /** {@inheritDoc} */
    public byte fromString(String str) {
        return Byte.parseByte(str);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}.
     * 
     * @return the default value of this attribute
     */
    public byte getDefaultValue() {
        return defaultIntValue;
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a scalar <tt>byte</tt>.
     * 
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public byte getValue(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    public byte getValue(WithAttributes attributes, byte defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive Byte as
     * parameter.
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(byte value) {
        return true;
    }

    /** {@inheritDoc} */
    public byte op(WithAttributes t) {
        return getValue(t);
    }

    /**
     * Analogous to {@link #set(AttributeMap, Byte)} except taking a primitive Byte as
     * parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Byte)}
     */
    public AttributeMap set(AttributeMap attributes, byte value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, byte value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Analogous to {@link #singleton(Byte)} except taking a primitive Byte as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(byte value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }
}
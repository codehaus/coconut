/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.util.Comparator;

/**
 * An implementation of an {@link Attribute} mapping to a byte. This implementation adds a number of
 * methods that works on primitive bytes instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ByteAttribute.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

public abstract class ByteAttribute extends Attribute<Byte> implements
         Comparator<WithAttributes> {
         
    /** The default value of this attribute. */
    private final transient byte defaultValue;

    /**
     * Creates a new ByteAttribute with a generated name and a default value of <tt>0</tt>.
     * 
     * @throws IllegalArgumentException
     *             if 0 is not a valid value according to {@link #checkValid(int)}
     */
    public ByteAttribute() {
        this((byte) 0);
    }

    /**
     * Creates a new ByteAttribute with a generated name.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(byte)}
     */
    public ByteAttribute(byte defaultValue) {
        super(Byte.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new ByteAttribute with a default value of <tt>0</tt>.
     * 
     * @param name
     *            the name of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if 0 is not a valid value according to {@link #checkValid(byte)}
     */
    public ByteAttribute(String name) {
        this(name, (byte) 0);
    }

    /**
     * Creates a new ByteAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(byte)}
     */
    public ByteAttribute(String name, byte defaultValue) {
        super(name, Byte.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }
    
    /** {@inheritDoc} */
    @Override
    public final void checkValid(Byte o) {
        checkValid(o.byteValue());
    }
    
    /**
     * Analogous to {@link #checkValid(Byte)} except taking a primitive byte.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(byte value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute [name=" + getName()
                    + ", type = " + getClass() + ", value = " + value + "]");
        }
    }
    
    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        byte thisVal = get(w1);
        byte anotherVal = get(w2);
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }
    
    
    /**
     * Creates a value instance of this attribute from the specified string.
     * 
     * @param str
     *            the string to create the value from.
     * @return a value instance from the specified string
     * @throws IllegalArgumentException
     *             if a valid value could not be created from the string.
     */
    public byte fromString(String str) {
        return Byte.parseByte(str);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}, but returning a primitive int instead.
     * 
     * @return the default value of this attribute
     */
    public byte getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>byte</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @return the value of this attribute if this attribute is present in the map. Otherwise
     *         {@link #getDefaultValue()}
     */
    public byte get(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>byte</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @param defaultValue
     *            the default value to return if this attribute is not present in the map
     * @return the value of this attribute if this attribute is present in the map. Otherwise the
     *         specified default value
     */
    public byte get(WithAttributes attributes, byte defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }


   /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive byte as
     * parameter.
     * <p>
     * The default version returns true for all parameters
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(byte value) {
        return true;
    }
    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Byte value) {
        return isValid(value.byteValue());
    }

    /**
     * Analogous to {@link #set(AttributeMap, Byte)} except taking a primitive byte as parameter.
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
     * Analogous to {@link #singleton(Byte)} except taking a primitive byte as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(byte value) {
        return super.singleton(value);
    }
}
/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.util.Comparator;

/**
 * An implementation of an {@link Attribute} mapping to a short. This implementation adds a number of
 * methods that works on primitive shorts instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ShortAttribute.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

public abstract class ShortAttribute extends Attribute<Short> implements
         Comparator<WithAttributes> {
         
    /** The default value of this attribute. */
    private final transient short defaultValue;

    /**
     * Creates a new ShortAttribute with a generated name and a default value of <tt>0</tt>.
     * 
     * @throws IllegalArgumentException
     *             if 0 is not a valid value according to {@link #checkValid(int)}
     */
    public ShortAttribute() {
        this((short) 0);
    }

    /**
     * Creates a new ShortAttribute with a generated name.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(short)}
     */
    public ShortAttribute(short defaultValue) {
        super(Short.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new ShortAttribute with a default value of <tt>0</tt>.
     * 
     * @param name
     *            the name of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if 0 is not a valid value according to {@link #checkValid(short)}
     */
    public ShortAttribute(String name) {
        this(name, (short) 0);
    }

    /**
     * Creates a new ShortAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(short)}
     */
    public ShortAttribute(String name, short defaultValue) {
        super(name, Short.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }
    
    /** {@inheritDoc} */
    @Override
    public final void checkValid(Short o) {
        checkValid(o.shortValue());
    }
    
    /**
     * Analogous to {@link #checkValid(Short)} except taking a primitive short.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(short value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute [name=" + getName()
                    + ", type = " + getClass() + ", value = " + value + "]");
        }
    }
    
    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        short thisVal = get(w1);
        short anotherVal = get(w2);
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
    public short fromString(String str) {
        return Short.parseShort(str);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}, but returning a primitive int instead.
     * 
     * @return the default value of this attribute
     */
    public short getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>short</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @return the value of this attribute if this attribute is present in the map. Otherwise
     *         {@link #getDefaultValue()}
     */
    public short get(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>short</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @param defaultValue
     *            the default value to return if this attribute is not present in the map
     * @return the value of this attribute if this attribute is present in the map. Otherwise the
     *         specified default value
     */
    public short get(WithAttributes attributes, short defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }


   /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive short as
     * parameter.
     * <p>
     * The default version returns true for all parameters
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(short value) {
        return true;
    }
    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Short value) {
        return isValid(value.shortValue());
    }

    /**
     * Analogous to {@link #set(AttributeMap, Short)} except taking a primitive short as parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Short)}
     */
    public AttributeMap set(AttributeMap attributes, short value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, short value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Analogous to {@link #singleton(Short)} except taking a primitive short as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(short value) {
        return super.singleton(value);
    }
}
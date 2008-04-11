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
public abstract class ShortAttribute extends Attribute<Short> implements Comparator<WithAttributes> {

    static final AtomicLong NAME = new AtomicLong();
    /** The default value of this attribute. */
    private final transient short defaultIntValue;

    /**
     * Creates a new IntAttribute with a default value of <tt>0</tt>.
     * 
     * @throws IllegalArgumentException
     *             if 0 is not a valid value
     */
    public ShortAttribute() {
        this((short) 0);
    }

    /**
     * Creates a new IntAttribute.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value
     */
    public ShortAttribute(short defaultValue) {
        this("ShortAttribute" + NAME.incrementAndGet(),defaultValue);
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
    public ShortAttribute(String name) {
        this(name,(short) 0);
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
    public ShortAttribute(String name, short defaultValue) {
        super(name, Short.TYPE, defaultValue);
        this.defaultIntValue = defaultValue;
    }

    /**
     * Analogous to {@link #checkValid(Short)} except taking a scalar short.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(short value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Short o) {
        checkValid(o.shortValue());
    }

    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        short thisVal = getValue(w1);
        short anotherVal = getValue(w2);
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /** {@inheritDoc} */
    public short fromString(String str) {
        return Short.parseShort(str);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}.
     * 
     * @return the default value of this attribute
     */
    public short getDefaultValue() {
        return defaultIntValue;
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a scalar <tt>short</tt>.
     * 
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public short getValue(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    public short getValue(WithAttributes attributes, short defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive Short as
     * parameter.
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(short value) {
        return true;
    }

    /** {@inheritDoc} */
    public short op(WithAttributes t) {
        return getValue(t);
    }

    /**
     * Analogous to {@link #set(AttributeMap, Short)} except taking a primitive Short as
     * parameter.
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
     * Analogous to {@link #singleton(Short)} except taking a primitive Short as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(short value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }
}

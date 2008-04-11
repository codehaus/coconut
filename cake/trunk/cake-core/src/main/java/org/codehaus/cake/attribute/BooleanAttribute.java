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
public abstract class BooleanAttribute extends Attribute<Boolean> implements
        Comparator<WithAttributes> {

    static final AtomicLong NAME = new AtomicLong();
    /** The default value of this attribute. */
    private final transient boolean defaultIntValue;

    /**
     * Creates a new IntAttribute with a default value of <tt>0</tt>.
     * 
     * @throws IllegalArgumentException
     *             if 0 is not a valid value
     */
    public BooleanAttribute() {
        this(false);
    }

    /**
     * Creates a new IntAttribute.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value
     */
    public BooleanAttribute(boolean defaultValue) {
        this("BooleanAttribute" + NAME.incrementAndGet(), defaultValue);
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
    public BooleanAttribute(String name) {
        this(name, false);
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
    public BooleanAttribute(String name, boolean defaultValue) {
        super(name, Boolean.TYPE, defaultValue);
        this.defaultIntValue = defaultValue;
    }

    /**
     * Analogous to {@link #checkValid(Boolean)} except taking a scalar boolean.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(boolean value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Boolean o) {
        checkValid(o.booleanValue());
    }

    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        boolean thisVal = getValue(w1);
        boolean anotherVal = getValue(w2);
        //fix this to something smarter
        return (thisVal && !anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /** {@inheritDoc} */
    public boolean fromString(String str) {
        return Boolean.parseBoolean(str);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}.
     * 
     * @return the default value of this attribute
     */
    public boolean getDefaultValue() {
        return defaultIntValue;
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a scalar <tt>boolean</tt>.
     * 
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public boolean getValue(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    public boolean getValue(WithAttributes attributes, boolean defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive Boolean as
     * parameter.
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(boolean value) {
        return true;
    }

    /** {@inheritDoc} */
    public boolean op(WithAttributes t) {
        return getValue(t);
    }

    /**
     * Analogous to {@link #set(AttributeMap, Boolean)} except taking a primitive Boolean as
     * parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Boolean)}
     */
    public AttributeMap set(AttributeMap attributes, boolean value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, boolean value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Analogous to {@link #singleton(Boolean)} except taking a primitive Boolean as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(boolean value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }
}

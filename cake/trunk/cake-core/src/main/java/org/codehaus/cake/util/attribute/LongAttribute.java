/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import java.util.Comparator;

import jsr166y.forkjoin.Ops.ObjectToLong;

/**
 * An abstract implementation of an {@link Attribute} mapping to a long. This implementation adds a
 * number of methods that works on primitive longs instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class LongAttribute extends Attribute<Long> implements
        ObjectToLong<WithAttributes>, Comparator<WithAttributes> {

    /** The default value of this attribute. */
    private final transient long defaultLongValue;

    /**
     * Creates a new LongAttribute.
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
    public LongAttribute(String name, long defaultValue) {
        super(name, Long.TYPE, defaultValue);
        this.defaultLongValue = defaultValue;
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Long o) {
        checkValid(o.longValue());
    }

    /**
     * Analogous to {@link #checkValid(Long)} except taking a primitive long.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(long value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Long fromString(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not create value", e);
        }
    }

    /**
     * Returns the default primitive value of this attribute. This is equivalent to calling
     * {@link #getDefault()} except returning a primitive long.
     * 
     * @return the default value of this attribute
     */
    public long getDefaultValue() {
        return defaultLongValue;
    }

    /**
     * Analogous to {@link #get(AttributeMap)} except returning a primitive <tt>long</tt>.
     * 
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public long getValue(AttributeMap attributes) {
        return attributes.getLong(this, defaultLongValue);
    }

    public long getValue(WithAttributes withAttributes) {
        return getValue(withAttributes.getAttributes());
    }

    public long getValue(WithAttributes attributes, long defaultValue) {
        return getValue(attributes.getAttributes(), defaultValue);
    }

    /** {@inheritDoc} */
    @Override
    public int compare(WithAttributes w1, WithAttributes w2) {
        long thisVal = getValue(w1);
        long anotherVal = getValue(w2);
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /**
     * Analogous to {@link #get(AttributeMap, Long)} except returning a primitive <tt>long</tt>.
     * 
     * @param attributes
     *            the attribute map to check for this attribute in
     * @param defaultValue
     *            the value to return if this attribute is not set in the specified attribute map
     * @return the value of this attribute
     */
    public long getValue(AttributeMap attributes, long defaultValue) {
        return attributes.getLong(this, defaultValue);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Long value) {
        return isValid(value.longValue());
    }

    /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive long as parameter.
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(long value) {
        return true;
    }

    /** {@inheritDoc} */
    public long op(WithAttributes t) {
        return getValue(t.getAttributes());
    }

    /**
     * Analogous to {@link #set(AttributeMap, Long)} except taking a primitive long as parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(long)}
     */
    public AttributeMap set(AttributeMap attributes, long value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.putLong(this, value);
        return attributes;
    }

    /**
     * Analogous to {@link #singleton(Long)} except taking a primitive long as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an immutable AttributeMap containing only this attribute mapping to the specified
     *         value
     */
    public AttributeMap singleton(long value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }
}

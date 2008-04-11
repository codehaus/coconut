/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

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

    public LongAttribute(long defaultValue) {
        this("", defaultValue);
    }

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
    public long getValue(AttributeMap attributes) {
        return attributes.get(this);
    }

    public long getValue(AttributeMap attributes, long defaultValue) {
        return attributes.get(this, defaultValue);
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
    public long fromString(String str) {
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

    public long getValue(WithAttributes withAttributes) {
        return withAttributes.getAttributes().get(this);
    }

    public long getValue(WithAttributes attributes, long defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        long thisVal = getValue(w1);
        long anotherVal = getValue(w2);
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /**
     * Returns whether or not the specified value is valid for this attribute. This method can be
     * overriden to only accept certain values.
     * 
     * @param value
     *            the specified value to check
     * @return <code>true</code> if the specified value is valid for this attribute, otherwise
     *         <code>false</code>
     */
    public boolean isValid(long value) {
        return true;
    }

    /** {@inheritDoc} */
    public long op(WithAttributes t) {
        return t.getAttributes().get(this);
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
        attributes.put(this, value);
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

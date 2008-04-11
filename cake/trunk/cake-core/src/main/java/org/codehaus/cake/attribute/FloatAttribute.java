/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.util.Comparator;

/**
 * An abstract implementation of an {@link Attribute} mapping to a double. This implementation add a
 * number of methods that works on primitive doubles instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class FloatAttribute extends Attribute<Float> implements Comparator<WithAttributes> {

    /** The default value of this attribute. */
    private final transient float defaultFloatValue;

    public FloatAttribute(float defaultValue) {
        this("", defaultValue);
    }

    /**
     * Creates a new AbstractFloatAttribute.
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
    public FloatAttribute(String name, float defaultValue) {
        super(name, Float.TYPE, defaultValue);
        this.defaultFloatValue = defaultValue;
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Float o) {
        checkValid(o.floatValue());
    }

    /**
     * Analogous to {@link #checkValid(Float)} except taking a primitive float.
     * <p>
     * The default implementation fails if the specified value is either
     * {@link Float#NEGATIVE_INFINITY}, {@link Float#POSITIVE_INFINITY} or {@link Float#NaN}.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(float value) {
        checkNotNaNInfinity(value);
    }

    /** {@inheritDoc} */
    public float fromString(String str) {
        return Float.parseFloat(str);
    }

    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        float thisVal = getValue(w1);
        float anotherVal = getValue(w2);
        return Float.compare(thisVal, anotherVal);
    }

    public float getValue(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    public float getValue(WithAttributes attributes, float defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /** {@inheritDoc} */
    public float op(WithAttributes t) {
        return t.getAttributes().get(this);
    }

    /**
     * Works as {@link Attribute#isValid(Object)} except taking a primitive float. The default
     * implementation returns <code>false</code> for {@link Float#NEGATIVE_INFINITY},
     * {@link Float#POSITIVE_INFINITY} and {@link Float#NaN}.
     * 
     * @return whether or not the value is valid
     * @param value
     *            the value to check
     */
    public boolean isValid(float value) {
        return !isNaNInfinity(value);
    }

    /**
     * Analogous to {@link #set(AttributeMap, Float)} except taking a primitive float as parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(float)}
     */
    public AttributeMap set(AttributeMap attributes, float value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public float getDefaultValue() {
        return defaultFloatValue;
    }

    /**
     * Check if the specified value is either {@link Float#NEGATIVE_INFINITY},
     * {@link Float#POSITIVE_INFINITY} or {@link Float#NaN}. If it is, this method will throw an
     * {@link IllegalArgumentException}.
     * 
     * @param d
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is Infinity or NaN
     */
    protected void checkNotNaNInfinity(float d) {
        if (isNaNInfinity(d)) {
            throw new IllegalArgumentException("invalid " + getName() + " (" + getName() + " = "
                    + Float.toString(d) + ")");
        }
    }

    /**
     * Returns <code>true</code> if the specified value is either {@link Float#NEGATIVE_INFINITY},
     * {@link Float#POSITIVE_INFINITY} or {@link Float#NaN}. Otherwise, false
     * 
     * @param d
     *            the value to check
     * @return whether or not the specified value is Infinity or NaN
     */
    protected boolean isNaNInfinity(float d) {
        return Float.isNaN(d) || Float.isInfinite(d);
    }

    /**
     * Analogous to {@link #singleton(Float)} except taking a primitive float as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(float value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }
}

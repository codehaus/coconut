/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.util.Comparator;

/**
 * An implementation of an {@link Attribute} mapping to a float. This implementation adds a number of
 * methods that works on primitive floats instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: FloatAttribute.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

public abstract class FloatAttribute extends Attribute<Float> implements
         Comparator<WithAttributes> {
         
    /** The default value of this attribute. */
    private final transient float defaultValue;

    /**
     * Creates a new FloatAttribute with a generated name and a default value of <tt>0</tt>.
     * 
     * @throws IllegalArgumentException
     *             if 0 is not a valid value according to {@link #checkValid(int)}
     */
    public FloatAttribute() {
        this(0f);
    }

    /**
     * Creates a new FloatAttribute with a generated name.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(float)}
     */
    public FloatAttribute(float defaultValue) {
        super(Float.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new FloatAttribute with a default value of <tt>0</tt>.
     * 
     * @param name
     *            the name of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if 0 is not a valid value according to {@link #checkValid(float)}
     */
    public FloatAttribute(String name) {
        this(name, 0f);
    }

    /**
     * Creates a new FloatAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(float)}
     */
    public FloatAttribute(String name, float defaultValue) {
        super(name, Float.TYPE, defaultValue);
        this.defaultValue = defaultValue;
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
     * {@link Float#NEGATIVE_INFINITY}, {@link Float#POSITIVE_INFINITY} or {@link {object}#NaN}.
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
    public int compare(WithAttributes w1, WithAttributes w2) {
        float thisVal = get(w1);
        float anotherVal = get(w2);
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
    public float fromString(String str) {
        return Float.parseFloat(str);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}, but returning a primitive int instead.
     * 
     * @return the default value of this attribute
     */
    public float getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>float</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @return the value of this attribute if this attribute is present in the map. Otherwise
     *         {@link #getDefaultValue()}
     */
    public float get(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>float</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @param defaultValue
     *            the default value to return if this attribute is not present in the map
     * @return the value of this attribute if this attribute is present in the map. Otherwise the
     *         specified default value
     */
    public float get(WithAttributes attributes, float defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
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
    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Float value) {
        return isValid(value.floatValue());
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
     *             if the specified value is not valid accordingly to {@link #checkValid(Float)}
     */
    public AttributeMap set(AttributeMap attributes, float value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, float value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Analogous to {@link #singleton(Float)} except taking a primitive float as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(float value) {
        return super.singleton(value);
    }

    /**
     * Check if the specified value is either {@link Float#NEGATIVE_INFINITY},
     * {@link Float#POSITIVE_INFINITY} or {@link Float#NaN}. If it is, this method will throw an
     * {@link IllegalArgumentException}.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is Infinity or NaN
     */
    protected void checkNotNaNInfinity(float value) {
        if (isNaNInfinity(value)) {
            throw new IllegalArgumentException("invalid " + getName() + " (" + getName() + " = "
                    + Float.toString(value) + ")");
        }
    }
    
    /**
     * Returns <code>true</code> if the specified value is either {@link Float#NEGATIVE_INFINITY},
     * {@link Float#POSITIVE_INFINITY} or {@link Float#NaN}. Otherwise, false
     * 
     * @param value
     *            the value to check
     * @return whether or not the specified value is Infinity or NaN
     */
    protected boolean isNaNInfinity(float value) {
        return Float.isNaN(value) || Float.isInfinite(value);
    }
}
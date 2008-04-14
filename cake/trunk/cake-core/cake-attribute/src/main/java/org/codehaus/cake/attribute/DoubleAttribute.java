/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.util.Comparator;

/**
 * An implementation of an {@link Attribute} mapping to a double. This implementation adds a number of
 * methods that works on primitive doubles instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DoubleAttribute.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

public abstract class DoubleAttribute extends Attribute<Double> implements
         Comparator<WithAttributes> {
         
    /** The default value of this attribute. */
    private final transient double defaultValue;

    /**
     * Creates a new DoubleAttribute with a generated name and a default value of <tt>0</tt>.
     * 
     * @throws IllegalArgumentException
     *             if 0 is not a valid value according to {@link #checkValid(int)}
     */
    public DoubleAttribute() {
        this(0d);
    }

    /**
     * Creates a new DoubleAttribute with a generated name.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(double)}
     */
    public DoubleAttribute(double defaultValue) {
        super(Double.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new DoubleAttribute with a default value of <tt>0</tt>.
     * 
     * @param name
     *            the name of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if 0 is not a valid value according to {@link #checkValid(double)}
     */
    public DoubleAttribute(String name) {
        this(name, 0d);
    }

    /**
     * Creates a new DoubleAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(double)}
     */
    public DoubleAttribute(String name, double defaultValue) {
        super(name, Double.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }
    
    /** {@inheritDoc} */
    @Override
    public final void checkValid(Double o) {
        checkValid(o.doubleValue());
    }
    
    /**
     * Analogous to {@link #checkValid(Double)} except taking a primitive double.
     * <p>
     * The default implementation fails if the specified value is either
     * {@link Double#NEGATIVE_INFINITY}, {@link Double#POSITIVE_INFINITY} or {@link {object}#NaN}.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(double value) {
        checkNotNaNInfinity(value);
    }
    
    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        double thisVal = get(w1);
        double anotherVal = get(w2);
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
    public double fromString(String str) {
        return Double.parseDouble(str);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}, but returning a primitive int instead.
     * 
     * @return the default value of this attribute
     */
    public double getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>double</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @return the value of this attribute if this attribute is present in the map. Otherwise
     *         {@link #getDefaultValue()}
     */
    public double get(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>double</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @param defaultValue
     *            the default value to return if this attribute is not present in the map
     * @return the value of this attribute if this attribute is present in the map. Otherwise the
     *         specified default value
     */
    public double get(WithAttributes attributes, double defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /**
     * Works as {@link Attribute#isValid(Object)} except taking a primitive double. The default
     * implementation returns <code>false</code> for {@link Double#NEGATIVE_INFINITY},
     * {@link Double#POSITIVE_INFINITY} and {@link Double#NaN}.
     * 
     * @return whether or not the value is valid
     * @param value
     *            the value to check
     */
    public boolean isValid(double value) {
        return !isNaNInfinity(value);
    }
    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Double value) {
        return isValid(value.doubleValue());
    }

    /**
     * Analogous to {@link #set(AttributeMap, Double)} except taking a primitive double as parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Double)}
     */
    public AttributeMap set(AttributeMap attributes, double value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, double value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Analogous to {@link #singleton(Double)} except taking a primitive double as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(double value) {
        return super.singleton(value);
    }

    /**
     * Check if the specified value is either {@link Double#NEGATIVE_INFINITY},
     * {@link Double#POSITIVE_INFINITY} or {@link Double#NaN}. If it is, this method will throw an
     * {@link IllegalArgumentException}.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is Infinity or NaN
     */
    protected void checkNotNaNInfinity(double value) {
        if (isNaNInfinity(value)) {
            throw new IllegalArgumentException("invalid " + getName() + " (" + getName() + " = "
                    + Double.toString(value) + ")");
        }
    }
    
    /**
     * Returns <code>true</code> if the specified value is either {@link Double#NEGATIVE_INFINITY},
     * {@link Double#POSITIVE_INFINITY} or {@link Double#NaN}. Otherwise, false
     * 
     * @param value
     *            the value to check
     * @return whether or not the specified value is Infinity or NaN
     */
    protected boolean isNaNInfinity(double value) {
        return Double.isNaN(value) || Double.isInfinite(value);
    }
}
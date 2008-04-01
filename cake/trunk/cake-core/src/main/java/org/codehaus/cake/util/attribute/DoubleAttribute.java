/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import java.io.Serializable;

import jsr166y.forkjoin.Ops.ObjectToDouble;

/**
 * An abstract implementation of an {@link Attribute} mapping to a double. This
 * implementation add a number of methods that works on primitive doubles instead of their
 * object counterpart.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class DoubleAttribute extends Attribute<Double> {

    /** The default value of this attribute. */
    private final double defaultDoubleValue;

    /**
     * A MapperToDouble that takes an AttributeMap and returns the value of this
     * attribute.
     */
    private final AttributeMapToDouble mapperToDouble = new AttributeMapToDouble();

    /**
     * Creates a new AbstractDoubleAttribute.
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
    public DoubleAttribute(String name, double defaultValue) {
        super(name, Double.TYPE, defaultValue);
        this.defaultDoubleValue = defaultValue;
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
     * {@link Double#NEGATIVE_INFINITY}, {@link Double#POSITIVE_INFINITY} or
     * {@link Double#NaN}.
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
    @Override
    public Double fromString(String str) {
        return Double.parseDouble(str);
    }

    /**
     * Analogous to {@link #get(AttributeMap)} except returning a primitive
     * <tt>double</tt>.
     *
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public double getValue(AttributeMap attributes) {
        return attributes.getDouble(this, defaultDoubleValue);
    }

    /**
     * Analogous to {@link #get(AttributeMap, Double)} except returning a primitive
     * <tt>double</tt>.
     *
     * @param attributes
     *            the attribute map to check for this attribute in
     * @param defaultValue
     *            the value to return if this attribute is not set in the specified
     *            attribute map
     * @return the value of this attribute
     */
    public double getValue(AttributeMap attributes, double defaultValue) {
        return attributes.getDouble(this, defaultValue);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Double value) {
        return isValid(value.doubleValue());
    }

    /**
     * Works as {@link Attribute#isValid(Object)} except taking a primitive double. The
     * default implementation returns <code>false</code> for
     * {@link Double#NEGATIVE_INFINITY}, {@link Double#POSITIVE_INFINITY} and
     * {@link Double#NaN}.
     *
     * @return whether or not the value is valid
     * @param value
     *            the value to check
     */
    public boolean isValid(double value) {
        return !isNaNInfinity(value);
    }

    /**
     * Returns a mapper that extracts the value of this attribute from an
     * {@link AttributeMap}, or returns {@link #getDefault()} if this attribute is
     * not present.
     *
     * @return a mapper from an AttributeMap to the value of this attribute
     */
    public ObjectToDouble<AttributeMap> mapToDouble() {
        return mapperToDouble;
    }

    /**
     * Analogous to {@link #set(AttributeMap, Double)} except taking a primitive
     * double as parameter.
     *
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to
     *             {@link #checkValid(double)}
     */
    public AttributeMap set(AttributeMap attributes, double value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.putDouble(this, value);
        return attributes;
    }
    public double getDefaultValue() {
        return defaultDoubleValue;
    }

    /**
     * Check if the specified value is either {@link Double#NEGATIVE_INFINITY},
     * {@link Double#POSITIVE_INFINITY} or {@link Double#NaN}. If it is, this method will
     * throw an {@link IllegalArgumentException}.
     *
     * @param d
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is Infinity or NaN
     */
    protected void checkNotNaNInfinity(double d) {
        if (isNaNInfinity(d)) {
            throw new IllegalArgumentException("invalid " + getName() + " (" + getName() + " = "
                    + Double.toString(d) + ")");
        }
    }

    /**
     * Returns <code>true</code> if the specified value is either
     * {@link Double#NEGATIVE_INFINITY}, {@link Double#POSITIVE_INFINITY} or
     * {@link Double#NaN}. Otherwise, false
     *
     * @param d
     *            the value to check
     * @return whether or not the specified value is Infinity or NaN
     */
    protected boolean isNaNInfinity(double d) {
        return Double.isNaN(d) || Double.isInfinite(d);
    }

    /**
     * Analogous to {@link #singleton(Double)} except taking a primitive double as
     * parameter.
     *
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified
     *         value
     */
    public AttributeMap singleton(double value) {
        return super.singleton(value);
    }

    /**
     * A MapperToDouble that maps from an attribute map to the value of this attribute.
     */
    class AttributeMapToDouble implements ObjectToDouble<AttributeMap>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -953844729549732090L;

        /** {@inheritDoc} */
        public double op(AttributeMap t) {
            return getValue(t);
        }
    }
}

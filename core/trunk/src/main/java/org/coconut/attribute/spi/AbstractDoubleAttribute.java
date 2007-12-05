/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import java.io.Serializable;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.spi.AbstractLongAttribute.AttributeMapToLong;
import org.coconut.operations.Ops;

/**
 * An abstract implementation of an {@link Attribute} mapping to a double.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractDoubleAttribute extends AbstractAttribute<Double> {

    /** The default value of this attribute. */
    private final double defaultValue;

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
     */
    public AbstractDoubleAttribute(String name, double defaultValue) {
        super(name, Double.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Double o) {
        checkValid(o.doubleValue());
    }

    /**
     * Works as {@link #checkValid(Double)} except taking a primitive double. The default
     * implementation fails for {@link Double#NEGATIVE_INFINITY},
     * {@link Double#POSITIVE_INFINITY} and {@link Double#NaN}.
     * 
     * @param value
     *            the value to check
     */
    public void checkValid(double value) {
        checkNotNaNInfinity(value);
    }

    /** {@inheritDoc} */
    public Double fromString(String str) {
        return Double.parseDouble(str);
    }

    public double getPrimitive(AttributeMap attributes) {
        return attributes.getDouble(this, defaultValue);
    }

    public double getPrimitive(AttributeMap attributes, double defaultValue) {
        return attributes.getDouble(this, defaultValue);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Double value) {
        return isValid(value.doubleValue());
    }

    /**
     * Works as {@link #isValid(Double)} except taking a primitive double. The default
     * implementation returns <code>false</code> for {@link Double#NEGATIVE_INFINITY},
     * {@link Double#POSITIVE_INFINITY} and {@link Double#NaN}.
     * 
     * @return whether or not the value is valid
     * @param value
     *            the value to check
     */
    public boolean isValid(double value) {
        return notNaNInfinity(value);
    }

    public Ops.MapperToDouble<AttributeMap> mapToDouble() {
        return mapperToDouble;
    }

    public AttributeMap setAttribute(AttributeMap attributes, double object) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(object);
        attributes.putDouble(this, object);
        return attributes;
    }

    protected void checkNotNaNInfinity(double d) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("invalid " + getName() + " (" + getName()
                    + " = NaN)");
        } else if (Double.isInfinite(d)) {
            throw new IllegalArgumentException("invalid " + getName() + " (" + getName()
                    + " = Infinity)");
        }
    }

    protected boolean notNaNInfinity(double d) {
        return !Double.isNaN(d) && !Double.isInfinite(d);
    }

    class AttributeMapToDouble implements Ops.MapperToDouble<AttributeMap>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -953844729549732090L;

        /** {@inheritDoc} */
        public double map(AttributeMap t) {
            return getPrimitive(t);
        }
    }
}

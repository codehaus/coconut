/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import org.coconut.attribute.AttributeMap;

public abstract class AbstractDoubleAttribute extends AbstractAttribute<Double> {

    /** The default value of this attribute. */
    private final double defaultValue;

    public AbstractDoubleAttribute(String name, double defaultValue) {
        super(name, Double.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Double o) {
        checkValid(o.doubleValue());
    }

    public void checkValid(double d) {
        checkNotNaNInfinity(d);
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

    public boolean isValid(double value) {
        return notNaNInfinity(value);
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
}
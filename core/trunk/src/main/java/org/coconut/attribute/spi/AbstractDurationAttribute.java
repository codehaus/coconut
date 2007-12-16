/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractDurationAttribute extends AbstractLongAttribute {

    /** The default value of this attribute. */
    protected static final long DEFAULT_DURATION = Long.MAX_VALUE;

    /**
     * Creates a new DurationAttribute.
     * 
     * @param name
     *            the name of the attribute
     */
    public AbstractDurationAttribute(String name) {
        super(name, DEFAULT_DURATION);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(long value) {
        return value > 0;
    }

    /**
     * Analogous to {@link #getPrimitive(AttributeMap)} except taking a parameter
     * indicating what time unit the value should be returned in.
     * 
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @param unit
     *            the time unit to return the value in
     * @return the value of this attribute
     */
    public long getPrimitive(AttributeMap attributes, TimeUnit unit) {
        return convertTo(getPrimitive(attributes), unit);
    }

    public long getPrimitive(AttributeMap attributes, TimeUnit unit, long defaultValue) {
        long val = getPrimitive(attributes, 0);
        if (val == 0) {
            return defaultValue;
        } else {
            return convertTo(val, unit);
        }
    }

    public AttributeMap setAttribute(AttributeMap attributes, long duration, TimeUnit unit) {
        return setAttribute(attributes, convertFrom(duration, unit));
    }

    public AttributeMap setAttribute(AttributeMap attributes, Long duration, TimeUnit unit) {
        return setAttribute(attributes, duration.longValue(), unit);
    }

    /**
     * Returns an immutable AttributeMap containing only this attribute mapping to the
     * specified value.
     * 
     * @param value
     *            the value to create the singleton from
     * @param unit
     *            the time unit of the value
     * @return an AttributeMap containing only this attribute mapping to the specified
     *         value
     */
    protected AttributeMap toSingleton(long value, TimeUnit unit) {
        return super.toSingletonLong(convertFrom(value, unit));
    }

    static long convertFrom(long value, TimeUnit unit) {
        if (value == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.toNanos(value);
        }
    }

    static long convertTo(long value, TimeUnit unit) {
        if (value == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.convert(value, TimeUnit.NANOSECONDS);
        }
    }
}

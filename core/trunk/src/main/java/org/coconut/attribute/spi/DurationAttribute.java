/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class DurationAttribute extends LongAttribute {
    protected static long DEFAULT_DURATION = Long.MAX_VALUE;

    /**
     * Creates a new DurationAttribute.
     * 
     * @param name
     *            the name of the attribute
     */
    public DurationAttribute(String name) {
        super(name, DEFAULT_DURATION);
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("invalid " + getName() + "refreshTime (" + getName()
                    + " = " + time + ")");
        }
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(long value) {
        return value >= 0;
    }

    public long getPrimitive(AttributeMap attributes, TimeUnit unit) {
        return convertTo(getPrimitive(attributes), unit);
    }

    public long getPrimitive(AttributeMap attributes, TimeUnit unit, long defaultValue) {
        long val = getPrimitive(attributes, -1);
        if (val == -1) {
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

    protected AttributeMap s(long value, TimeUnit unit) {
        final long converted = convertFrom(value, unit);
        checkValid(converted);
        return AttributeMaps.singleton(this, converted);
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

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;

public abstract class DurationAttribute extends LongAttribute {

    public DurationAttribute(String name) {
        super(name,0l);
    }

    public long getPrimitive(AttributeMap attributes, TimeUnit unit) {
        return unit.convert(getPrimitive(attributes), TimeUnit.NANOSECONDS);
    }

    public long getPrimitive(AttributeMap attributes, TimeUnit unit, long defaultValue) {
        long val = getPrimitive(attributes);
        if (val == 0) {
            return defaultValue;
        } else {
            return unit.convert(val, TimeUnit.NANOSECONDS);
        }
    }

    public AttributeMap setAttribute(AttributeMap attributes, long duration, TimeUnit unit) {
        return setAtttribute(attributes, unit.toNanos(duration));
    }

    public AttributeMap set(AttributeMap attributes, Long duration, TimeUnit unit) {
        return setAttribute(attributes, duration.longValue(), unit);
    }
}

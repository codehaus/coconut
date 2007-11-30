/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.attribute.spi.DurationAttribute;
/**
 * This key can be used to indicate how long time a cache entry should live before it
 * refreshed from a cacheloader. The time-to-refresh value should be a long and should
 * be measured in nano seconds. Use {@link java.util.concurrent.TimeUnit} to convert
 * between different time units.
 */
public class TimeToRefreshAttribute extends DurationAttribute {

    /** The default value of this attribute. */
    public static final long DEFAULT_VALUE = 0;

    /** The singleton instance of this attribute. */
    public final static TimeToRefreshAttribute INSTANCE = new TimeToRefreshAttribute();
    /** The timeunit of this attribute. */
    public static final TimeUnit TIME_UNIT = TimeUnit.NANOSECONDS;

    /** The name of this attribute. */
    public static final String NAME = "timeToRefresh";

    /** serialVersionUID. */
    private static final long serialVersionUID = -2353351535602223603L;

    /** Creates a new TimeToLiveAttribute. */
    private TimeToRefreshAttribute() {
        super(NAME);
    }
    /** {@inheritDoc} */
    @Override
    public void checkValid(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("invalid refreshTime (refreshTimeNs = " + time + ")");
        }
    }
    /** {@inheritDoc} */
    @Override
    public boolean isValid(long value) {
        return value >= 0;
    }

    /** @return Preserves singleton property */
    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * Returns the value of this attribute in the specified attribute map, or DEFAULT_VALUE
     * if the attribute is not mapped to any value in the specified attribute map.
     * 
     * @param attributes the attribute map to return the value from
     * @return the value of this attribute in the specified attribute map, or DEFAULT_VALUE
     * if the attribute is not mapped to any value in the specified attribute map
     */
    public static long get(AttributeMap attributes) {
        return INSTANCE.getPrimitive(attributes);
    }

    /**
     * Sets the value of this attribute in the specified attribute map.
     * 
     * @param attributes
     *            the attribute map to set set specified value in
     * @param value
     *            the value that this attribute should be set to
     * @return the specified attribute map
     */
    public static AttributeMap set(AttributeMap attributes, long value) {
        return INSTANCE.setAttribute(attributes, value);
    }

    /**
     * Returns an AttributeMap containing only this attribute mapping to specified value.
     * 
     * @param value
     *            the value to map to
     * @return an AttributeMap containing only this attribute mapping to specified value
     */
    public static AttributeMap singleton(long value) {
        INSTANCE.checkValid(value);
        return AttributeMaps.singleton(INSTANCE, value);
    }
}

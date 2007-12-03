/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.spi.AbstractDurationAttribute;

/**
 * This key can be used to indicate how long time a cache entry should live before it
 * refreshed from a cacheloader. The time-to-refresh value should be a long and should be
 * measured in nano seconds. Use {@link java.util.concurrent.TimeUnit} to convert between
 * different time units.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class TimeToRefreshAttribute extends AbstractDurationAttribute {

    /** The default value of this attribute. */
    public static final long DEFAULT_VALUE = AbstractDurationAttribute.DEFAULT_DURATION;

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

    /** @return Preserves singleton property */
    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * Returns the value of this attribute in the specified attribute map, or
     * DEFAULT_VALUE if the attribute is not mapped to any value in the specified
     * attribute map.
     * 
     * @param attributes
     *            the attribute map to return the value from
     * @return the value of this attribute in the specified attribute map, or
     *         DEFAULT_VALUE if the attribute is not mapped to any value in the specified
     *         attribute map
     */
    public static long get(AttributeMap attributes) {
        return INSTANCE.getPrimitive(attributes);
    }

    /**
     * Sets the value of this attribute in the specified attribute map.
     * 
     * @param attributes
     *            the attribute map to set set specified value in
     * @param duration
     *            the value that this attribute should be set to
     * @param unit
     *            the time unit of the specified duration
     * @return the specified attribute map
     */
    public static AttributeMap set(AttributeMap attributes, long duration, TimeUnit unit) {
        return INSTANCE.setAttribute(attributes, duration, unit);
    }

    /**
     * Returns an AttributeMap containing only this attribute mapping to the specified value.
     * 
     * @param value
     *            the value to map to
     * @param unit
     *            the time unit of the specified value
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public static AttributeMap singleton(long value, TimeUnit unit) {
        return INSTANCE.toSingleton(value, unit);
    }
}

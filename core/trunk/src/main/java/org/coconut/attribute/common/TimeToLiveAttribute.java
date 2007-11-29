/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.spi.DurationAttribute;

public class TimeToLiveAttribute extends DurationAttribute {
    public final static TimeToLiveAttribute INSTANCE = new TimeToLiveAttribute();
    private TimeToLiveAttribute() {
        super("TimeToLive");
    }

    /**
     * This key can be used to indicate how long time a cache entry should live before it
     * expires. The time-to-live value should be a long between 1 and
     * {@link Long#MAX_VALUE} measured in nanoseconds. Use
     * {@link java.util.concurrent.TimeUnit} to convert between different time units.
     * 
     * @see org.coconut.cache.service.expiration.CacheExpirationService
     */
    public static final String TIME_TO_LIVE_NS = "time_to_live_ns";

//    /**
//     * Sets a value for the {@link #TIME_TO_LIVE_NS} attribute in the specified
//     * AttributeMap.
//     * 
//     * @param attributes
//     *            the map of attributes to set the cost attribute in
//     * @param timeToLive
//     *            the time to live
//     * @param unit
//     *            the unit of the specified time to live
//     * @return the specified attribute map
//     * @throws NullPointerException
//     *             if the specified attributeMap or time unit is <code>null</code>
//     * @throws IllegalArgumentException
//     *             if the specified time to live is a negative number
//     * @see #getTimeToLive(AttributeMap, TimeUnit, long)
//     * @see #TIME_TO_LIVE_NS
//     */
//    public static AttributeMap setTimeToLive(AttributeMap attributes, long timeToLive, TimeUnit unit) {
//        if (attributes == null) {
//            throw new NullPointerException("attributes is null");
//        } else if (timeToLive < 0) {
//            throw new IllegalArgumentException("timeToLive must not be negative, was " + timeToLive);
//        } else if (unit == null) {
//            throw new NullPointerException("unit is null");
//        }
//        if (timeToLive == 0) {
//            // ignore
//        } else if (timeToLive == Long.MAX_VALUE) {
//            CacheAttributes.TIME_TO_REFRESH_ATR.set(attributes, Long.MAX_VALUE);
//        } else {
//            CacheAttributes.TIME_TO_REFRESH_ATR.set(attributes, timeToRefresh, unit);
//        }
//        return attributes;
//    }
//
//    /**
//     * Returns the value that the specified AttributeMap maps the {@link #TIME_TO_LIVE_NS}
//     * attribute to or the default specified value if no such mapping exist.
//     * 
//     * @param attributes
//     *            the map to retrieve the value of the time to live attribute from
//     * @param unit
//     *            the unit that the time should be returned in
//     * @param defaultValue
//     *            the value that should be returned if a mapping for the time to live
//     *            attribute does not exist in the specified attribute map
//     * @return returns the value that the specified AttributeMap maps the
//     *         {@link #TIME_TO_LIVE_NS} attribute to default specified value if no such
//     *         mapping exist
//     * @throws NullPointerException
//     *             if the specified attributeMap is <code>null</code>
//     * @throws IllegalArgumentException
//     *             if the specified attributeMap returns a negative number
//     * @see #setTimeToLive(AttributeMap, long, TimeUnit)
//     * @see #TIME_TO_LIVE_NS
//     */
//    public static long getTimeToLive(AttributeMap attributes, TimeUnit unit, long defaultValue) {
//        if (attributes == null) {
//            throw new NullPointerException("attributes is null");
//        } else if (unit == null) {
//            throw new NullPointerException("unit is null");
//        } else if (defaultValue <= 0) {
//            throw new IllegalArgumentException("defaultValue must be a positive value");
//        }
//        long ttl = attributes.getLong(TIME_TO_LIVE_NS);
//        if (ttl < 0) {
//            throw new IllegalArgumentException("invalid timeToLive (timeToLiveNs = " + ttl + ")");
//        }
//        if (ttl == 0) {
//            return defaultValue;
//        } else if (ttl == Long.MAX_VALUE) {
//            return Long.MAX_VALUE;
//        } else {
//            return unit.convert(ttl, TimeUnit.NANOSECONDS);
//        }
//    }

    @Override
    public boolean isValid(long ttl) {
        return ttl >= 0;
    }

    @Override
    public void checkValid(long ttl) {
        if (ttl < 0) {
            throw new IllegalArgumentException("invalid timeToLive (timeToLiveNs = " + ttl + ")");
        }
    }
}

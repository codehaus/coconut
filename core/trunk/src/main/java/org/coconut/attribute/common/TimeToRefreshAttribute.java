/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.spi.DurationAttribute;

public class TimeToRefreshAttribute extends DurationAttribute {
    public final static TimeToRefreshAttribute INSTANCE = new TimeToRefreshAttribute();
    private TimeToRefreshAttribute() {
        super("TimeToRefresh");
    }

    /**
     * This key can be used to indicate how long time a cache entry should live before it
     * refreshed from a cacheloader. The time-to-refresh value should be a long and should
     * be measured in nano seconds. Use {@link java.util.concurrent.TimeUnit} to convert
     * between different time units.
     */
    public static final String TIME_TO_REFRESH_NS = "time_to_refresh_ns";

    @Override
    protected void checkValid(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("invalid refreshTime (refreshTimeNs = " + time + ")");
        }
    }

    @Override
    public boolean isValid(long value) {
        return value >= 0;
    }

//
// /**
// * Returns the value that the specified AttributeMap maps the
// * {@link #TIME_TO_REFRESH_NS} attribute to or the default specified value if no such
// * mapping exist.
// *
// * @param attributes
// * the map to retrieve the value of the time to refresh attribute from
// * @param unit
// * the unit that the time should be returned in
// * @param defaultValue
// * the value that should be returned if a mapping for the time to live
// * attribute does not exist in the specified attribute map
// * @return returns the value that the specified AttributeMap maps the
// * {@link #TIME_TO_REFRESH_NS} attribute to, or the default specified value if
// * no such mapping exist
// * @throws NullPointerException
// * if the specified attributeMap is <code>null</code>
// * @throws IllegalArgumentException
// * if the specified attributeMap returns a negative number for the time to
// * refresh attribute
// * @see #setTimeToRefresh(AttributeMap, long, TimeUnit)
// * @see #TIME_TO_REFRESH_NS
// */
// public static long getTimeToRefresh(AttributeMap attributes, TimeUnit unit, long
// defaultValue) {
// if (attributes == null) {
// throw new NullPointerException("attributes is null");
// } else if (unit == null) {
// throw new NullPointerException("unit is null");
// } else if (defaultValue <= 0) {
// throw new IllegalArgumentException("defaultValue must be a positive value");
// }
// long ttl = attributes.getLong(TIME_TO_REFRESH_NS);
// if (ttl < 0) {
// throw new IllegalArgumentException("invalid refreshTime (refreshTimeNs = " + ttl +
// ")");
// }
// if (ttl == 0) {
// return defaultValue;
// } else if (ttl == Long.MAX_VALUE) {
// return Long.MAX_VALUE;
// } else {
// return unit.convert(ttl, TimeUnit.NANOSECONDS);
// }
// }

}

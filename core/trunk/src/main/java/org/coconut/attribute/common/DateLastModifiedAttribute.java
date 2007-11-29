/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.spi.LongAttribute;
import org.coconut.core.Clock;

public class DateLastModifiedAttribute extends LongAttribute {

    public final static DateLastModifiedAttribute INSTANCE = new DateLastModifiedAttribute();

    private DateLastModifiedAttribute() {
        super("LastModifiedTime",0l);
    }

    @Override
    public boolean isValid(long time) {
        return time >= 0;

    }

    @Override
    public void checkValid(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("creationTime was negative (creationTime = " + time
                    + ")");
        }
    }

    /**
     * The <tt>Last updated time</tt> attribute indicates when a cache element was last
     * updated. The mapped value must be of a type <tt>long</tt> between 1 and
     * {@link Long#MAX_VALUE}.
     * 
     * @see #setLastUpdated(AttributeMap, long)
     * @see #getLastUpdated(AttributeMap, Clock)
     */
    public static final String LAST_UPDATED_TIME = "last_updated";

// /**
// * Sets a value for the {@link #LAST_UPDATED_TIME} attribute in the specified
// * AttributeMap.
// *
// * @param attributes
// * the map of attributes to set the last updated time attribute in
// * @param lastUpdated
// * the last updated time
// * @return the specified attribute map
// * @throws NullPointerException
// * if the specified attributeMap is <code>null</code>
// * @throws IllegalArgumentException
// * if the specified last updated time is a negative number
// * @see #getLastUpdated(AttributeMap, Clock)
// * @see #LAST_UPDATED_TIME
// */
// public static AttributeMap setLastUpdated(AttributeMap attributes, long lastUpdated) {
// if (attributes == null) {
// throw new NullPointerException("attributes is null");
// } else if (lastUpdated < 0) {
// throw new IllegalArgumentException("invalid creationTime (creationTime = "
// + lastUpdated + ")");
// }
// attributes.putLong(LAST_UPDATED_TIME, lastUpdated);
// return attributes;
// }
// /**
// * Returns the value that the specified AttributeMap maps the
// * {@link #LAST_UPDATED_TIME} attribute to or the return value from a call to
// * {@link Clock#timestamp()} on the specified clock if no such mapping exist.
// *
// * @param attributes
// * the map to retrieve the value of the creation time attribute from
// * @param clock
// * the clock to retrieve the timestamp from
// * @return returns the value that the specified AttributeMap maps the
// * {@link #LAST_UPDATED_TIME} attribute to or the return value from a call to
// * {@link Clock#timestamp()} on the specified clock if no such mapping exist
// * @throws NullPointerException
// * if the specified attributeMap or clock is <code>null</code>
// * @throws IllegalArgumentException
// * if the specified attributeMap returns a negative number or if the
// * specified clock returns a negative number when calling
// * {@link Clock#timestamp()}
// * @see #setLastUpdated(AttributeMap, long)
// * @see #LAST_UPDATED_TIME
// */
// public static long getLastUpdated(AttributeMap attributes, Clock clock) {
// if (attributes == null) {
// throw new NullPointerException("attributes is null");
// } else if (clock == null) {
// throw new NullPointerException("clock is null");
// }
// long time = attributes.getLong(CacheAttributes.LAST_UPDATED_TIME);
// if (time < 0) {
// throw new IllegalArgumentException("lastUpdated was negative (lastUpdated = " + time
// + ")");
// } else if (time == 0) {
// time = clock.timestamp();
// if (time < 0) {
// throw new IllegalArgumentException(
// "the timestamp returned by the specified clock was negative (lastUpdated = "
// + time + ")");
// }
// }
// return time;
// }
}

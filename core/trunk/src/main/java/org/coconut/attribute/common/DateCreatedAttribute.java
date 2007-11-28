/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.spi.LongAttribute;

public class DateCreatedAttribute extends LongAttribute {
    public final static DateCreatedAttribute INSTANCE = new DateCreatedAttribute();

    /**
     * The <tt>Creation time</tt> attribute indicates the creation time of a cache
     * element. The mapped value must be of a type <tt>long</tt> between 1 and
     * {@link Long#MAX_VALUE}.
     * 
     * @see #setCreationTime(AttributeMap, long)
     * @see #getCreationTime(AttributeMap)
     * @see #getCreationTime(AttributeMap, Clock)
     */
    public static final String CREATION_TIME = "creation_time";

    private DateCreatedAttribute() {
        super("CreationTime", 0l);
    }

    @Override
    public boolean isValid(long time) {
        return time >= 0;

    }

    @Override
    protected void checkValid(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("creationTime was negative (creationTime = " + time
                    + ")");
        }
    }
}

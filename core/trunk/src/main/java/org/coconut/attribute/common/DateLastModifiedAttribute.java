/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.attribute.spi.AbstractLongAttribute;
import org.coconut.core.Clock;

/**
 * The <tt>Last updated time</tt> attribute indicates when a cache element was last
 * updated. The mapped value must be of a type <tt>long</tt> between 1 and
 * {@link Long#MAX_VALUE}.
 * 
 * @see #setLastUpdated(AttributeMap, long)
 * @see #getLastUpdated(AttributeMap, Clock)
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class DateLastModifiedAttribute extends AbstractLongAttribute {

    /** The default value of this attribute. */
    public static final long DEFAULT_VALUE = 0;

    /** The singleton instance of this attribute. */
    public final static DateLastModifiedAttribute INSTANCE = new DateLastModifiedAttribute();

    /** The name of this attribute. */
    public static final String NAME = "lastModified";

    /** serialVersionUID. */
    private static final long serialVersionUID = -2353351535602223603L;

    /** Creates a new SizeAttribute. */
    private DateLastModifiedAttribute() {
        super(NAME, DEFAULT_VALUE);
    }

    /** {@inheritDoc} */
    @Override
    public void checkValid(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("creationTime was negative (creationTime = " + time
                    + ")");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid(long time) {
        return time >= 0;

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
     * @param value
     *            the value that this attribute should be set to
     * @return the specified attribute map
     */
    public static AttributeMap set(AttributeMap attributes, long value) {
        return INSTANCE.setAttribute(attributes, value);
    }

    /**
     * Returns an AttributeMap containing only this attribute mapping to the specified value.
     * 
     * @param value
     *            the value to map to
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public static AttributeMap singleton(long value) {
        INSTANCE.checkValid(value);
        return AttributeMaps.singleton(INSTANCE, value);
    }

}

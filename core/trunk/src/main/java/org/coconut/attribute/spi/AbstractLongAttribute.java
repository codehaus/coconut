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
public abstract class AbstractLongAttribute extends AbstractAttribute<Long> {

    /** The default value of this attribute. */
    private final long defaultValue;

    /**
     * Creates a new LongAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of this attribute
     */
    public AbstractLongAttribute(String name, long defaultValue) {
        super(name, Long.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Long o) {
        checkValid(o.longValue());
    }

    public void checkValid(long value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    /** {@inheritDoc} */
    public Long fromString(String str) {
        return Long.parseLong(str);
    }

    /**
     * As {@link #getValue(AttributeMap)} except that is returns a <tt>long</tt> and not
     * a <tt>Long</tt>.
     * 
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public long getPrimitive(AttributeMap attributes) {
        return attributes.getLong(this, defaultValue);
    }

    public long getPrimitive(AttributeMap attributes, long defaultValue) {
        return attributes.getLong(this, defaultValue);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Long value) {
        return isValid(value.longValue());
    }

    public boolean isValid(long value) {
        return true;
    }

    protected AttributeMap toSingleton(long value) {
        return super.toSingleton(value);
    }

    public AttributeMap setAttribute(AttributeMap attributes, long value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.putLong(this, value);
        return attributes;
    }
}

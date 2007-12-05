/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import java.io.Serializable;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.operations.Ops;

/**
 * An abstract implementation of an {@link Attribute} mapping to a long.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractLongAttribute extends AbstractAttribute<Long> {

    /** The default value of this attribute. */
    private final long defaultValue;

    /**
     * A MapperToLong that takes an AttributeMap and returns the value of this attribute.
     */
    private final Ops.MapperToLong<AttributeMap> mapperToLong = new AttributeMapToLong();

    /**
     * Creates a new AbstractLongAttribute.
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

    /**
     * Works as {@link #checkValid(Long)} except taking a primitive long.
     * 
     * @param value
     *            the value to check
     */
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

    /**
     * Works as {@link #isValid(Long)} except taking a primitive long.
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(long value) {
        return true;
    }

    /**
     * Returns a mapper that extracts the value of this attribute from an
     * {@link AttributeMap} or {@link #getDefaultValue()} if this attribute is not
     * present.
     * 
     * @return a mapper from an AttributeMap to the value of this attribute
     */
    public Ops.MapperToLong<AttributeMap> mapToLong() {
        return mapperToLong;
    }

    public AttributeMap setAttribute(AttributeMap attributes, long value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.putLong(this, value);
        return attributes;
    }

    /**
     * Returns an AttributeMap containing only this attribute mapping to the specified
     * value.
     * 
     * @param value
     *            the value to map to
     * @return an AttributeMap containing only this attribute mapping to the specified
     *         value
     */
    protected AttributeMap toSingleton(long value) {
        return super.toSingleton(value);
    }

    class AttributeMapToLong implements Ops.MapperToLong<AttributeMap>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -953844729549732090L;

        /** {@inheritDoc} */
        public long map(AttributeMap t) {
            return getPrimitive(t);
        }
    }
}

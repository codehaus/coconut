/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import java.io.Serializable;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.operations.LongPredicates;
import org.coconut.operations.Ops.LongPredicate;
import org.coconut.operations.Ops.MapperToLong;
import org.coconut.operations.Ops.Predicate;

/**
 * An abstract implementation of an {@link Attribute} mapping to a long. This
 * implementation adds a number of methods that works on primitive longs instead of their
 * object counterpart.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractLongAttribute extends AbstractAttribute<Long> {

    /** The default value of this attribute. */
    private final transient long defaultLongValue;

    /**
     * A MapperToLong that takes an AttributeMap and returns the value of this attribute.
     */
    private final transient MapperToLong<AttributeMap> mapperToLong = new AttributeMapToLong();

    /**
     * Creates a new AbstractLongAttribute.
     *
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of this attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value
     */
    public AbstractLongAttribute(String name, long defaultValue) {
        super(name, Long.TYPE, defaultValue);
        this.defaultLongValue = defaultValue;
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Long o) {
        checkValid(o.longValue());
    }

    /**
     * Analogous to {@link #checkValid(Long)} except taking a primitive long.
     *
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(long value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Long fromString(String str) {
        return Long.parseLong(str);
    }

    /**
     * Analogous to {@link #getValue(AttributeMap)} except returning a primitive
     * <tt>long</tt>.
     *
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public long getPrimitive(AttributeMap attributes) {
        return attributes.getLong(this, defaultLongValue);
    }

    /**
     * Analogous to {@link #getValue(AttributeMap, Long)} except returning a primitive
     * <tt>long</tt>.
     *
     * @param attributes
     *            the attribute map to check for this attribute in
     * @param defaultValue
     *            the value to return if this attribute is not set in the specified
     *            attribute map
     * @return the value of this attribute
     */
    public long getPrimitive(AttributeMap attributes, long defaultValue) {
        return attributes.getLong(this, defaultValue);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Long value) {
        return isValid(value.longValue());
    }

    /**
     * Analogous to {@link #isValid(Long)} except taking a primitive long as parameter.
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
     * {@link AttributeMap}, or returns {@link #getDefaultValue()} if this attribute is
     * not present.
     *
     * @return a mapper from an AttributeMap to the value of this attribute
     */
    public MapperToLong<AttributeMap> mapToLong() {
        return mapperToLong;
    }

    protected Predicate<AttributeMap> filterLong(LongPredicate p) {
        return LongPredicates.mapAndEvaluate(mapToLong(), p);
    }

    /**
     * Analogous to {@link #setValue(AttributeMap, Long)} except taking a primitive long
     * as parameter.
     *
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to
     *             {@link #checkValid(long)}
     */
    public AttributeMap setAttribute(AttributeMap attributes, long value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.putLong(this, value);
        return attributes;
    }

    /**
     * Analogous to {@link #singleton(Long)} except taking a primitive long as parameter.
     *
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified
     *         value
     */
    protected AttributeMap toSingletonLong(long value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }

    /**
     * A MapperToLong that maps from an attribute map to the value of this attribute.
     */
    class AttributeMapToLong implements MapperToLong<AttributeMap>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -953844729549732090L;

        /** {@inheritDoc} */
        public long map(AttributeMap t) {
            return getPrimitive(t);
        }
    }
}

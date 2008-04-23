/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.attribute;

import java.util.Comparator;

/**
 * An implementation of an {@link Attribute} mapping to a long. This implementation adds a number of
 * methods that works on primitive longs instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: LongAttribute.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class LongAttribute extends Attribute<Long> implements Comparator<WithAttributes> {

    /** The default value of this attribute. */
    private final transient long defaultValue;

    /**
     * Creates a new LongAttribute with a generated name and a default value of
     * <tt>$defaultValueNoCast</tt>.
     * 
     * @throws IllegalArgumentException
     *             if $defaultValueNoCast is not a valid value according to {@link #checkValid(int)}
     */
    public LongAttribute() {
        this(0L);
    }

    /**
     * Creates a new LongAttribute with a generated name.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(long)}
     */
    public LongAttribute(long defaultValue) {
        super(Long.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new LongAttribute with a default value of <tt>$defaultValueNoCast</tt>.
     * 
     * @param name
     *            the name of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if $defaultValueNoCast is not a valid value according to
     *             {@link #checkValid(long)}
     */
    public LongAttribute(String name) {
        this(name, 0L);
    }

    /**
     * Creates a new LongAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(long)}
     */
    public LongAttribute(String name, long defaultValue) {
        super(name, Long.TYPE, defaultValue);
        this.defaultValue = defaultValue;
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
            throw new IllegalArgumentException("Illegal value for attribute [name=" + getName()
                    + ", type = " + getClass() + ", value = " + value + "]");
        }
    }

    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        long thisVal = get(w1);
        long anotherVal = get(w2);
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /**
     * Creates a value instance of this attribute from the specified string.
     * 
     * @param str
     *            the string to create the value from.
     * @return a value instance from the specified string
     * @throws IllegalArgumentException
     *             if a valid value could not be created from the string.
     */
    public long fromString(String str) {
        return Long.parseLong(str);
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>long</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @return the value of this attribute if this attribute is present in the map. Otherwise
     *         {@link #getDefaultValue()}
     */
    public long get(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>long</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @param defaultValue
     *            the default value to return if this attribute is not present in the map
     * @return the value of this attribute if this attribute is present in the map. Otherwise the
     *         specified default value
     */
    public long get(WithAttributes attributes, long defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}, but returning a primitive int instead.
     * 
     * @return the default value of this attribute
     */
    public long getDefaultValue() {
        return defaultValue;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Long value) {
        return isValid(value.longValue());
    }

    /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive long as parameter.
     * <p>
     * The default version returns true for all parameters
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(long value) {
        return true;
    }

    /**
     * Analogous to {@link #set(AttributeMap, Long)} except taking a primitive long as parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Long)}
     */
    public AttributeMap set(AttributeMap attributes, long value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, long value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Analogous to {@link #singleton(Long)} except taking a primitive long as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(long value) {
        return super.singleton(value);
    }
}

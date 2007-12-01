/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import java.io.Serializable;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;

/**
 * An abstract implementation of {@link Attribute}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <T>
 *            the type of this attribute
 */
public abstract class AbstractAttribute<T> implements Attribute<T>, Serializable {

    /** The type of this attribute, as returned {@link #getAttributeType()}. */
    private final Class<T> clazz;

    /** The default value of this attribute. */
    private final T defaultValue;

    /** The name of this attribute. */
    private final String name;

    /**
     * Creates a new AbstractAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param clazz
     *            the type of this attribute
     * @param defaultValue
     *            the default value of this attribute
     */
    public AbstractAttribute(String name, Class<T> clazz, T defaultValue) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (clazz == null) {
            throw new NullPointerException("clazz is null");
        }
        this.name = name;
        this.clazz = clazz;
        checkValid(defaultValue);
        this.defaultValue = defaultValue;
    }

    /** {@inheritDoc} */
    public void checkValid(T value) {/* all values are acceped. */}

    /** {@inheritDoc} */
    public final Class<T> getAttributeType() {
        return clazz;
    }

    /** {@inheritDoc} */
    public T getDefaultValue() {
        return defaultValue;
    }

    /** {@inheritDoc} */
    public final String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public T getValue(AttributeMap attributes) {
        return (T) attributes.get(this, defaultValue);
    }

    /** {@inheritDoc} */
    public T getValue(AttributeMap attributes, T defaultValue) {
        return (T) attributes.get(this, defaultValue);
    }

    /** {@inheritDoc} */
    public boolean isSet(AttributeMap attributes) {
        return attributes.containsKey(this);
    }

    /** {@inheritDoc} */
    public boolean isValid(T value) {
        // all values are accepted.
        return true;
    }
    /** {@inheritDoc} */
    public AttributeMap setValue(AttributeMap attributes, T value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    /** {@inheritDoc} */
    public void unSet(AttributeMap attributes) {
        attributes.remove(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }
}

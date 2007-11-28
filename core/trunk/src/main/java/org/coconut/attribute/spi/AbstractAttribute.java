/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import java.io.Serializable;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;

public abstract class AbstractAttribute<T> implements Attribute<T>, Serializable {
    private final Class<T> clazz;

    private final String name;

    private final T defaultValue;

    public AbstractAttribute(String name, Class<T> clazz, T defaultValue) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (clazz == null) {
            throw new NullPointerException("clazz is null");
        }
        this.name = name;
        this.clazz = clazz;
        this.defaultValue = defaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T get(AttributeMap attributes) {
        return (T) attributes.get(this, defaultValue);
    }

    public T get(AttributeMap attributes, T defaultValue) {
        return (T) attributes.get(this, defaultValue);
    }

    public final Class<T> getAttributeType() {
        return clazz;
    }

    public final String getName() {
        return name;
    }

    public boolean isValid(T t) {
        return false;
    }

    public AttributeMap setAttribute(AttributeMap attributes, T object) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        // object==null??? override by longAttribute and DoubleAttribute

        checkValid(object);
        attributes.put(this, object);
        return attributes;
    }

    public void checkValid(T o) {}
}

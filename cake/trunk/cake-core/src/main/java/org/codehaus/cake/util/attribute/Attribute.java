/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import java.io.Serializable;

import jsr166y.forkjoin.Ops.Op;
import jsr166y.forkjoin.Ops.Predicate;

import org.codehaus.cake.jsr166y.ops.Predicates;

/**
 * Attribute-value pairs are a fundamental data representation in many computing systems and
 * applications. Designers often desire an open-ended data structure that allows for future
 * extension without modifying existing code or data. In such situations, all or part of the data
 * model may be expressed as a collection of tuples attribute name, value; each element is an
 * attribute-value pair.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <T>
 *            the type of objects that this attribute maps to
 * @see AttributeMap
 */
public abstract class Attribute<T> implements Serializable {
    /* All fields are transient because attributes should be a singleton. */

    /**
     * A MapperToLong that maps from an attribute map to the value of this attribute.
     */
    class AttributeMapToT implements Op<WithAttributes, T>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -953844729549732090L;

        /** {@inheritDoc} */
        public T op(WithAttributes t) {
            return get(t);
        }
    }

    /** The type of this attribute, as returned {@link #getType()}. */
    private final transient Class<T> clazz;

    /** The default value of this attribute. */
    private final transient T defaultValue;

    /**
     * A Mapper that takes an AttributeMap and returns the value of this attribute.
     */
    private final transient Op<WithAttributes, T> mapper = new AttributeMapToT();

    /** The name of this attribute. */
    private final transient String name;

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
    public Attribute(String name, Class<T> clazz, T defaultValue) {
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

    /**
     * Checks if the specified value is valid for this attribute. If the specified value is not
     * valid this method will throw an {@link IllegalArgumentException}.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(T value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    protected Predicate<WithAttributes> filter(Predicate<? super T> p) {
        return Predicates.mapAndEvaluate(mapper, p);
    }

    /**
     * Creates a value instance of this attribute from the specified string.
     * 
     * @param str
     *            the string to create the value from.
     * @return a value instance from the specified string
     * @throws UnsupportedOperationException
     *             if this operation is not supported by this attribute
     * @throws IllegalArgumentException
     *             if a valid attribute value could not be created from the string.
     */
    public T fromString(String str) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the default value of this attribute (<code>null</code> values are allowed).
     * 
     * @return the default value of this attribute
     */
    public T getDefault() {
        return defaultValue;
    }

    /**
     * Returns the name of the attribute.
     * 
     * @return the name of the attribute
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the type of this attribute.
     * 
     * @return the type of this attribute
     */
    public final Class<T> getType() {
        return clazz;
    }

    public final T get(WithAttributes withAttributes) {
        return get(withAttributes.getAttributes());
    }

    /**
     * Returns the value of this attribute from the specified attribute map. If this attribute is
     * not set in the map, the value of {@link #getDefault()} will be returned instead.
     * 
     * @param attributes
     *            the attribute map for which to retrieve the value of this attribute
     * @return the value of this attribute
     */
    public final T get(AttributeMap attributes) {
        return get(attributes, defaultValue);
    }

    public final T get(WithAttributes withAttributes, T defaultValue) {
        return get(withAttributes.getAttributes(), defaultValue);
    }

    /**
     * Returns the value of this attribute from the specified attribute map. If this attribute is
     * not set in the map, the specified defaultValue will be returned instead.
     * 
     * @param attributes
     *            the attribute map for which to retrieve the value of this attribute
     * @param defaultValue
     *            the value to return if this attribute is not set in the specified attribute map
     * @return the value of this attribute
     */
    public T get(AttributeMap attributes, T defaultValue) {
        return (T) attributes.get(this, defaultValue);
    }

    /**
     * Returns whether or not this attribute is set in the specified attribute map. This method is
     * useful for distinguishing those case where an attribute maps to <code>null</code> or 0.
     * 
     * @param attributes
     *            the attribute map to check if this attribute is set
     * @return <code>true</code> if this attribute is set in the specified attribute map,
     *         otherwise false
     */
    public boolean isSet(AttributeMap attributes) {
        return attributes.containsKey(this);
    }

    /**
     * Returns whether or not the specified value is valid for this attribute.
     * 
     * @param value
     *            the specified value to check
     * @return <code>true</code> if the specified value is valid for this attribute, otherwise
     *         <code>false</code>
     */
    public boolean isValid(T value) {
        return true; // all values are accepted by default.
    }

    /**
     * Returns a mapper that extracts the value of this attribute from an {@link AttributeMap}, or
     * returns {@link #getDefault()} if this attribute is not present.
     * 
     * @return a mapper from an AttributeMap to the value of this attribute
     */
    public Op<WithAttributes, T> map() {
        return mapper;
    }

    /**
     * Removes this attribute from the specified attribute map if it is present.
     * 
     * @param attributes
     *            the attribute map to remove this attribute from
     */
    public void remove(AttributeMap attributes) {
        attributes.remove(this);
    }

    /**
     * Sets the specified value in the specified attribute map.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Object)}
     */
    public AttributeMap set(AttributeMap attributes, T value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, T value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Returns an AttributeMap containing only this attribute mapping to the specified value.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(T value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }
}

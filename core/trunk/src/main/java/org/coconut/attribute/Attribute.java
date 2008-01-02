/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

/**
 * Attribute-value pairs are a fundamental data representation in many computing systems
 * and applications. Designers often desire an open-ended data structure that allows for
 * future extension without modifying existing code or data. In such situations, all or
 * part of the data model may be expressed as a collection of tuples attribute name,
 * value; each element is an attribute-value pair.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <T>
 *            the type of objects that this attribute maps to
 * @see AttributeMap
 */
public interface Attribute<T> {

    /**
     * Checks if the specified value is valid for this attribute. If the specified value
     * is not valid this method will throw an {@link IllegalArgumentException}.
     *
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    void checkValid(T value);

    /**
     * Creates a value instance of this attribute from the specified string.
     *
     * @param str
     *            the string to create the value from.
     * @return a value instance from the specified string
     * @throws UnsupportedOperationException
     *             if this operation is not supported by this attribute
     */
    T fromString(String str);

    /**
     * Returns the type of this attribute.
     *
     * @return the type of this attribute
     */
    Class<T> getAttributeType();

    /**
     * Returns the default value of this attribute (<code>null</code> values are
     * allowed).
     *
     * @return the default value of this attribute
     */
    T getDefaultValue();

    /**
     * Returns the name of the attribute.
     *
     * @return the name of the attribute
     */
    String getName();

    /**
     * Returns the value of this attribute from the specified attribute map. If this
     * attribute is not set in the map, the value of {@link #getDefaultValue()} will be
     * returned instead.
     *
     * @param attributes
     *            the attribute map for which to retrieve the value of this attribute
     * @return the value of this attribute
     */
    T getValue(AttributeMap attributes);

    /**
     * Returns the value of this attribute from the specified attribute map. If this
     * attribute is not set in the map, the specified defaultValue will be returned
     * instead.
     *
     * @param attributes
     *            the attribute map for which to retrieve the value of this attribute
     * @param defaultValue
     *            the value to return if this attribute is not set in the specified
     *            attribute map
     * @return the value of this attribute
     */
    T getValue(AttributeMap attributes, T defaultValue);

    /**
     * Returns whether or not this attribute is set in the specified attribute map. This
     * method is useful for distinguishing those case where an attribute maps to
     * <code>null</code> or 0.
     *
     * @param attributes
     *            the attribute map to check if this attribute is set
     * @return <code>true</code> if this attribute is set in the specified attribute
     *         map, otherwise false
     */
    boolean isSet(AttributeMap attributes);

    /**
     * Returns whether or not the specified value is valid for this attribute.
     *
     * @param value
     *            the specified value to check
     * @return <code>true</code> if the specified value is valid for this attribute,
     *         otherwise <code>false</code>
     */
    boolean isValid(T value);

    /**
     * Removes this attribute from the specified attribute map if it is present.
     *
     * @param attributes
     *            the attribute map to remove this attribute from
     */
    void remove(AttributeMap attributes);

    /**
     * Sets the specified value in the specified attribute map.
     *
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to
     *             {@link #checkValid(Object)}
     */
    AttributeMap setValue(AttributeMap attributes, T value);
}

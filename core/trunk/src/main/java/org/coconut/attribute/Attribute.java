/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

/**
 * Attribute-value pairs are a fundamental data representation in many computing systems
 * and applications. Designers often desire an open-ended data structure that allows for
 * future extension without modifying existing code or data. In such situations, all or
 * part of the data model may be expressed as a collection of tuples <attribute name,
 * value>; each element is an attribute-value pair.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface Attribute<T> {

    /**
     * Returns the name of the attribute.
     * 
     * @return the name of the attribute
     */
    String getName();

    /**
     * Returns the default value of this attribute (may be <code>null</code>).
     * 
     * @return the default value of this attribute
     */
    T getDefaultValue();

    /**
     * Returns the type of this attribute.
     * 
     * @return the type of this attribute
     */
    Class<T> getAttributeType();

    /**
     * Returns whether or not the specified value is valid for this attribute.
     * 
     * @param value
     *            the specified value to check
     * @return <code>true</code> if the specified value is valid for this attribute,
     *         otherwise <code>false</code>
     */
    boolean isValid(T value);

    void unSet(AttributeMap attributes);
    boolean isSet(AttributeMap attributes);

    T getValue(AttributeMap attributes);

    T getValue(AttributeMap attributes, T defaultValue);

    /**
     * Sets the specified value in the specified attributemap.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     */
    AttributeMap setValue(AttributeMap attributes, T value);

    T fromString(String str);

    void checkValid(T value);
}

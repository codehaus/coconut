/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

public interface Attribute<T> {

    /**
     * Returns the name of the attribute.
     * 
     * @return the name of the attribute
     */
    String getName();

    Class<T> getAttributeType();

    boolean isValid(T t);

    T get(AttributeMap attributes);

    T get(AttributeMap attributes, T defaultValue);

    AttributeMap set(AttributeMap attributes, T t);

    T fromString(String str);
}

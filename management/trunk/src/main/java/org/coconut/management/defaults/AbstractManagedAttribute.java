/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ReflectionException;

/**
 * An AbstractAttribute is a wrapper for a JMX attribute.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
abstract class AbstractManagedAttribute {
    /** The description of the operation. */
    private final String description;

    /** The name of the operation. */
    private final String name;

    /**
     * Creates a new AbstractManagedAttribute with the specified name and description.
     * 
     * @param name
     *            the name of the attribute
     * @param description
     *            the description of the attribute
     * @throws NullPointerException
     *             if the specified name or description is <code>null</code>
     */
    AbstractManagedAttribute(final String name, final String description) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (description == null) {
            throw new NullPointerException("description is null");
        }
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the description of this attribute.
     * 
     * @return the description of this attribute
     */
    String getDescription() {
        return description;
    }

    /**
     * Returns the name of this attribute.
     * 
     * @return the name of this attribute
     */
    String getName() {
        return name;
    }

    /**
     * Returns the MBeanAttributeInfo for this attribute.
     * 
     * @return the MBeanAttributeInfo for this attribute
     * @throws IntrospectionException
     *             could not optain the information for this attribute
     */
    abstract MBeanAttributeInfo getInfo() throws IntrospectionException;

    /**
     * Returns the value of the attribute.
     *
     * @return the value of the attribute
     * @throws ReflectionException
     *             could not get the value of the attribute
     */
    abstract Object getValue() throws ReflectionException;

    /**
     * Sets the value of the attribute to specified object.
     * 
     * @param o
     *            the value that the attribute should be set to
     * @throws ReflectionException
     *             could not set the attribute
     */
    abstract void setValue(Object o) throws ReflectionException;
}

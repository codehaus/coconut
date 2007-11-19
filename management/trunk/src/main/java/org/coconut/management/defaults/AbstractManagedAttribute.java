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
    private final String description;

    private final String name;

    AbstractManagedAttribute(final String name, final String description) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (description == null) {
            throw new NullPointerException("description is null");
        }
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    abstract MBeanAttributeInfo getInfo() throws IntrospectionException;

    abstract boolean hasSetter();

    abstract boolean hasGetter();

    abstract Object getValue() throws MBeanException, ReflectionException;

    abstract Object setValue(Object o) throws MBeanException, ReflectionException;
}

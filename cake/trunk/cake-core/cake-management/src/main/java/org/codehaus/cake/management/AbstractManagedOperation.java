/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management;

import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

/**
 * An AbstractOperation corresponds to a JMX operation.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AbstractManagedOperation.java 473 2007-11-19 12:58:01Z kasper $
 */
abstract class AbstractManagedOperation {

    /** The description of the operation. */
    private final String description;

    /** The name of the operation. */
    private final String name;

    /**
     * Creates a new AbstractManagedOperation with the specified name and description.
     * 
     * @param name
     *            the name of the operation
     * @param description
     *            the description of the operation
     * @throws NullPointerException
     *             if the specified name or description is <code>null</code>
     */
    AbstractManagedOperation(final String name, final String description) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (description == null) {
            throw new NullPointerException("description is null");
        }
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the MBeanOperationInfo for this operation.
     * 
     * @return the MBeanOperationInfo for this operation
     * @throws IntrospectionException
     *             could not optain the information for this operation
     */
    abstract MBeanOperationInfo getInfo() throws IntrospectionException;

    /**
     * Invoke the operation with specified arguments.
     * 
     * @param arguments
     *            the arguments used for invoking the operation
     * @return the result of the invocation
     * @throws MBeanException
     *             could not invoke the operation
     * @throws ReflectionException
     *             could not invoke the operation
     */
    abstract Object invoke(Object... arguments) throws MBeanException, ReflectionException;

    /**
     * Returns the description of this operation.
     * 
     * @return the description of this operation
     */
    String getDescription() {
        return description;
    }

    /**
     * Returns the name of this operation.
     * 
     * @return the name of this operation
     */
    String getName() {
        return name;
    }
}

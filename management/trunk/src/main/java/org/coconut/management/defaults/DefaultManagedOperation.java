/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class DefaultManagedOperation extends AbstractManagedOperation {
    /** The method to invoke when this operation is called. */
    private final Method m;

    /** The object to invoke on. */
    private final Object o;

    /**
     * Creates a new DefaultManagedOperation.
     * @param obj
     *            the object the specified method should be invoked on
     * @param method
     *            the method corresponding to the operation
     * @param name
     *            the name of the operation
     * @param description
     *            the description of the operation
     * @throws NullPointerException
     *             if the specified object, method, name or description is
     *             <code>null</code>
     */
    DefaultManagedOperation(Object obj, Method method, final String name, final String description) {
        super(name, description);
        if (method == null) {
            throw new NullPointerException("method is null");
        } else if (obj == null) {
            throw new NullPointerException("obj is null");
        }
        this.m = method;
        this.o = obj;
    }

    /** {@inheritDoc} */
    @Override
    Object invoke(Object... objects) throws MBeanException, ReflectionException {
        try {
            return m.invoke(o, objects);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException(e);/* Should never happen */
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);/* Should never happen */
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                final String msg = "RuntimeException thrown in method " + m;
                throw new RuntimeMBeanException((RuntimeException) t, msg);
            } else if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t, "Error thrown in the method " + m);
            } else {
                throw new MBeanException((Exception) t, "Exception thrown in the method " + m);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    MBeanOperationInfo getInfo() throws IntrospectionException {
        return new MBeanOperationInfo(getName(), getDescription(), ManagementUtil
                .methodSignature(m), m.getReturnType().getName(), MBeanOperationInfo.UNKNOWN);
    }

}

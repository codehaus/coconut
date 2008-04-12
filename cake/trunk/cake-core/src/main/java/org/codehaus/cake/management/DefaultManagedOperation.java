/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management;

import java.beans.MethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.codehaus.cake.util.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DefaultManagedOperation.java 511 2007-12-13 14:37:02Z kasper $
 */
class DefaultManagedOperation extends AbstractManagedOperation {
    /** The method to invoke when this operation is called. */
    private final Method m;

    /** The object to invoke on. */
    private final Object o;

    /**
     * Creates a new DefaultManagedOperation.
     * 
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
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e, "Exception thrown trying to"
                    + " invoke the operation " + getName());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new ReflectionException((Exception) t,
                        "Exception thrown while invoking the operation " + getName());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    MBeanOperationInfo getInfo() throws IntrospectionException {
        return new MBeanOperationInfo(getName(), getDescription(), ManagementUtil
                .methodSignature(m), m.getReturnType().getName(), MBeanOperationInfo.UNKNOWN);
    }

    /**
     * Creates a DefaultManagedAttribute from the specified MethodDescriptors if the
     * {@link ManagedOperation} annotation is present.
     * 
     * @param mds
     *            the MethodDescriptors for the object
     * @param obj
     *            the object that the operations should be invoked on
     * @return a map mapping from the combined name of the attribute to the
     *         AbstractManagedOperation
     */
    static Map<OperationKey, AbstractManagedOperation> fromMethodDescriptors(
            MethodDescriptor[] mds, Object obj) {
        Map<OperationKey, AbstractManagedOperation> result = new HashMap<OperationKey, AbstractManagedOperation>();
        for (MethodDescriptor pd : mds) {
            ManagedOperation mo = pd.getMethod().getAnnotation(ManagedOperation.class);
            if (mo != null) {
                String name = ManagementUtil.filterString(obj, mo.defaultValue());
                if (name.equals("")) {
                    name = pd.getName();
                }
                String description = ManagementUtil.filterString(obj, mo.description());
                DefaultManagedOperation dmo = new DefaultManagedOperation(obj, pd.getMethod(),
                        name, description);
                // create String[] signature
                List<String> p = new ArrayList<String>();
                for (Class c : pd.getMethod().getParameterTypes()) {
                    p.add(c.getName());
                }
                result.put(new OperationKey(name, p.toArray(new String[0])), dmo);
            }
        }
        return result;
    }
}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class ReflectionOperation extends AbstractOperation {
    private final Method m;

    private final Object o;

    ReflectionOperation(Method m, Object o, final String name, final String description) {
        super(name, description);
        this.m = m;
        m.getParameterTypes();
        this.o = o;
    }

    /**
     * @see org.coconut.jmx.defaults.AbstractOperation#invoke()
     */
    @Override
    Object invoke(Object... objects) throws MBeanException, ReflectionException {
        try {
            return m.invoke(o, objects);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                final String msg = "RuntimeException thrown in method " + m;
                throw new RuntimeMBeanException((RuntimeException) t, msg);
            } else if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t, "Error thrown in the method "
                        + m);
            } else {
                throw new MBeanException((Exception) t, "Exception thrown in the method "
                        + m);
            }
        }
    }

    /**
     * @see org.coconut.jmx.defaults.AbstractOperation#signature()
     */
    @Override
    String[] getSignature() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.management.defaults.AbstractOperation#getInfo()
     */
    @Override
    MBeanOperationInfo getInfo() throws IntrospectionException {
        return new MBeanOperationInfo(name, description, methodSignature(m), m
                .getReturnType().getName(), MBeanOperationInfo.UNKNOWN);
    }

    private static MBeanParameterInfo[] methodSignature(Method method) {
        final Class[] classes = method.getParameterTypes();
        final MBeanParameterInfo[] params = new MBeanParameterInfo[classes.length];

        for (int i = 0; i < classes.length; i++) {
            final String pn = "p" + (i + 1);
            params[i] = new MBeanParameterInfo(pn, classes[i].getName(), "");
        }

        return params;
    }

}

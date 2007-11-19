/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;

import org.coconut.internal.util.StringUtil;
import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class DefaultManagedAttribute extends AbstractManagedAttribute {
    private final Method getter;

    private final Object o;

    private final Method setter;

    public DefaultManagedAttribute(String attribute, String description, Object o, Method reader,
            Method writer) {
        super(attribute, description);
        this.o = o;
        this.getter = reader;
        this.setter = writer;
    }

    /** {@inheritDoc}*/
    MBeanAttributeInfo getInfo() throws IntrospectionException {
        return new MBeanAttributeInfo(getName(), getDescription(), getter, setter);
    }

    /** {@inheritDoc}     */
    Object getValue() throws MBeanException, ReflectionException {
        try {
            return getter.invoke(o, null);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                final String msg = "RuntimeException thrown in the getter for the attribute "
                        + getName();
                throw new RuntimeMBeanException((RuntimeException) t, msg);
            } else if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t,
                        "Error thrown in the getter for the attribute " + getName());
            } else {
                throw new MBeanException((Exception) t,
                        "Exception thrown in the getter for the attribute " + getName());
            }
        } catch (RuntimeException e) {
            throw new RuntimeOperationsException(e,
                    "RuntimeException thrown trying to invoke the getter" + " for the attribute "
                            + getName());
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e, "Exception thrown trying to"
                    + " invoke the getter for the attribute " + getName());
        } catch (Error e) {
            throw new RuntimeErrorException(e, "Error thrown trying to invoke the getter "
                    + " for the attribute " + getName());
        }
    }

    /** {@inheritDoc} */
    boolean hasGetter() {
        return getter != null;
    }

    /** {@inheritDoc} */
    boolean hasSetter() {
        return setter != null;
    }

    /** {@inheritDoc} */
    Object setValue(Object o) throws ReflectionException, MBeanException {
        try {
            setter.invoke(this.o, o);
            return o;
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e, "IllegalAccessException"
                    + " occured trying to invoke the setter on the MBean");
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                final String msg = "RuntimeException thrown in the setter for the attribute "
                        + getName();
                throw new RuntimeMBeanException((RuntimeException) t, msg);
            } else if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t, "Error thrown in the MBean's setter");
            } else {
                throw new MBeanException((Exception) t, "Exception thrown in the MBean's setter");
            }
        }
    }

    public static DefaultManagedAttribute fromPropertyDescriptor(PropertyDescriptor pd, Object o) {
        ManagedAttribute readAttribute = pd.getReadMethod() == null ? null : pd.getReadMethod()
                .getAnnotation(ManagedAttribute.class);
        ManagedAttribute writeAttribute = pd.getWriteMethod() == null ? null : pd.getWriteMethod()
                .getAnnotation(ManagedAttribute.class);
        Method writer = null;
        Method reader = null;
        if (readAttribute != null) {
            if (writeAttribute != null) {
                throw new IllegalArgumentException(
                        "cannot define ManagedAttribute on both setter and getter for "
                                + pd.getReadMethod());
            }
            if (readAttribute.writeOnly()) {
                throw new IllegalArgumentException("cannot set writeonly on getter "
                        + pd.getReadMethod());
            }
            reader = pd.getReadMethod();
            writeAttribute = readAttribute;
        } else if (writeAttribute != null) {
            writer = pd.getWriteMethod();
            if (!writeAttribute.writeOnly()) {
                reader = pd.getReadMethod();
            }
        }
        if (reader != null || writer != null) {
            String name = ManagementUtil.filterString(o, writeAttribute.defaultValue());
            if (name.equals("") || name.equals("$methodname")) {
                name = StringUtil.capitalize(pd.getName());
            }
            String description = ManagementUtil.filterString(o, writeAttribute.description());
            return new DefaultManagedAttribute(name, description, o, reader, writer);
        }
        return null;
    }

    public static Map<String, AbstractManagedAttribute> fromPropertyDescriptors(PropertyDescriptor[] pds,
            Object o) {
        Map<String, AbstractManagedAttribute> result = new HashMap<String, AbstractManagedAttribute>();
        for (PropertyDescriptor pd : pds) {
            AbstractManagedAttribute a = fromPropertyDescriptor(pd, o);
            if (a != null) {
                result.put(a.getName(), a);
            }
        }
        return result;
    }
}

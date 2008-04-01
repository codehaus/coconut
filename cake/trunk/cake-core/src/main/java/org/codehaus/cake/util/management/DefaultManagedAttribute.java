/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.management;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.ReflectionException;

import org.codehaus.cake.internal.util.StringUtil;
import org.codehaus.cake.util.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DefaultManagedAttribute.java 510 2007-12-12 08:52:55Z kasper $
 */
class DefaultManagedAttribute extends AbstractManagedAttribute {
    /** The getter for this attribute or <code>null</code> if it is write-only. */
    private final Method getter;

    /** The object that this attribute should be invoked on. */
    private final Object obj;

    /** The setter for this attribute or <code>null</code> if it is read-only. */
    private final Method setter;

    /**
     * Creates a new DefaultManagedAttribute.
     * 
     * @param obj
     *            the object that contains the attribute
     * @param reader
     *            the reader method of the attribute or <code>null</code> if it is
     *            write-only.
     * @param writer
     *            the writer method of the attribute or <code>null</code> if it is
     *            read-only.
     * @param name
     *            the name of the attribute
     * @param description
     *            the description of the attribute
     * @throws NullPointerException
     *             if the specified object, name or description is <code>null</code>.
     *             Or if both reader and writer are <code>null</code>
     */
    DefaultManagedAttribute(Object obj, Method reader, Method writer, String name,
            String description) {
        super(name, description);
        if (obj == null) {
            throw new NullPointerException("obj is null");
        } else if (reader == null && writer == null) {
            throw new NullPointerException("both reader and writer cannot be null");
        }
        this.obj = obj;
        this.getter = reader;
        this.setter = writer;
    }

    /** {@inheritDoc} */
    MBeanAttributeInfo getInfo() throws IntrospectionException {
            return new MBeanAttributeInfo(getName(), getDescription(), getter, setter);
    }

    /** {@inheritDoc} */
    Object getValue() throws ReflectionException {
        if (getter == null) {
            throw new IllegalStateException("Attribute is write-only");
        }
        try {
            return getter.invoke(obj, (Object[]) null);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new ReflectionException((Exception) t,
                        "Exception thrown in the getter for the attribute " + getName());
            }
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e, "Exception thrown trying to"
                    + " invoke the getter for the attribute " + getName());
        }
    }

    /** {@inheritDoc} */
    void setValue(Object o) throws ReflectionException {
        if (setter == null) {
            throw new IllegalStateException("Attribute is read-only");
        }
        try {
            setter.invoke(this.obj, o);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e, "IllegalAccessException"
                    + " occured trying to invoke the setter on the MBean");
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new ReflectionException((Exception) t,
                        "Exception thrown in the MBean's setter");
            }
        }
    }

    /**
     * Creates a DefaultManagedAttribute from the specified PropertyDescriptor if the
     * {@link ManagedAttribute} annotation is present on the getter or setter.
     * 
     * @param pd
     *            the PropertyDescriptor of the attribute
     * @param obj
     *            the object where the attribute should be read and written to.
     * @return a DefaultManagedAttribute if the ManagedAttribute annotation is present on
     *         the getter or setter. Or <code>null</code> if no annotation is present.
     * @throws IllegalArgumentException
     *             if an attribute has the ManagedAttribute set for both the reader and
     *             the writter. Or if it has a ManagedAttribute set on the reader where
     *             isWriteOnly is set to <code>true</code>
     */
    static DefaultManagedAttribute fromPropertyDescriptor(PropertyDescriptor pd, Object obj) {
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
            if (readAttribute.isWriteOnly()) {
                throw new IllegalArgumentException("cannot set writeonly on getter "
                        + pd.getReadMethod());
            }
            reader = pd.getReadMethod();
            writeAttribute = readAttribute;
        } else if (writeAttribute != null) {
            writer = pd.getWriteMethod();
            if (!writeAttribute.isWriteOnly()) {
                reader = pd.getReadMethod();
            }
        }
        if (reader != null || writer != null) {
            String name = ManagementUtil.filterString(obj, writeAttribute.defaultValue());
            if (name.equals("")) {
                name = StringUtil.capitalize(pd.getName());
            }
            String description = ManagementUtil.filterString(obj, writeAttribute.description());
            return new DefaultManagedAttribute(obj, reader, writer, name, description);
        }
        return null; // no annotation for property
    }

    /**
     * Creates {@link AbstractManagedAttribute}'s for the object and its
     * {@link PropertyDescriptor}'s.
     * 
     * @param pds
     *            the PropertyDescriptor that should be created AbstractManagedAttributes
     *            for
     * @param obj
     *            the object that the properties can be set and retrieved from
     * @return a map mapping from the name of the attribute to the
     *         AbstractManagedAttribute
     */
    static Map<String, AbstractManagedAttribute> fromPropertyDescriptors(PropertyDescriptor[] pds,
            Object obj) {
        Map<String, AbstractManagedAttribute> result = new HashMap<String, AbstractManagedAttribute>();
        for (PropertyDescriptor pd : pds) {
            AbstractManagedAttribute a = fromPropertyDescriptor(pd, obj);
            if (a != null) {
                result.put(a.getName(), a);
            }
        }
        return result;
    }
}

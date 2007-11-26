/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultManagedGroup extends AbstractManagedGroup implements DynamicMBean {

    private final Map<String, AbstractManagedAttribute> attributes = new ConcurrentHashMap<String, AbstractManagedAttribute>();

    private volatile MBeanInfo mbeanInfo;

    private final Map<OperationKey, AbstractManagedOperation> ops = new ConcurrentHashMap<OperationKey, AbstractManagedOperation>();

    private final Set<Object> os = new CopyOnWriteArraySet<Object>();

    /**
     * Creates a new DefaultManagedGroup with the specified name and description.
     * 
     * @param name
     *            the name of the group
     * @param description
     *            the description of the group
     * @throws NullPointerException
     *             if the specified name or description is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified name does not follow the naming standard of managed
     *             groups
     */
    public DefaultManagedGroup(String name, String description) {
        super(name, description);
    }

    /** {@inheritDoc} */
    public synchronized ManagedGroup add(Object o) {
        if (o == null) {
            throw new NullPointerException("o is null");
        }
        BeanInfo bi;
        try {
            bi = Introspector.getBeanInfo(o.getClass());
        } catch (java.beans.IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
        attributes.putAll(DefaultManagedAttribute.fromPropertyDescriptors(bi
                .getPropertyDescriptors(), o));
        ops.putAll(DefaultManagedOperation.fromPropertyDescriptors(bi.getMethodDescriptors(), o));
        os.add(o);
        return this;
    }

    /** {@inheritDoc} */
    public synchronized ManagedGroup addChild(String name, String description) {
        DefaultManagedGroup g = new DefaultManagedGroup(name, description);
        return super.addNewGroup(g);
    }

    /** {@inheritDoc} */
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException,
            ReflectionException {
        return findAttribute(attribute).getValue();
    }

    /** {@inheritDoc} */
    public final AttributeList getAttributes(String[] attributes) {
        final AttributeList result = new AttributeList(attributes.length);
        for (String attrName : attributes) {
            try {
                final Object attrValue = getAttribute(attrName);
                result.add(new Attribute(attrName, attrValue));
            } catch (Exception e) {
                // OK: attribute is not included in returned list, per spec
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    public MBeanInfo getMBeanInfo() {
        if (mbeanInfo != null) {
            return mbeanInfo;
        }
        List<MBeanAttributeInfo> l = new ArrayList<MBeanAttributeInfo>();
        for (AbstractManagedAttribute aa : attributes.values()) {
            try {
                l.add(aa.getInfo());
            } catch (IntrospectionException e) {
                throw new IllegalStateException(e);
            }
        }
        List<MBeanOperationInfo> lo = new ArrayList<MBeanOperationInfo>();
        for (AbstractManagedOperation op : ops.values()) {
            try {
                lo.add(op.getInfo());
            } catch (IntrospectionException e) {
                throw new IllegalStateException(e);
            }
        }

        mbeanInfo = new MBeanInfo(getName(), getDescription(),
                l.toArray(new MBeanAttributeInfo[0]), null, lo.toArray(new MBeanOperationInfo[0]),
                null);
        return mbeanInfo;
    }

    /** {@inheritDoc} */
    public Collection<?> getObjects() {
        return new ArrayList(os);
    }

    /** {@inheritDoc} */
    public Object invoke(String actionName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        AbstractManagedOperation aa = ops.get(new OperationKey(actionName, signature));
        if (aa != null) {
            return aa.invoke(params);
        }
        return null;
    }

    /** {@inheritDoc} */
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException, ReflectionException {
        findAttribute(attribute.getName()).setValue(attribute.getValue());
    }

    /** {@inheritDoc} */
    public final AttributeList setAttributes(AttributeList attributes) {
        final AttributeList result = new AttributeList(attributes.size());
        for (Object attrObj : attributes) {
            Attribute attr = (Attribute) attrObj;
            try {
                setAttribute(attr);
                result.add(new Attribute(attr.getName(), attr.getValue()));
            } catch (Exception e) {
                // OK: attribute is not included in returned list, per spec
            }
        }
        return result;
    }

    private AbstractManagedAttribute findAttribute(String attribute)
            throws AttributeNotFoundException {
        AbstractManagedAttribute att = attributes.get(attribute);
        if (att == null) {
            throw new AttributeNotFoundException("Attribute " + attribute + " could not be found");
        }
        return att;
    }
}

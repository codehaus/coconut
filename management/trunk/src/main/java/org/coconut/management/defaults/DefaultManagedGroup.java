/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.lang.reflect.Method;
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
import org.coconut.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultManagedGroup extends AbstractManagedGroup implements DynamicMBean {

    private final Map<String, AbstractManagedAttribute> attributes = new ConcurrentHashMap<String, AbstractManagedAttribute>();

    private volatile MBeanInfo mbeanInfo;

    private final Map<String, List<AbstractManagedOperation>> ops = new ConcurrentHashMap<String, List<AbstractManagedOperation>>();

    private final Set<Object> os = new CopyOnWriteArraySet<Object>();

    /**
     * @param name
     *            the name of this group
     * @param description
     *            the description of this group
     */
    public DefaultManagedGroup(String name, String description) {
        super(name, description);
    }

    /**
     * {@inheritDoc}
     */
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
        attributes.putAll(DefaultManagedAttribute.fromPropertyDescriptors(
                bi.getPropertyDescriptors(), o));

        
        for (Method m : o.getClass().getMethods()) {
            ManagedOperation mo = m.getAnnotation(ManagedOperation.class);
            if (mo != null) {
                String name = ManagementUtil.filterString(o, mo.defaultValue());
                if (name.equals("")) {
                    name = m.getName();
                }
                String description = ManagementUtil.filterString(o, mo.description());
                AbstractManagedOperation io = new DefaultManagedOperation(m, o, name, description);
                List<AbstractManagedOperation> l = ops.get(name);
                if (l == null) {
                    l = new ArrayList<AbstractManagedOperation>();
                    ops.put(name, l);
                }
                l.add(io);
                // TODO fix
                // throw new IllegalArgumentException(name);
            }
        }
        os.add(o);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized ManagedGroup addChild(String name, String description) {
        DefaultManagedGroup g = new DefaultManagedGroup(name, description);
        return super.addNewGroup(g);
    }

    /**
     * {@inheritDoc}
     */
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException,
            ReflectionException {
        AbstractManagedAttribute att = findAttribute(attribute);
        return att.getValue();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public MBeanInfo getMBeanInfo() {
        if (mbeanInfo != null) {
            return mbeanInfo;
        }
        List<MBeanAttributeInfo> l = new ArrayList<MBeanAttributeInfo>();
        for (AbstractManagedAttribute aa : attributes.values()) {
            try {
                l.add(aa.getInfo());
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
        }
        List<MBeanOperationInfo> lo = new ArrayList<MBeanOperationInfo>();
        for (List<AbstractManagedOperation> li : ops.values()) {
            for (AbstractManagedOperation aa : li) {
                try {
                    lo.add(aa.getInfo());
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
            }
        }

        mbeanInfo = new MBeanInfo(getName(), getDescription(),
                l.toArray(new MBeanAttributeInfo[0]), null, lo.toArray(new MBeanOperationInfo[0]),
                null);
        return mbeanInfo;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<?> getObjects() {
        return new ArrayList(os);
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(String actionName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        List<AbstractManagedOperation> aa = ops.get(actionName);
        if (aa != null) {
            for (AbstractManagedOperation ao : aa) {
                return ao.invoke(params);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException, ReflectionException {
        AbstractManagedAttribute att = findAttribute(attribute.getName());
        att.setValue(attribute.getValue());
    }

    /**
     * {@inheritDoc}
     */
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

    private AbstractManagedAttribute findAttribute(String attribute) throws AttributeNotFoundException {
        AbstractManagedAttribute att = attributes.get(attribute);
        if (att == null) {
            for (String aa : attributes.keySet()) {
                System.out.println(aa);
            }
            throw new AttributeNotFoundException("Attribute " + attribute + " could not be found");
        }
        return att;
    }
}

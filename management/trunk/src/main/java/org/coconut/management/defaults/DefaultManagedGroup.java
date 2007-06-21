/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

public class DefaultManagedGroup extends AbstractManagedGroup implements DynamicMBean {

    @Override
    public String toString() {
        return "Name= " + getName() + ", Description =" + getDescription();
    }

    private final Class<?> mbeanInterface = null;

    // private final MBeanIntrospector<M> introspector;
    private volatile MBeanInfo mbeanInfo;

    private final Map<String, AbstractAttribute> attributes = new ConcurrentHashMap<String, AbstractAttribute>();

    private final Map<String, List<AbstractOperation>> ops = new ConcurrentHashMap<String, List<AbstractOperation>>();

    private final Set<Object> os = new CopyOnWriteArraySet<Object>();

    /**
     * @param name
     * @param description
     */
    public DefaultManagedGroup(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     */
    public DefaultManagedGroup(String name, String description, boolean register) {
        super(name, description);
    }

    public synchronized ManagedGroup addChild(String name, String description) {
        DefaultManagedGroup g = new DefaultManagedGroup(name, description);
        return super.addNewGroup(g);
    }

    public synchronized ManagedGroup add(Object o) {
        if (o == null) {
            throw new NullPointerException("o is null");
        }
        os.add(o);
        BeanInfo bi;
        try {
            bi = Introspector.getBeanInfo(o.getClass());
        } catch (java.beans.IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
        for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
            ManagedAttribute ma = null;
            Method writer = null;

            if (pd.getReadMethod() != null) {
                ma = pd.getReadMethod().getAnnotation(ManagedAttribute.class);
            }
            Method reader = ma == null ? null : pd.getReadMethod();
            if (pd.getWriteMethod() != null) {
                if (ma == null) {
                    ma = pd.getWriteMethod().getAnnotation(ManagedAttribute.class);
                }
                writer = ma == null || ma.readOnly() ? null : pd.getWriteMethod();
            }
            if (reader != null || writer != null) {
                String name = filterString(o, ma.defaultValue());
                if (name.equals("") || name.equals("$methodname")) {
                    name = capitalize(pd.getName());
                }
                String description = filterString(o, ma.description());
                attributes.put(name, new IntrospectedAttribute(name, description, o,
                        reader, writer));
            }
        }

        for (Method m : o.getClass().getMethods()) {
            ManagedOperation mo = m.getAnnotation(ManagedOperation.class);
            if (mo != null) {
                String name = filterString(o, mo.defaultValue());
                if (name.equals("")) {
                    name = m.getName();
                }
                String description = filterString(o, mo.description());
                AbstractOperation io = new ReflectionOperation(m, o, name, description);
                List<AbstractOperation> l = ops.get(name);
                if (l == null) {
                    l = new ArrayList<AbstractOperation>();
                    ops.put(name, l);
                }
                l.add(io);
                // TODO fix
                // throw new IllegalArgumentException(name);
            }
        }
        return this;
    }

    private String filterString(Object o, String str) {
        // if (o instanceof Named) {
        // Named n = (Named) o;
        // str = str.replace("$name", n.getName());
        // // System.out.println(n.getName());
        // }
        // if (o instanceof Described) {
        // Described n = (Described) o;
        // str = str.replace("$description", n.getDescription());
        // }
        return str;
    }

    public Object invoke(String actionName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        List<AbstractOperation> aa = ops.get(actionName);
        if (aa != null) {
            for (AbstractOperation ao : aa) {
                return ao.invoke(params);
            }
        }
        return null;
    }

    private AbstractAttribute findAttribute(String attribute)
            throws AttributeNotFoundException {
        AbstractAttribute att = attributes.get(attribute);
        if (att == null) {
            for (String aa : attributes.keySet()) {
                System.out.println(aa);
            }
            throw new AttributeNotFoundException("Attribute " + attribute
                    + " could not be found");
        }
        return att;
    }

    /**
     * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attribute) throws AttributeNotFoundException,
            MBeanException, ReflectionException {
        AbstractAttribute att = findAttribute(attribute);
        return att.getValue();
    }

    /**
     * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
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
     * @see javax.management.DynamicMBean#getMBeanInfo()
     */
    public MBeanInfo getMBeanInfo() {
        if (mbeanInfo != null) {
            return mbeanInfo;
        }
        List<MBeanAttributeInfo> l = new ArrayList<MBeanAttributeInfo>();
        for (AbstractAttribute aa : attributes.values()) {
            try {
                l.add(aa.getInfo());
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
        }
        List<MBeanOperationInfo> lo = new ArrayList<MBeanOperationInfo>();
        for (List<AbstractOperation> li : ops.values()) {
            for (AbstractOperation aa : li) {
                try {
                    lo.add(aa.getInfo());
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
            }
        }

        mbeanInfo = new MBeanInfo(getName(), getDescription(), l
                .toArray(new MBeanAttributeInfo[0]), null, lo
                .toArray(new MBeanOperationInfo[0]), null);
        return mbeanInfo;
    }

    /**
     * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
     */
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException, ReflectionException {
        AbstractAttribute att = findAttribute(attribute.getName());
        att.setValue(attribute.getValue());
    }

    /**
     * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
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

    private static String capitalize(String s) {
        if (s.length() == 0)
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public Collection<?> getObjects() {
        return new ArrayList(os);
    }
}

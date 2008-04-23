/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

/**
 * Various utility functions.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class ManagementUtil {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private ManagementUtil() {}

    // /CLOVER:ON
    /**
     * Filters the specified string, currently does nothing.
     * 
     * @param o
     *            the object to filter
     * @param str
     *            the string to filter
     * @return the specified string
     */
    static String filterString(Object o, String str) {
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

    /**
     * Creates a {@link DynamicMBean} from the specified parameters.
     * 
     * @param name
     *            the name of the MBean
     * @param description
     *            the description of the MBean
     * @param attributes
     *            a Map of all attributes
     * @param ops
     *            a Map of all operations
     * @return the newly created MBean
     */
    public static DynamicMBean from(String name, String description,
            Map<String, AbstractManagedAttribute> attributes,
            Map<OperationKey, AbstractManagedOperation> ops) {
        return new MBean(name, description, attributes, ops);
    }

    /**
     * Returns information about the parameters of the specified method.
     * 
     * @param method
     *            the method to return parameter info about
     * @return information about the parameters of the specified method
     */
    static MBeanParameterInfo[] methodSignature(Method method) {
        Class[] classes = method.getParameterTypes();
        MBeanParameterInfo[] params = new MBeanParameterInfo[classes.length];

        for (int i = 0; i < classes.length; i++) {
            String parameterName = "p" + (i + 1);
            params[i] = new MBeanParameterInfo(parameterName, classes[i].getName(), "");
        }

        return params;
    }

    /**
     * The DynamicMBean that is used to expose this group.
     */
    static class MBean implements DynamicMBean {

        /** A map of all attributes. */
        private final Map<String, AbstractManagedAttribute> attributes;

        /** The description of this MBean. */
        private final String description;

        /** The name of this MBean. */
        private final String name;

        /** A map of all operations. */
        private final Map<OperationKey, AbstractManagedOperation> ops;

        /**
         * Creates a {@link DynamicMBean} from the specified parameters.
         * 
         * @param name
         *            the name of the MBean
         * @param description
         *            the description of the MBean
         * @param attributes
         *            a Map of all attributes
         * @param ops
         *            a Map of all operations
         */
        public MBean(String name, String description,
                Map<String, AbstractManagedAttribute> attributes,
                Map<OperationKey, AbstractManagedOperation> ops) {
            this.name = name;
            this.description = description;
            this.attributes = attributes;
            this.ops = ops;
        }

        /**
         * Finds and returns the an attribute with the specified name, or throws an Exception.
         * 
         * @param attribute
         *            the name of the attribute
         * @return the attribute with the specified name
         * @throws AttributeNotFoundException
         *             if no such attribute existed
         */
        private AbstractManagedAttribute findAttribute(String attribute)
                throws AttributeNotFoundException {
            AbstractManagedAttribute att = attributes.get(attribute);
            if (att == null) {
                throw new AttributeNotFoundException("Attribute " + attribute
                        + " could not be found");
            }
            return att;
        }

        /** {@inheritDoc} */
        public Object getAttribute(String attribute) throws AttributeNotFoundException,
                MBeanException, ReflectionException {
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
            List<MBeanAttributeInfo> l = new ArrayList<MBeanAttributeInfo>();
            for (AbstractManagedAttribute aa : attributes.values()) {
                try {
                    l.add(aa.getInfo());
                } catch (IntrospectionException e) {
                    // /CLOVER:OFF
                    throw new IllegalStateException(e);// don't test
                    // /CLOVER:ON
                }
            }
            List<MBeanOperationInfo> lo = new ArrayList<MBeanOperationInfo>();
            for (AbstractManagedOperation op : ops.values()) {
                try {
                    lo.add(op.getInfo());
                } catch (IntrospectionException e) {
                    // /CLOVER:OFF
                    throw new IllegalStateException(e);// don't test
                    // /CLOVER:ON
                }
            }

            return new MBeanInfo(name, description, l.toArray(new MBeanAttributeInfo[0]), null, lo
                    .toArray(new MBeanOperationInfo[0]), null);
        }

        /** {@inheritDoc} */
        public Object invoke(String actionName, Object[] params, String[] signature)
                throws MBeanException, ReflectionException {
            AbstractManagedOperation aa = ops.get(new OperationKey(actionName, signature));
            if (aa != null) {
                return aa.invoke(params);
            }
            throw new IllegalArgumentException("Unknown method " + actionName + " [ signature = "
                    + Arrays.toString(signature) + "]");
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
    }

}

/**
 * 
 */
package org.coconut.management.spi;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.management.ManagementFactory;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.coconut.core.EventHandler;
import org.coconut.core.Named;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class NumberDynamicBean implements DynamicMBean, JMXConfigurator {

    static class AbstractFoo {
        final String description;

        final String name;

        AbstractFoo(final String name, final String description) {
            if (name == null) {
                throw new NullPointerException("name is null");
            } else if (description == null) {
                throw new NullPointerException("description is null");
            }
            this.name = name;
            this.description = description;
        }
    }

    static class CustomAttribute extends AbstractAttribute {
        private final Callable reader;

        private final EventHandler writer;

        private final Class type;

        public CustomAttribute(String name, String description, Callable reader,
                EventHandler writer, Class type) {
            super(name, description);
            this.reader = reader;
            this.writer = writer;
            this.type = type;
        }

        /**
         * @see org.coconut.metric.spi.NumberDynamicBean.AbstractAttribute#getInfo()
         */
        @Override
        MBeanAttributeInfo getInfo() throws IntrospectionException {
            return new MBeanAttributeInfo(name, type.getName(), description,
                    reader != null, writer != null, false);
        }

        /**
         * @see org.coconut.metric.spi.NumberDynamicBean.AbstractAttribute#getValue()
         */
        @Override
        Object getValue() throws Exception {
            return reader.call();
        }

        /**
         * @see org.coconut.metric.spi.NumberDynamicBean.AbstractAttribute#setValue(java.lang.Object)
         */
        @Override
        void setValue(Object o) {
            writer.handle(o);
        }

    }

    static class IntrospectedAttribute extends AbstractAttribute {
        private final Method getter;

        private final Method setter;

        private final Object o;

        /**
         * @param name
         * @param string
         * @param o2
         * @param reader
         * @param writer
         */
        public IntrospectedAttribute(String name, String description, Object o,
                Method reader, Method writer) {
            super(name, description);
            this.o = o;
            this.getter = reader;
            this.setter = writer;
        }

        /**
         * @param name
         * @param description
         * @param type
         * @param number
         */
        // InternalAttribute(final Callable getter, final String name,
        // final String description, final Class type) {
        // super(name, description);
        // this.getter = getter;
        // this.type = type;
        // }
        MBeanAttributeInfo getInfo() throws IntrospectionException {
            return new MBeanAttributeInfo(name, description, getter, setter);
        }

        Object getValue() throws Exception {
            return getter.invoke(o, null);
        }

        void setValue(Object o) throws IllegalAccessException, InvocationTargetException {
            setter.invoke(this.o, o);
        }
    }

    static abstract class AbstractAttribute {
        final String description;

        final String name;

        AbstractAttribute(final String name, final String description) {
            if (name == null) {
                throw new NullPointerException("name is null");
            } else if (description == null) {
                throw new NullPointerException("description is null");
            }
            this.name = name;
            this.description = description;
        }

        abstract MBeanAttributeInfo getInfo() throws IntrospectionException;

        abstract Object getValue() throws Exception;

        abstract void setValue(Object o) throws Exception;
    }

    static class InternalOperation extends AbstractFoo {
        final Method m;

        final Object o;

        final Runnable r;

        InternalOperation(Method m, Object o, final String name, final String description) {
            super(name, description);
            this.m = m;
            this.o = o;
            r = null;
        }

        /**
         * @param r
         */
        InternalOperation(final Runnable r, final String name, final String description) {
            super(name, description);
            this.r = r;
            m = null;
            o = null;
        }

        Object invoke() throws IllegalArgumentException, IllegalAccessException,
                InvocationTargetException {
            if (r != null) {
                r.run();
            } else {
                m.invoke(o, null);
            }
            return null;
        }
    }

    private static ObjectName toObjectName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        try {
            return new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException("Specified string ( name = " + name
                    + ") results in an invalid object name", e);
        }
    }

    private final String description;

    private final Map<String, AbstractAttribute> map = Collections
            .synchronizedMap(new HashMap<String, AbstractAttribute>());

    private final Map<String, InternalOperation> mapOper = Collections
            .synchronizedMap(new HashMap<String, InternalOperation>());

    // gotta delete this so we are serializeable
    private SoftReference<MBeanServer> mbs;

    private ObjectName name;

    public NumberDynamicBean(String description) {
        this.description = description;
    }

    /**
     * @see org.coconut.metric.spi.ManagedConfigurator#add(java.lang.String,
     *      java.lang.Object)
     */
    public synchronized void add(Object o) {
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
                String description = filterString(o, ma.description());
                map.put(name, new IntrospectedAttribute(name, description, o, reader,
                        writer));
            }
        }

        for (Method m : o.getClass().getMethods()) {
            ManagedOperation mo = m.getAnnotation(ManagedOperation.class);
            if (mo != null) {
                String name = filterString(o, mo.defaultValue());
                String description = filterString(o, mo.description());
                InternalOperation io = new InternalOperation(m, o, name, description);
                if (mapOper.containsKey(name)) {
                    // TODO fix
                 //   throw new IllegalArgumentException(name);
                } else {
                    mapOper.put(name, io); // check not already
                }
            }
        }
    }

    private String filterString(Object o, String str) {
        if (o instanceof Named) {
            Named n = (Named) o;
            str = str.replace("$name", n.getName());
            System.out.println(n.getName());
        }
        if (o instanceof Described) {
            Described n = (Described) o;
            str = str.replace("$description", n.getDescription());
        }
        return str;
    }

    public synchronized <T> void addAttribute(String name, String description,
            Callable<T> reader, Class<? extends T> type) {
        addAttribute(name, description, reader, null, type);
    }

    public synchronized <T> void addAttribute(String name, String description,
            Callable<T> reader, EventHandler<T> writer, Class<? extends T> type) {
        CustomAttribute m = new CustomAttribute(name, description, reader, writer, type);
        if (map.containsKey(name)) {
            throw new IllegalArgumentException("Already registered");
        }
        map.put(name, m); // check not already registered
    }

    /**
     * @see org.coconut.metric.spi.ManagedConfigurator#addOperation(java.lang.Runnable,
     *      java.lang.String, java.lang.String)
     */
    public synchronized void addOperation(Runnable r, String name, String description) {
        InternalOperation m = new InternalOperation(r, name, description);
        if (mapOper.containsKey(name)) {
            throw new IllegalArgumentException("Already registered");
        }
        mapOper.put(name, m);

    }

    /**
     * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attribute) throws AttributeNotFoundException,
            MBeanException, ReflectionException {
        try {
            AbstractAttribute aa = map.get(attribute);
            return aa == null ? null : aa.getValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not compute value", e);
        }
    }

    /**
     * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
     */
    public AttributeList getAttributes(String[] attributes) {
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
        String classname = MBeanInfo.class.getName();

        // add attributes
        IntrospectedAttribute[] mn = new TreeMap<String, AbstractAttribute>(map).values()
                .toArray(new IntrospectedAttribute[map.size()]);
        MBeanAttributeInfo[] aInfo = new MBeanAttributeInfo[mn.length];
        for (int i = 0; i < mn.length; i++) {
            try {
                aInfo[i] = mn[i].getInfo();
            } catch (IntrospectionException e) {
                throw new IllegalStateException(e);
            }
        }

        // add operations
        InternalOperation[] mo = new TreeMap<String, InternalOperation>(mapOper).values()
                .toArray(new InternalOperation[mapOper.size()]);
        MBeanOperationInfo[] oInfo = new MBeanOperationInfo[mo.length];
        for (int i = 0; i < mo.length; i++) {
            oInfo[i] = new MBeanOperationInfo(mo[i].name, mo[i].description, null,
                    Void.TYPE.getName(), MBeanOperationInfo.UNKNOWN);
        }
        MBeanInfo info = new MBeanInfo(classname, description, aInfo, null, oInfo, null);
        return info;
    }

    /**
     * @see javax.management.DynamicMBean#invoke(java.lang.String,
     *      java.lang.Object[], java.lang.String[])
     */
    public Object invoke(String actionName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        try {
            InternalOperation aa = mapOper.get(actionName);
            return aa == null ? null : aa.invoke();

        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not compute value", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not compute value", e);
        }
    }

    public boolean isRegistered() {
        return name != null;
    }

    public synchronized void register(MBeanServer server, ObjectName name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        if (server == null) {
            throw new NullPointerException("server is null");
        } else if (server == null) {
            throw new NullPointerException("name is null");
        }
        if (this.name != null) {
            if (name.equals(name) && server.equals(mbs.get())) {
                return;
            }
            throw new IllegalArgumentException("Already registered under name " + name);
        }
        this.name = name;
        mbs = new SoftReference<MBeanServer>(server);
        try {
            server.registerMBean(this, name);
        } catch (NotCompliantMBeanException e) {
            throw new Error("error in class", e);
        }
    }

    public synchronized void register(MBeanServer server, String name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        register(server, toObjectName(name));
    }

    public synchronized void register(ObjectName on)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        register(ManagementFactory.getPlatformMBeanServer(), on);
    }

    public synchronized void register(String name) throws InstanceAlreadyExistsException,
            MBeanRegistrationException {
        register(ManagementFactory.getPlatformMBeanServer(), name);
    }

    /**
     * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
     */
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException, ReflectionException {

        AbstractAttribute aa = map.get(attribute.getName());
        if (aa == null) {
            throw new AttributeNotFoundException("Attribute " + attribute.getName()
                    + " could not be found");
        }
        try {
            aa.setValue(attribute.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MBeanException(e);
        }
    }

    /**
     * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
     */
    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    public synchronized void unregister() throws MBeanRegistrationException {
        if (mbs != null) {
            MBeanServer server = mbs.get();
            if (server != null) {
                try {
                    server.unregisterMBean(name);
                } catch (InstanceNotFoundException e) {
                    // ignore
                }
            }
            name = null;
            server = null;
        }
    }
}

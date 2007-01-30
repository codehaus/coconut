/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.coconut.core.util.Transformers;
import org.coconut.internal.util.ClassUtils;
import org.coconut.management.defaults.DefaultExecutableGroup;
import org.coconut.management.defaults.DefaultManagedGroup;

/**
 * Factory and utility methods for {@link ManagedGroup}, {@link
 * ManagedExecutableGroup} and various other classes defined in this package.
 * This class supports the following kinds of methods:
 * <ul>
 * <li> Methods that create and return an {@link ManagedGroup} set up with
 * commonly useful configuration settings.
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Managements {
    static class JmxRegistrant1 implements JmxRegistrant {
        private final String domain;

        private final int level;

        private final String[] levels;

        JmxRegistrant1(String domain, String[] levels) {
            this(domain, levels.clone(), 0);
        }

        JmxRegistrant1(String domain, String[] levels, int level) {
            this.level = level;
            this.domain = domain;
            this.levels = levels;
        }

        /**
         * @see org.coconut.management.JmxRegistrant#getDomain()
         */
        public String getDomain() {
            return domain;
        }

        /**
         * @see org.coconut.management.JmxRegistrant#registerChild(org.coconut.management.ManagedGroup)
         */
        public void registerChild(ManagedGroup mg) throws JMException {
            mg.registerAll(new JmxRegistrant1(domain, levels, level + 1));
        }

        /**
         * @see org.coconut.management.JmxRegistrant#getName(org.coconut.management.ManagedGroup)
         */
        public ObjectName getName(ManagedGroup mg) throws MalformedObjectNameException {
            ManagedGroup[] hierarchy = new ManagedGroup[level + 1];

            hierarchy[level] = mg;
            for (int i = level - 1; i >= 0; i--) {
                hierarchy[i] = hierarchy[i + 1].getParent();
            }

            StringBuilder name = new StringBuilder();
            name.append(domain);
            name.append(':');
            for (int i = 0; i <= level; i++) {
                name.append(levels[i]);
                name.append('=');
                name.append(hierarchy[i].getName());
                if (i != level) {
                    name.append(',');
                }
            }
            // System.out.println(name);
            return new ObjectName(name.toString());
        }
    }

    public static JmxRegistrant newRegistrant(String domain, String... levelname) {
        return new JmxRegistrant1(domain, levelname);
    }

    public static ManagedExecutableGroup newExecutableGroup() {
        return newExecutableGroup("");
    }

    /**
     * @param name
     * @return
     */
    public static ManagedExecutableGroup newExecutableGroup(String name) {
        return newExecutableGroup(name, "no description");
    }

    /**
     * @param name
     * @return
     */
    public static ManagedExecutableGroup newExecutableGroup(String name,
            String description) {
        return newExecutableGroup(name, description, true);
    }

    /**
     * @param name
     * @return
     */
    public static ManagedExecutableGroup newExecutableGroup(String name,
            String description, boolean register) {
        return new DefaultExecutableGroup(name, description, register);
    }

    public static ManagedGroup newGroup() {
        return newGroup("noname");
    }

    public static ManagedGroup newGroup(String name) {
        return newGroup(name, "no description");
    }

    public static ManagedGroup newGroup(String name, String description) {
        return new DefaultManagedGroup(name, description, false);
    }

    public static ManagedGroup newGroup(String name, String description,
            MBeanServer server) {
        DefaultManagedGroup d = new DefaultManagedGroup(name, description, false);
        d.setMbeanServer(server);
        return d;
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

    // TODO create a version where we can parse an attribute name along as well
    public static Number createNumberProxy(MBeanServerConnection server, String name)
            throws Exception {
        return createNumberProxy(server, toObjectName(name));
    }

    public static Number createNumberProxy(MBeanServerConnection server, ObjectName name)
            throws Exception {
        return new JMXNumberProxy(server, name, null);
    }

    public static Number createNumberProxy(MBeanServerConnection server, ObjectName name,
            Number defaultNumber) throws Exception {
        return new JMXNumberProxy(server, name, defaultNumber);
    }

    public static Number runningNumber(Object o, String method) {
        return new NumberProxy(o, method);
    }

    static class NumberProxy extends Number {
        private Object o;

        private Transformers.DynamicTransformer t;

        NumberProxy(Object o, String method) {
            this.o = o;
            t = Transformers.transform(o.getClass(), method);
            if (!ClassUtils.isNumberOrPrimitiveNumber(t.getMethod().getReturnType()))
                throw new IllegalArgumentException("Method " + t.getMethod()
                        + " does not return an instance of java.lang.Number, but "
                        + t.getMethod().getReturnType());
        }

        Number getNumber() {
            Number n = (Number) t.transform(o);
            return n;
        }

        /**
         * @see java.lang.Number#doubleValue()
         */
        @Override
        public double doubleValue() {
            return getNumber().doubleValue();
        }

        /**
         * @see java.lang.Number#floatValue()
         */
        @Override
        public float floatValue() {
            return getNumber().floatValue();

        }

        /**
         * @see java.lang.Number#intValue()
         */
        @Override
        public int intValue() {
            return getNumber().intValue();
        }

        /**
         * @see java.lang.Number#longValue()
         */
        @Override
        public long longValue() {
            return getNumber().longValue();
        }

    }

    static class JMXNumberProxy extends Number {
        private final MBeanServerConnection server;

        private final ObjectName name;

        private final String attributeName;

        private final Number defaultNumber;

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return getNumber().equals(obj);
        }

        @Override
        public int hashCode() {
            return getNumber().hashCode();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return getNumber().toString();
        }

        private Number getNumber() {
            try {
                return (Number) server.getAttribute(name, attributeName);
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                if (defaultNumber == null) {
                    throw new IllegalStateException("Update of number failed ", e);
                }
            }
            return defaultNumber;
        }

        /**
         * @param server
         * @param name
         */
        JMXNumberProxy(MBeanServerConnection server, ObjectName name, Number defaultNumber)
                throws Exception {
            MBeanAttributeInfo[] s = server.getMBeanInfo(name).getAttributes();
            if (s.length != 1) {
                throw new IllegalStateException("");
            }
            MBeanAttributeInfo info = s[0];
            Class c = Class.forName(info.getType());
            if (!Number.class.isAssignableFrom(c)) {
                throw new IllegalArgumentException();
            }
            // TODO check c is number
            this.server = server;
            this.name = name;
            this.attributeName = info.getName();
            this.defaultNumber = defaultNumber;
        }

        /**
         * @see java.lang.Number#doubleValue()
         */
        @Override
        public double doubleValue() {
            return getNumber().doubleValue();
        }

        /**
         * @see java.lang.Number#floatValue()
         */
        @Override
        public float floatValue() {
            return getNumber().floatValue();
        }

        /**
         * @see java.lang.Number#intValue()
         */
        @Override
        public int intValue() {
            return getNumber().intValue();
        }

        /**
         * @see java.lang.Number#longValue()
         */
        @Override
        public long longValue() {
            return getNumber().longValue();
        }
    }

    // easy to use jxm from
    // static Metric fromThreadPoolExecutor(ThreadPoolExecutor t) {
    // return null;
    // }
    // fromCollection (size?) ,fromQueue, fromMap (size?)
    // from Locks

    // we need some way of easy adding operations.
    // something with reflection
    // ala reflect(ThreadPoolExecutor t, "shutdown");
}

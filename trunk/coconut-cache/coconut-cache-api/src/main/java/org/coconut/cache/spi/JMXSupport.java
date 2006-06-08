/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.spi;


import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.coconut.cache.Cache;
import org.coconut.cache.management.CacheMXBean;
import org.coconut.event.bus.EventBus;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class JMXSupport {

    public static CacheMXBean createProxy(MBeanServer server, String name)
            throws MalformedObjectNameException {
        return createProxy(server, toObjectName(name));
    }

    public static CacheMXBean createProxy(MBeanServer server, ObjectName name) {
        return (CacheMXBean) MBeanServerInvocationHandler.newProxyInstance(
                server, name, CacheMXBean.class, false);
    }

    public static ObjectName registerCache(Cache c, String name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException, MalformedObjectNameException {

        ObjectName oName = toObjectName(name);
        registerCache(c, oName, ManagementFactory
                .getPlatformMBeanServer());
        return oName;
    }

    public static void unregisterCache(String name)
            throws InstanceNotFoundException, MBeanRegistrationException,
            MalformedObjectNameException {
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(
                toObjectName(name));

    }

    public static void registerCache(Cache c, ObjectName name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {
        registerCache(c, name, ManagementFactory.getPlatformMBeanServer());
    }

    public static void registerCache(Cache c, ObjectName name,
            MBeanServer mbs) throws InstanceAlreadyExistsException,
            MBeanRegistrationException, NotCompliantMBeanException {
        final AbstractCacheMXBean cache;

        if (c instanceof AbstractCache) {
            EventBus eb = ((AbstractCache) c).jmxRegistrant();
            cache = new AbstractCacheMXBean(c, eb, new Properties());
        } else {
            cache = new AbstractCacheMXBean(c, new Properties());
        }
        mbs.registerMBean(cache, name);

    }

    public static ObjectName toObjectName(String name)
            throws MalformedObjectNameException {
        return new ObjectName("org.coconut.cache:type=Cache,name=" + name);
    }
}
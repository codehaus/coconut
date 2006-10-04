/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.sandbox.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: FooBB.java 38 2006-08-22 10:09:08Z kasper $
 */
public class FooBB extends StandardMBean implements FooMXBean {

    /**
     * @param mbeanInterface
     * @throws NotCompliantMBeanException
     */
    protected FooBB() throws NotCompliantMBeanException {
        super(FooMXBean.class);
    }

    /**
     * @see org.coconut.cache.sandbox.jmx.FooMXBean#getFoo()
     */
    public long getFoo() {
        return 6;
    }

    public static void main(String[] args) throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException, InterruptedException {
        ObjectName o = new ObjectName("org.coconut.cache:type=Cache,name=foo");
        
        ObjectName o1 = new ObjectName("org.coconut.cache:type=Cache,name=foo,percentile=95");
        ObjectName o2 = new ObjectName("org.coconut.cache:type=Cache,name=foo,percentile=90th");
        ObjectName o3 = new ObjectName("org.coconut.cache:type=Cache,stuff=foo,percentile=90th");
        ObjectName o4 = new ObjectName("org.coconut.cache:type=Cache,buff=foo,percentile=90");
        ManagementFactory.getPlatformMBeanServer() 
                .registerMBean(new FooBB(), o);
        ManagementFactory.getPlatformMBeanServer()
        .registerMBean(new FooBB(), o1);
        ManagementFactory.getPlatformMBeanServer()
        .registerMBean(new FooBB(), o2);
        ManagementFactory.getPlatformMBeanServer()
        .registerMBean(new FooBB(), o3);
        ManagementFactory.getPlatformMBeanServer()
        .registerMBean(new FooBB(), o4);
        Thread.sleep(400009);
    }

}

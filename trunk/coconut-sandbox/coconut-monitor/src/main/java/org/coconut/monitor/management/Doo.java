/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.monitor.management;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Doo extends StandardMBean implements ProfileMXBean {

    /**
     * @param mbeanInterface
     * @throws NotCompliantMBeanException
     */
    protected Doo() throws NotCompliantMBeanException {
        super(ProfileMXBean.class);
    }

    final ConcurrentLinkedQueue<MovingAvgImpl> clq = new ConcurrentLinkedQueue<MovingAvgImpl>();

    private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    private String prefix = "org.coconut.profile:Type=Profile,component=Doo";

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#getTotal()
     */
    public long getTotal() {
        
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#getTotalSamplings()
     */
    public long getTotalSamplings() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#getTotalAverage()
     */
    public double getTotalAverage() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#registerMovingAverage(int)
     */
    public String registerMovingAverage(int samplings) {
        String name = prefix + ",name=mvg_avg_" + samplings;
        try {
            ObjectName on = new ObjectName(name);
            MovingAvgImpl i = new MovingAvgImpl(this,on,samplings);
            server.registerMBean(i, on);
            clq.add(i);
            return name;
        } catch (InstanceAlreadyExistsException i) {
            //ignore
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) throws MalformedObjectNameException,
            NullPointerException, InstanceAlreadyExistsException,
            MBeanRegistrationException, NotCompliantMBeanException,
            InterruptedException {
        ObjectName o = new ObjectName(
                "org.coconut.profile:Type=Profile,component=Doo");
        Doo d = new Doo();
        ManagementFactory.getPlatformMBeanServer().registerMBean(d, o);
        Random r = new Random();
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(15);
            long next = r.nextInt(1000);
            for (MovingAvgImpl m : d.clq) {
                m.add(next);
            }
        }
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#stuff()
     */
    public void stuff() {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#addProbe(java.lang.String)
     */
    public String addProbe(String uniqueName, int samplings) {
        String name = prefix + ",name=mvg_avg_" + uniqueName;
        try {
            ObjectName on = new ObjectName(name);
            MovingAvgImpl i = new MovingAvgImpl(this,on,samplings);
            server.registerMBean(i, on);
            clq.add(i);
            return name;
        } catch (InstanceAlreadyExistsException i) {
            //ignore
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#addProbe(java.lang.String)
     */
    public String addProbe(String uniqueName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#registerMovingAverage(java.lang.String, int)
     */
    public String registerMovingAverage(String name, int samplings) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#getInfo()
     */
    public StuffInfo getInfo() {
        return null;
    }

    /**
     * @see org.coconut.monitor.management.ProfileMXBean#getInfo2()
     */
    public StuffInfo getInfo2() {
       
        return new StuffComplex();
    }
}

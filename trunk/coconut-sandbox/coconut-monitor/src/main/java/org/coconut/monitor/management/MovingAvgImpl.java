/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.monitor.management;

import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

/**
 * This class is only synchronized because of memory effects
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class MovingAvgImpl extends StandardMBean implements MovingAverageMXBean {
    private long total;

    private final long[] circleBuffer;

    private boolean filledUp;

    private int pointer;

    private double average;

    private int samples;

    private long t;

    private ObjectName name;

    private Doo doo;
    MovingAvgImpl(Doo doo, ObjectName name, int size) throws NotCompliantMBeanException {
        super(MovingAverageMXBean.class);
        circleBuffer = new long[size];
        this.name = name;
        this.doo=doo;
    }

    synchronized void add(long value) {
        total -= circleBuffer[pointer];
        circleBuffer[pointer] = value;
        total += value;
        if (pointer == circleBuffer.length - 1) {
            pointer = 0;
            filledUp = true;
        } else {
            pointer++;
        }
    }

    /**
     * @see org.coconut.monitor.management.MovingAverageMXBean#getSamplings()
     */
    public synchronized int getSamplings() {
        if (filledUp) {
            return circleBuffer.length;
        } else {
            return pointer;
        }
    }

    /**
     * @see org.coconut.monitor.management.MovingAverageMXBean#getTotal()
     */
    public synchronized long getTotal() {
        return total;
    }

    /**
     * @see org.coconut.monitor.management.MovingAverageMXBean#getAverage()
     */
    public synchronized double getAverage() {
        int s = getSamplings();
        if (s == 0) {
            return Double.POSITIVE_INFINITY;
        } else {
            return total / getSamplings();
        }
    }

    /**
     * @see org.coconut.monitor.management.MovingAverageMXBean#removeBean()
     */
    public void remove() {
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(name);
            doo.clq.remove(this);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see org.coconut.monitor.management.Statistics#reset()
     */
    public void reset() {
        // TODO Auto-generated method stub
    }

    /**
     * @see org.coconut.monitor.management.Statistics#getStartTime()
     */
    public long getStartTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.monitor.management.Statistics#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.monitor.management.Statistics#getDescription()
     */
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.monitor.management.Statistics#getUnit()
     */
    public String getUnit() {
        // TODO Auto-generated method stub
        return null;
    }


}

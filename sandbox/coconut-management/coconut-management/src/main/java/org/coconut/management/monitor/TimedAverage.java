/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;

import org.coconut.core.EventProcessor;
import org.coconut.core.Named;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.spi.AbstractApmNumber;
import org.coconut.management.spi.Described;
import org.coconut.management.spi.JMXConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class TimedAverage extends AbstractApmNumber implements Runnable /*
                                                                             * ,
                                                                             * MetricHub<TimedAverage>
                                                                             */{
    /**
     * @see org.coconut.metric.spi.SingleJMXNumber#getNumberClass()
     */
    @Override
    protected Class<? extends Number> getNumberClass() {
        return Double.TYPE;
    }

    private final Number n;

    private long lastTimeStamp;

    private double lastValue;

    private double currentAverage;

    private final long adjust;

    /**
     * @param n
     * @param adjust
     */
    public TimedAverage(final Number n) {
        this(n, TimeUnit.SECONDS);
    }

    /**
     * @param n
     * @param adjust
     */
    public TimedAverage(final Number n, String name) {
        this(n, name, "No Description");
        setName(name);
    }

    /**
     * @param n
     * @param adjust
     */
    public TimedAverage(final Number n, String name, String description) {
        if (n instanceof Long || n instanceof Integer || n instanceof Double
                || n instanceof Float || n instanceof Short || n instanceof Byte) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        adjust = TimeUnit.SECONDS.toNanos(1);
        setName(name);
        setDescription(description);
    }

    public TimedAverage(final Number n, TimeUnit unit) {
        this(n, unit, 1);
    }

    public TimedAverage(final Number n, TimeUnit unit, long time) {
        if (n instanceof Long || n instanceof Integer || n instanceof Double
                || n instanceof Float || n instanceof Short || n instanceof Byte) {
            throw new IllegalArgumentException();
        }
        if (time <= 0) {
            throw new IllegalArgumentException();
        }
        if (!(n instanceof Named)) {
            throw new IllegalArgumentException();
        }
        Named na = (Named) n;
        // String name= time=1 ? +""
        // // TODO should probably check that it isn't an instance of
        // // Long, Double, Float, ...
        // // does make much sense to use them
        this.n = n;
        adjust = unit.toNanos(time);
        setName(na.getName() + "/s");
        if (na instanceof Described) {
            setDescription(((Described) na).getDescription() + " the last second");
        }
    }

    public TimedAverage(final Number n, String name, String description, TimeUnit unit,
            long time) {
        this.n = n;
        adjust = unit.toNanos(time);
    }

    public static TimedAverage newAndRegister(Number n, String name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        TimedAverage lr = new TimedAverage(n);
        lr.register("org.coconut.metric:name=" + name);
        return lr;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public synchronized void run() {
        if (lastTimeStamp == 0) {
            lastValue = n.doubleValue();
            lastTimeStamp = System.nanoTime();
            currentAverage = Double.NaN;
        } else {
            long now = System.nanoTime();
            double currentValue = n.doubleValue();
            double diff = currentValue - lastValue;
            currentAverage = diff == 0 ? Double.NaN : diff / ((now - lastTimeStamp))
                    * adjust;
            lastValue = currentValue;
            lastTimeStamp = now;
        }
        update();
    }

    @ManagedAttribute(defaultValue = "$name", description = "$description")
    public synchronized double getAverage() {
        return currentAverage;
    }

    /**
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public synchronized double doubleValue() {
        return currentAverage;
    }

    /**
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    /**
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return (int) doubleValue();
    }

    /**
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return (long) doubleValue();
    }

    /**
     * @see org.coconut.metric.spi.SingleJMXNumber#getValue()
     */
    @Override
    protected Number getValue() {
        return doubleValue();
    }

    /**
     * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
     */
    public long getDelay(TimeUnit unit) {
        return TimeUnit.NANOSECONDS.convert(adjust, unit);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Delayed o) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.concurrent.Callable#call()
     */
    public Number call() {
        return doubleValue();
    }

    /**
     * @see org.coconut.metric.spi.AbstractManagedNumber#prepare(org.coconut.metric.spi.ManagedConfigurator)
     */
    public void configure(JMXConfigurator jmx) {
        jmx.add(this);
    }

    private final List<EventProcessor<? super TimedAverage>> dependent = new ArrayList<EventProcessor<? super TimedAverage>>();

    /**
     * @see org.coconut.metric.MetricHub#addEventHandler(org.coconut.core.EventHandler)
     */
    public synchronized EventProcessor<? super TimedAverage> addEventHandler(
            EventProcessor<? super TimedAverage> e) {
        dependent.add(e);
        return e;
    }

    /**
     * @see org.coconut.metric.MetricHub#getEventHandlers()
     */
    public synchronized List<EventProcessor<? super TimedAverage>> getEventHandlers() {
        return new ArrayList<EventProcessor<? super TimedAverage>>(dependent);
    }

    protected void update() {
        if (dependent.size() > 0) {
            for (EventProcessor<? super TimedAverage> e : dependent) {
                e.process(this);
            }
        }
    }
}

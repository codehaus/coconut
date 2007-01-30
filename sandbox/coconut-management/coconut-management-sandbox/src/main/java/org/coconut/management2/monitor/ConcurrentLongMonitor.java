/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.monitor;

import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.EventProcessor;
import org.coconut.event.EventBus;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;
import org.coconut.management.spi.JMXConfigurator;
import org.coconut.management2.BaseMonitor;
import org.coconut.management2.spi.AbstractPassiveNumberMonitor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ConcurrentLongMonitor extends AbstractPassiveNumberMonitor implements
        EventProcessor<Number> {

    /** serialVersionUID */
    private static final long serialVersionUID = 2996708582772432042L;

    private final AtomicLong l = new AtomicLong();

    /**
     * @param name
     * @param description
     */
    ConcurrentLongMonitor(String name, String description) {
        super(name, description);
    }

    /**
     * Atomically adds the given value to the current value.
     * 
     * @param delta
     *            the value to add
     * @return the updated value
     */
    public long addAndGet(long delta) {
        return l.addAndGet(delta);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Number anotherLong) {
        double thisVal = this.longValue();
        double anotherVal = anotherLong.longValue();
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /**
     * @see org.coconut.metric.spi.AbstractManagedNumber#prepare(org.coconut.metric.spi.ManagedConfigurator)
     */
    public void configure(JMXConfigurator jmx) {
        jmx.add(this);
    }

    /**
     * Atomically decrements by one the current value.
     * 
     * @return the updated value
     */
    public long decrementAndGet() {
        return l.decrementAndGet();
    }

    /**
     * @see java.lang.Number#doubleValue()
     */
    public double doubleValue() {
        return l.doubleValue();
    }

    /**
     * @see java.lang.Number#floatValue()
     */
    public float floatValue() {
        return l.floatValue();
    }

    /**
     * Gets the current value.
     * 
     * @return the current value
     */
    @ManagedAttribute(defaultValue = "$name Total", description = "Total $description")
    public long get() {
        return l.get();
    }

    /**
     * Atomically adds the given value to the current value.
     * 
     * @param delta
     *            the value to add
     * @return the previous value
     */
    public long getAndAdd(long delta) {
        return l.getAndAdd(delta);
    }

    /**
     * Atomically decrements by one the current value.
     * 
     * @return the previous value
     */
    public long getAndDecrement() {
        return l.getAndDecrement();
    }

    /**
     * Atomically increments by one the current value.
     * 
     * @return the previous value
     */
    public long getAndIncrement() {
        return l.getAndIncrement();
    }

    /**
     * Atomically sets to the given value and returns the old value.
     * 
     * @param newValue
     *            the new value
     * @return the previous value
     */
    public long getAndSet(long newValue) {
        return l.getAndSet(newValue);
    }

    /**
     * @see org.coconut.management2.BaseMonitor#getEventBus()
     */
    public EventBus<? extends BaseMonitor> getEventBus() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public void process(Number n) {
        set(n.longValue());
    }

    /**
     * Atomically increments by one the current value.
     * 
     * @return the updated value
     */
    public long incrementAndGet() {
        return l.incrementAndGet();
    }

    /**
     * @see java.lang.Number#intValue()
     */
    public int intValue() {
        return l.intValue();
    }

    /**
     * @see java.lang.Number#longValue()
     */
    public long longValue() {
        return l.longValue();
    }

    @ManagedOperation(defaultValue = "reset $name", description = "Sets the value of $name to 0")
    public void reset() {
        set(0l);
    }

    /**
     * Sets to the given value.
     * 
     * @param newValue
     *            the new value
     */
    public void set(long newValue) {
        l.set(newValue);
    }

    /**
     * @see org.coconut.metric.spi.SingleJMXNumber#getNumberClass()
     */
    @Override
    protected final Class<? extends Number> getNumberClass() {
        return Long.TYPE;
    }

    /**
     * @see org.coconut.metric.spi.AbstractManagedNumber#getValue()
     */
    @Override
    protected final Number getValue() {
        return get();
    }
}

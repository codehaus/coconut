/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.active;

import java.util.Date;

import org.coconut.core.Clock;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;
import org.coconut.management.spi.AbstractApm;
import org.coconut.management.spi.JMXConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DateSampler extends AbstractApm implements Runnable {

    public final static long NO_TIMESTAMP = -1;

    private volatile long last = NO_TIMESTAMP;

    private final Clock clock;

    /**
     * Creates a new {@value class}
     * 
     * @param name
     *            the name of the apm
     * @param description
     *            the description of the apm
     */
    public DateSampler(String name, String description) {
        this(name, description, Clock.DEFAULT_CLOCK);
    }

    public DateSampler(String name, String description, Clock clock) {
        super(name, description);
        this.clock = clock;
    }

    /**
     * Sets the last update to the value of
     * {@link org.coconut.core.Clock#timestamp() }
     */
    public void run() {
        last = clock.timestamp();
    }

    /**
     * Returns the last time (as a {@link Date}) when this item was updated.
     */
    public Date getLastDate() {
        return new Date(last);
    }

    @ManagedAttribute(defaultValue = "$name last update", description = "$description")
    public long get() {
        return last;
    }

    /**
     * Sets the value of this item to the initial value ({@value #NO_TIMESTAMP}).
     */
    @ManagedOperation(defaultValue = "reset $name", description = "Sets the value of $name to the initial value ("
            + NO_TIMESTAMP + ")")
    public void reset() {
        last = NO_TIMESTAMP;
    }

    /**
     * @see org.coconut.metric.spi.ManagedMetric#prepare(org.coconut.metric.spi.ManagedConfigurator)
     */
    public void configure(JMXConfigurator jmx) {
        jmx.add(this);
    }
}

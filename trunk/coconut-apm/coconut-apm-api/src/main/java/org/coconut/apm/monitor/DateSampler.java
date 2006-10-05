/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.monitor;

import java.util.Date;

import org.coconut.apm.spi.AbstractApm;
import org.coconut.apm.spi.JMXConfigurator;
import org.coconut.core.Clock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DateSampler extends AbstractApm {

    private volatile long last;

    private final Clock clock;

    public DateSampler(String name, String description) {
        this(name, description, Clock.MILLI_CLOCK);
    }

    public DateSampler(String name, String description, Clock clock) {
        super(name,description);
        this.clock = clock;
    }

    public void updateNow() {
        last = clock.absolutTime();
    }

    public Date getLastDate() {
        return new Date(last);
    }

    public long get() {
        return last;
    }

    /**
     * @see org.coconut.metric.spi.ManagedMetric#prepare(org.coconut.metric.spi.ManagedConfigurator)
     */
    public void configureJMX(JMXConfigurator jmx) {
        jmx.add(this);
    }

}

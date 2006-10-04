/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.monitor;

import org.coconut.apm.spi.AbstractApm;
import org.coconut.apm.spi.JMXConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DateSampler extends AbstractApm {

    private long last;

    public void updateNow() {
        last = System.currentTimeMillis();
    }

    /**
     * @see org.coconut.metric.spi.ManagedMetric#prepare(org.coconut.metric.spi.ManagedConfigurator)
     */
    public void configureJMX(JMXConfigurator jmx) {
        jmx.add(this);
    }

}

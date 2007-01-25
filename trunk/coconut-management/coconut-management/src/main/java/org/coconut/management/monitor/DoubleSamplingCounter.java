/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.monitor;

import org.coconut.core.EventProcessor;
import org.coconut.management.Managements;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;
import org.coconut.management.spi.AbstractApm;
import org.coconut.management.spi.JMXConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DoubleSamplingCounter extends AbstractApm implements Runnable,
        EventProcessor<Number> {
    private double high = Double.NaN;

    private double low = Double.NaN;

    private double latest = Double.NaN;

    private double total = 0;

    private long samplings;

    private final Number n;

    public DoubleSamplingCounter() {
        this.n = null;
    }

    public DoubleSamplingCounter(Number number) {
        this.n = number;
    }

    private void update(double value) {
        if (!Double.isNaN(value)) {
            synchronized (this) {
                if (value < low || Double.isNaN(low)) {
                    low = value;
                }
                if (value > high || Double.isNaN(high)) {
                    high = value;
                }
                latest = value;
                total += value;
                samplings++;
            }
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        if (n == null) {
            throw new IllegalStateException("No number configured for Counter");
        }
        update(n.doubleValue());
    }

    /**
     * @see org.coconut.metric.MetricReporter#reset()
     */
    @ManagedOperation(defaultValue = "reset", description = "Resets all variables to their initial value")
    public synchronized void reset() {
        high = Double.NaN;
        low = Double.NaN;
        latest = Double.NaN;
        total = 0;
        samplings = 0;
    }

    @ManagedAttribute(defaultValue = "Low", description = "The lowest measured value (or Long.MAX_VALUE if no values have been recorded) ")
    public synchronized double getLow() {
        return low;
    }

    @ManagedAttribute(defaultValue = "High", description = "The highest measured value (or Long.MIN_VALUE if no values have been recorded) ")
    public synchronized double getHigh() {
        return high;
    }

    @ManagedAttribute(defaultValue = "Latest", description = "The latest recorded value (or Long.MIN_VALUE if no values have been recorded)")
    public synchronized double getLatest() {
        return latest;
    }

    @ManagedAttribute(defaultValue = "Total", description = "The total cummulative value of all recordings")
    public synchronized double getTotal() {
        return total;
    }

    public Number getRunningTotal() {
        return Managements.runningNumber(this, "getTotal");
    }

    @ManagedAttribute(defaultValue = "Samplings", description = "The total number of recordings")
    public synchronized double getSamplings() {
        return samplings;
    }

    public Number getRunningSamplings() {
        return Managements.runningNumber(this, "getSamplings");
    }

    @ManagedAttribute(defaultValue = "Average", description = "The average recorded value")
    public synchronized double getAverage() {
        return samplings == 0 ? Double.NaN : total / samplings;
    }

    Live live() {
        return new Live();
    }

    public class Live {
        public Number samplings() {
            return Managements.runningNumber(DoubleSamplingCounter.this, "getSamplings");
        }

        public Number total() {
            return Managements.runningNumber(DoubleSamplingCounter.this, "getTotal");
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Low: ");
        sb.append(low);
        sb.append(" High: ");
        sb.append(high);
        sb.append("\n");
        return sb.toString();
    }

    public String getName() {
        return "High/Low Metric stat";
    }

    public String getDescription() {
        return "Returns the highest and lowest value of all measurements "
                + " since the start (or the last reset)";
    }

    /**
     * @see org.coconut.metric.spi.ManagedMetric#prepare(org.coconut.metric.spi.ManagedConfigurator)
     */
    public void configure(JMXConfigurator jmx) {
        jmx.add(this);
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public void process(Number event) {
        if (n != null) {
            throw new IllegalStateException("Value cannot be updated externally");
        }
        update(event.doubleValue());
    }

}

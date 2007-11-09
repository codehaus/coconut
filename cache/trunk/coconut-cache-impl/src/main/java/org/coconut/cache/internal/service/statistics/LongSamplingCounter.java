/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class LongSamplingCounter {
    private long high = Long.MIN_VALUE;

    private long low = Long.MAX_VALUE;

    private long latest = Long.MIN_VALUE;

    private long total;

    private long samplings;

    /**
     * @param name
     */

    public LongSamplingCounter(String name, String description) {
       
    }


    public synchronized void report(long value) {
        if (value < low) {
            low = value;
        }
        if (value > high) {
            high = value;
        }
        latest = value;
        total += value;
        samplings++;
    }


    // @ManagedOperation(defaultValue = "reset", description = "Resets all
    // variables to their initial value")
    public synchronized void reset() {
        high = Long.MIN_VALUE;
        low = Long.MAX_VALUE;
        latest = Long.MIN_VALUE;
        total = 0;
        samplings = 0;
    }

    @ManagedAttribute(defaultValue = "$name Low", description = "The lowest measured value of $name (or Long.MAX_VALUE if no values have been recorded) ")
    public synchronized long getLow() {
        return low;
    }

    @ManagedAttribute(defaultValue = "$name High", description = "The highest measured value of $name (or Long.MIN_VALUE if no values have been recorded) ")
    public synchronized long getHigh() {
        return high;
    }

    @ManagedAttribute(defaultValue = "$name Latest", description = "The latest recorded value of $name (or Long.MIN_VALUE if no values have been recorded)")
    public synchronized long getLatest() {
        return latest;
    }

    @ManagedAttribute(defaultValue = "$name Total", description = "The total cummulative value of all recordings of $name")
    public synchronized long getTotal() {
        return total;
    }

    @ManagedAttribute(defaultValue = "$name Samplings", description = "The total number of recordings")
    public synchronized long getSamplings() {
        return samplings;
    }

    @ManagedAttribute(defaultValue = "$name Average", description = "The average recorded value of $name")
    public synchronized double getAverage() {
        return samplings == 0 ? Double.NaN : (double) total / samplings;
    }

    public Number liveSamplings() {
    	return null;
        //return Managements.runningNumber(this, "getSamplings");
    }

    public Number liveTotal() {
    	return null;
        //return Managements.runningNumber(this, "getTotal");
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
        sb.append(" Total: ");
        sb.append(total);
        sb.append(" Average: ");
        sb.append(getAverage());
        sb.append("\n");
        return sb.toString();
    }

    public String getDescription() {
        return "Returns the highest and lowest value of all measurements "
                + " since the start (or the last reset)";
    }

}

/**
 * 
 */
package org.coconut.management.threshold;

import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Thresholder implements Runnable, EventProcessor<Number> {
    private Number n;

    // when value has been high-> and gets back to normal
    private boolean sendBackToNormalUsage;

    /* Don't send warnings for this long */
    private long silentPeriod;

    private double toLow;

    private double toHigh;

    private long lastUpdate;

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        if (n == null) {
            throw new IllegalStateException("No Number configured for object");
        }
        update(n);
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public void process(Number event) {
        if (n != null) {
            throw new IllegalStateException("Number configured for object");
        }
        update(n);
    }

    private synchronized void update(Number value) {
        if (value.doubleValue() >= toHigh) {
            toHigh();
        } else if (value.doubleValue()<=toLow) {
            toHigh();
        }
    }

    private void toHigh() {

    }
}

/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.management2.monitor;

import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management2.spi.AbstractPassiveNumberMonitor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: SingleExponentialSmoothing.java 26 2006-07-14 11:42:29Z kasper $
 */
public class SingleExponentialSmoothingMonitor extends AbstractPassiveNumberMonitor {

    private double alpha;

    private double s = Double.NaN;

    public SingleExponentialSmoothingMonitor(double alpha, String name) {
        super(name);
        setAlpha(alpha);
    }

    public synchronized void setAlpha(double alpha) {
        if (alpha <= 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha should be >0 and <=1");
        }
        this.alpha = alpha;
    }

    /**
     * @see org.coconut.metric.spi.AbstractManagedNumber#getValue()
     */
    @Override
    protected synchronized Number getValue() {
        return s;
    }

    /**
     * @return the alpha
     */
    @ManagedAttribute(defaultValue = "$name SES Alpha", description = "The Alpha parameter used for smoothing $name", readOnly = false)
    public synchronized double getAlpha() {
        return alpha;
    }

    @ManagedAttribute(defaultValue = "$name Smoothed SES", description = "The Smoothed value of $name")
    public synchronized double getSmoothedValue() {
        return s;
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public void process(Number event) {
        handle(event.doubleValue());
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public synchronized void handle(double value) {
        double prev = s;
        s = Double.isNaN(s) ? value : value * alpha + (1 - alpha) * s;
        update(prev != s);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Number anotherDouble) {
        return Double.compare(getSmoothedValue(), anotherDouble.doubleValue());
    }
}

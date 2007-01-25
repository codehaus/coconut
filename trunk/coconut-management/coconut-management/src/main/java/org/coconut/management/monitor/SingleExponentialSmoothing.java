/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.monitor;

import org.coconut.core.EventProcessor;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.spi.AbstractApmNumber;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: SingleExponentialSmoothing.java 26 2006-07-14 11:42:29Z kasper $
 */
public class SingleExponentialSmoothing extends AbstractApmNumber implements
        Runnable, EventProcessor<Number> {

    private final Number n;

    private double alpha;

    private double s = Double.NaN;

    public SingleExponentialSmoothing(Number n, double alpha) {
        if (n == null) {
            throw new NullPointerException("n is null");
        }
        checkAlpha(alpha);
        this.n = n;
        this.alpha = alpha;
    }

    public SingleExponentialSmoothing(Number n, double alpha, String name) {
        super(name);
        checkAlpha(alpha);
        this.n = n;
        this.alpha = alpha;
    }

    public SingleExponentialSmoothing(double alpha, String name) {
        super(name);
        checkAlpha(alpha);
        this.n = null;
        this.alpha = alpha;
    }

    private void checkAlpha(double alpha) {
        if (alpha <= 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha should be >0 and <=1");
        }
    }

    // we should normalize it, ie like AveragePerTime

    // public SingleExponentialSmoothing(Number n, double alpha, long[]
    // observations) {
    // this.n = n;
    // this.alpha = alpha;
    // SingleExponentialSmoothing ses = new SingleExponentialSmoothing(n, alpha,
    // observations[0]);
    // for (int i = 1; i < observations.length; i++) {
    // ses.report(observations[i]);
    // }
    // this.s = ses.getDouble();
    // }

    public synchronized void setAlpha(double alpha) {
        checkAlpha(alpha);
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
     * @see java.lang.Runnable#run()
     */
    public void run() {
        if (n == null) {
            throw new IllegalStateException("No Number configured for object");
        }
        update(n.doubleValue());
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public void process(Number event) {
        if (n != null) {
            throw new IllegalStateException("Number configured for object");
        }
        update(event.doubleValue());
    }

    private synchronized void update(double value) {
        s = Double.isNaN(s) ? value : value * alpha + (1 - alpha) * s;
    }

}

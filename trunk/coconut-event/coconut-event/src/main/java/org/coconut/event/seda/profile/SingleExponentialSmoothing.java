/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.profile;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SingleExponentialSmoothing implements LongProfile, DoubleValue {
    private double alpha;

    private double s;

    public SingleExponentialSmoothing(double alpha, double initialValue) {
        this.alpha = alpha;
        this.s = initialValue;
    }

    public SingleExponentialSmoothing(double alpha, long[] observations) {
        this.alpha = alpha;
        SingleExponentialSmoothing ses = new SingleExponentialSmoothing(alpha,
                observations[0]);
        for (int i = 1; i < observations.length; i++) {
            ses.register(observations[i]);
        }
        this.s = ses.getDouble();
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * @see org.coconut.event.seda.profile.LongProfile#register(long)
     */
    public void register(long value) {
        s = value * alpha + (1 - alpha) * s;
    }

    public double getDouble() {
        return s;
    }
}

/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.concurrent.responsetime;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class RateLimiter {

    private double countNano;

    private long last;

    private double availableTokens;

    private final int tokenDepth;

    public RateLimiter(double rate, TimeUnit unit, int tokenDepth) {
        // check rate<0
        countNano = 1 / (TimeUnit.NANOSECONDS.convert(1, unit) * rate);
        System.out.println(countNano);
        availableTokens = tokenDepth;
        this.tokenDepth = tokenDepth;
        last = currentTimeNano();

    }

    protected long currentTimeNano() {
        return System.nanoTime();
    }

    public boolean accept() {
        return acceptAll(1);
    }

    public int acceptMaximum(int maximum) {
        return accept(maximum, true);
    }

    public boolean acceptAll(int count) {
        return accept(count, false) == count;
    }

    private int accept(int count, boolean acceptPartials) {
        long timeNow = currentTimeNano();
        long timeDelta = timeNow - last;
        double newTokens = timeDelta * countNano;
        availableTokens = Math.min(availableTokens + newTokens, tokenDepth);
        last = timeNow;
        if (availableTokens >= count) {
            availableTokens -= count;
            return count;
        } else if (acceptPartials) {
            int num = (int) availableTokens;
            availableTokens -= num;
            return num;
        } else {
            return 0;
        }
    }

    // public long set
    public double getTargetRate(TimeUnit unit) {
        return 1 / (countNano * TimeUnit.NANOSECONDS.convert(1, unit));
    }

    public double setTargetRate(double rate, TimeUnit unit) {
        double old = countNano;
        countNano = 1 / (TimeUnit.NANOSECONDS.convert(1, unit) * rate);
        return old;
    }

    public static void main(String[] args) {
        RateLimiter rl = new RateLimiter(3, TimeUnit.SECONDS, 10);
        for (int i = 0; i < 1000; i++) {
            // System.out.println(rl.acceptMaximum(6));
        }
        Class c;
        System.out.println(rl.getTargetRate(TimeUnit.SECONDS));
        System.out.println(rl.getTargetRate(TimeUnit.MILLISECONDS));
    }
}

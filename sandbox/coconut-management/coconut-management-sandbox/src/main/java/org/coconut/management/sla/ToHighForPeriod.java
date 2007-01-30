/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.Clock;
import org.coconut.core.Named;
import org.coconut.filter.ComparisonFilters;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ToHighForPeriod<E> implements Runnable {

    private final Clock clock = Clock.DEFAULT_CLOCK;

    private final AtomicLong id = new AtomicLong();

    private final AtomicLong counts = new AtomicLong();

    private final TimeUnit unit;

    private long nanoTime;

    private final Filter<E> f;

    private final E e;

    private long lastHigh = 0;

    ToHighForPeriod(Filter<E> f, E e, long time, TimeUnit period) {
        this.f = f;
        this.e = e;
        nanoTime = period.toNanos(time);
        this.unit = period;
    }

    public static ToHighForPeriod GreatherThen(Number b, Number cmp, long time,
            TimeUnit period) {
        Filter<Number> f = (Filter) ComparisonFilters.lessThen(new CmpNumber(b));
        return new ToHighForPeriod(f, cmp, time, TimeUnit.SECONDS);
    }

    public void run() {
        if (f.accept(e)) {
            if (lastHigh != 0) {
                if (counts.get() > 0) {
                    long diff = clock.relativeTime() - lastHigh;
                    outOfWarning((double) diff / unit.toNanos(1), unit);
                }
                counts.set(0);
                lastHigh = 0;
            }
        } else {
            if (lastHigh == 0) {
                lastHigh = clock.relativeTime();
            } else {
                long diff = clock.relativeTime() - lastHigh;
                if (diff > nanoTime) {
                    if (counts.get() == 0) {
                        id.incrementAndGet();
                    }
                    counts.incrementAndGet();
                    warning((double) diff / unit.toNanos(1), unit);
                }
            }
        }
    }

    protected void warning(double time, TimeUnit unit) {
        if (e instanceof Named) {
            System.out.println(((Named) e).getName() + " out of limit");
        }
        System.out.println(((Number) e).doubleValue() + " " + f.toString());
        System.out.println("warning " + time + " " + unit);
    }

    protected void outOfWarning(double time, TimeUnit unit) {
        if (e instanceof Named) {
            System.out.println(((Named) e).getName() + " in limit");
        }
        System.out.println(((Number) e).doubleValue() + " " + f.toString());
        System.out.println("outOfWarning " + time + " " + unit);
    }

    static class CmpNumber extends Number implements Comparable<Number> {
        private final double value;

        CmpNumber(double value) {
            this.value = value;
        }

        CmpNumber(Number value) {
            this.value = value.doubleValue();
        }

        /**
         * @see java.lang.Number#doubleValue()
         */
        @Override
        public double doubleValue() {
            return value;
        }

        /**
         * @see java.lang.Number#floatValue()
         */
        @Override
        public float floatValue() {
            return (float) value;
        }

        /**
         * @see java.lang.Number#intValue()
         */
        @Override
        public int intValue() {
            return (int) value;
        }

        /**
         * @see java.lang.Number#longValue()
         */
        @Override
        public long longValue() {
            return (long) value;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "" + value;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Number anotherLong) {
            double thisVal = this.doubleValue();
            double anotherVal = anotherLong.doubleValue();
            return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
        }
    }
}

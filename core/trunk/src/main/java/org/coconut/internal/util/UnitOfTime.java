/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Mostly Copied from the TimeUnit class for Jave SE 6.0. The reason is primarily support
 * for minutes,hours and days.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public enum UnitOfTime {

    DAYS {
        public long convert(long d, UnitOfTime u) {
            return u.toDays(d);
        }

        public String getSymbol() {
            return "d";
        }

        public long toDays(long d) {
            return d;
        }

        public long toHours(long d) {
            return x(d, C6 / C5, MAX / (C6 / C5));
        }

        public long toMicros(long d) {
            return x(d, C6 / C1, MAX / (C6 / C1));
        }

        public long toMillis(long d) {
            return x(d, C6 / C2, MAX / (C6 / C2));
        }

        public long toMinutes(long d) {
            return x(d, C6 / C4, MAX / (C6 / C4));
        }

        public long toNanos(long d) {
            return x(d, C6 / C0, MAX / (C6 / C0));
        }

        public long toSeconds(long d) {
            return x(d, C6 / C3, MAX / (C6 / C3));
        };

        int excessNanos(long d, long m) {
            return 0;
        }
    },
    HOURS {
        public long convert(long d, UnitOfTime u) {
            return u.toHours(d);
        }

        public String getSymbol() {
            return "h";
        }

        public long toDays(long d) {
            return d / (C6 / C5);
        }

        public long toHours(long d) {
            return d;
        }

        public long toMicros(long d) {
            return x(d, C5 / C1, MAX / (C5 / C1));
        }

        public long toMillis(long d) {
            return x(d, C5 / C2, MAX / (C5 / C2));
        }

        public long toMinutes(long d) {
            return x(d, C5 / C4, MAX / (C5 / C4));
        }

        public long toNanos(long d) {
            return x(d, C5 / C0, MAX / (C5 / C0));
        }

        public long toSeconds(long d) {
            return x(d, C5 / C3, MAX / (C5 / C3));
        };

        int excessNanos(long d, long m) {
            return 0;
        }
    },
    MICROSECONDS {
        public long convert(long d, UnitOfTime u) {
            return u.toMicros(d);
        }

        public String getSymbol() {
            return "µs";
        }

        public long toDays(long d) {
            return d / (C6 / C1);
        }

        public long toHours(long d) {
            return d / (C5 / C1);
        }

        public long toMicros(long d) {
            return d;
        }

        public long toMillis(long d) {
            return d / (C2 / C1);
        }

        public long toMinutes(long d) {
            return d / (C4 / C1);
        }

        public long toNanos(long d) {
            return x(d, C1 / C0, MAX / (C1 / C0));
        }

        public long toSeconds(long d) {
            return d / (C3 / C1);
        };

        int excessNanos(long d, long m) {
            return (int) ((d * C1) - (m * C2));
        }
    },
    MILLISECONDS {
        public long convert(long d, UnitOfTime u) {
            return u.toMillis(d);
        }

        public String getSymbol() {
            return "ms";
        }

        public long toDays(long d) {
            return d / (C6 / C2);
        }

        public long toHours(long d) {
            return d / (C5 / C2);
        }

        public long toMicros(long d) {
            return x(d, C2 / C1, MAX / (C2 / C1));
        }

        public long toMillis(long d) {
            return d;
        }

        public long toMinutes(long d) {
            return d / (C4 / C2);
        }

        public long toNanos(long d) {
            return x(d, C2 / C0, MAX / (C2 / C0));
        }

        public long toSeconds(long d) {
            return d / (C3 / C2);
        };

        int excessNanos(long d, long m) {
            return 0;
        }
    },
    MINUTES {
        public long convert(long d, UnitOfTime u) {
            return u.toMinutes(d);
        }

        public String getSymbol() {
            return "min";
        }

        public long toDays(long d) {
            return d / (C6 / C4);
        }

        public long toHours(long d) {
            return d / (C5 / C4);
        }

        public long toMicros(long d) {
            return x(d, C4 / C1, MAX / (C4 / C1));
        }

        public long toMillis(long d) {
            return x(d, C4 / C2, MAX / (C4 / C2));
        }

        public long toMinutes(long d) {
            return d;
        }

        public long toNanos(long d) {
            return x(d, C4 / C0, MAX / (C4 / C0));
        }

        public long toSeconds(long d) {
            return x(d, C4 / C3, MAX / (C4 / C3));
        };

        int excessNanos(long d, long m) {
            return 0;
        }
    },
    NANOSECONDS {
        public long convert(long d, UnitOfTime u) {
            return u.toNanos(d);
        }

        public String getSymbol() {
            return "ns";
        }

        public long toDays(long d) {
            return d / (C6 / C0);
        }

        public long toHours(long d) {
            return d / (C5 / C0);
        }

        public long toMicros(long d) {
            return d / (C1 / C0);
        }

        public long toMillis(long d) {
            return d / (C2 / C0);
        }

        public long toMinutes(long d) {
            return d / (C4 / C0);
        }

        public long toNanos(long d) {
            return d;
        }

        public long toSeconds(long d) {
            return d / (C3 / C0);
        };

        int excessNanos(long d, long m) {
            return (int) (d - (m * C2));
        }
    },
    SECONDS {
        public long convert(long d, UnitOfTime u) {
            return u.toSeconds(d);
        }

        public String getSymbol() {
            return "s";
        }

        public long toDays(long d) {
            return d / (C6 / C3);
        }

        public long toHours(long d) {
            return d / (C5 / C3);
        }

        public long toMicros(long d) {
            return x(d, C3 / C1, MAX / (C3 / C1));
        }

        public long toMillis(long d) {
            return x(d, C3 / C2, MAX / (C3 / C2));
        }

        public long toMinutes(long d) {
            return d / (C4 / C3);
        }

        public long toNanos(long d) {
            return x(d, C3 / C0, MAX / (C3 / C0));
        }

        public long toSeconds(long d) {
            return d;
        };

        int excessNanos(long d, long m) {
            return 0;
        }
    };

    // Handy constants for conversion methods

    final static long C0 = 1L;

    final static long C1 = C0 * 1000L;

    final static long C2 = C1 * 1000L;

    final static long C3 = C2 * 1000L;

    final static long C4 = C3 * 60L;

    final static long C5 = C4 * 60L;

    final static long C6 = C5 * 24L;

    final static long MAX = Long.MAX_VALUE;

    /**
     * Convert the given time duration in the given unit to this unit. Conversions from
     * finer to coarser granularities truncate, so lose precision. For example converting
     * <tt>999</tt> milliseconds to seconds results in <tt>0</tt>. Conversions from
     * coarser to finer granularities with arguments that would numerically overflow
     * saturate to <tt>Long.MIN_VALUE</tt> if negative or <tt>Long.MAX_VALUE</tt> if
     * positive.
     * <p>
     * For example, to convert 10 minutes to milliseconds, use:
     * <tt>TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)</tt>
     * 
     * @param sourceDuration
     *            the time duration in the given <tt>sourceUnit</tt>
     * @param sourceUnit
     *            the unit of the <tt>sourceDuration</tt> argument
     * @return the converted duration in this unit, or <tt>Long.MIN_VALUE</tt> if
     *         conversion would negatively overflow, or <tt>Long.MAX_VALUE</tt> if it
     *         would positively overflow.
     */
    public abstract long convert(long sourceDuration, UnitOfTime sourceUnit);

    /**
     * Returns the SI symbol of the time unit.
     * 
     * @return the SI symbol of the time unit
     */
    public abstract String getSymbol();

    /**
     * Performs a <tt>Thread.sleep</tt> using this unit. This is a convenience method
     * that converts time arguments into the form required by the <tt>Thread.sleep</tt>
     * method.
     * 
     * @param timeout
     *            the minimum time to sleep. If less than or equal to zero, do not sleep
     *            at all.
     * @throws InterruptedException
     *             if interrupted while sleeping.
     * @see Thread#sleep(long)
     */
    public void sleep(long timeout) throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            Thread.sleep(ms, ns);
        }
    }

    /**
     * Performs a timed <tt>Thread.join</tt> using this time unit. This is a convenience
     * method that converts time arguments into the form required by the
     * <tt>Thread.join</tt> method.
     * 
     * @param thread
     *            the thread to wait for
     * @param timeout
     *            the maximum time to wait. If less than or equal to zero, do not wait at
     *            all.
     * @throws InterruptedException
     *             if interrupted while waiting.
     * @see Thread#join(long, int)
     */
    public void timedJoin(Thread thread, long timeout) throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            thread.join(ms, ns);
        }
    }

    /**
     * Performs a timed <tt>Object.wait</tt> using this time unit. This is a convenience
     * method that converts timeout arguments into the form required by the
     * <tt>Object.wait</tt> method.
     * <p>
     * For example, you could implement a blocking <tt>poll</tt> method (see
     * {@link BlockingQueue#poll BlockingQueue.poll}) using:
     * 
     * <pre>
     *   public synchronized Object poll(long timeout, TimeUnit unit) throws InterruptedException {
     *    while (empty) {
     *      unit.timedWait(this, timeout);
     *      ...
     *    }
     *  }
     * </pre>
     * 
     * @param obj
     *            the object to wait on
     * @param timeout
     *            the maximum time to wait. If less than or equal to zero, do not wait at
     *            all.
     * @throws InterruptedException
     *             if interrupted while waiting.
     * @see Object#wait(long, int)
     */
    public void timedWait(Object obj, long timeout) throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            obj.wait(ms, ns);
        }
    }

    /**
     * Equivalent to <tt>DAYS.convert(duration, this)</tt>.
     * 
     * @param duration
     *            the duration
     * @return the converted duration
     * @see #convert
     * @since 1.6
     */
    public abstract long toDays(long duration);

    /**
     * Equivalent to <tt>HOURS.convert(duration, this)</tt>.
     * 
     * @param duration
     *            the duration
     * @return the converted duration, or <tt>Long.MIN_VALUE</tt> if conversion would
     *         negatively overflow, or <tt>Long.MAX_VALUE</tt> if it would positively
     *         overflow.
     * @see #convert
     * @since 1.6
     */
    public abstract long toHours(long duration);

    /**
     * Equivalent to <tt>MICROSECONDS.convert(duration, this)</tt>.
     * 
     * @param duration
     *            the duration
     * @return the converted duration, or <tt>Long.MIN_VALUE</tt> if conversion would
     *         negatively overflow, or <tt>Long.MAX_VALUE</tt> if it would positively
     *         overflow.
     * @see #convert
     */
    public abstract long toMicros(long duration);

    /**
     * Equivalent to <tt>MILLISECONDS.convert(duration, this)</tt>.
     * 
     * @param duration
     *            the duration
     * @return the converted duration, or <tt>Long.MIN_VALUE</tt> if conversion would
     *         negatively overflow, or <tt>Long.MAX_VALUE</tt> if it would positively
     *         overflow.
     * @see #convert
     */
    public abstract long toMillis(long duration);

    /**
     * Equivalent to <tt>MINUTES.convert(duration, this)</tt>.
     * 
     * @param duration
     *            the duration
     * @return the converted duration, or <tt>Long.MIN_VALUE</tt> if conversion would
     *         negatively overflow, or <tt>Long.MAX_VALUE</tt> if it would positively
     *         overflow.
     * @see #convert
     * @since 1.6
     */
    public abstract long toMinutes(long duration);

    /**
     * Equivalent to <tt>NANOSECONDS.convert(duration, this)</tt>.
     * 
     * @param duration
     *            the duration
     * @return the converted duration, or <tt>Long.MIN_VALUE</tt> if conversion would
     *         negatively overflow, or <tt>Long.MAX_VALUE</tt> if it would positively
     *         overflow.
     * @see #convert
     */
    public abstract long toNanos(long duration);

    /**
     * Equivalent to <tt>SECONDS.convert(duration, this)</tt>.
     * 
     * @param duration
     *            the duration
     * @return the converted duration, or <tt>Long.MIN_VALUE</tt> if conversion would
     *         negatively overflow, or <tt>Long.MAX_VALUE</tt> if it would positively
     *         overflow.
     * @see #convert
     */
    public abstract long toSeconds(long duration);

    /**
     * Utility to compute the excess-nanosecond argument to wait, sleep, join.
     * 
     * @param d
     *            the duration
     * @param m
     *            the number of milliseconds
     * @return the number of nanoseconds
     */
    abstract int excessNanos(long d, long m);

    public static UnitOfTime fromSiSymbol(String symbol) {
        for (UnitOfTime b : UnitOfTime.values()) {
            if (b.getSymbol().equals(symbol)) {
                return b;
            }
        }
        throw new IllegalArgumentException("not a valid timeunit, was '" + symbol + "'");
    }

    public static UnitOfTime fromTimeUnit(TimeUnit unit) {
        return UnitOfTime.values()[unit.ordinal()];
    }

    /**
     * Scale d by m, checking for overflow. This has a short name to make above code more
     * readable.
     */
    static long x(long d, long m, long over) {
        if (d > over)
            return Long.MAX_VALUE;
        if (d < -over)
            return Long.MIN_VALUE;
        return d * m;
    }

}

/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.util.concurrent.TimeUnit;

import org.w3c.dom.Element;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public enum UnitOfTime {
    
    NANOSECONDS {
        public long toNanos(long d)   { return d; }
        public long toMicros(long d)  { return d/(C1/C0); }
        public long toMillis(long d)  { return d/(C2/C0); }
        public long toSeconds(long d) { return d/(C3/C0); }
        public long toMinutes(long d) { return d/(C4/C0); }
        public long toHours(long d)   { return d/(C5/C0); }
        public long toDays(long d)    { return d/(C6/C0); }
        public long convert(long d, UnitOfTime u) { return u.toNanos(d); }
        public String getSymbol() {return "ns";};
        int excessNanos(long d, long m) { return (int)(d - (m*C2)); }
    },
    MICROSECONDS {
        public long toNanos(long d)   { return x(d, C1/C0, MAX/(C1/C0)); }
        public long toMicros(long d)  { return d; }
        public long toMillis(long d)  { return d/(C2/C1); }
        public long toSeconds(long d) { return d/(C3/C1); }
        public long toMinutes(long d) { return d/(C4/C1); }
        public long toHours(long d)   { return d/(C5/C1); }
        public long toDays(long d)    { return d/(C6/C1); }
        public long convert(long d, UnitOfTime u) { return u.toMicros(d); }
        public String getSymbol() {return "µs";};
        int excessNanos(long d, long m) { return (int)((d*C1) - (m*C2)); }
    },
    MILLISECONDS {
        public long toNanos(long d)   { return x(d, C2/C0, MAX/(C2/C0)); }
        public long toMicros(long d)  { return x(d, C2/C1, MAX/(C2/C1)); }
        public long toMillis(long d)  { return d; }
        public long toSeconds(long d) { return d/(C3/C2); }
        public long toMinutes(long d) { return d/(C4/C2); }
        public long toHours(long d)   { return d/(C5/C2); }
        public long toDays(long d)    { return d/(C6/C2); }
        public long convert(long d, UnitOfTime u) { return u.toMillis(d); }
        public String getSymbol() {return "ms";};
        int excessNanos(long d, long m) { return 0; }
    },
    SECONDS {
        public long toNanos(long d)   { return x(d, C3/C0, MAX/(C3/C0)); }
        public long toMicros(long d)  { return x(d, C3/C1, MAX/(C3/C1)); }
        public long toMillis(long d)  { return x(d, C3/C2, MAX/(C3/C2)); }
        public long toSeconds(long d) { return d; }
        public long toMinutes(long d) { return d/(C4/C3); }
        public long toHours(long d)   { return d/(C5/C3); }
        public long toDays(long d)    { return d/(C6/C3); }
        public long convert(long d, UnitOfTime u) { return u.toSeconds(d); }
        public String getSymbol() {return "s";};
        int excessNanos(long d, long m) { return 0; }
    },
    MINUTES {
        public long toNanos(long d)   { return x(d, C4/C0, MAX/(C4/C0)); }
        public long toMicros(long d)  { return x(d, C4/C1, MAX/(C4/C1)); }
        public long toMillis(long d)  { return x(d, C4/C2, MAX/(C4/C2)); }
        public long toSeconds(long d) { return x(d, C4/C3, MAX/(C4/C3)); }
        public long toMinutes(long d) { return d; }
        public long toHours(long d)   { return d/(C5/C4); }
        public long toDays(long d)    { return d/(C6/C4); }
        public long convert(long d, UnitOfTime u) { return u.toMinutes(d); }
        public String getSymbol() {return "min";};
        int excessNanos(long d, long m) { return 0; }
    },
    HOURS {
        public long toNanos(long d)   { return x(d, C5/C0, MAX/(C5/C0)); }
        public long toMicros(long d)  { return x(d, C5/C1, MAX/(C5/C1)); }
        public long toMillis(long d)  { return x(d, C5/C2, MAX/(C5/C2)); }
        public long toSeconds(long d) { return x(d, C5/C3, MAX/(C5/C3)); }
        public long toMinutes(long d) { return x(d, C5/C4, MAX/(C5/C4)); }
        public long toHours(long d)   { return d; }
        public long toDays(long d)    { return d/(C6/C5); }
        public long convert(long d, UnitOfTime u) { return u.toHours(d); }
        public String getSymbol() {return "h";};
        int excessNanos(long d, long m) { return 0; }
    },
    DAYS {
        public long toNanos(long d)   { return x(d, C6/C0, MAX/(C6/C0)); }
        public long toMicros(long d)  { return x(d, C6/C1, MAX/(C6/C1)); }
        public long toMillis(long d)  { return x(d, C6/C2, MAX/(C6/C2)); }
        public long toSeconds(long d) { return x(d, C6/C3, MAX/(C6/C3)); }
        public long toMinutes(long d) { return x(d, C6/C4, MAX/(C6/C4)); }
        public long toHours(long d)   { return x(d, C6/C5, MAX/(C6/C5)); }
        public long toDays(long d)    { return d; }
        public long convert(long d, UnitOfTime u) { return u.toDays(d); }
        public String getSymbol() {return "d";};
        int excessNanos(long d, long m) { return 0; }
    };

    public static void toElement(Element e, Long time, TimeUnit unit) {
        e.setTextContent(Long.toString(time));
        e.setAttribute("time-unit",UnitOfTime.from(unit).getSymbol());
    }

    public static void toElementCompact(Element e, Long time, TimeUnit unit) {
        long t = time;
        UnitOfTime b = UnitOfTime.from(unit);
        while (b.ordinal() != UnitOfTime.values().length) {
            UnitOfTime next = UnitOfTime.values()[b.ordinal() + 1];
            long from = next.convert(t, b);
            if (t == b.convert(from, next)) {
                b = next;
                t = from;
            } else {
                break;
            }
        }
        e.setAttribute("time-unit", b.getSymbol());
        e.setTextContent(Long.toString(t));
    }

    public static long fromElement(Element e, TimeUnit unit) {
        long val = Long.parseLong(e.getTextContent());
        UnitOfTime from = UnitOfTime.fromSymbol(e.getAttribute("time-unit"));
        return from.convertTo(val, unit);
    }
    public static void toElementAttributes(Element e, Long time, TimeUnit unit, String attrTime, String attrUnit) {
        long t = time;
        UnitOfTime b = UnitOfTime.from(unit);
        while (b.ordinal() != UnitOfTime.values().length) {
            UnitOfTime next = UnitOfTime.values()[b.ordinal() + 1];
            long from = next.convert(t, b);
            if (t == b.convert(from, next)) {
                b = next;
                t = from;
            } else {
                break;
            }
        }
        e.setAttribute(attrTime,Long.toString(t));
        e.setAttribute(attrUnit,b.getSymbol());
    }
    public static long fromAttributes(Element e, TimeUnit unit, String attrTime, String attrUnit) {
        long val = Long.parseLong(e.getAttribute(attrTime));
        UnitOfTime from = UnitOfTime.fromSymbol(e.getAttribute(attrUnit));
        return from.convertTo(val, unit);
    }
    
    public static UnitOfTime from(TimeUnit unit) {
        return UnitOfTime.values()[unit.ordinal()];
    }
    
    public long convertTo(long value, TimeUnit to) {
        return UnitOfTime.from(to).convert(value, this);
    }

    public static UnitOfTime fromSymbol(String symbol) {
        for (UnitOfTime b : UnitOfTime.values()) {
            if (b.getSymbol().equals(symbol)) {
                return b;
            }
        }
        throw new IllegalArgumentException("not a valid timeunit, was '" + symbol + "'");
    }
    // Handy constants for conversion methods
    static final long C0 = 1L;
    static final long C1 = C0 * 1000L;
    static final long C2 = C1 * 1000L;
    static final long C3 = C2 * 1000L;
    static final long C4 = C3 * 60L;
    static final long C5 = C4 * 60L;
    static final long C6 = C5 * 24L;

    static final long MAX = Long.MAX_VALUE;

    public abstract String getSymbol();
    /**
     * Scale d by m, checking for overflow.
     * This has a short name to make above code more readable.
     */
    static long x(long d, long m, long over) {
        if (d >  over) return Long.MAX_VALUE;
        if (d < -over) return Long.MIN_VALUE;
        return d * m;
    }

    /**
     * Convert the given time duration in the given unit to this
     * unit.  Conversions from finer to coarser granularities
     * truncate, so lose precision. For example converting
     * <tt>999</tt> milliseconds to seconds results in
     * <tt>0</tt>. Conversions from coarser to finer granularities
     * with arguments that would numerically overflow saturate to
     * <tt>Long.MIN_VALUE</tt> if negative or <tt>Long.MAX_VALUE</tt>
     * if positive.
     *
     * <p>For example, to convert 10 minutes to milliseconds, use:
     * <tt>TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)</tt>
     *
     * @param sourceDuration the time duration in the given <tt>sourceUnit</tt>
     * @param sourceUnit the unit of the <tt>sourceDuration</tt> argument
     * @return the converted duration in this unit,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     */
    public long convert(long sourceDuration, UnitOfTime sourceUnit) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to <tt>NANOSECONDS.convert(duration, this)</tt>.
     * @param duration the duration
     * @return the converted duration,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     * @see #convert
     */
    public long toNanos(long duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to <tt>MICROSECONDS.convert(duration, this)</tt>.
     * @param duration the duration
     * @return the converted duration,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     * @see #convert
     */
    public long toMicros(long duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to <tt>MILLISECONDS.convert(duration, this)</tt>.
     * @param duration the duration
     * @return the converted duration,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     * @see #convert
     */
    public long toMillis(long duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to <tt>SECONDS.convert(duration, this)</tt>.
     * @param duration the duration
     * @return the converted duration,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     * @see #convert
     */
    public long toSeconds(long duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to <tt>MINUTES.convert(duration, this)</tt>.
     * @param duration the duration
     * @return the converted duration,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     * @see #convert
     * @since 1.6
     */
    public long toMinutes(long duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to <tt>HOURS.convert(duration, this)</tt>.
     * @param duration the duration
     * @return the converted duration,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     * @see #convert
     * @since 1.6
     */
    public long toHours(long duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to <tt>DAYS.convert(duration, this)</tt>.
     * @param duration the duration
     * @return the converted duration
     * @see #convert
     * @since 1.6
     */
    public long toDays(long duration) {
        throw new AbstractMethodError();
    }

    /**
     * Utility to compute the excess-nanosecond argument to wait,
     * sleep, join.
     * @param d the duration
     * @param m the number of milliseconds
     * @return the number of nanoseconds
     */
    abstract int excessNanos(long d, long m);

    /**
     * Performs a timed <tt>Object.wait</tt> using this time unit.
     * This is a convenience method that converts timeout arguments
     * into the form required by the <tt>Object.wait</tt> method.
     *
     * <p>For example, you could implement a blocking <tt>poll</tt>
     * method (see {@link BlockingQueue#poll BlockingQueue.poll})
     * using:
     *
     * <pre>  public synchronized Object poll(long timeout, TimeUnit unit) throws InterruptedException {
     *    while (empty) {
     *      unit.timedWait(this, timeout);
     *      ...
     *    }
     *  }</pre>
     *
     * @param obj the object to wait on
     * @param timeout the maximum time to wait. If less than
     * or equal to zero, do not wait at all.
     * @throws InterruptedException if interrupted while waiting.
     * @see Object#wait(long, int)
     */
    public void timedWait(Object obj, long timeout)
    throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            obj.wait(ms, ns);
        }
    }

    /**
     * Performs a timed <tt>Thread.join</tt> using this time unit.
     * This is a convenience method that converts time arguments into the
     * form required by the <tt>Thread.join</tt> method.
     * @param thread the thread to wait for
     * @param timeout the maximum time to wait. If less than
     * or equal to zero, do not wait at all.
     * @throws InterruptedException if interrupted while waiting.
     * @see Thread#join(long, int)
     */
    public void timedJoin(Thread thread, long timeout)
    throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            thread.join(ms, ns);
        }
    }

    /**
     * Performs a <tt>Thread.sleep</tt> using this unit.
     * This is a convenience method that converts time arguments into the
     * form required by the <tt>Thread.sleep</tt> method.
     * @param timeout the minimum time to sleep. If less than
     * or equal to zero, do not sleep at all.
     * @throws InterruptedException if interrupted while sleeping.
     * @see Thread#sleep
     */
    public void sleep(long timeout) throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            Thread.sleep(ms, ns);
        }
    }

}

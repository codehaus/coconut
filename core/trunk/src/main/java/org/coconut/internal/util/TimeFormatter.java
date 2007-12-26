/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.text.DecimalFormat;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class TimeFormatter {

    public static TimeFormatter SHORT_FORMAT = new ShortFormat();

    public static TimeFormatter UPTIME_FORMAT = new UnixTime();

    final static DecimalFormat NN = new DecimalFormat("00");

    final static DecimalFormat NNN = new DecimalFormat("000");

    final static DecimalFormat Z = new DecimalFormat("##0.000");

    public String format(long time, UnitOfTime unit) {
        return formatNanos(unit.toNanos(time));
    }

    public String formatNanos(long nanos) {
        UnitOfTime t = UnitOfTime.NANOSECONDS;
        long reminder = 1000;

        int nano = (int) (t.toNanos(nanos) % 1000);
        if (nanos < reminder) {
            return doFormat(nano);
        }

        reminder *= 1000;
        int micro = (int) (t.toMicros(nanos) % 1000);
        if (nanos < reminder) {
            return doFormat(micro, nano);
        }

        reminder *= 1000;
        int millies = (int) (t.toMillis(nanos) % 1000);
        if (nanos < reminder) {
            return doFormat(millies, micro, nano);
        }

        reminder *= 60;
        int seconds = (int) (t.toSeconds(nanos) % 60);
        if (nanos < reminder) {
            return doFormat(seconds, millies, micro, nano);
        }

        reminder *= 60;
        int minutes = (int) (t.toMinutes(nanos) % 60);
        if (nanos < reminder) {
            return doFormat(minutes, seconds, millies, micro, nano);
        }

        reminder *= 24;
        int hours = (int) (t.toHours(nanos) % 24);
        if (nanos < reminder) {
            return doFormat(hours, minutes, seconds, millies, micro, nano);
        }

        int days = (int) t.toDays(nanos);
        return doFormat(days, hours, minutes, seconds, millies, micro, nano);
    }

    protected String doFormat(int nano) {
        return doFormat(0, nano);
    }

    protected String doFormat(int micros, int nano) {
        return doFormat(0, micros, nano);
    }

    protected String doFormat(int millies, int micros, int nano) {
        return doFormat(0, millies, micros, nano);
    }

    protected String doFormat(int seconds, int millies, int micros, int nano) {
        return doFormat(0, seconds, millies, micros, nano);
    }

    protected String doFormat(int minutes, int seconds, int millies, int micros, int nano) {
        return doFormat(0, minutes, seconds, millies, micros, nano);
    }

    protected String doFormat(int hours, int minutes, int seconds, int millies, int micros, int nano) {
        return doFormat(0, hours, minutes, seconds, millies, micros, nano);
    }

    protected String doFormat(int days, int hours, int minutes, int seconds, int millies,
            int micros, int nano) {
        throw new IllegalArgumentException("Cannot format time");
    }

    static class ShortFormat extends UnixTime {

        @Override
        protected String doFormat(int millies, int micros, int nano) {
            return Z.format((millies * 1000 + micros) / 1000.0) + " ms";
        }

        @Override
        protected String doFormat(int seconds, int millies, int micros, int nano) {
            return Z.format((seconds * 1000 + millies) / 1000.0) + " s";
        }
    }

    static class UnixTime extends TimeFormatter {
        @Override
        protected String doFormat(int days, int hours, int minutes, int seconds, int millies,
                int micros, int nano) {
            return days + " day(s), " + NN.format(hours) + ":" + NN.format(minutes) + ":"
                    + NN.format(seconds) + " hours";
        }
    }
}

/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class TimeFormatter {

    public static TimeFormatter DEFAULT_TIME_FORMATTER = new ShortFormat();

    public static TimeFormatter UPTIME_TIME_FORMATTER = new UptimeFormatter();

    private final static DecimalFormat NN = new DecimalFormat("00");

    private final static DecimalFormat Z = new DecimalFormat("##0.000");

    private final static String[] NAME = new String[] { "nanosecond", "microsecond", "millisecond",
            "second", "minute", "hour", "day" };

    private final static String[] NAMES = new String[] { "nanoseconds", "microseconds",
            "milliseconds", "seconds", "minutes", "hours", "days" };

    private final static String[] SI_NAMES = new String[] { "ns", "µs", "ms", "s", "min", "h", "d" };

    protected String getSIName(TimeUnit unit) {
        return SI_NAMES[unit.ordinal()];
    }

    protected String getName(long value, TimeUnit unit) {
        return value == 1 ? NAME[unit.ordinal()] : NAMES[unit.ordinal()];
    }

    public String format(long time, TimeUnit unit) {
        return formatNanos(unit.toNanos(time));
    }

    public String formatMillies(long millies) {
        return formatNanos(TimeUnit.MILLISECONDS.toNanos(millies));
    }

    public String formatNanos(long nanos) {
        int nano = (int) (nanos % 1000);
        int micro = (int) (nanos / 1000 % 1000);
        int milli = (int) (nanos / 1000 / 1000 % 1000);
        int second = (int) (nanos / 1000 / 1000 / 1000 % 60);
        int minute = (int) (nanos / 1000 / 1000 / 1000 / 60 % 60);
        int hour = (int) (nanos / 1000 / 1000 / 1000 / 60 / 60 % 24);
        int day = (int) (nanos / 1000 / 1000 / 1000 / 60 / 60 / 24);
        if (day > 0) {
            return doFormat(day, hour, minute, second, milli, micro, nano);
        } else if (hour > 0) {
            return doFormat(hour, minute, second, milli, micro, nano);
        } else if (minute > 0) {
            return doFormat(minute, second, milli, micro, nano);
        } else if (second > 0) {
            return doFormat(second, milli, micro, nano);
        } else if (milli > 0) {
            return doFormat(milli, micro, nano);
        } else if (micro > 0) {
            return doFormat(micro, nano);
        } else {
            return doFormat(nano);
        }
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
        throw new IllegalArgumentException("Cannot format the specified time");
    }

    static class ShortFormat extends UptimeFormatter {

        @Override
        protected String doFormat(int millies, int micros, int nano) {
            return Z.format((millies * 1000 + micros) / 1000.0) + " ms";
        }

        @Override
        protected String doFormat(int seconds, int millies, int micros, int nano) {
            return Z.format((seconds * 1000 + millies) / 1000.0) + " s";
        }
    }

    static class UptimeFormatter extends TimeFormatter {
        @Override
        protected String doFormat(int days, int hours, int minutes, int seconds, int millies,
                int micros, int nano) {
            return days + " day(s), " + NN.format(hours) + ":" + NN.format(minutes) + ":"
                    + NN.format(seconds) + " hours";
        }
    }
}

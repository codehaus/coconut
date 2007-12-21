/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimeFormatterTest {
    private TimeFormatter tf;

    @Test
    public void uptimeFormat() {
        tf = TimeFormatter.UPTIME_FORMAT;
        assertEquals("11 day(s), 14:25:43 hours", tf.formatNanos(1002343410000000L));
        assertEquals("11 day(s), 14:25:43 hours", tf.format(1002343, UnitOfTime.SECONDS));
        assertEquals("11 day(s), 14:25:00 hours", tf.format(1002300, UnitOfTime.SECONDS));
        assertEquals("11 day(s), 14:25:00 hours", tf.format(16705, UnitOfTime.MINUTES));
        assertEquals("11 day(s), 14:00:00 hours", tf.format(278, UnitOfTime.HOURS));
    }

    @Test
    public void shortFormat() {
        TimeFormatter tf = TimeFormatter.SHORT_FORMAT;
        assertTrue(tf.formatNanos(1000L).equals("0,001 ms")
                || tf.formatNanos(1000L).equals("0.001 ms"));
        assertTrue(tf.formatNanos(12321033L).equals("12,321 ms")
                || tf.formatNanos(12321033L).equals("12.321 ms"));
        assertTrue(tf.formatNanos(12321033000L).equals("12,321 s")
                || tf.formatNanos(12321033000L).equals("12.321 s"));

    }

    @Test
    public void formatNanos() {
        tf = new TestHelper();
        assertEquals("0000001", tf.formatNanos(1));
        assertEquals("0000010", tf.formatNanos(1000));
        assertEquals("0000100", tf.formatNanos(1000000));
        assertEquals("0001000", tf.formatNanos(1000000000));
        assertEquals("0010000", tf.formatNanos(60000000000L));
        assertEquals("0100000", tf.formatNanos(3600000000000L));
        assertEquals("1000000", tf.formatNanos(86400000000000L));
        assertEquals("1111111", tf.formatNanos(90061001001001L));
    }

    @Test
    public void format() {
        tf = new TestHelper();
        assertEquals("1000000", tf.format(1, UnitOfTime.DAYS));
        assertEquals("1493161641000", tf.format(1290061001, UnitOfTime.SECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatIAE() {
        new TimeFormatter() {}.formatNanos(1);
    }

    static class TestHelper extends TimeFormatter {
        @Override
        protected String doFormat(int days, int hours, int minutes, int seconds, int millies,
                int micros, int nano) {
            return "" + days + hours + minutes + seconds + millies + micros + nano;
        }
    }
}

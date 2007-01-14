/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.util;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CronExpression {

    public static CronExpression yearly() {
        return new CronExpression("0 0 1 1 *");
    }
    public static CronExpression annually() {
        return new CronExpression("0 0 1 1 *");
    }

    public static CronExpression monthly() {
        return new CronExpression("0 0 1 * *");
    }

    public static CronExpression weekly() {
        return new CronExpression("0 0 * * 0");
    }

    public static CronExpression daily() {
        return new CronExpression("0 0 * * *");
    }

    public static CronExpression midnight() {
        return new CronExpression("0 0 * * *");
    }

    public static CronExpression hourly() {
        return new CronExpression("0 * * * *");
    }

    private final String expression;

    private volatile TimeZone timeZone = TimeZone.getDefault();

    /**
     * Returns the time zone for which this <tt>CronExpression</tt> will be
     * evaluated.
     */
    public TimeZone getTimezone() {
        return timeZone;
    }

    /**
     * @param timezone
     *            the timezone to set
     */
    public void setTimezone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Constructs a new <CODE>CronExpression</CODE> based on the specified
     * parameter.
     * 
     * @param expression
     *            String representation of the cron expression the new object
     *            should represent
     * @throws java.text.ParseException
     *             if the string expression cannot be parsed into a valid
     *             <tt>CronExpression</tt>
     * @throws NullPointerException
     *             if the specified expression is <tt>null</tt>
     */
    public CronExpression(String expression) {
        this(expression, Locale.US);
    }

    private CronExpression(String expression, Locale locale) {
        if (expression == null) {
            throw new NullPointerException("expression is null");
        }

        this.expression = expression.toUpperCase(Locale.US);

        String[] split = expression.split("\t|\u0020");
        if (split.length != 5) {
            throw new IllegalArgumentException("Exactly five operands are needed, was "
                    + split.length + " " + Arrays.toString(split));
        }
        System.out.println(Arrays.toString(split));
    }

    public static void main(String[] args) throws ParseException {
        new CronExpression("* * * * *");
    }

    /**
     * Indicates whether the specified cron expression is a valid cron
     * expression
     * 
     * @param expression
     *            the expression to evaluate
     * @return <tt>true</tt> if the expression is a valid cron expression,
     *         otherwise false
     * @throws NullPointerException
     *             if the specified expression is <tt>null</tt>
     */
    public static boolean isValidExpression(String expression) {
        try {
            new CronExpression(expression);
        } catch (Exception pe) {
            return false;
        }
        return true;
    }
}

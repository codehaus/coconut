/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util.tabular;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class TabularFormatter {

    public final static DecimalFormat zz3 = new DecimalFormat("00");

    public final static DecimalFormat z3 = new DecimalFormat("0.000");

    public final static DecimalFormat z2 = new DecimalFormat("00.00");

    public final static DecimalFormat z1 = new DecimalFormat("000.0");

    public final static DecimalFormat z = new DecimalFormat("##0.000");

    public static String form(double value) {
        if (value >= 1000) {
            return Long.toString(((long) value));
        }
        if (value >= 0.1 && value < 1000) {
            String s = z.format(value);
            return s.substring(0, 5);
        } else {
            throw new IllegalArgumentException("");
        }
    }

    public static void main2(String[] args) {
        DecimalFormat ss = new DecimalFormat("##0.000");
        System.out.println(ss.format(400.34));
        System.out.println(ss.format(40.343));
        System.out.println(ss.format(4.3455));
        // System.out.println(ss.format(400.34));
    }

    static String app = "                          ";

    private String[][] list;

    private String[] header;

    private int currentRow;

    private int rows;

    private boolean useResultLine;

    public TabularFormatter(int rows, int columns) {
        list = new String[rows][columns];
        header = new String[columns];
        this.rows = rows;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                list[i][j] = "";
            }
        }
    }

    public void setResultLine(boolean useResultLine) {
        this.useResultLine = useResultLine;
    }

    public void setHeader(String... header) {
        this.header = header.clone();
    }

    public void add(int row, int column, String value) {
        list[row][column] = value;
    }

    public void addNextRow(Object... values) {
        for (int i = 0; i < values.length; i++) {
            list[currentRow][i] = values[i].toString();
        }
        currentRow++;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        int max[] = new int[header.length];
        for (int i = 0; i < header.length; i++) {
            max[i] = header[i].length();
            for (int j = 0; j < rows; j++) {
                if (list[j][i] != null) {
                    max[i] = Math.max(max[i], list[j][i].length());
                }
            }
        }
        for (int i = 0; i < header.length; i++) {
            format(sb, header[i], max[i]);
            if (i != header.length) {
                sb.append(" | ");
            }
        }
        sb.append("\n");
        int lineWidth = sb.length() - 2;
        addLine(sb, lineWidth);

        for (int i = 0; i < rows; i++) {
            if (i == rows - 1 && useResultLine) {
                addLine(sb, lineWidth);
            }
            for (int j = 0; j < header.length; j++) {
                format(sb, list[i][j], max[j]);
                if (j != header.length) {
                    sb.append(" | ");
                }
                // max[i] = Math.max(max[i], list[j][i].length());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void addLine(StringBuilder sb, int length) {
        for (int i = 0; i < length; i++) {
            sb.append("-");
        }
        sb.append("\n");
    }

    private void format(StringBuilder sb, String value, int max) {
        if (value == null) {
            value = "";
        }
        int diff = max - value.length();
        int pre = (int) Math.ceil(((double) Math.max(0, diff)) / 2);
        int post = (int) Math.floor(((double) Math.max(0, diff)) / 2);
        sb.append(app.substring(app.length() - pre));
        sb.append(value);
        sb.append(app.substring(app.length() - post));
    }

    public static String formatTime(double nanos) {
        double micros = (nanos) / 1000;
        double millies = (micros) / 1000;
        double seconds = (millies) / 1000;
        if (micros < 1) {
            return nanos + " ns";
        } else if (micros < 1000) {
            return form(micros) + " µs";
        } else if (millies < 1000) {
            return form(millies) + " ms";
        } else if (seconds < 1000) {
            return form(seconds) + " s";
        } else
            return ((long) seconds) + " s";
    }
    public static String formatTime2(final long total,TimeUnit unit) {
        return formatTime2(unit.toNanos(total));
    }
    public static String formatTime2(final long totalNano) {
        long nano = TimeUnit.NANOSECONDS.toNanos(totalNano) % 1000;
        long micro = TimeUnit.NANOSECONDS.toMicros(totalNano) % 1000;
        long millies = TimeUnit.NANOSECONDS.toMillis(totalNano) % 1000;
        long sup = TimeUnit.NANOSECONDS.toSeconds(totalNano);
        long seconds = (sup) % 60;
        sup -= seconds;
        long minutes = (sup / 60) % 60;
        sup -= minutes * 60;
        long hours = (sup / 3600) % 24;
        sup -= hours * 24 * 60;
        long days = (sup / (24 * 3600));
        StringBuilder sb = new StringBuilder();
        if (days != 0) {
            sb.append(days + " day(s), ");
        }
        if (days != 0 || hours != 0 || minutes != 0 || seconds != 0) {
            sb.append(hours + ":" + zz3.format(minutes) + ":" + zz3.format(seconds)
                    + " hours");
            return sb.toString();
        }
        return sb.toString();
        // if (n != 0) {
        // return n + " ns";
        // } else if (micros != 0) {
        // return micros + " µs";
        // } else if (millies != 0) {
        // return millies + " ms";
        // } else {
        // return seconds + " s";
        // }
    }

    public static void main(String[] args) {
        System.out.println(formatTime2(3));
        long value = 10023434l * 1000000000l;
        System.out.println(formatTime2(value));
        System.out.println(formatTime2(4000000));
        System.out.println(formatTime2(124000234433000l));
        System.out.println(zz3.format(2));
    }
}

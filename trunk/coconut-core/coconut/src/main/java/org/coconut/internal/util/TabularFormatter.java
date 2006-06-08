/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.internal.util;

import java.text.DecimalFormat;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class TabularFormatter {

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

    public static void main(String[] args) {
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
        this.header = header;
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

    public static String formatTime(long nanos) {
        double micros = ((double) nanos) / 1000;
        double millies = ((double) micros) / 1000;
        double seconds = ((double) millies) / 1000;
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
}

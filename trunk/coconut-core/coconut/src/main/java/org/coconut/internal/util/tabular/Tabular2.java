/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.internal.util.tabular;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Tabular2 {

    static String space = "                          ";

    private String[][] list;

    private int currentRow;

    private int rows;

    private int columns;

    private boolean showHeaderColumn;

    private boolean showHeaderRow;

    public Tabular2(int rows, int columns, boolean showHeaderColumn,
            boolean showHeaderRow) {
        list = new String[rows][columns];
        this.rows = rows;
        this.columns = columns;
        this.showHeaderColumn = showHeaderColumn;
        this.showHeaderRow = showHeaderRow;
    }

    public void set(int row, int column, String value) {
        list[row][column] = value;
    }

    void addNextRow(Object... values) {
        for (int i = 0; i < values.length; i++) {
            list[currentRow][i] = values[i].toString();
        }
        currentRow++;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int max[] = new int[columns];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (list[j][i] == null) {
                    list[j][i] = "";
                }
                max[i] = Math.max(max[i], list[j][i].length());
                
            }
        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                format(sb, list[i][j], max[j]);
                if (j != columns) {
                    sb.append(" | ");
                }
                // max[i] = Math.max(max[i], list[j][i].length());
            }
            sb.append("\n");
            if (i == 0 && showHeaderColumn) {
                int l = sb.length()-2;
                for (int j = 0; j < l; j++) {
                    sb.append("-");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void format(StringBuilder sb, String value, int max) {
        if (value == null) {
            value = "";
        }
        final int diff = max - value.length();

        int pre = (int) Math.ceil(((double) Math.max(0, diff)) / 2);
        int post = (int) Math.floor(((double) Math.max(0, diff)) / 2);
        sb.append(space.substring(space.length() - pre));
        sb.append(value);
        sb.append(space.substring(space.length() - post));
    }
}

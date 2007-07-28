/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util.tabular;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Tabular {

    //private char separator = '*';

    private final List<ArrayList<String>> columns = new ArrayList<ArrayList<String>>();

    private int currentRow ;

    public void addRow(String... strs) {
        if (strs == null) {
            throw new NullPointerException("strs is null");
        }
        for (int i = 0; i < strs.length; i++) {
            if (currentRow++ >= columns.size()) {
                columns.add(new ArrayList<String>());
            }
            ArrayList<String> column = columns.get(i);
            column.add(strs[i]);
        }
    }

    public void addColumn(String... strs) {
        if (strs == null) {
            throw new NullPointerException("strs is null");
        }
        for (int i = 0; i < strs.length; i++) {
            if (currentRow++ >= columns.size()) {
                columns.add(new ArrayList<String>());
            }
            ArrayList<String> column = columns.get(i);
            column.add(strs[i]);
        }
    }

    public void addRowToString(Object... strs) {

    }

//    public String toString() {
//
//        StringBuilder sb = new StringBuilder();
//        int max[] = new int[header.length];
//        for (int i = 0; i < header.length; i++) {
//            max[i] = header[i].length();
//            for (int j = 0; j < rows; j++) {
//                max[i] = Math.max(max[i], list[j][i].length());
//            }
//        }
//        for (int i = 0; i < header.length; i++) {
//            format(sb, header[i], max[i]);
//            if (i != header.length) {
//                sb.append(" | ");
//            }
//        }
//        sb.append("\n");
//        int lineWidth = sb.length() - 2;
//        addLine(sb, lineWidth);
//
//        for (int i = 0; i < rows; i++) {
//            if (i == rows - 1 && useResultLine) {
//                addLine(sb, lineWidth);
//            }
//            for (int j = 0; j < header.length; j++) {
//                format(sb, list[i][j], max[j]);
//                if (j != header.length) {
//                    sb.append(" | ");
//                }
//                // max[i] = Math.max(max[i], list[j][i].length());
//            }
//            sb.append("\n");
//        }
//        return sb.toString();
//    }

}

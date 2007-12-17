/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

public final class SystemErrOutHelper {
    public LinkedList<String> last = new LinkedList<String>();

    private final boolean isErr;

    PrintStream old;

    PrintStream p;

    private SystemErrOutHelper(boolean isError) {
        this.isErr = isError;
    }

    public String getFromLast(int pos) {
        int size = last.size();
        return last.get(size - pos - 1);
    }

    public void printString(String str) {
        last.add(str);
    }

    public void terminate() {
        if (isErr) {
            System.setErr(old);
        } else {
            System.setOut(old);
        }
    }

    public static SystemErrOutHelper get() {
        SystemErrOutHelper ps = new SystemErrOutHelper(false);
        ps.p = new PrintStream(ps.new MyOutput());
        ps.old = System.out;
        ps.last.add("");
        System.setOut(ps.p);
        return ps;
    }

    public static SystemErrOutHelper getErr() {
        SystemErrOutHelper ps = new SystemErrOutHelper(true);
        ps.p = new PrintStream(ps.new MyOutput());
        ps.old = System.err;
        //System.out.println(System.identityHashCode(ps.old));
        ps.last.add("");
        System.setErr(ps.p);
        return ps;
    }

    private class MyOutput extends OutputStream {
        private StringBuffer buf = new StringBuffer();

        public void write(int b) throws IOException {
            buf.append((char) b);
            // System.err.println(b);
            if (b == 10) {
                printString(buf.toString());
                buf = new StringBuffer();
            }
        }
    }
}

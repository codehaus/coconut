/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.test.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public final class SystemErrCatcher {
    private final StringBuffer sb = new StringBuffer();

    private final PrintStream old;

    SystemErrCatcher() {
        old = System.err;
        System.setErr(new PrintStream(new MyOutput()));
    }

    public String toString() {
        return sb.toString();
    }

    public void stop() {
        System.setErr(old);
    }

    public static SystemErrCatcher start() {
        return new SystemErrCatcher();
    }

    private class MyOutput extends OutputStream {
        public void write(int b) throws IOException {
            sb.append((char) b);
        }
    }

}

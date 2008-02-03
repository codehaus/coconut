/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.test.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public final class SystemOutCatcher {
    private StringBuffer sb = new StringBuffer();

    private final PrintStream old;

    SystemOutCatcher() {
        old = System.out;
        System.setOut(new PrintStream(new MyOutput()));
    }

    public String toString() {
        return sb.toString();
    }

    public void terminate() {
        System.setOut(old);
    }

    public static SystemOutCatcher get() {
        return new SystemOutCatcher();
    }

    private class MyOutput extends OutputStream {
        public void write(int b) throws IOException {
            sb.append((char) b);
        }
    }

}

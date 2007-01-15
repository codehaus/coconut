/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.net;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Ping {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Process p = new ProcessBuilder("ping", "-n", "1",
        // "localhost").start();
        Process p = new ProcessBuilder("cmd").start();

        PrintWriter pw = new PrintWriter(p.getOutputStream());
        pw.append("ping -n 1 localhost\n");
        long start = System.nanoTime();
        pw.flush();
        pw.append("exit\n");
        pw.flush();
        p.waitFor();
        System.out.println(System.nanoTime() - start);
        int i = p.getInputStream().read();

        while (p.getInputStream().available() > 0) {
            System.out.print((char) i);
            i = p.getInputStream().read();
        }

    }
}

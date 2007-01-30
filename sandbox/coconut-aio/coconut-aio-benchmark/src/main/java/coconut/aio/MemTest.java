/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package coconut.aio;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncSocket;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class MemTest {
    public static void main(String[] args) throws IOException {
        AsyncSocket as = AsyncSocket.open();
        long time = System.currentTimeMillis();
        System.out.println("started");
        for (int i = 0; i < 100; i++) {
            AsyncServerSocket s = AsyncServerSocket.open();
            s.close().getIO();
        }
        int count = 0;
        AsyncServerSocket[] sockets = new AsyncServerSocket[100000];
        long mem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
        time = System.currentTimeMillis();
        try {
            for (int i = 0; i < 100000; i++) {
                sockets[i] = AsyncServerSocket.open();
                count++;
                // s.close().getIO();
            }
        } finally {
            System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() - mem);
        }
        System.out.println();
        System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
        System.out.println("endded");
        System.out.println(System.currentTimeMillis() - time);
    }
}
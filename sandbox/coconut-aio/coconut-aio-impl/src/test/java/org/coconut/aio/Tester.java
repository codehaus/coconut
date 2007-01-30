/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class Tester {

    public static void closeException(String[] args) throws IOException, InterruptedException {
        SocketChannel sc = SocketChannel.open();
        Runnable r = new Runnable() {

            public void run() {
                try {
                    ServerSocketChannel ch = ServerSocketChannel.open();
                    ch.socket().bind(new InetSocketAddress(1231));
                    ch.accept();
                    System.out.println("accepted");
                    Thread.sleep(4000);
                    ch.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        Executors.newSingleThreadExecutor().submit(r);
        Thread.sleep(15);
        sc.connect(new InetSocketAddress("localhost", 1231));
        sc.configureBlocking(false);
        Thread.sleep(1000);
        sc.socket().setSoLinger(true, 1);
        ByteBuffer buf = DebugUtil.allocate(1024 * 1024 * 16, (byte) 34);
        sc.write(buf);
        sc.close();

        System.out.println("bye");
    }
}
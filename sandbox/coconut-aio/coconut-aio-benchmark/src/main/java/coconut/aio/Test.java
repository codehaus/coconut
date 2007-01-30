/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package coconut.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.coconut.aio.AsyncSocket;
import org.coconut.aio.management.ManagementFactory;
import org.coconut.aio.management.SocketMXBean;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class Test extends Thread {

    private static volatile long count = 0;
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel sc = SocketChannel.open();
        final int bufSize = 8192 * 128;
        final long loop = 10000;
        final long total = bufSize * loop;
        (new Test()).start();

        ByteBuffer buf = ByteBuffer.allocate(bufSize);

        AsyncSocket s = AsyncSocket.open();
        s.connect(new InetSocketAddress("localhost", 561)).getIO();
        for (int i = 0; i < loop; i++) {
            s.write(buf.duplicate());
        }

        /*
         * sc.connect(new InetSocketAddress("localhost", 561));
         * 
         * for (int i = 0; i < loop; i++) { sc.write(buf.duplicate()); }
         */

        SocketMXBean mf = ManagementFactory.getSocketMXBean();

        long start = System.currentTimeMillis();
        long last = 0;
        for (;;) {
            long c = count;
            long bw = mf.getBytesWritten(s.getId());
            if (bw < total) {
                //System.out.println("ALL" + (bufSize));
                System.out.println("Remaining " + (total - bw) + " bytes");
                long now = System.currentTimeMillis();
                double rate = ((double) bw) / (now - start);
                System.out.println("avg " + rate + " kb /s");
                Thread.sleep(10000);
            } else {
                if (c < total) {
                    if (c == last) {
                        System.out.println(c + " received ");
                        System.out.println(bufSize * loop + " expected ");
                        break;
                    } else {
                        last = c;
                        Thread.sleep(5000);
                    }

                } else {
                    System.out.println("Socces " + count + " written (100 %)");
                    break;
                }
            }
        }

        System.out.println(mf.getBytesWritten(s.getId()) + " says management");
        Thread.sleep(10000);
        System.out.println(mf.getBytesWritten(s.getId()) + " says management");
        System.out.println(mf.getBytesWritten() + " says management");
        System.out.println(mf.getSocketInfo(s.getId()).getBytesWritten() + " says management");

        Thread.sleep(10000);
        System.out.println(mf.getBytesWritten(s.getId()) + " says management");
        System.out.println(mf.getBytesWritten() + " says management");
        System.out.println(mf.getSocketInfo(s.getId()).getBytesWritten() + " says management");
        System.out.println(count);

    }
    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(561));
            SocketChannel sc = ssc.accept();
            //ssc.configureBlocking(false);
            ByteBuffer buf = ByteBuffer.allocate(8192);
            for (;;) {
                count += sc.read(buf);
                buf.rewind();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
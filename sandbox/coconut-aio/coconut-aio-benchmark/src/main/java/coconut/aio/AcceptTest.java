/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package coconut.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncServerSocket.SocketAccepted;
import org.coconut.core.EventProcessor;

public class AcceptTest {

    public void accept(long time, TimeUnit unit, int port) throws IOException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger count = new AtomicInteger(-1);

        AsyncServerSocket s = AsyncServerSocket.open();
        s.bind(new InetSocketAddress(port));
        QueueOfferable o = new QueueOfferable(new EventProcessor() {
            public void process(Object socket) {
                AsyncServerSocket.SocketAccepted sa = (SocketAccepted) socket;
                if (count.incrementAndGet() == 0)
                    latch.countDown();
                try {
                    sa.getAcceptedSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        s.startAccepting(o).getIO();
        System.out.println("started");
        latch.await();
        long start = System.currentTimeMillis();
        unit.sleep(time);
        long stop = System.currentTimeMillis();
        System.out.println("Total time:" + (stop - start));
        System.out.println("Total accepts:" + (count.get()));
        s.close().getIO();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AcceptTest at = new AcceptTest();
        at.accept(60, TimeUnit.SECONDS, 80);
    }
}
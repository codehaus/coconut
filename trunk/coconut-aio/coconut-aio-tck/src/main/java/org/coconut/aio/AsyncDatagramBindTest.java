package org.coconut.aio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.CountDownLatch;

import org.coconut.aio.monitor.DatagramMonitor;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AsyncDatagramBindTest extends AioTestCase {

    public void testBind() throws IOException {
        final int port = getNextPort();
        final AsyncDatagram socket = getFactory().openDatagram();
        
        assertFalse(socket.isBound());
        assertSame(socket, socket.bind(createBindingAddress(port)));
        assertTrue(socket.isBound());
        //assertEquals(port, socket.getLocalPort()); //NIO bug windows
        //assertTrue(socket.getInetAddress().isAnyLocalAddress());//NIO bug win
        socket.close().getIO();
    }

    public void testBindMonitor() throws IOException, InterruptedException {

        final AsyncDatagram socket = getFactory().openDatagram();
        final SocketAddress adr = createBindingAddress(getNextPort());
        final CountDownLatch latch = new CountDownLatch(1);

        socket.setMonitor(new DatagramMonitor() {
            public void bound(AsyncDatagram s, SocketAddress address) {
                assertSame(socket, s);
                assertEquals(adr, address);
                latch.countDown();
            }
        });

        socket.bind(adr);
        awaitOnLatch(latch);
        socket.close().getIO();
    }

    public void testBindIOException() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram();
        socket.socket().setReuseAddress(false);
        final SocketAddress adr = createBindingAddress(getNextPort());
        final DatagramChannel channel = DatagramChannel.open();

        channel.socket().bind(adr);

        try {
            socket.bind(adr);
        } catch (IOException e) {
            return;
        } finally {
            channel.close();
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindIOExceptionMonitor() throws IOException, InterruptedException {
        final AsyncDatagram socket = getFactory().openDatagram();
        socket.socket().setReuseAddress(false);
        final DatagramChannel channel = DatagramChannel.open();
        final CountDownLatch latch = new CountDownLatch(1);
        final SocketAddress adr = createBindingAddress(getNextPort());

        channel.socket().bind(adr);

        socket.setMonitor(new DatagramMonitor() {
            public void bindFailed(AsyncDatagram s, SocketAddress address, Throwable t) {
                assertSame(socket, s);
                assertTrue(t instanceof IOException);
                assertEquals(adr, address);
                latch.countDown();
            }
        });

        try {
            socket.bind(adr);
        } catch (IOException e) {
            awaitOnLatch(latch);
            return;
        } finally {
            channel.close();
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

}
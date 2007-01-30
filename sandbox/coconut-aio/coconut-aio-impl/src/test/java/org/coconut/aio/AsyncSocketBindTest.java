/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

import org.coconut.aio.monitor.SocketMonitor;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AsyncSocketBindTest extends AioTestCase {

    public void testBind() throws IOException {
        final int port = getNextPort();
        final AsyncSocket socket = getFactory().openSocket();

        assertFalse(socket.isBound());
        assertSame(socket, socket.bind(createBindingAddress(port)));
        assertTrue(socket.isBound());
        //assertEquals(port, socket.getLocalPort()); //NIO bug windows
        //assertTrue(socket.getInetAddress().isAnyLocalAddress());//NIO bug win
        socket.closeNow().getIO();
    }

    public void testBindMonitor() throws IOException, InterruptedException {

        final AsyncSocket socket = getFactory().openSocket();
        final SocketAddress adr = createBindingAddress(getNextPort());
        final CountDownLatch latch = new CountDownLatch(1);

        socket.setMonitor(new SocketMonitor() {
            public void bound(AsyncSocket s, SocketAddress address) {
                assertSame(socket, s);
                assertEquals(adr, address);
                latch.countDown();
            }
        });

        socket.bind(adr);
        awaitOnLatch(latch);
        socket.closeNow().getIO();
    }

    public void testBindIOException() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();
        socket.socket().setReuseAddress(false);
        final SocketAddress adr = createBindingAddress(getNextPort());
        final SocketChannel channel = SocketChannel.open();

        channel.socket().bind(adr);

        try {
            socket.bind(adr);
        } catch (IOException e) {
            return;
        } finally {
            channel.close();
            socket.closeNow().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindIOExceptionMonitor() throws IOException, InterruptedException {
        final AsyncSocket socket = AsyncSocket.open(); //getFactory().openSocket();
        socket.socket().setReuseAddress(false);
        final SocketChannel channel = SocketChannel.open();
        final CountDownLatch latch = new CountDownLatch(1);
        final SocketAddress adr = createBindingAddress(getNextPort());

        channel.socket().bind(adr);

        socket.setMonitor(new SocketMonitor() {
            public void bindFailed(AsyncSocket s, SocketAddress address, Throwable t) {
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
            socket.closeNow().getIO();
        }
        fail("Bind did not fail");
    }

}